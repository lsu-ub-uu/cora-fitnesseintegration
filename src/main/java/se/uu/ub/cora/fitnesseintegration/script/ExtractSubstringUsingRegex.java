package se.uu.ub.cora.fitnesseintegration.script;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractSubstringUsingRegex {

	public String getSubstringUsingTextAndRegex(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "No match found";
	}
}
