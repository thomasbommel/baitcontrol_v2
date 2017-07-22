package baitcontrol.v2;

import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import utils.Utils;;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	private static boolean continueLoop = true;

	public static final Date startTime = new Date();

	public static void main(String[] args) throws InterruptedException {
		// create gpio controller
		LOGGER.info("trying to get gpio controller... ");
		Thread.sleep(1000);
		GpioController gpio = GpioFactory.getInstance();
		if (gpio != null) {
			System.out.println("opened gpio controller");
			LOGGER.info("opened gpio controller");
		} else {
			//init failed
			System.out.println("gpio controller initialisation failed");
			System.exit(1);
		}
		Thread.sleep(1000);
		Thread gpsControllerThread = new Thread(GPSController.getInstance());
		Thread.sleep(100);
		Thread dropControllerThread = new Thread(DropController.getInstance());
		Thread.sleep(100);
		Thread lcdControllerThread = new Thread(LCDController.getInstance());

		gpsControllerThread.start();
		dropControllerThread.start();
		lcdControllerThread.start();

		while (continueLoop) {
			try {

				if (GPSController.getInstance().getLastGPVTGUpdateEvent() != null
						&& GPSController.getInstance().getLastGPVTGUpdateEvent().getSpeed() != null) {
					if (DropController.getInstance().getLastDropEvent() != null
							&& DropController.getInstance().getLastDropEvent().getLat() != null) {
						LCDController.getInstance()
								.setFirstLine(Utils.numberToString(
										GPSController.getInstance().getLastGPVTGUpdateEvent().getSpeed(), 6, 2) + " "
										+ DropController.getInstance().calculateDelay(
												DropController.getInstance().getLastDropEvent().getSpeed()));
						
						LCDController.getInstance()
						.setSecondLine("" + new Random().nextInt(10)+ " "+Utils.dateToTimeString(GPSController.getInstance().getLastGPVTGUpdateEvent().getTime()));
					}
				}
			} catch (Exception e) {
				System.out.println("main" + e.getMessage());
				Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
			}
			Thread.sleep(100);
		}
	}

}
