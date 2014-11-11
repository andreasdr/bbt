package net.drewke.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.regex.Pattern;

/**
 * Wildcard file filter, see http://code.hammerpig.com/search-for-files-in-directory-using-wildcards-in-java.html
 * @author andreas.drewke
 * @version $Id: WildcardFileFilter.java 4743 2012-07-13 12:41:53Z andreasdrewke $
 */
public class WildcardFileFilter implements FileFilter {

	private Pattern pattern;

	public WildcardFileFilter(String patternString) {
		String patternEscaped = "";
		for(int i = 0; i < patternString.length(); i++) {
			char c = patternString.charAt(i);
			if (c == '*' || c == '?') {
				patternEscaped+= c;
			} else
			if (c == '\\'){
				patternEscaped+= "\\" + c;
			} else {
				patternEscaped+= c;
			}
		}
		pattern = Pattern.compile(patternEscaped.replace("*", ".*").replace("?", "."));
	}

	public boolean accept(File file) {
		return pattern.matcher(file.getName()).matches();
	}

}