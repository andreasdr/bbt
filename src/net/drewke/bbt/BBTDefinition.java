package net.drewke.bbt;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Black Box Test Definition
 * @author andreas
 * @version $Id: BBTDefinition.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBTDefinition {

	private final static String PARSE_PREFIX_SETUP = "setup.";
	private final static String PARSE_PREFIX_REQUEST = "request.";
	private final static String PARSE_PREFIX_RESPONSE = "response.";

	private Hashtable<String,String> setup = null;
	Vector<BBTRequestMember> request = null;
	Vector<BBTResponseMember> response = null;	

	/**
	 * Factory method, generates a BBT definition from file
	 * @param filename
	 * @return
	 * @throws IOException
	 * @throws BBTDefinitionException
	 */
	public static BBTDefinition loadFromFile(String filename)
		throws IOException, BBTDefinitionException {
		// we need to provide the following
		Hashtable<String,String> setup = new Hashtable<String,String>();
		Vector<BBTRequestMember> request = new Vector<BBTRequestMember>();
		Vector<BBTResponseMember> response = new Vector<BBTResponseMember>();

		// get file reader
		int lineNr = 0;
		BufferedReader fileReader = new BufferedReader(new FileReader(new File(filename)));
		try {

			String line;
			// read file line by line
			while ((line = fileReader.readLine()) != null) {
				lineNr++;
				String lineUntrimmed = line;
				line = line.trim();

				// skip on empty lines or comments
				if (line.length() == 0 || line.startsWith("#")) {
					continue;
				}

				// check for setup, request or response input line
				if (line.startsWith(PARSE_PREFIX_SETUP)) {
					// we have a setup
					line = line.substring(PARSE_PREFIX_SETUP.length());
					String key = null;
					String value = null;
					StringTokenizer t = new StringTokenizer(line, "=");
					// parse key
					if (t.hasMoreTokens()) {
						key = t.nextToken().trim().toLowerCase();
					}
					// parse key
					if (t.hasMoreTokens()) {
						value = t.nextToken().trim();
					}
					//
					if (key != null && value != null &&
						key.length() > 0 && value.length() > 0) {
						//
						setup.put(key, value);
					} else {
						throw new BBTDefinitionException(
							lineNr,
							BBTDefinitionException.PARSE_SETUP_INVALID
						);
					}
				} else
				if (line.startsWith(PARSE_PREFIX_REQUEST)) {
					line = line.substring(PARSE_PREFIX_REQUEST.length());
					// we have a request
					String name;
					String type;
					String value;
					StringTokenizer t = new StringTokenizer(line, ":");
					name = t.nextToken().trim();
					StringTokenizer t1 = new StringTokenizer(t.nextToken().trim(), "=");
					type = t1.nextToken().trim().toLowerCase();
					value = t1.nextToken().trim();
					request.add(
						new BBTRequestMember(lineNr, name, type, value)
					);
				} else
				if (line.startsWith(PARSE_PREFIX_RESPONSE)) {
					// parse depth of conditions
					StringTokenizer t = new StringTokenizer(line, ";");
					StringTokenizer t2 = new StringTokenizer(t.nextToken(), ":");
					// parse "response.accountId : int[20]"
					int depth = 0;
					for(int i = 0; i < lineUntrimmed.length(); i++) {
						if (lineUntrimmed.charAt(i) == '\t') {
							depth++;
						} else {
							break;
						}
					}
					String name = t2.nextToken().trim().substring(PARSE_PREFIX_RESPONSE.length());
					String type = t2.nextToken().trim().toLowerCase();

					// parse optional expression
					String expression = null;
					if (t.hasMoreTokens()) {
						expression = t.nextToken().trim();
						// we dont want to eval empty expressions
						if (expression.length() == 0) {
							expression = null;
						}
					}
					// parse optional output
					String outputTo = null;
					if (t.hasMoreTokens()) {
						outputTo = t.nextToken().trim();
					}

					response.add(
						new BBTResponseMember(
							lineNr,
							depth,
							expression == null?null:new BBTExpression(lineNr, expression),
							name,
							type,
							outputTo
						)
					);

				} else {
					throw new BBTDefinitionException(
						lineNr,
						BBTDefinitionException.PARSE_PREFIX_NOTSUPPORTED
					);
				}
			}

			//
			return new BBTDefinition(
				setup,
				request,
				response
			);
		} catch (NoSuchElementException e) {
			throw new BBTDefinitionException(lineNr, e.getClass().getName() + " : " + e.getMessage());
		} finally {
			// Close the input stream
			fileReader.close();
		}
	}

	/**
	 * Private constructor
	 * @param setup
	 */
	private BBTDefinition(Hashtable<String,String> setup, Vector<BBTRequestMember> request, Vector<BBTResponseMember> response) {
		this.setup = setup;
		this.request = request;
		this.response = response;
	}

	/**
	 * @return setup members
	 */
	public Hashtable<String, String> getSetup() {
		return setup;
	}

	/**
	 * @return request members
	 */
	public Vector<BBTRequestMember> getRequest() {
		return request;
	}

	/**
	 * @return response members
	 */
	public Vector<BBTResponseMember> getResponse() {
		return response;
	}

}