
import java.io.IOException;
import java.util.Date;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.serial.Baud;
import com.pi4j.io.serial.DataBits;
import com.pi4j.io.serial.FlowControl;
import com.pi4j.io.serial.Parity;
import com.pi4j.io.serial.Serial;
import com.pi4j.io.serial.SerialConfig;
import com.pi4j.io.serial.SerialFactory;
import com.pi4j.io.serial.StopBits;
import com.pi4j.util.CommandArgumentParser;

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

	private static String serialPort = "/dev/ttyUSB0";// "/dev/ttyUSB0"

	private GPSController() {
		// prevent usage
	}

	/**
	 * @param args
	 *            the args
	 */
	public static void main(final String[] args) {
		initGPIO();
		// addShutDownHook();

		try {
			final GPSdEndpoint ep = new GPSdEndpoint(HOST, PORT, new ResultParser());

			dropController = DropController.getInstance();
			lcdController = LCDController.getInstance();

			ep.addListener(new MyObjectListener() {
				public void handleTPV(TPVObject tpv) {
					log.info("gpsUpdate   " + Utils.dateToString(new Date((long) tpv.getTimestamp() * 1000l)) + "  "
							+ tpv.getLatitude() + "  " + tpv.getLongitude() + "  " + tpv.getSpeed() + " "
							+ DropController.getDelayForKMH(tpv.getSpeed() * 3.6));
					System.out.println("gpsUpdate   " + Utils.dateToString(new Date((long) tpv.getTimestamp() * 1000l))
							+ "  " + tpv.getLatitude() + "  " + tpv.getLongitude() + "  " + tpv.getSpeed() + " "
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

			MotorController dropTask = MotorController.getInstance();
			Thread dropTaskThread = new Thread(dropTask);
			dropTaskThread.start();

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			setBaudRate();
			Thread.sleep(1000);

			setUpdateRate();
			Thread.sleep(1000);

			System.out.println("-------------------- SUCCESS 3 --------------");
			log.info("-------------------- SUCCESS 3 --------------");
			try {
				Thread.sleep(Long.MAX_VALUE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (final Exception e) {
			log.severe("Problem encountered: " + e.getMessage());
		}

	}

	private static void setBaudRate() {
		final Serial serial = SerialFactory.createInstance();
		try {
			SerialConfig config = new SerialConfig();
			config.device(serialPort).baud(Baud._9600).dataBits(DataBits._8).parity(Parity.NONE).stopBits(StopBits._1)
					.flowControl(FlowControl.NONE);

			log.info(" Connecting to: " + config.toString()
					+ " We are sending ASCII data on the serial port every 1 second."
					+ " Data received on serial port will be displayed below.");
			System.out.println(" Connecting to: " + config.toString()
					+ " We are sending ASCII data on the serial port every 1 second."
					+ " Data received on serial port will be displayed below.");

			serial.open(config);

			try {
				serial.write("$PMTK251,57600*2C\r\n");
				serial.write('\r');
				serial.write('\n');
				System.out.println("-------------------- SUCCESS 1 --------------");
				log.info("-------------------- SUCCESS 1 --------------");
			} catch (IllegalStateException ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
				log.info(ex.getMessage());
			}
		} catch (IOException ex) {
			log.info(" ==>> SERIAL SETUP 1 FAILED : " + ex.getMessage());
			System.out.println(" ==>> SERIAL SETUP 1 FAILED : " + ex.getMessage());
			return;
		}
	}

	private static void setUpdateRate() {
		final Serial serial = SerialFactory.createInstance();
		try {
			SerialConfig config = new SerialConfig();
			config.device(serialPort).baud(Baud._57600).dataBits(DataBits._8).parity(Parity.NONE).stopBits(StopBits._1)
					.flowControl(FlowControl.NONE);

			log.info(" Connecting to: " + config.toString()
					+ " We are sending ASCII data on the serial port every 1 second."
					+ " Data received on serial port will be displayed below.");
			System.out.println(" Connecting to: " + config.toString()
					+ " We are sending ASCII data on the serial port every 1 second."
					+ " Data received on serial port will be displayed below.");

			serial.open(config);

			try {
				serial.write("$PMTK220,200*2C\r\n");
				serial.write('\r');
				serial.write('\n');
				System.out.println("-------------------- SUCCESS 2 --------------");
				log.info("-------------------- SUCCESS 2 --------------");
			} catch (IllegalStateException ex) {
				ex.printStackTrace();
				System.out.println(ex.getMessage());
				log.info(ex.getMessage());
			}
		} catch (IOException ex) {
			log.info(" ==>> SERIAL SETUP 2 FAILED : " + ex.getMessage());
			System.out.println(" ==>> SERIAL SETUP 2 FAILED : " + ex.getMessage());
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

	private static void addShutDownHook() {
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				System.out.println("\n\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				log.warning("\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				Utils.addToTxt("interrupted_" + Utils.dateToTimeString(new Date()),
						"\nPROGRAM WAS INTERRUPTED. SHUTTING " + "DOWN!");
				gpioController.shutdown();
			}
		});
	}
}
