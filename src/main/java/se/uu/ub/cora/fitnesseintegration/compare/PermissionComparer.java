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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class PermissionComparer implements DataComparer {

	private DataRecord dataRecord;

	public PermissionComparer(DataRecord dataRecord) {
		this.dataRecord = dataRecord;
	}

	@Override
	public List<String> checkDataRecordContains(JsonValue jsonValue) {
		JsonObject permissions = (JsonObject) jsonValue;
		List<String> missingReadPermissions = new ArrayList<>();
		List<String> missingWritePermissions = new ArrayList<>();
		addMessagesIfMissingReadPermissions(missingReadPermissions, permissions);
		addMessagesIfMissingWritePermissions(missingWritePermissions, permissions);
		missingReadPermissions.addAll(missingWritePermissions);

		return missingReadPermissions;

	}

	private void addMessagesIfMissingReadPermissions(List<String> missingPermissions,
			JsonObject permissions) {
		if (permissions.containsKey("read")) {
			checkReadPermissions(missingPermissions, permissions);
		}
	}

	private void checkReadPermissions(List<String> missingPermissions, JsonObject permissions) {
		JsonArray readPermissions = permissions.getValueAsJsonArray("read");

		for (JsonValue readPermission : readPermissions) {
			addMessageIfReadPermissionIsMissing(missingPermissions, readPermission);
		}
	}

	private void addMessageIfReadPermissionIsMissing(List<String> missingPermissions,
			JsonValue readPermission) {
		String permission = ((JsonString) readPermission).getStringValue();
		if (readPermissionIsMissing(permission)) {
			addHeaderForMissingPermissions(missingPermissions, "Missing read permissions:");
			missingPermissions.add(permission);
		}
	}

	private void addHeaderForMissingPermissions(List<String> missingPermissions, String header) {
		if (missingPermissions.isEmpty()) {
			missingPermissions.add(header);
		}
	}

	private boolean readPermissionIsMissing(String permission) {
		return !getDataRecord().getReadPermissions().contains(permission);
	}

	private void addMessagesIfMissingWritePermissions(List<String> missingPermissions,
			JsonObject permissions) {
		if (permissions.containsKey("write")) {
			checkWritePermissions(missingPermissions, permissions);
		}
	}

	private List<String> checkWritePermissions(List<String> missingPermissions,
			JsonObject permissions) {
		JsonArray writePermissions = permissions.getValueAsJsonArray("write");

		for (JsonValue writePermission : writePermissions) {
			addMessageIfWritePermissionIsMissing(missingPermissions, writePermission);
		}
		return missingPermissions;
	}

	private void addMessageIfWritePermissionIsMissing(List<String> missingPermissions,
			JsonValue writePermission) {
		String permission = ((JsonString) writePermission).getStringValue();
		if (writePermissionIsMissing(permission)) {
			addHeaderForMissingPermissions(missingPermissions, "Missing write permissions:");
			missingPermissions.add(permission);
		}
	}

	private boolean writePermissionIsMissing(String permission) {
		return !getDataRecord().getWritePermissions().contains(permission);
	}

	public DataRecord getDataRecord() {
		// needed for test
		return dataRecord;
	}

}
