package exceptions;

public class LodestoneParserException extends Exception {

	private static final long serialVersionUID = 8666539731191111629L;

	public LodestoneParserException() {
		super();
	}
	
	public LodestoneParserException(String message) {
		super(message);
	}

	public LodestoneParserException(String message, Throwable cause) {
		super(message, cause);
	}

	public LodestoneParserException(Throwable cause) {
		super(cause);
	}
	
	public LodestoneParserException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
