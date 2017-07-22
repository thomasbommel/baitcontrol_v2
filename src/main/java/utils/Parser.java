package utils;

import java.util.Date;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import baitcontrol.v2.GPGGAEvent;
import baitcontrol.v2.GPVTGEvent;
import baitcontrol.v2.Main;
import exceptions.NMEAparseException;

public class Parser {
	private static final Logger LOGGER = Logger.getLogger(Parser.class.getName());

	private Parser() {
		// hide this constructor
	}

	/**
	 * used to create the GPGGAEvent (positionEvent) from the serial line
	 * @param line
	 * @return
	 * @throws NMEAparseException
	 */
	public static GPGGAEvent parseGPGGAToGPSEvent(String line) throws NMEAparseException {
		final String longitudeRegex = "((?<=[NS]\\,)\\d+(\\.+\\d+)*)";
		final String latitudeRegex = "(\\d+\\.?\\d*)(?=\\,[NS])";

		final Pattern longitudePattern = Pattern.compile(longitudeRegex);
		final Matcher longitudeMatcher = longitudePattern.matcher(line);

		final Pattern latitudePattern = Pattern.compile(latitudeRegex);
		final Matcher latitudeMatcher = latitudePattern.matcher(line);

		Double longitude = null;
		Double latitude = null;

		if (longitudeMatcher.find()) {
			longitude = parseGPGGACoordinateToDegree(longitudeMatcher.group(0));
		}

		if (latitudeMatcher.find()) {
			latitude = parseGPGGACoordinateToDegree(latitudeMatcher.group(0));
		}

		return new GPGGAEvent(latitude, longitude, new Date(System.currentTimeMillis()));
	}

	private static double parseGPGGACoordinateToDegree(String gpggaCoordinate) throws NMEAparseException {
		LOGGER.finest("gpggaCoordinate: " + gpggaCoordinate);
		try {
			double degree = Double.parseDouble(gpggaCoordinate.substring(0,Math.max(0,gpggaCoordinate.indexOf('.') - 2)));
			LOGGER.finest("degree " + degree);
			double minutes = Double
					.parseDouble(gpggaCoordinate.substring(Math.max(0,gpggaCoordinate.indexOf('.') - 2), gpggaCoordinate.length()));
			double comma = minutes / 60;
			LOGGER.finest("minutes " + minutes);
			degree += comma;
			LOGGER.info("parsing \"" + gpggaCoordinate + "\" successful, " + degree+" \n");
			return degree;
		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			LOGGER.warning("Parsing coordinate " + gpggaCoordinate + " failed.");
			System.out.println("Parsing coordinate " + gpggaCoordinate + " failed."+e.getMessage());
			Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
			throw new NMEAparseException("Parsing coordinate " + gpggaCoordinate + " failed.");
		}
	}

	/**
	 * used to create the GPVTGEvent (speedEvent) from the serial line
	 * @param line
	 * @return
	 * @throws NMEAparseException
	 */
	public static GPVTGEvent parseGPVTGToGPSEvent(String line) throws NMEAparseException {
		String gpvtgLine = line.replace(" ", "");
		final String speedRegex = "(\\d+\\.?\\d*)(?=\\,K)";
		final Pattern speedPattern = Pattern.compile(speedRegex);
		final Matcher speedMatcher = speedPattern.matcher(gpvtgLine);

		Double speed = null;

		try {
			if (speedMatcher.find()) {
				speed = Double.parseDouble(speedMatcher.group(0));
				LOGGER.info("parsing speed\"" + line.replace("\n","") + "\" successful, speed:" + speed+". \n");
			}
		} catch (NumberFormatException | StringIndexOutOfBoundsException e) {
			LOGGER.warning("Parsing speed" + line + " failed.");
			System.out.println("Parsing speed" + line + " failed."+e.getMessage());
			Utils.addToTxt("error_" + Utils.dateToString(Main.startTime), e.getMessage());
			throw new NMEAparseException("Parsing speed" + line + " failed.");
		}
		return new GPVTGEvent(speed, new Date(System.currentTimeMillis()));
	}

}
