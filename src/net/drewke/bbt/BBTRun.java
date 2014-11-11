package net.drewke.bbt;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Implements a BBT test run
 * @author andreas.drewke
 * @version $Id: BBTRun.java 4796 2012-08-28 14:34:33Z andreasdrewke $
 */
public class BBTRun {

	public final static int STATUS_SUCCESS = 0;
	public final static int STATUS_FAILED = -1;

	private final static int IP_NONE = -1;

	private BBTDefinition bbtDefinition;

	/**
	 * Does a http post
	 * @param url name
	 * @param request
	 * @param post parameter name of null if using body
	 * @return response if json compatible
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	private static String doHTTPPost(String urlName, JSONObject request, String parameter)
		throws UnsupportedEncodingException, MalformedURLException, IOException {

		// Construct data
	    String data =
	    	parameter == null?
			request.toString():
	    	URLEncoder.encode("request", "UTF-8") + "=" + URLEncoder.encode(request.toString(), "UTF-8");

	    // Send data
	    URL url = new URL(urlName);
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
	    wr.write(data);
	    wr.flush();

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String response = "";
	    String line;
	    while ((line = rd.readLine()) != null) {
	        // Process line..
	    	response+= line + "\n";
	    }
	    wr.close();
	    rd.close();
	    return response;
	}

	/**
	 * Does a http get
	 * @param url
	 * @return response if json compatible
	 * @throws UnsupportedEncodingException
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws JSONException
	 */
	private static String doHTTPGet(String urlName)
		throws UnsupportedEncodingException, MalformedURLException, IOException {

	    // Send data
	    URL url = new URL(urlName);
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(false);

	    // Get the response
	    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    String response = "";
	    String line;
	    while ((line = rd.readLine()) != null) {
	        // Process line..
	    	response+= line + "\n";
	    }
	    rd.close();
	    return response;
	}

	/**
	 * Public constructor
	 * @param definition
	 * @param bbtArgs
	 */
	public BBTRun(BBTDefinition bbtDefinition) {
		this.bbtDefinition = bbtDefinition;
	}

	/**
	 * Runs the test
	 * @param bbtArgs
	 * @throws BBTRunException
	 */
	public int run(Properties bbtArgs, boolean verbose) throws BBTRunException {
		Hashtable<String,String> bbtSetup = bbtDefinition.getSetup();
		Vector<BBTRequestMember> bbtRequest = bbtDefinition.getRequest();
		Vector<BBTResponseMember> bbtResponse = bbtDefinition.getResponse();	
		Hashtable<String, Boolean> bbtConditionsMet = new Hashtable<String,Boolean>();

		//
		if (verbose) {
			System.out.println("Arguments = " + bbtSetup + "\n");
		}

		String url = bbtSetup.get("script");

		boolean useSession = bbtSetup.get("session.use") != null && bbtSetup.get("session.use").trim().equalsIgnoreCase("true");
		if (useSession) {
			Object sessionId = bbtArgs.get("session_id");
			if (sessionId == null) {
				throw new BBTRunException(
					BBTRunException.LINENUMBER_NONE,
					BBTRunException.REQUEST_SESSION_USE_SESSIONIDMISSING
				);
			}
			url+= "?session_id=" +  sessionId;
		}

		//
		if (verbose) {
			System.out.println("Url = " + url + "\n");
		}

		// construct json request object
		JSONObject jsonRequest = new JSONObject();
		JSONObject jsonResponse = new JSONObject();
		if (bbtSetup.get("request.wrapinjson").equalsIgnoreCase("true")) {
			try {
				for(BBTRequestMember bbtMember: bbtRequest) {
					String name = bbtMember.getName();
					String type = bbtMember.getType();
					String value = bbtMember.getValue();
					// TODO: replace args
					Enumeration bbtArgsEnum = bbtArgs.keys();
					while (bbtArgsEnum.hasMoreElements()) {
						String argKey = (String)bbtArgsEnum.nextElement();
						String argValue = (String)bbtArgs.get(argKey);
						String argTarget = "${args." + argKey + "}";
						int argPos = value.indexOf(argTarget);
						if (argPos != -1) {
							value = value.replace(
								argTarget.subSequence(0, argTarget.length()),
								argValue.subSequence(0, argValue.length())
							);
						}
					}
					if (type.equals(BBTMember.TYPE_BOOLEAN)) {
						jsonRequest.put(name, Boolean.valueOf(value));
					} else
					if (type.equals(BBTMember.TYPE_INT)) {
						jsonRequest.put(name, Integer.valueOf(value));
					} else
					if (type.equals(BBTMember.TYPE_FLOAT)) {
						jsonRequest.put(name, Double.valueOf(value));
					} else
					if (type.equals(BBTMember.TYPE_STRING)) {
						jsonRequest.put(name, value);
					} else
					if (type.equals(BBTMember.TYPE_ARRAY_INTEGER)) {
						// constrcut our json array
						JSONArray jsonStringArray = new JSONArray();
						StringTokenizer t = new StringTokenizer(value, ",");
						int i = 0;
						while(t.hasMoreTokens()) {
							jsonStringArray.put(i++, Integer.valueOf(t.nextToken()));
						}
						// add it to the request
						jsonRequest.put(name, jsonStringArray);
					} else {
					if (type.equals(BBTMember.TYPE_ARRAY_STRING)) {
						// constrcut our json array
						JSONArray jsonStringArray = new JSONArray();
						StringTokenizer t = new StringTokenizer(value, ",");
						int i = 0;
						while(t.hasMoreTokens()) {
							jsonStringArray.put(i++, t.nextToken());
						}
						// add it to the request
						jsonRequest.put(name, jsonStringArray);
					} else
						throw new BBTRunException(
							bbtMember.getLineNumber(),
							BBTRunException.REQUEST_TYPE_UNSUPPORTED
						);
					}
				}
	
				//
				if (verbose) {
					System.out.println("Request = " + jsonRequest.toString(4) + "\n");
				}
			} catch (JSONException exception) {
				exception.printStackTrace();
			} catch (NumberFormatException exception) {
				exception.printStackTrace();
			}

			// run test
			String response = null;
			try {
				// post
				if (bbtSetup.get("method").equalsIgnoreCase("POST")) {
					// in post request field
					if (bbtSetup.get("request.wrapinjson.parameter").length() > 0) {
						response = BBTRun.doHTTPPost(url, jsonRequest, bbtSetup.get("request.wrapinjson.parameter"));
					} else {
						response = BBTRun.doHTTPPost(url, jsonRequest, null);
					}
				} else
				// get in query parameter
				if (bbtSetup.get("method").equalsIgnoreCase("GET")) {
					String query = URLEncoder.encode(bbtSetup.get("request.wrapinjson.parameter"), "UTF-8") + "=" + URLEncoder.encode(jsonRequest.toString(), "UTF-8");
					response = BBTRun.doHTTPGet(url + "?" + query);
				} else {
					throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_SETUP_METHOD_INVALID);
				}

				// validate response
				jsonResponse = new JSONObject(response);

				//
				if (verbose) {
					System.out.println("Response = " + jsonResponse.toString(4) + "\n");
				}
			} catch(UnsupportedEncodingException exception) {
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_ENCODE_FAILED);
			} catch(MalformedURLException exception) {
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_URL_INVALID);
			} catch (IOException exception) {
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_IO_FAILED);
			} catch(JSONException exception) {
				//
				if (verbose) {
					System.out.println("Response string = " + response + "\n");
				}

				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_RESPONSE_JSON_INVALID);
			}
		} else {
			String query = "";
			try {
				for(BBTRequestMember bbtMember: bbtRequest) {
					String name = bbtMember.getName();
					String type = bbtMember.getType();
					String value = bbtMember.getValue();
					// TODO: replace args
					Enumeration bbtArgsEnum = bbtArgs.keys();
					while (bbtArgsEnum.hasMoreElements()) {
						String argKey = (String)bbtArgsEnum.nextElement();
						String argValue = (String)bbtArgs.get(argKey);
						String argTarget = "${args." + argKey + "}";
						int argPos = value.indexOf(argTarget);
						if (argPos != -1) {
							value = value.replace(
								argTarget.subSequence(0, argTarget.length()),
								argValue.subSequence(0, argValue.length())
							);
						}
					}
					query+=
						((url + query).indexOf('?') == -1?'?':'&') +
						URLEncoder.encode(name, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
				}
			} catch (UnsupportedEncodingException uee) {
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_ENCODE_FAILED);
			}

			String response = null;
			try {
				if (bbtSetup.get("method").equalsIgnoreCase("GET")) {
					response = BBTRun.doHTTPGet(url + query);
					jsonResponse = new JSONObject(response);
				} else {
					throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_SETUP_METHOD_INVALID);
				}
	
				//
				if (verbose) {
					System.out.println("Request = " + query + "\n");
				}
			} catch (IOException exception) {
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_REQUEST_IO_FAILED);
			} catch(JSONException exception) {
				//
				if (verbose) {
					System.out.println("Response string = " + response + "\n");
				}
	
				throw new BBTRunException(BBTRunException.LINENUMBER_NONE, BBTRunException.HTTP_RESPONSE_JSON_INVALID);
			}
		}

		// validate response by definition
		Stack<Boolean>expressions = new Stack<Boolean>();
		Vector<BBTArray>arrays = new Vector<BBTArray>();
		String name = null;
		String type = null;
		String givenType = null;
		Object value = null;
		try {
			int newIP = 0; 
			while (newIP != IP_NONE) {
				for (int responseMemberIdx = newIP; responseMemberIdx < bbtResponse.size(); responseMemberIdx++) {
					BBTResponseMember bbtMember = bbtResponse.elementAt(responseMemberIdx);
					int depth = bbtMember.getDepth();
					value = null;
					givenType = null;
					name = bbtMember.getName();
					type = bbtMember.getType();
					BBTExpression bbtExpression = bbtMember.getExpression();
					String outputTo = bbtMember.getOutputTo();

					// do we have a deeper depth than expressions
					if (depth > expressions.size()) {
						throw new BBTRunException(
							bbtMember.getLineNumber(),
							BBTRunException.RESPONSE_PROPERTY_DEPTH_INVALID
						);
					} else {
						// case in which we dont dive into expressions
						while (depth < expressions.size()) {
							expressions.pop();
						}
					}

					// test if to take arrays from the stack
					newIP = IP_NONE;
					while(arrays.size() > 0 && name.startsWith(arrays.lastElement().getName()) == false) {
						BBTArray lastArray = arrays.lastElement();
						// do we have more elements in given array, just reran the test
						if (lastArray.hasNextElement()) {
							lastArray.setupNextElement();
							newIP = lastArray.getIP();
							break;
						}
						arrays.remove(lastArray);
					}
	
					if (newIP != IP_NONE) {
						responseMemberIdx = newIP;
						continue;
					}
	
					// test if to skip this property
					if (expressions.contains(Boolean.FALSE)) {
						// process expression 
						if (bbtExpression != null) {
							Boolean bbtConditionMet = bbtConditionsMet.get(name);
							// put on conditions met stack that we did not yet met the condition if not set up
							if (bbtConditionMet == null) {
								bbtConditionsMet.put(name, Boolean.valueOf(true));
							}
							// put failed expression on stack
							expressions.push(new Boolean(Boolean.valueOf(false)));
						}

						if (verbose) {
							System.out.println(
								"[" + bbtMember.getLineNumber() + "] Skipping 'response." +
								name + "' : " +
								type + ", expression not true"
							);
						}
						continue;
					}
	
					// itereate to property
					StringTokenizer t = new StringTokenizer(name, ".");
					Object jsonObject = jsonResponse;
					String fullPropertyName = null;
					boolean arrayEmpty = false;
					while(t.hasMoreTokens()) {
						String subPropertyName = t.nextToken();
						// clip []
						if (subPropertyName.endsWith("[]")) {
							subPropertyName = subPropertyName.substring(0, subPropertyName.length() - 2);
						}
						if (jsonObject instanceof JSONArray) {
							boolean arrayFound = false;
							for(BBTArray element: arrays) {
								if (new String(fullPropertyName).equals(element.getName().replace("[]".subSequence(0,2), "".subSequence(0,0)))) {
									jsonObject = element.getCurrentElement();
									arrayEmpty = element.isEmpty();
									arrayFound = true;
									break;
								}
							}
							if (arrayFound == false) {
								throw new BBTRunException(
									bbtMember.getLineNumber(),
									BBTRunException.RESPONSE_ARRAY_MISSING
								);
							}
						}
						// check if to skip property processing
						if (arrayEmpty) {
							break;
						}
						// check for object
						if (jsonObject instanceof JSONObject) {
							jsonObject = ((JSONObject)jsonObject).opt(subPropertyName);
						} else
						// else validate scalar values
						if (jsonObject instanceof String == false &&
							jsonObject instanceof Integer == false &&
							jsonObject instanceof Float == false &&
							jsonObject instanceof Boolean == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_MISSING
							);
						}
						//
						if (jsonObject == null) {
							// can actually not happen
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_MISSING
							);
						}
						// add sub property to full name 
						fullPropertyName = fullPropertyName == null?subPropertyName:fullPropertyName + '.' + subPropertyName;
					}

					// check if to skip property
					if (arrayEmpty) {
						// test if to skip this property
						if (verbose) {
							System.out.println(
								"[" + bbtMember.getLineNumber() + "] Skipping 'response." +
								name + "' : " +
								type + ", array empty"
							);
						}
						// process expression 
						if (bbtExpression != null) {
							Boolean bbtConditionMet = bbtConditionsMet.get(name);
							// put on conditins met stack that we did not yet met the condition if not set up
							if (bbtConditionMet == null) {
								bbtConditionsMet.put(name, Boolean.valueOf(true));
							}
							// put failed expression on stack
							expressions.push(new Boolean(Boolean.valueOf(false)));
						}
						// get in main processing loop
						continue;
					}

					value = jsonObject;
					givenType = value.getClass().getSimpleName().toLowerCase();
					// map particular given type, TODO: make me more elegant
					if (givenType.equals("jsonarray")) {
						if (type.equals(BBTMember.TYPE_ARRAY_OBJECT)) {
							givenType = "object[]";
						} else
						if (type.equals(BBTMember.TYPE_ARRAY_INTEGER)) {
							givenType = "integer[]";
						} else {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
					} else
					if (givenType.equals("jsonobject")) {
						givenType = "object";
					}

					if (verbose) {
						System.out.println(
							"[" + bbtMember.getLineNumber() + "] Validating 'response." +
							name + "' : " +
							type + ", given type = " +
							givenType + ", value = " +
							(value == null?"null":"'" + value.toString() + "'")
						);
					}
	
					// check for type
					if (type.equals(BBTMember.TYPE_BOOLEAN)) {
						if (value instanceof Boolean == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
					} else
					if (type.equals(BBTMember.TYPE_INT)) {
						if (value instanceof Integer == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
					} else
					if (type.equals(BBTMember.TYPE_FLOAT)) {
						if (value instanceof Float == false &&
								value instanceof Double == false &&
								value instanceof Integer == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						} else {
							// floats with no decimal place are acceptable too
						}
					} else
					if (type.equals(BBTMember.TYPE_STRING)) {
						if (value instanceof String == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
					} else
					if (type.equals(BBTMember.TYPE_OBJECT)) {
						if (value instanceof JSONObject == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),									
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
					} else
					if (type.equals(BBTMember.TYPE_ARRAY_OBJECT) || type.equals(BBTMember.TYPE_ARRAY_INTEGER)) {
						if (value instanceof JSONArray == false) {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_PROPERTY_TYPE_INVALID
							);
						}
						// check if array is on array stack already, means nth run
						if (arrays.size() > 0 && arrays.lastElement().getName().equals(name)) {
							// setup response validation for next element
							arrays.lastElement().setupNextElement();
						} else {
							// put it to our stack of array
							arrays.add(
								new BBTArray(
									name,
									responseMemberIdx,
									(JSONArray)value
								)
							);
						}
					} else {
						throw new BBTRunException(
							bbtMember.getLineNumber(),
							BBTRunException.RESPONSE_TYPE_UNSUPPORTED
						);
					}

					// perform expression test if required
					if (bbtExpression != null) {
						Object javaValue;
						// convert json objects to java compatible objects for js engine
						if (givenType.equals(BBTMember.TYPE_ARRAY_INTEGER)) {
							// do integer[]
							javaValue = new int[((JSONArray)value).length()];
							for(int i = 0; i < ((JSONArray)value).length(); i++) {
								((int[])javaValue)[i] = ((JSONArray)value).optInt(i);
							}
						} else {
							// for now just map the rest to
							javaValue = value;
						}
						boolean bbtExpressionResult = bbtExpression.eval(javaValue);
						Boolean bbtConditionMet = bbtConditionsMet.get(name);
						if (bbtConditionMet == null) {
							bbtConditionsMet.put(name, Boolean.valueOf(bbtExpressionResult));
						} else {
							if (bbtConditionMet.booleanValue() == false) {
								bbtConditionsMet.put(name, Boolean.valueOf(bbtExpressionResult));
							}
						}
						expressions.push(new Boolean(Boolean.valueOf(bbtExpressionResult)));
					}

					// put it to args if desired
					if (outputTo != null) {
						if (outputTo.toLowerCase().startsWith("args.")) {
							bbtArgs.put(outputTo.substring("args.".length()), value.toString());
						} else {
							throw new BBTRunException(
								bbtMember.getLineNumber(),
								BBTRunException.RESPONSE_TYPE_UNSUPPORTED
							);
						}
					}
				}

				newIP = IP_NONE;
				while(arrays.size() > 0) {
					BBTArray lastArray = arrays.lastElement();
					// do we have more elements in given array, just reran the test
					if (lastArray.hasNextElement()) {
						newIP = lastArray.getIP();
						break;
					}
					arrays.remove(lastArray);
				}
			}

			// print out conditions that were never met
			Enumeration<String> bbtConditionsMetEnum = bbtConditionsMet.keys();
			while(bbtConditionsMetEnum.hasMoreElements()) {
				String cmName = bbtConditionsMetEnum.nextElement();
				Boolean cmValue =  bbtConditionsMet.get(cmName);
				if (cmValue.booleanValue() == false) {
					System.out.println("TEST WARNING @ 'response." + cmName + "' : no expression for given property was ever met");
				}
			}
		} catch (BBTRunException exception) {
			//
			if (verbose) {
				System.out.println();
			}
			System.out.println(
				"[" + exception.getLineNumber() +  "] TEST FAILED @ 'response." +
				name + "' : " +
				type + ", given type = " +
				givenType + ", value = " +
				(value == null?"null":"'" + value.toString() + "'") + ", " +
				exception.getMessage());
			return STATUS_FAILED; 
		} catch (BBTExpressionException exception) {
			//
			if (verbose) {
				System.out.println();
			}
			System.out.println(
				"[" + exception.getLineNumber() + "]TEST FAILED @ 'response." +
				name + "' : " +
				type + ", given type = " +
				givenType +", value = " +
				(value == null?"null":"'" + value.toString() + "'") + ", " +
				exception.getMessage());
			return STATUS_FAILED; 
		}

		//
		if (verbose) {
			System.out.println();
		}

		//
		return STATUS_SUCCESS;
	}

}