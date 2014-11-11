package net.drewke.bbt;

/**
 * Exception class for BBT run
 * @author andreas.drewke
 * @version $Id: BBTRunException.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTRunException extends Exception {

	public final static int LINENUMBER_NONE = -1;

	public final static String REQUEST_TYPE_UNSUPPORTED = "request type is unsupported";
	public final static String REQUEST_SESSION_USE_SESSIONIDMISSING = "request is with session, but session id is missing in args";

	public final static String HTTP_REQUEST_IO_FAILED = "http request io failed";
	public final static String HTTP_REQUEST_ENCODE_FAILED = "http request encode data failed";
	public final static String HTTP_REQUEST_URL_INVALID = "http request invalid url";
	public final static String HTTP_REQUEST_SETUP_METHOD_INVALID = "Unsupported setup.method";
	public final static String HTTP_RESPONSE_JSON_INVALID = "http response json is invalid";

	public final static String RESPONSE_TYPE_UNSUPPORTED = "response type is unsupported";
	public final static String RESPONSE_PROPERTY_MISSING = "response property missing";
	public final static String RESPONSE_ARRAY_MISSING = "response array missing";
	public final static String RESPONSE_PROPERTY_TYPE_INVALID = "response type is wrong";
	public final static String RESPONSE_PROPERTY_DEPTH_INVALID = "response property depth is wrong";
	public final static String RESPONSE_PROPERTY_OUTPUTTO_UNSUPPORTED = "response property outputto is unsupported";
	public final static String RESPONSE_SUBPROPERTY_INVALIDTYPE = "response sub property has an invalid type";

	private int lineNumber;

	/**
	 * Public constructor
	 * @param msg
	 */
	public BBTRunException(int lineNumber, String msg) {
		super(msg);
		this.lineNumber = lineNumber;
	}

	/**
	 * @return line number
	 */
	public int getLineNumber() {
		return lineNumber;
	}

}