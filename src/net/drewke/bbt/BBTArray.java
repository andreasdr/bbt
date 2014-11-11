package net.drewke.bbt;

import org.json.JSONArray;

/**
 * Class helping with iterating json arrays
 * @author andreas.drewke
 * @version $Id: BBTArray.java 4744 2012-07-13 13:29:29Z andreasdrewke $
 */
public class BBTArray {

	private String name;
	private JSONArray array;
	private int index;
	private int ip; 

	/**
	 * Public constructor
	 * @param name
	 * @param ip
	 * @param array
	 */
	public BBTArray(String name, int ip, JSONArray array) {
		this.name = name;
		this.ip = ip;
		this.array = array;
		this.index = 0;
	}

	/**
	 * @return index at which element in array are we at
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @return instruction pointer, code name for response member position for array validation start
	 */
	public int getIP() {
		return ip;
	}

	/**
	 * @return property name of this array
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return next element available
	 */
	public boolean hasNextElement() {
		return array.opt(index + 1) != null;
	}

	/**
	 * @return array is empty
	 */
	public boolean isEmpty() {
		return array.length() == 0;
	}

	/**
	 * Returns current element of this array
	 * @return Object or null
	 */
	public Object getCurrentElement() {
		Object element = array.opt(index);
		return element;
	}

	/**
	 * Sets up given array for next element
	 */
	public void setupNextElement() {
		index++;
	}

	/**
	 * String representation of this BBTArray
	 */
	public String toString() {
		return "BBTArray [name=" + name + ", index=" + index + "]";
	}

}