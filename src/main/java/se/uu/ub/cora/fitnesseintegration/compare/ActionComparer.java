package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ActionComparer implements DataComparer {

	private DataRecord dataRecord;

	public ActionComparer(DataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}

	@Override
	public List<String> checkDataRecordContains(JsonValue jsonValue) {
		JsonObject actionAsJsonObject = (JsonObject) jsonValue;
		List<String> errorMessages = new ArrayList<>();
		addErrorMessageIfActionIsMissing(actionAsJsonObject, errorMessages);
		return errorMessages;
	}

	private void addErrorMessageIfActionIsMissing(JsonObject actionAsJsonObject,
			List<String> errorMessages) {
		JsonArray actions = actionAsJsonObject.getValueAsJsonArray("actions");

		Map<String, ActionLink> actionLinks = getDataRecord().getActionLinks();
		for (JsonValue action : actions) {
			String actionString = ((JsonString) action).getStringValue();
			if (!actionLinks.containsKey(actionString)) {
				errorMessages.add("Action " + actionString + " is missing.");
			}
		}
	}

	public DataRecord getDataRecord() {
		return dataRecord;
	}

}
