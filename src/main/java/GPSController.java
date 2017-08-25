
import java.util.Date;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import de.taimos.gpsd4java.backend.GPSdEndpoint;
import de.taimos.gpsd4java.backend.ResultParser;
import de.taimos.gpsd4java.types.TPVObject;

/**
 * This class provides tests during the startup phase of GPSd4Java<br>
 * It will later be replaced by JUnit Tests
 * 
 * created: 17.01.2011
 * 
 */
public class GPSController {

	private static final String HOST = "localhost";
	private static final int PORT = 2947;

	private static final Logger log = Logger.getLogger(GPSController.class.getName());

	private static DropController dropController;
	private static LCDController lcdController;
	private static GpioController gpioController;

	private GPSController() {
		// prevent usage
	}

	/**
	 * @param args
	 *            the args
	 */
	public static void main(final String[] args) {
		initGPIO();

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("\n\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				log.warning("\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				Utils.addToTxt("interrupted_" + Utils.dateToTimeString(new Date()),"\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				gpioController.shutdown();
			}
		});

		try {
			final GPSdEndpoint ep = new GPSdEndpoint(HOST, PORT, new ResultParser());
			
	
			dropController = DropController.getInstance();
			lcdController = LCDController.getInstance();

			ep.addListener(new MyObjectListener() {
				public void handleTPV(TPVObject tpv) {
					log.info(tpv.getLatitude() + "  " + tpv.getLongitude() + "  " + tpv.getSpeed() + " "
							+ DropController.getDelayForKMH(tpv.getSpeed() * 3.6));
					System.out.println(tpv.getLatitude() + "  " + tpv.getLongitude() + "  " + tpv.getSpeed() + " "
							+ DropController.getDelayForKMH(tpv.getSpeed() * 3.6));
					dropController.setLastGPSObject(tpv);
				}
			});

			ep.start();
			ep.watch(true, true); // never remove me !!!

			Thread dropControllerThread = new Thread(dropController);
			Thread lcdControllerThread = new Thread(lcdController);

			lcdControllerThread.start();
			dropControllerThread.start();

			Thread.sleep(10000000000l);
		} catch (final Exception e) {
			log.severe("Problem encountered: " + e.getMessage());
		}
	}

	/**
	 * is needed for initialization of the LCD and the Relay
	 */
	private static void initGPIO() {
		log.info("trying to get gpio controller... ");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gpioController = GpioFactory.getInstance();
		if (gpioController != null) {
			System.out.println("opened gpio controller");
			log.info("opened gpio controller");
		} else {
			System.out.println("gpio controller initialisation failed");
			System.exit(1);
		}
	}

	public static DropController getDropController() {
		return dropController;
	}

	public static GpioController getGpioController() {
		if (gpioController == null) {
			System.out.println(">>>>> GPIO CONTROLLER IS NULL <<<<");
		}
		return gpioController;
	}
}
