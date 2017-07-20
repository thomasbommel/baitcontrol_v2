package exceptions;

public class LCDInitialisationFailedException extends Exception {

	private static final long serialVersionUID = 0L;

	public LCDInitialisationFailedException(String msg) {
		super(msg);
	}

}
