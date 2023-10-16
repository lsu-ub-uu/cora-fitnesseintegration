/*
 * Copyright 2020, 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */
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
					.getActionLink(ClientAction.valueOf(actionString.toUpperCase()));
			if (actionLink.isEmpty()) {
				errorMessages.add("Action " + actionString + " is missing.");
			}
		}
	}

	public ClientDataRecord getClientDataRecord() {
		return dataRecord;
	}

}
