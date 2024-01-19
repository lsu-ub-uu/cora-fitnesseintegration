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
		String fullJson = getTestJson();

		String extractDataElement = assertActionLinksRemoved(fullJson);
		assertCorrectJsonAndTopLevelsRemoved(extractDataElement);
	}

	private void assertCorrectJsonAndTopLevelsRemoved(String extractDataElement) {
		OrgJsonParser jsonParser = new OrgJsonParser();
		JsonObject parseStringAsObject = jsonParser.parseStringAsObject(extractDataElement);
		assertFalse(parseStringAsObject.containsKey("record"));
		assertFalse(parseStringAsObject.containsKey("data"));
		assertTrue(parseStringAsObject.containsKey("children"));
		assertTrue(parseStringAsObject.containsKey("name"));
	}

	private String assertActionLinksRemoved(String fullJson) {
		String extractDataElement = script.extractDataElement(fullJson);
		assertFalse(extractDataElement.contains("actionLinks"));
		return extractDataElement;
	}

	private String getTestJson() {
		return """
				{
				  "record": {
				    "data": {
				      "children": [
				        {
				          "children": [
				            {
				              "children": [
				                {
				                  "name": "linkedRecordType",
				                  "value": "system"
				                },
				                {
				                  "name": "linkedRecordId",
				                  "value": "systemOne"
				                }
				              ],
				              "actionLinks": {
				                "read": {
				                  "requestMethod": "GET",
				                  "rel": "read",
				                  "url": "https://cora.epc.ub.uu.se/systemone/rest/record/system/systemOne",
				                  "accept": "application/vnd.uub.record+json"
				                }
				              },
				              "name": "dataDivider"
				            },
				            {
				              "children": [
				                {
				                  "name": "linkedRecordType",
				                  "value": "user"
				                },
				                {
				                  "name": "linkedRecordId",
				                  "value": "141414"
				                }
				              ],
				              "actionLinks": {
				                "read": {
				                  "requestMethod": "GET",
				                  "rel": "read",
				                  "url": "https://cora.epc.ub.uu.se/systemone/rest/record/user/141414",
				                  "accept": "application/vnd.uub.record+json"
				                }
				              }
				            }
				          ],
				          "name": "recordInfo"
				        },
				        {
				          "children": [
				            {
				              "name": "resourceId",
				              "value": "binary:3622353869664222-master"
				            },
				            {
				              "actionLinks": {
				                "read": {
				                  "requestMethod": "GET",
				                  "rel": "read",
				                  "url": "https://cora.epc.ub.uu.se/systemone/rest/record/binary/binary:3622353869664222/master",
				                  "accept": "image/jpeg"
				                }
				              },
				              "name": "master",
				              "mimeType": "image/jpeg"
				            }
				          ],
				          "name": "master"
				        },
				        {
				          "children": [
				            {
				              "name": "resourceId",
				              "value": "binary:3622353869664222-jp2"
				            },
				            {
				              "actionLinks": {
				                "read": {
				                  "requestMethod": "GET",
				                  "rel": "read",
				                  "url": "https://cora.epc.ub.uu.se/systemone/rest/record/binary/binary:3622353869664222/jp2",
				                  "accept": "image/jp2"
				                }
				              },
				              "name": "jp2",
				              "mimeType": "image/jp2"
				            }
				          ],
				          "name": "jp2"
				        }
				      ],
				      "name": "binary",
				      "attributes": {
				        "type": "image"
				      }
				    },
				    "actionLinks": {
				      "read": {
				        "requestMethod": "GET",
				        "rel": "read",
				        "url": "https://cora.epc.ub.uu.se/systemone/rest/record/binary/binary:3622353869664222",
				        "accept": "application/vnd.uub.record+json"
				      }
				    }
				  }
				}
				""";
	}

}
