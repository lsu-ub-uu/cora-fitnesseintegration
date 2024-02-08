package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

public class ExtractSubstringUsingRegexTest {

	private ExtractSubstringUsingRegex extractScript;
	private String regex = "\"tsVisibility\",\"value\":\"\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{6}Z\"";

	@Test
	public void testSubstringExtraction() throws Exception {
		extractScript = new ExtractSubstringUsingRegex();
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(), regex);
		assertTrue(regexExtract.contains("tsVisibility"));

	}

	private String getChunkOfText() {
		return """
				{"record":{"data":{"children":[{"children":[{"children":[{"name":"linkedRecordType","value":"recordType"},{"name":"linkedRecordId","value":"binary"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/recordType/binary","accept":"application/vnd.uub.record+json"}},"name":"type"},{"children":[{"name":"linkedRecordType","value":"validationType"},{"name":"linkedRecordId","value":"genericBinary"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/validationType/genericBinary","accept":"application/vnd.uub.record+json"}},"name":"validationType"},{"children":[{"name":"linkedRecordType","value":"system"},{"name":"linkedRecordId","value":"cora"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/system/cora","accept":"application/vnd.uub.record+json"}},"name":"dataDivider"},{"name":"id","value":"binary:3735508612180"},{"repeatId":"0","children":[{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/user/131313","accept":"application/vnd.uub.record+json"}},"name":"updatedBy"},{"name":"tsUpdated","value":"2024-02-08T09:53:42.223824Z"}],"name":"updated"},{"repeatId":"1","children":[{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/user/131313","accept":"application/vnd.uub.record+json"}},"name":"updatedBy"},{"name":"tsUpdated","value":"2024-02-08T09:53:42.976167Z"}],"name":"updated"},{"children":[{"name":"linkedRecordType","value":"user"},{"name":"linkedRecordId","value":"131313"}],"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/user/131313","accept":"application/vnd.uub.record+json"}},"name":"createdBy"},{"name":"tsCreated","value":"2024-02-08T09:53:42.223824Z"}],"name":"recordInfo"},{"children":[{"name":"visibility","value":"published"},{"name":"tsVisibility","value":"2024-02-08T09:53:43.016913Z"}],"name":"adminInfo"}],"name":"binary","attributes":{"type":"generic"}},"permissions":{"read":["originalFileName","resourceId","thumbnail","large","checksum","checksumType","medium","resourceInfo","jp2","master"],"write":["originalFileName","resourceId","thumbnail","large","checksum","checksumType","medium","resourceInfo","jp2","master"]},"actionLinks":{"read":{"requestMethod":"GET","rel":"read","url":"http://localhost:8080/systemone/rest/record/binary/binary:3735508612180","accept":"application/vnd.uub.record+json"},"upload":{"requestMethod":"POST","rel":"upload","contentType":"multipart/form-data","url":"http://localhost:8080/systemone/rest/record/binary/binary:3735508612180/master"},"update":{"requestMethod":"POST","rel":"update","contentType":"application/vnd.uub.record+json","url":"http://localhost:8080/systemone/rest/record/binary/binary:3735508612180","accept":"application/vnd.uub.record+json"},"index":{"requestMethod":"POST","rel":"index","body":{"children":[{"children":[{"name":"linkedRecordType","value":"recordType"},{"name":"linkedRecordId","value":"binary"}],"name":"recordType"},{"name":"recordId","value":"binary:3735508612180"},{"name":"type","value":"index"}],"name":"workOrder"},"contentType":"application/vnd.uub.record+json","url":"http://localhost:8080/systemone/rest/record/workOrder/","accept":"application/vnd.uub.record+json"},"delete":{"requestMethod":"DELETE","rel":"delete","url":"http://localhost:8080/systemone/rest/record/binary/binary:3735508612180"}}}}
				""";
	}

	@Test
	public void testSubstringExtractionNotFound() throws Exception {
		extractScript = new ExtractSubstringUsingRegex();
		String regexExtract = extractScript.getSubstringUsingTextAndRegex(getChunkOfText(),
				"hejsan");
		assertEquals(regexExtract, "No match found");

	}

}
