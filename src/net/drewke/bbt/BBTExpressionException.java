package net.drewke.bbt;

/**
 * BBT expression exception
 * @author andreas.drewke
 * @version $Id: BBTExpressionException.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTExpressionException extends Exception {

	public final static String EXPRESSION_INVALID_CHAR = "invalid character in expression";
	public final static String JSENGINE_MISSING = "Missing javascript engine";
	public final static String JSENGINE_EVAL_FAILED = "Eval failed. Expression might be wrong";
	public final static String JSENGINE_EVAL_RETURNINVALID = "Given expression must return a boolean value. Expression might be wrong";

	private int lineNumber;

	/**
	 * Public constructor 
	 * @param message
	 */
	public BBTExpressionException(int lineNumber, String msg) {
		super(msg);
		this.lineNumber = lineNumber;
	}

	/**
	 * @return line number
	 */
	public int getLineNumber() {
		return this.lineNumber;
	}

}
