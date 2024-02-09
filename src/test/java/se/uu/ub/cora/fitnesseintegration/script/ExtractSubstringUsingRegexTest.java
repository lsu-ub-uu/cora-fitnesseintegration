package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;

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
}
