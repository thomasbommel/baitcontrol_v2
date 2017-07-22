package manager;

import java.util.logging.Logger;

import com.pi4j.wiringpi.Lcd;

import exceptions.LCDInitialisationFailedException;

public class LCDManager {
	private static final Logger LOGGER = Logger.getLogger(LCDManager.class.getName());

	private static final int LCD_ROWS = 2;
	private static final int LCD_COLUMNS = 16;
	private static final int LCD_BITS = 4;

	private int lcdHandle;
	private static LCDManager instance;

	private LCDManager() throws LCDInitialisationFailedException {
		this.lcdHandle = Lcd.lcdInit(LCD_ROWS, // number of row supported by LCD
				LCD_COLUMNS, // number of columns supported by LCD
				LCD_BITS, // number of bits used to communicate to LCD
				11, // LCD RS pin
				10, // LCD strobe pin
				0, // LCD data bit 1
				1, // LCD data bit 2
				2, // LCD data bit 3
				3, // LCD data bit 4
				0, // LCD data bit 5 (set to 0 if using 4 bit communication)
				0, // LCD data bit 6 (set to 0 if using 4 bit communication)
				0, // LCD data bit 7 (set to 0 if using 4 bit communication)
				0); // LCD data bit 8 (set to 0 if using 4 bit communication)

		// verify initialization
		if (lcdHandle == -1) {
			throw new LCDInitialisationFailedException("LCD initialisation failed (!!!)");
		}
		Lcd.lcdClear(lcdHandle);
		Lcd.lcdPosition(lcdHandle, 0, 0);
		Lcd.lcdPuts(lcdHandle, "   >> v0.1 <<");
		Lcd.lcdPosition(lcdHandle, 0, 1);
		Lcd.lcdPuts(lcdHandle, "by Thomas");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			LOGGER.warning(e.getMessage());
		}
	}

	public static LCDManager getInstance() {
		if (instance == null) {
			try {
				instance = new LCDManager();
			} catch (LCDInitialisationFailedException e) {
				System.out.println(e.getMessage());
				LOGGER.severe(e.getMessage());
			}
		}
		if (instance == null) {
			throw new IllegalStateException("LCDManager instance should never be null");
		} else {
			return instance;
		}
	}

	public void clearLCD() {
		Lcd.lcdClear(this.lcdHandle);
	}

	public void printLineToLCD(String text, int line) {
		if (text.length() > 16) {
			LOGGER.info("text:" + text + " " + (text.length() - 16) + " characters too long");
		}
		Lcd.lcdPosition(this.lcdHandle, 0, line);
		Lcd.lcdPuts(lcdHandle, text);
		for (int i = text.length(); i < 16; i++) {
			Lcd.lcdPuts(lcdHandle, " ");
		}
	}

}
