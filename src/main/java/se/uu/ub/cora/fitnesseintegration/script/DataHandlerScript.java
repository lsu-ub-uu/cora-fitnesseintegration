package se.uu.ub.cora.fitnesseintegration.script;

import java.util.Iterator;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DataHandlerScript {

	private static final String CHILDREN = "children";

	public String extractDataElement(String fullJson) {
		OrgJsonParser jsonParser = new OrgJsonParser();

		JsonObject fullJsonObject = jsonParser.parseStringAsObject(fullJson);
		JsonObject processedJsonObject = removeTopLevels(fullJsonObject);
		removeAllActionLinksFromData(processedJsonObject);

		return processedJsonObject.toJsonFormattedString();
	}

	private JsonObject removeTopLevels(JsonObject fullJsonObject) {
		return fullJsonObject.getValueAsJsonObject("record").getValueAsJsonObject("data");
	}

	private void removeAllActionLinksFromData(JsonObject data) {
		data.removeKey("actionLinks");
		if (data.containsKey(CHILDREN)) {
			traverseAndProcessChildren(data);
		}
	}

	private void traverseAndProcessChildren(JsonObject data) {
		JsonArray valueAsJsonArray = data.getValueAsJsonArray(CHILDREN);
		Iterator<JsonValue> jsonArrayIterator = valueAsJsonArray.iterator();
		while (jsonArrayIterator.hasNext()) {
			traverseAnyObjects(jsonArrayIterator);
		}
	}

	private void traverseAnyObjects(Iterator<JsonValue> jsonArrayIterator) {
		JsonValue nextJsonValue = jsonArrayIterator.next();
		removeAllActionLinksFromData((JsonObject) nextJsonValue);
	}
}
