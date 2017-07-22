package exceptions;

public class TooLowSpeedException extends Exception {

	private static final long serialVersionUID = 1L;

	public TooLowSpeedException(){
		super();
	}
	
	public TooLowSpeedException(String msg) {
		super(msg);
	}
	
	
}
