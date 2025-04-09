/*
 * Copyright 2024 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
package se.uu.ub.cora.fitnesseintegration.script;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractSubstringUsingRegex {

	public String getSubstringUsingTextAndRegex(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.find() ? matcher.group() : "No match found";
	}

	public boolean matchFoundUsingTextAndRegex(String text, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		return matcher.find();
	}

	/**
	 * Separate the includes and excludes with AND and AND NOT, if starting input with negative,
	 * start with NOT (ex; "NOT regextext AND regex2 AND NOT regex3")
	 */
	public boolean matchFoundUsingTextAndIncludesAndNotExcludes(String body,
			String regexListAsString) {
		StringBuilder includes = new StringBuilder();
		StringBuilder excludes = new StringBuilder();

		var patterns = regexListAsString.trim().split(" AND ");
		for (String pattern : patterns) {
			String matchPattern = extractMatchPattern(pattern);
			String actualMatchPattern = isValidRegex(matchPattern) ? matchPattern
					: Pattern.quote(matchPattern);

			if (isNotExpected(pattern)) {
				excludes.append("(?!.*").append(actualMatchPattern).append(")");
			} else {
				includes.append("(?=.*").append(actualMatchPattern).append(")");
			}
		}

		Pattern compiledRegEx = Pattern.compile(includes.toString() + excludes.toString() + ".*");
		return compiledRegEx.matcher(body).find();
	}

	private String extractMatchPattern(String pattern) {
		String trimmedPattern = pattern.trim();
		return trimmedPattern.startsWith("NOT ") ? trimmedPattern.substring(4).trim()
				: trimmedPattern;
	}

	private boolean isNotExpected(String pattern) {
		return pattern.trim().startsWith("NOT ");
	}

	private boolean isValidRegex(String pattern) {
		try {
			Pattern.compile(pattern);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
