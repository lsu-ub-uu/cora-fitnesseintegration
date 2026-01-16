/*
 * Copyright 2024, 2026 Uppsala University Library
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

	private static final String NO_MATCH_FOUND = "No match found";
	private static final String REG_EX_THAT_WONT_BE_A_MATCH = "someRegExThatWontBeAMatch";
	private ExtractSubstringUsingRegex extractScript;
	private String regex = "\"tsVisibility\",\"value\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z\"";
	private String regexWithGroups = "\"(tsVisibility)\",\"value\":\"(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z)\"";

	@BeforeTest
	public void testInit() {
		extractScript = new ExtractSubstringUsingRegex();
	}

	@Test
	public void testSubstringExtraction() {
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(), regex);
		assertEquals(regexExtract, "\"tsVisibility\",\"value\":\"2024-02-08T09:53:43.016913Z\"");
	}

	private String getChunkOfText() {
		return """
				{"children":[{"children":[{"name":"visibility","value":"published"},{"name":"tsVisibility","value":"2024-02-08T09:53:43.016913Z"}],"name":"adminInfo"}],"name":"binary"}
				""";
	}

	@Test
	public void testSubstringExtractionGroup0NoMatch() {
		int groupNo = 0;
		String regexExtract = extractScript.getSubstringUsingTextAndRegexAndGroup(getChunkOfText(),
				REG_EX_THAT_WONT_BE_A_MATCH, groupNo);
		assertEquals(regexExtract, NO_MATCH_FOUND);
	}

	@Test
	public void testSubstringExtractionGroup0() {
		int groupNo = 0;
		String regexExtract = extractScript.getSubstringUsingTextAndRegexAndGroup(getChunkOfText(),
				regexWithGroups, groupNo);
		assertEquals(regexExtract, "\"tsVisibility\",\"value\":\"2024-02-08T09:53:43.016913Z\"");
	}

	@Test
	public void testSubstringExtractionGroup1() {
		int groupNo = 1;
		String regexExtract = extractScript.getSubstringUsingTextAndRegexAndGroup(getChunkOfText(),
				regexWithGroups, groupNo);
		assertEquals(regexExtract, "tsVisibility");
	}

	@Test
	public void testSubstringExtractionGroup2() {
		int groupNo = 2;
		String regexExtract = extractScript.getSubstringUsingTextAndRegexAndGroup(getChunkOfText(),
				regexWithGroups, groupNo);
		assertEquals(regexExtract, "2024-02-08T09:53:43.016913Z");
	}

	@Test
	public void testSubstringExtractionNotFound() {
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(),
				REG_EX_THAT_WONT_BE_A_MATCH);
		assertEquals(regexExtract, NO_MATCH_FOUND);
	}

	@Test
	public void testRegExMatch() {
		boolean regexMatch = extractScript.matchFoundUsingTextAndRegex(getChunkOfText(), regex);
		assertTrue(regexMatch);
	}

	@Test
	public void testRegExDoesntMatch() {
		boolean regexMatch = extractScript.matchFoundUsingTextAndRegex(getChunkOfText(),
				REG_EX_THAT_WONT_BE_A_MATCH);
		assertFalse(regexMatch);
	}

	@Test
	public void testMatchFoundWithBothPositivesAndNegatives() {
		String patternString = "\"name\":\"visibility\",\"value\":\"published\" AND \"name\":\"adminInfo\"} AND NOT shouldNotBeFound";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithMixedOrderOfPositivesAndNegatives() {
		String patternString = "\"name\":\"visibility\",\"value\":\"published\" AND NOT shouldNotBeFound AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithOnlyPositives() {
		String patternString = "\"name\":\"visibility\",\"value\":\"published\" AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithMissingExpectedPositives() {
		String patternString = "\"name\":\"someMissingName\",\"value\":\"published\" AND \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertFalse(matchFound);
	}

	@Test
	public void testMatchFoundWithPresentNegative() {
		String patternString = "\"name\":\"someMissingName\",\"value\":\"published\" AND NOT \"name\":\"adminInfo\"}";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertFalse(matchFound);
	}

	@Test
	public void testMatchFoundWithIncludedRegex() {
		String patternString = "\"name\":\"visibility\",\"value\":\"published\" AND \"tsVisibility\",\"value\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z\"";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertTrue(matchFound);
	}

	@Test
	public void testMatchFoundWithOnlyNegatives() {
		String patternString = "NOT shouldNotBeFound AND NOT thisEither";

		boolean matchFound = extractScript
				.matchFoundUsingTextAndIncludesAndNotExcludes(getChunkOfText(), patternString);
		assertTrue(matchFound);
	}
}
