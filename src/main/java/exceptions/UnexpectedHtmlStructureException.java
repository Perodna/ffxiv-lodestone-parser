package exceptions;

public class UnexpectedHtmlStructureException extends LodestoneParserException {

	private static final long serialVersionUID = 2982942390986286016L;

	public UnexpectedHtmlStructureException(String message) {
		super(message);
	}

}
