package se.uu.ub.cora.fitnesseintegration.script;

import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DataHandlerScript {

	public String extractDataElement(String json) {

		OrgJsonParser jsonParser = new OrgJsonParser();

		JsonObject parseStringAsObject = jsonParser.parseStringAsObject(json);

		return parseStringAsObject.getValueAsJsonObject("record").getValueAsJsonObject("data")
				.toJsonFormattedString();

	}

}
