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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ExtractSubstringUsingRegexTest {

	private ExtractSubstringUsingRegex extractScript;
	private String regex = "\"tsVisibility\",\"value\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z\"";

	@BeforeTest
	public void testInit() {
		extractScript = new ExtractSubstringUsingRegex();
	}

	@Test
	public void testSubstringExtraction() throws Exception {
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(), regex);
		assertEquals(regexExtract, "\"tsVisibility\",\"value\":\"2024-02-08T09:53:43.016913Z\"");
	}

	private String getChunkOfText() {
		return """
				{"children":[{"children":[{"name":"visibility","value":"published"},{"name":"tsVisibility","value":"2024-02-08T09:53:43.016913Z"}],"name":"adminInfo"}],"name":"binary"}
				""";
	}

	@Test
	public void testSubstringExtractionNotFound() throws Exception {
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(),
				"someRegExThatWontBeAMatch");
		assertEquals(regexExtract, "No match found");
	}

	@Test
	public void testRegExMatch() throws Exception {
		boolean regexMatch = extractScript.matchFoundUsingTextAndRegex(getChunkOfText(), regex);
		assertTrue(regexMatch);
	}

	@Test
	public void testRegExDoesntMatch() throws Exception {
		boolean regexMatch = extractScript.matchFoundUsingTextAndRegex(getChunkOfText(),
				"someRegExThatWontBeAMatch");
		assertFalse(regexMatch);
	}

	@Test
	public void testMatchFoundWithBothPositivesAndNegatives() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"visibility\",\"value\":\"published\" AND \"name\":\"adminInfo\"} AND NOT shouldNotBeFound";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithMixedOrderOfPositivesAndNegatives() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"visibility\",\"value\":\"published\" AND NOT shouldNotBeFound AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithOnlyPositives() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"visibility\",\"value\":\"published\" AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithMissingExpectedPositives() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"someMissingName\",\"value\":\"published\" AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertFalse(matchFound);
	}

	@Test
	public void testMatchFoundWithPresentNegative() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"someMissingName\",\"value\":\"published\" AND NOT \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertFalse(matchFound);
	}

	@Test
	public void testMatchFoundWithIncludedRegex() {
		String body = getChunkOfText();
		String matchList = "\"name\":\"visibility\",\"value\":\"published\" AND \"tsVisibility\",\"value\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z\"";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithOnlyNegatives() {
		String body = getChunkOfText();
		String matchList = "NOT shouldNotBeFound AND NOT thisEither";

		boolean matchFound = extractScript.matchFoundUsingTextAndIncludesAndNotExcludes(body,
				matchList);
		assertTrue(matchFound);
	}
}
