package net.drewke.bbt;

/**
 * BBT definition exceptions
 * @author andreas.drewke
 * @version $Id: BBTDefinitionException.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTDefinitionException extends Exception {

	public final static String PARSE_PREFIX_NOTSUPPORTED = "given parseprefix is not supported";
	public final static String PARSE_SETUP_INVALID = "given parse setup is invalid";

	public final static String EXPRESSION_INVALID = "given expression is invalid";

	private int lineNumber;

	/**
	 * Public constructor
	 * @param msg error message
	 */
	public BBTDefinitionException(int lineNumber, String msg) {
		super(msg);
		this.lineNumber = lineNumber;
	}

	/**
	 * @return line number at which the error occured
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}