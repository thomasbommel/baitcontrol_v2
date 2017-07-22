package test;

import exceptions.NMEAparseException;
import utils.Parser;

public class Test {

	public static void main(String[] args) {
		try {
			Parser.parseGPVTGToGPSEvent("$GPVTG,12.34 T, , M, 36.34, N, , K, D*0F");
		} catch (NMEAparseException e) {
			e.printStackTrace();
		}

	}

}
