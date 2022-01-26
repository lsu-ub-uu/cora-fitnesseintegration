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

import java.util.List;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;

public class PermissionComparerFixture extends ComparerFixture {

	private ComparerFactory comparerFactory;
	private String permissions;

	public PermissionComparerFixture() {
		super();
		comparerFactory = DependencyProvider.getComparerFactory();
	}

	public String testCheckPermissions() {
		DataRecord dataRecord = DataHolder.getRecord();
		return comparePermissionUsingDataRecord(dataRecord);
	}

	private String comparePermissionUsingDataRecord(DataRecord dataRecord) {
		JsonObject permissionObject = jsonHandler.parseStringAsObject(permissions);

		DataComparer comparer = comparerFactory.factor("permission", dataRecord);

		List<String> errorMessages = comparer.checkDataRecordContains(permissionObject);
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
			DataRecord dataRecord = DataHolder.getRecordList().get(indexToCompareTo);
			return comparePermissionUsingDataRecord(dataRecord);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

}
