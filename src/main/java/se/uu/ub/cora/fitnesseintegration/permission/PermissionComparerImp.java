/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.permission;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class PermissionComparerImp implements PermissionComparer {

	private ClientDataRecord dataRecord;

	public PermissionComparerImp(ClientDataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}

	@Override
	public List<String> checkDataRecordContainsPermissions(JsonValue jsonValue) {
		JsonObject permissions = (JsonObject) jsonValue;
		List<String> errorMessages = new ArrayList<>();
		addMessagesIfMissingReadPermissions(errorMessages, permissions);
		addMessagesIfMissingWritePermissions(errorMessages, permissions);
		return errorMessages;
	}

	private void addMessagesIfMissingReadPermissions(List<String> errorMessages,
			JsonObject permissions) {
		if (permissions.containsKey("read")) {
			checkReadPermissions(errorMessages, permissions);
		}
	}

	private void checkReadPermissions(List<String> errorMessages, JsonObject permissions) {
		JsonArray readPermissions = permissions.getValueAsJsonArray("read");

		for (JsonValue readPermission : readPermissions) {
			addMessageIfReadPermissionIsMissing(errorMessages, readPermission);
		}
	}

	private void addMessageIfReadPermissionIsMissing(List<String> errorReadMessages,
			JsonValue readPermission) {
		String permission = ((JsonString) readPermission).getStringValue();
		if (readPermissionIsMissing(permission)) {
			errorReadMessages.add("Read permission " + permission + " is missing.");
		}
	}

	private boolean readPermissionIsMissing(String permission) {
		return !dataRecord.getReadPermissions().contains(permission);
	}

	private void addMessagesIfMissingWritePermissions(List<String> errorMessages,
			JsonObject permissions) {
		if (permissions.containsKey("write")) {
			checkWritePermissions(errorMessages, permissions);
		}
	}

	private List<String> checkWritePermissions(List<String> errorMessages, JsonObject permissions) {
		JsonArray writePermissions = permissions.getValueAsJsonArray("write");

		for (JsonValue writePermission : writePermissions) {
			addMessageIfWritePermissionIsMissing(errorMessages, writePermission);
		}
		return errorMessages;
	}

	private void addMessageIfWritePermissionIsMissing(List<String> errorWriteMessages,
			JsonValue writePermission) {
		String permission = ((JsonString) writePermission).getStringValue();
		if (writePermissionIsMissing(permission)) {
			errorWriteMessages.add("Write permission " + permission + " is missing.");
		}
	}

	private boolean writePermissionIsMissing(String permission) {
		return !dataRecord.getWritePermissions().contains(permission);
	}

}
