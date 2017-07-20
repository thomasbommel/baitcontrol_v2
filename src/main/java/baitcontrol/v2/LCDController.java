package baitcontrol.v2;

import java.util.logging.Logger;

import manager.LCDManager;

public class LCDController implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(LCDController.class.getName());

	private static LCDController instance;
	private static boolean continueLoop = true;

	private String firstLine = "";
	private String secondLine = "";

	@Override
	public void run() {
		LOGGER.info("LCDController started");
		while (continueLoop) {
			LCDManager.getInstance().printLineToLCD(firstLine, 0);
			LCDManager.getInstance().printLineToLCD(secondLine, 1);
 
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				LOGGER.warning(e.getMessage());
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

	public void setFirstLine(String firstLine) {
		this.firstLine = firstLine;
	}

	public void setSecondLine(String secondLine) {
		this.secondLine = secondLine;
	}

}
