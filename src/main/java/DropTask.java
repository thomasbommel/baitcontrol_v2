import java.util.Date;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class DropTask implements Runnable {
	private static final Logger log = Logger.getLogger(DropTask.class.getName());
	private static DropTask instance;

	private PinState lightBarrierState;
	private static DropController dropController;
	private boolean running = true;

	public static DropTask getInstance() {
		if (instance == null) {
			dropController = DropController.getInstance();
			instance = new DropTask();
			instance.lightBarrierState = PinState.LOW;
		}
		return instance;
	}
	
	

	private DropTask() {
		final GpioPinDigitalInput lightBarrier = GPSController.getGpioController()
				.provisionDigitalInputPin(RaspiPin.GPIO_06, PinPullResistance.PULL_DOWN);
		lightBarrier.setShutdownOptions(true);

		lightBarrier.addListener(new GpioPinListenerDigital() {
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
				log.info(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());

				if (event.getState() == PinState.HIGH) {
					dropController.getRelaisController().disableRelais();
					running = false;
				}
				lightBarrierState = event.getState();
			}
		});
	}

	@Override
	public void run() {
		try {
			if(lightBarrierState == PinState.LOW){
				dropController.getRelaisController().enableRelais();
				log.info("relais enabled");
				System.out.println("relais enabled");
			}else{
				log.info("relais was already enabled");
				System.out.println("relais was already enabled");
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
			dropController.getRelaisController().disableRelais();
		}
		while(running){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
