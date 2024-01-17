package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DataHandlerScriptTest {

	DataHandlerScript script;

	@Test
	public void testExtractData() throws Exception {
		script = new DataHandlerScript();
		String json = """
				{
				  "record": {
				    "data": {
				      "children": [
				        {
				          "name": "fitnesseTextVar",
				          "value": "hejsan"
				        }
				      ],
				      "name": "fitnesseExample"
				    },
				    "actionLinks": {
				      "read": {
				        "requestMethod": "GET",
				        "rel": "read",
				        "url": "http://localhost:8080/systemone/rest/record/fitnesseRecordType/fitnesseRecordType:18696185347863",
				        "accept": "application/vnd.uub.record+json"
				      }
				    }
				  }
				}
				""";

		String extractDataElement = script.extractDataElement(json);

		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonObject parseStringAsObject = jsonParser.parseStringAsObject(extractDataElement);
		assertFalse(parseStringAsObject.containsKey("record"));
		assertFalse(parseStringAsObject.containsKey("data"));
		assertTrue(parseStringAsObject.containsKey("children"));
		assertTrue(parseStringAsObject.containsKey("name"));
	}

}
