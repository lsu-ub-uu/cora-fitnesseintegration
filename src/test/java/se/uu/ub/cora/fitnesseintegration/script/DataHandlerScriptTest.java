package se.uu.ub.cora.fitnesseintegration.script;

import org.testng.annotations.Test;

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
		// String extractDataElement = script.extractDataElement(json);
		// JSONObject jsonObject = new JSONObject(extractDataElement);
		// assertEquals(jsonObject.names().get(0), "children");
		// assertEquals(jsonObject.names().get(1), "children");

	}

}
