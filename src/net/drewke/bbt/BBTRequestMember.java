package net.drewke.bbt;

/**
 * Represents an BBT request member 
 * @author andreas.drewke
 * @version $Id: BBTRequestMember.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTRequestMember extends BBTMember {

	private String value;

	/**
	 * Constructor
	 * @param name
	 * @param type
	 * @param value
	 */
	public BBTRequestMember(int line, String name, String type, String value) {
		super(line, name, type);
		this.value = value;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * generates a string representation of object
	 */
	public String toString() {
		return "BBTRequestMember [value=" + value + ", name=" + name
				+ ", type=" + type + "]";
	}

}