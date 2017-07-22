package baitcontrol.v2;

import java.util.Date;
import java.util.logging.Logger;

import com.pi4j.wiringpi.Serial;

import exceptions.NMEAparseException;
import utils.Parser;
import utils.Utils;

public class GPSController implements Runnable {
	private static final Logger LOGGER = Logger.getLogger(GPSController.class.getName());

	private int serialPort;
	private static boolean continueLoop = true;

	private static GPSController instance;

	private GPGGAEvent lastGPGGAUpdateEvent;
	private GPVTGEvent lastGPVTGUpdateEvent;

	private GPSController() {
		System.out.println("GPSController init");
		LOGGER.info("GPSController init");
		this.serialPort = Serial.serialOpen("/dev/serial0", 14400);//9600 
		System.out.println("GPSController initialised");
		LOGGER.info("GPSController initialised");
		if (this.serialPort == -1) {
			LOGGER.severe("Serial Port Failed");
			System.out.println("Serial Port Failed");
		}
	}

	public static GPSController getInstance() {
		if (instance == null) {
			instance = new GPSController();
		}
		//instance.serialPort = Serial.serialOpen("/dev/serial0", 9600);//TODO
		return instance;
	}

	@Override
	public void run() {
		LOGGER.info("GPSController started");
		String nmea = "";
		int i=0;
		while (continueLoop) {

			if (Serial.serialDataAvail(serialPort) > 0) {
				byte[] rawData = Serial.serialGetAvailableBytes(serialPort);
				for (byte dataByte : rawData) {
					char character = (char) dataByte;
					if (character == '\n') {
						LOGGER.fine("new nmealine " + nmea);
						if (nmea.contains("GPGGA")) {
							LOGGER.fine("new GPGGA line");
							try {
								lastGPGGAUpdateEvent = Parser.parseGPGGAToGPSEvent(nmea);
							} catch (NMEAparseException e) {
								LOGGER.warning(e.getMessage());
								System.out.println(e.getMessage());
								Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
							}
						}
						if (nmea.contains("GPVTG")) {
							LOGGER.fine("new GPGGA line");
							try {
								lastGPVTGUpdateEvent = Parser.parseGPVTGToGPSEvent(nmea);
							} catch (NMEAparseException e) {
								LOGGER.warning(e.getMessage());
								System.out.println(e.getMessage());
								Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
							}
						}
						System.out.println(nmea);
						nmea = "";
						i=0;
					} else {
						nmea += Character.toString(character);
					}
				}
			} else {
				System.out.println("data empty "+i++);
				LOGGER.finest("data empty");
			}

			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				LOGGER.finest(e.getMessage());
				System.out.println(e.getMessage());
				Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
			}
		}
	}

	public GPGGAEvent getLastGPGGAUpdateEvent() {
		return lastGPGGAUpdateEvent;
	}

	public GPVTGEvent getLastGPVTGUpdateEvent() {
		return lastGPVTGUpdateEvent;
	}

	public boolean positionHasAlreadyBeenUpdated() {
		return lastGPGGAUpdateEvent != null;
	}

	public boolean speedHasAlreadyBeenUpdated() {
		return lastGPVTGUpdateEvent != null;
	}

}
