package net.drewke.bbt;

/**
 * Represents an BBT response member
 * @author andreas.drewke
 * @version $Id: BBTResponseMember.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTResponseMember extends BBTMember {

	private int depth;
	private BBTExpression expression;
	private String outputTo;

	/**
	 * Constructor
	 * @param expressions
	 * @param name
	 * @param type
	 */
	public BBTResponseMember(int line, int depth, BBTExpression expression, String name, String type, String outputTo) {
		super(line, name, type);
		this.depth = depth;
		this.expression = expression;
		this.outputTo = outputTo;
	}

	/**
	 * @return Depth, needs corresponding expressions
	 */
	public int getDepth() {
		return this.depth;
	}

	/**
	 * @return expression or null
	 */
	public BBTExpression getExpression() {
		return expression;
	}

	/**
	 * @return output content of response member to variables
	 */
	public String getOutputTo() {
		return outputTo;
	}

	/**
	 * generates a string representation for class instance 
	 */
	public String toString() {
		return "BBTResponseMember [depth=" + depth + ", expression="
				+ expression + ", name=" + name + ", type=" + type + "]";
	}

}