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

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;

public class PermissionComparerFixture extends ComparerFixture {

	private ComparerFactory comparerFactory;
	private String permissions;
	private JsonHandler jsonHandler;

	public PermissionComparerFixture() {
		super();
		comparerFactory = DependencyProvider.getComparerFactory();
		jsonHandler = DependencyProvider.getJsonHandler();
	}

	public String testCheckPermissions() {
		ClientDataRecord dataRecord = DataHolder.getRecord();
		DataComparer comparer = comparerFactory.factor("permission", dataRecord);

		JsonObject permissionObject = jsonHandler.parseStringAsObject(permissions);
		List<String> errorMessages = comparer.checkClientDataRecordContains(permissionObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public void setPermissions(String permissions) {
		this.permissions = permissions;

	}

	ComparerFactory getComparerFactory() {
		return comparerFactory;
	}

	public String testReadFromListCheckPermissions() {
		try {
			ClientDataRecord dataRecord = DataHolder.getRecordList().get(indexToCompareTo);
			return comparePermissionUsingClientDataRecord(dataRecord);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private String comparePermissionUsingClientDataRecord(ClientDataRecord dataRecord) {
		JsonObject permissionObject = jsonHandler.parseStringAsObject(permissions);
		DataComparer comparer = comparerFactory.factor("permission", dataRecord);
		List<String> errorMessages = comparer.checkClientDataRecordContains(permissionObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public void onlyForTestSetJsonHandler(JsonHandlerImp jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

	public JsonHandler onlyForTestGetJsonHandler() {
		return jsonHandler;
	}

}
