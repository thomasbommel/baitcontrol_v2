

import java.util.Date;
import java.util.logging.Logger;

import de.taimos.gpsd4java.types.TPVObject;

public class DropController implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(DropController.class.getName());

	private static final double SECONDS_TO_DROP = 0.25; // in seconds
	private static final double MAXIMUM_TIME_TO_DROP_IN_SECONDS = 10;

	private static final long DISTANCE_BETWEEN_DROPS = 50; // in meter
	private static boolean continueLoop = true;

	private static DropController instance;
	private RelaisController relaisController;
	
	private static Date firstDrop;
	private TPVObject lastGPSObject;
	private TPVObject lastDropObject;
	
	private DropController(){
		//prevent usage;
	}

	public static DropController getInstance() {
		if (instance == null) {
			firstDrop = new Date();
			instance = new DropController();
		}
		return instance;
	}

	public void run() {
		System.out.println("dropcontroller thread started");
		relaisController = RelaisController.getInstance(GPSController.getGpioController());
		
		while (continueLoop) {
			if (lastGPSObject != null && !Double.isNaN(lastGPSObject.getLatitude()) && !Double.isNaN(lastGPSObject.getLongitude()) && !Double.isNaN(lastGPSObject.getSpeed())    ) {
				lastDropObject = lastGPSObject;
				String dropDate = Utils.dateToTimeString(new Date((long) (lastGPSObject.getTimestamp() * 1000)));
				String latitude = Utils.numberToString(lastGPSObject.getLatitude());
				String longitude = Utils.numberToString(lastGPSObject.getLongitude());
				String speed = Utils.numberToString(lastGPSObject.getSpeed() * 3.6);
				long delay = getDelayForKMH(lastGPSObject.getSpeed() * 3.6);
				String delayString = Utils.numberToString(delay);

				DropTask dropTask = DropTask.getInstance();
				Thread dropTaskThread = new Thread(dropTask);
				dropTaskThread.start(); 
			
				Utils.addToTxt("drop_" + Utils.dateToTimeString(firstDrop),
						dropDate + " " + latitude + " " + longitude + " " + speed + " " + delayString);
				try {
					Thread.sleep(delay);
					System.out.println("current Delay: " + Utils.numberToString(delay, 10, 3) + "ms at speed: " + Utils.numberToString(lastDropObject.getSpeed()*3.6,8, 2) + "km/h");
				} catch (InterruptedException e) {
					LOGGER.warning("DropController Thread interrupted " + e.getMessage());
				}
			}
		}
	}

	/**
	 * calculates the delay for a speed(in kmh) <b>!!! library uses m/s !!!</b>
	 * 
	 * @param speed <b>in kmh</b>
	 * @return
	 */
	public static long getDelayForKMH(double speed) {
		double delay = Math.max((long) ((DISTANCE_BETWEEN_DROPS / (speed / 3.6d) ) * 1000),
				(long) (SECONDS_TO_DROP * 1000.0));
		return (long) Math.min(delay, (long) (MAXIMUM_TIME_TO_DROP_IN_SECONDS * 1000));
	}

	public void setLastGPSObject(TPVObject obj) {
		this.lastGPSObject = obj;
	}
	
	public TPVObject getLastGPSObject() {
		return lastGPSObject;
	}
	
	public TPVObject getLastDropObject() {
		return lastDropObject;
	}
	
	public RelaisController getRelaisController() {
		return relaisController;
	}

}
