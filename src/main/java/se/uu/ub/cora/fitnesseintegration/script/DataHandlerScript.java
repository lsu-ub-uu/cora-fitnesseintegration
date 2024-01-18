package se.uu.ub.cora.fitnesseintegration.script;

import java.util.Iterator;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.JsonValueType;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DataHandlerScript {

	public String extractDataElement(String json) {
		OrgJsonParser jsonParser = new OrgJsonParser();

		JsonObject parseStringAsObject = jsonParser.parseStringAsObject(json);

		JsonObject valueAsJsonObject = parseStringAsObject.getValueAsJsonObject("record")
				.getValueAsJsonObject("data");

		removeAllActionLinksFromJson(valueAsJsonObject);

		return valueAsJsonObject.toJsonFormattedString();
	}

	private void removeAllActionLinksFromJson(JsonObject valueAsJsonObject) {
		valueAsJsonObject.removeKey("actionLinks");
		traverseAnyChildren(valueAsJsonObject);
	}

	private void traverseAnyChildren(JsonObject valueAsJsonObject) {
		if (valueAsJsonObject.containsKey("children")) {
			JsonArray valueAsJsonArray = valueAsJsonObject.getValueAsJsonArray("children");
			Iterator<JsonValue> iterator = valueAsJsonArray.iterator();
			while (iterator.hasNext()) {
				JsonValue next = iterator.next();
				if (next.getValueType() == JsonValueType.OBJECT) {
					removeAllActionLinksFromJson((JsonObject) next);
				}
			}
		}
	}
}
