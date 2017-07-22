

import java.util.Date;
import java.util.logging.Logger;

import de.taimos.gpsd4java.types.TPVObject;

public class LCDController implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(LCDController.class.getName());

	private static LCDController instance;
	private static boolean continueLoop = true;

	// private String firstLine = "";
	// private String secondLine = "";

	@Override
	public void run() {
		System.out.println("dropcontroller thread started");

		while (continueLoop) {

			TPVObject lastUpdateObject = GPSController.getDropController().getLastGPSObject();
			TPVObject lastDropObject = GPSController.getDropController().getLastDropObject();
			if (lastUpdateObject != null) {
				try {
					String speed = Utils.numberToString(GPSController.getDropController().getLastGPSObject().getSpeed(),
							6, 1);
					
					String delay = Utils.numberToString(GPSController.getDropController().getDelayForKMH(GPSController.getDropController().getLastGPSObject().getSpeed()*3.6),
							6, 0);

					LCDManager.getInstance().printLineToLCD(speed+" "+delay, 0);

					if (lastDropObject != null) {
						String lastgps = Utils.dateToTimeString(new Date(
								(long) (GPSController.getDropController().getLastGPSObject().getTimestamp() * 1000)));
						String lastdrop = Utils.dateToTimeString(new Date(
								(long) (GPSController.getDropController().getLastDropObject().getTimestamp() * 1000)));
						LCDManager.getInstance().printLineToLCD(lastgps.substring(3, lastgps.length()) + "   " + lastdrop.substring(3, lastdrop.length()), 1);
					}

				} catch (Exception e) {
					LOGGER.warning(e.getMessage());
					System.out.println(e.getMessage());
				}
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				LOGGER.warning(e.getMessage());
				System.out.println(e.getMessage());
			}
		}
	}

	public static LCDController getInstance() {
		if (instance == null) {
			instance = new LCDController();
			instance.clearLCD();
		}
		return instance;
	}

	public void clearLCD() {
		LCDManager.getInstance().clearLCD();
	}

	public void printLineToLCD(String text, int line) {
		LCDManager.getInstance().printLineToLCD(text, line);
	}

	// public void setFirstLine(String firstLine) {
	// this.firstLine = firstLine;
	// }
	//
	// public void setSecondLine(String secondLine) {
	// this.secondLine = secondLine;
	// }

}
