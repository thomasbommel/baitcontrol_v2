package baitcontrol.v2;

import java.util.logging.Logger;

import com.pi4j.io.gpio.GpioFactory;;

public class Main {

	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	//private static boolean continueLoop = true;
	
	static LCDController lcd;

	public static void main(String[] args) throws InterruptedException {
		LOGGER.info("<<--- SUCCESSFULLY STARTED --->>>");
		GpioFactory.getInstance();
		lcd = LCDController.getInstance(); 
		
		lcd.setFirstLine("test");
		lcd.setSecondLine("dsadas");
		
		Thread lcdControllerThread = new Thread(lcd);
		lcdControllerThread.start();
	}

}
