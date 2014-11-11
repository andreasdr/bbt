package net.drewke.bbt;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Represents an BBT reponse member expression. Must be Javascript
 * @author andreas.drewke
 * @version $Id: BBTExpression.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTExpression {

	private static ScriptEngine jsEngine;

	private int lineNumber;
	private String expression;

	/**
	 * Public constructor
	 */
	public BBTExpression(int lineNumber, String expression) throws BBTDefinitionException {
		this.lineNumber = lineNumber;
		this.expression = expression;
	}

	/**
	 * @return line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return expression
	 */
	public String getExpression() {
		return expression;
	}

	/**
	 * @return expression result
	 */
	public boolean eval(Object p) throws BBTExpressionException {
		Object result = null;

		// set js variable p
		jsEngine.put("p", p);

		// do the evil
		try {
			result = jsEngine.eval(expression);
		} catch (ScriptException exception) {
			throw new BBTExpressionException(lineNumber, exception.getMessage());
		}

		// reset js variable p
		jsEngine.put("p", null);

		// validate result
		if (result == null || result instanceof Boolean == false) {
			throw new BBTExpressionException(lineNumber, BBTExpressionException.JSENGINE_EVAL_RETURNINVALID);
		}

		return ((Boolean)result).booleanValue();
	}

	/**
	 * Generates a string representation of expression
	 */
	public String toString() {
		return "BBTExpression [line=" + lineNumber + ", expression=" + expression + "]";
	}

	static {
		ScriptEngineManager mgr = new ScriptEngineManager();
		jsEngine = mgr.getEngineByName("JavaScript");
		if (jsEngine == null) {
			System.out.println("Javascript engine is not available! Exiting!");
			System.exit(-1);
		}
		jsEngine.put("p", null);
	}

}