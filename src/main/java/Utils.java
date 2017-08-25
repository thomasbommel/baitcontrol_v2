

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Logger;

public class Utils {
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());
	public static final int NUMBER_LENGTH = 25, DECIMALPLACES = 12, STRINGLENGTH = 30;
	public static final String ENDING = "; ";
	
	private static final String desktopPath = "/home/pi/Desktop/";

	public static String stringWithFixedLength(String s, int length) {
		return String.format("%1$" + length + "s", s);
	}

	public static String stringWithFixedLength(String s) {
		return stringWithFixedLength(s, STRINGLENGTH);
	}

	public static String numberToString(double number, int length, int decimalpl) {
		return String.format("%" + length + "." + decimalpl + "f", number);
	}

	public static String numberToString(double number) {
		return numberToString(number, NUMBER_LENGTH, DECIMALPLACES);
	}

	public static String dateToString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
		return dateFormat.format(date);
	}

	public static String dateToTimeString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat("mm-ss-SSS");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+2"));
		return dateFormat.format(date);
	}

	public static void addToTxt(String filename, String text){
		try (FileWriter fw = new FileWriter(desktopPath+filename+".txt", true);
				BufferedWriter bw = new BufferedWriter(fw);
				PrintWriter out = new PrintWriter(bw)) {
			out.println(text);
			out.flush();
			LOGGER.info("added: '"+text+"' to: "+filename);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOGGER.severe("wasn't able to apped to txt file " + e.getMessage());
		}
	}

}
