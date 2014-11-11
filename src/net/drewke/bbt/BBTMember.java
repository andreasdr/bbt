package net.drewke.bbt;

/**
 * Baseclass for BBT members, be it request or response
 * @author andreas.drewke
 * @version $Id: BBTMember.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTMember {

	public final static String TYPE_BOOLEAN = "boolean";
	public final static String TYPE_INT = "integer";
	public final static String TYPE_FLOAT = "float";
	public final static String TYPE_STRING = "string";
	public final static String TYPE_OBJECT = "object";
	public final static String TYPE_ARRAY_INTEGER = "integer[]";
	public final static String TYPE_ARRAY_STRING = "string[]";
	public final static String TYPE_ARRAY_OBJECT = "object[]";

	protected int lineNumber;
	protected String name;
	protected String type;

	/**
	 * Constructor
	 * @param name
	 * @param type
	 * @param length
	 */
	public BBTMember(int lineNumber, String name, String type) {
		this.lineNumber = lineNumber;
		this.name = name;
		this.type = type;
	}

	/**
	 * @return line number of definition
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

}