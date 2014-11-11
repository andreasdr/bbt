package net.drewke.bbt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import net.drewke.tools.WildcardFileFilter;

/**
 * Main class for running automated black box tests
 * @author andreas.drewke
 * @version $Id: BBT.java 4761 2012-07-19 16:16:46Z andreasdrewke $
 */
public class BBT {

	private final static String MSG_INFO = "drewke.net black box test engine 0.2\nProgrammed 2012,2014 by A. Drewke, Copyright SlipShift GmbH, drewke.net\n";
	private final static String MSG_USAGE = "Usage: bbt args.properties bbtfileorwildcard1.bbt [bbtfile2.bbt] [-verbose]\n";

	/**
	 * Main method
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(MSG_INFO);

		boolean verbose = false;

		// check for arguments
		if (args.length < 2) {
			System.out.println(MSG_USAGE);
			System.exit(-1);
		}

		// get args
		String argsFilename = args[0];

		// retrieve files to test
		Vector<File> bbtFiles = new Vector<File>();
		for(int i = 1; i < args.length; i++) {
			String bbtArgument = args[i];

			// check for verbose switch
			if (bbtArgument.equals("-verbose")) {
				verbose = true;
				continue;
			}

			String bbtFolder = System.getProperty("user.dir");
			// separate folder and wild card from argument's input 
			if (bbtArgument.indexOf(File.separator) >= 0) {
				bbtFolder = bbtArgument.substring(0, bbtArgument.lastIndexOf(File.separator));
				bbtArgument = bbtArgument.substring(bbtArgument.lastIndexOf(File.separator) + 1, bbtArgument.length());
			}

			File[] _bbtFiles = new File(bbtFolder).listFiles(new WildcardFileFilter(bbtArgument));
			if (_bbtFiles == null) {
				System.out.println("Could not retrieve folder list by wildcard.");
				System.exit(-1);
			}

			// add files to vector
			for(File file: _bbtFiles) {
				bbtFiles.add(file);
			}
		}

		// load args
		Properties bbtArgs = new Properties();
		try {
			bbtArgs.load(new FileInputStream(argsFilename));
		} catch(IOException ioException) {
			System.out.println("Error: Couldnt load args properties '" + argsFilename + "'. Exiting.");
			System.exit(-1);
		}

		int status = BBTRun.STATUS_FAILED;

		//
		for(File bbtFile: bbtFiles) {
			// parse BBT definition
			BBTDefinition bbtDef = null;
			String bbtFilename = bbtFile.getAbsolutePath();
			try {
				bbtDef = BBTDefinition.loadFromFile(
					bbtFilename
				);
			} catch(Exception exception) {
				String lineNr = "none";
				if ((exception instanceof BBTDefinitionException)) {
					lineNr = Integer.valueOf(((BBTDefinitionException)exception).getLineNumber()).toString();
				}
				System.out.println("Error: Couldnt parse definition '" + bbtFilename + " at line number " + lineNr + ", message = " + exception.getMessage() + ". Exiting.");
				System.exit(-1);
			}

			status = BBTRun.STATUS_FAILED;
			try {
				BBTRun bbtRun = new BBTRun(bbtDef);
				status = bbtRun.run(bbtArgs, verbose);
				switch(status) {
					case(BBTRun.STATUS_SUCCESS):
						System.out.println(bbtFilename + " : OK");
						break;
					default:
						System.out.println(bbtFilename + " : FAILED");
						break;
				}
			} catch (BBTRunException exception) {
				System.out.println(bbtFilename + " : FAILED[" + exception.getLineNumber() + "] with message = '" + exception.getMessage() + "'");
				System.exit(-1);
			}
		}

		try {
			bbtArgs.store(new FileOutputStream(argsFilename), null);
		} catch(IOException ioException) {
			System.out.println("Error: Couldnt store args properties '" + argsFilename + "'. Exiting.");
			System.exit(-1);
		}

		//
		System.exit(status);
	}

}