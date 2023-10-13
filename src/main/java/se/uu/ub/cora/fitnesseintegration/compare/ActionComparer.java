package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.clientdata.ClientAction;
import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ActionComparer implements DataComparer {

	private ClientDataRecord dataRecord;

	public ActionComparer(ClientDataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}

	@Override
	public List<String> checkClientDataRecordContains(JsonValue jsonValue) {
		JsonObject actionAsJsonObject = (JsonObject) jsonValue;
		List<String> errorMessages = new ArrayList<>();
		addErrorMessageIfActionIsMissing(actionAsJsonObject, errorMessages);
		return errorMessages;
	}

	private void addErrorMessageIfActionIsMissing(JsonObject actionAsJsonObject,
			List<String> errorMessages) {
		JsonArray actions = actionAsJsonObject.getValueAsJsonArray("actions");

		for (JsonValue action : actions) {
			String actionString = ((JsonString) action).getStringValue();
			Optional<ClientActionLink> actionLink = dataRecord
					.getActionLink(ClientAction.valueOf(actionString));
			if (actionLink.isEmpty()) {
				errorMessages.add("Action " + actionString + " is missing.");
			}
		}
	}

	public ClientDataRecord getClientDataRecord() {
		return dataRecord;
	}

}
