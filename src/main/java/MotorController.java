import java.util.Date;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import de.taimos.gpsd4java.types.TPVObject;

public class MotorController implements Runnable {
	private static final Logger log = Logger.getLogger(MotorController.class.getName());
	private static MotorController instance;

	private PinState lightBarrierState;
	private static DropController dropController;
	private boolean running = true;

	public static MotorController getInstance() {
		if (instance == null) {
			dropController = DropController.getInstance();
			instance = new MotorController();
			instance.lightBarrierState = PinState.LOW;
		}
		return instance;
	}

	private MotorController() {
		final GpioPinDigitalInput lightBarrier = GPSController.getGpioController()
				.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);
		lightBarrier.setShutdownOptions(true);

		lightBarrier.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				log.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

				if (event.getState() == PinState.HIGH) {
					stopMotor();
					

					// drop ==================
					TPVObject dropTPVObject = DropController.getInstance().getLastGPSObject();

					String dropDate = Utils.dateToTimeString(new Date((long) (dropTPVObject.getTimestamp() * 1000)));
					String latitude = Utils.numberToString(dropTPVObject.getLatitude());
					String longitude = Utils.numberToString(dropTPVObject.getLongitude());
					String speed = Utils.numberToString(dropTPVObject.getSpeed() * 3.6);
					long delay = DropController.getDelayForKMH(dropTPVObject.getSpeed() * 3.6);
					String delayString = Utils.numberToString(delay);

					// ADD drop to txt
					Utils.addToTxt("drop_" + Utils.dateToString(DropController.getFirstDrop()),
							dropDate + " " + latitude + " " + longitude + " " + speed + " " + delayString);

					// ==========================
				}
				lightBarrierState = event.getState();
			}
		});
	}

	@Override
	public void run() {
		dropController.getRelaisController().disableRelais();

		while (running) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void startMotor() {
			try {
				if (lightBarrierState == PinState.LOW) {
					dropController.getRelaisController().enableRelais();

					log.info("relais enabled");
					System.out.println("relais enabled");
				} else {
					log.info("relais was already enabled");
					System.out.println("relais was already enabled");
				}
			} catch (Exception e) {
				System.out.println("ERROR: " + e.getMessage());
				dropController.getRelaisController().disableRelais();
			}	
	}
	
	public synchronized void stopMotor(){
		dropController.getRelaisController().disableRelais();
	}

}
