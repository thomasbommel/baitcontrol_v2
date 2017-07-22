package baitcontrol.v2;

import java.util.Date;
import java.util.logging.Logger;

import exceptions.TooLowSpeedException;
import utils.Utils;

public class DropController implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(DropController.class.getName());

	private final static double SECONDS_TO_DROP = 0.1; // in seconds
	private final static double MAXIMUM_TIME_TO_DROP_IN_SECONDS = 10;

	private final static long DISTANCE_BETWEEN_DROPS = 50; // in meter
	private final static double MINIMUM_SPEED_IN_KMH = 0; // in kmh
	private static boolean continueLoop = true;

	private static DropController instance;
	private DropEvent lastDropEvent;

	public static DropController getInstance() {
		if (instance == null) {
			instance = new DropController();
		}
		return instance;
	}

	@Override
	public void run() {
		while (continueLoop) {
			if (GPSController.getInstance().positionHasAlreadyBeenUpdated()) {
				if (GPSController.getInstance().speedHasAlreadyBeenUpdated()) {
					double speed = GPSController.getInstance().getLastGPVTGUpdateEvent().getSpeed();
					double lat = GPSController.getInstance().getLastGPGGAUpdateEvent().getLat();
					double lng = GPSController.getInstance().getLastGPGGAUpdateEvent().getLng();

					DropEvent dropEvent = new DropEvent(lat, lng, speed, new Date());
					lastDropEvent = dropEvent;

					if (speed >= MINIMUM_SPEED_IN_KMH) {
						//LOGGER.info(dropEvent.toString() + " delay was " + getDelay(dropEvent.getSpeed()));
						//TODO do drop
						Utils.addToTxt("drop_" + Utils.dateToString(Main.startTime), dropEvent.toString());

						try {
							Thread.sleep(getDelay(speed));
						} catch (InterruptedException | TooLowSpeedException e) {
							LOGGER.warning(e.getMessage() + " trying again in 100 milliseconds");
							System.out.println("dropcontroller error "+e.getMessage());
						}
					} else {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							LOGGER.info(e1.getMessage());
							System.out.println("dropcontroller error "+e1.getMessage());
						}
					}

				} else {
					LOGGER.info("waiting for first speedUpdate...");
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						System.out.println("dropcontroller thread error "+e.getMessage());
						LOGGER.warning(e.getMessage());
					}
				}
			} else {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					LOGGER.warning(e.getMessage());
					System.out.println("dropcontroller error "+e.getMessage());
				}
				LOGGER.info("waiting for first positionUpdate...");
			}
		}
	}

	/**
	 * 
	 * @param speed
	 *            in kmh
	 * @return delay in milliseconds
	 */
	public long getDelay(double speed) throws TooLowSpeedException {
		if (speed < MINIMUM_SPEED_IN_KMH) {
			throw new TooLowSpeedException("speed is to low for a drop, minimum speed " + MINIMUM_SPEED_IN_KMH
					+ " actual speed was: " + speed);
		} else {
			double delay = Math.max((long) ((DISTANCE_BETWEEN_DROPS / (speed / 3.6d) * 1000) * 1000),
					(long) (SECONDS_TO_DROP * 1000.0));

			return (long) Math.min(delay, (long) (MAXIMUM_TIME_TO_DROP_IN_SECONDS * 1000));
		}
	}
	
	public long calculateDelay(double speed){
		try{
			return getDelay(speed);
		}catch(TooLowSpeedException e){
			return -1;
		}
	}

	public DropEvent getLastDropEvent() {
		return lastDropEvent;
	}

}
