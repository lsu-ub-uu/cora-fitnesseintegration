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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class PermissionComparerTest {

	private JsonParser jsonParser;
	private PermissionComparer comparer;
	private ClientDataRecord dataRecord;

	@BeforeMethod
	public void setUp() {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		dataRecord = ClientDataRecord.withClientDataGroup(dataGroup);
		comparer = new PermissionComparerImp(dataRecord);
		jsonParser = new OrgJsonParser();
	}

	@Test
	public void testOneReadPermissionsInRecordOk() {
		dataRecord.addReadPermission("readPermissionOne");
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testOneReadPermissionsInJsonTwoPermissionsInRecordOk() {
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addReadPermission("readPermissionTwo");
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testOneReadAndOneWritePermissionsInRecordOk() {
		dataRecord.addReadPermission("readPermissionOne");
		dataRecord.addWritePermission("writePermissionOne");
		String permissions = "{\"read\":[\"readPermissionOne\"],\"write\":[\"writePermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testNoReadPermissionsInRecordNOTOk() {
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0), "Read permission readPermissionOne is missing.");

	}

	@Test
	public void testOneReadPermissionsInRecordTwoInJsonNOTOk() {
		dataRecord.addReadPermission("readPermissionOne");
		String permissions = "{\"read\":[\"readPermissionOne\", \"readPermissionTwo\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0), "Read permission readPermissionTwo is missing.");

	}

	@Test
	public void testNoWritePermissionsInRecordNOTOk() {
		String permissions = "{\"write\":[\"writePermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0), "Write permission writePermissionOne is missing.");

	}

	@Test
	public void testOneWritePermissionsInRecordOk() {
		dataRecord.addWritePermission("writePermissionOne");
		String permissions = "{\"write\":[\"writePermissionOne\"]}";
		JsonValue jsonValue = jsonParser.parseString(permissions);

		List<String> results = comparer.checkDataRecordContainsPermissions(jsonValue);
		assertTrue(results.isEmpty());
	}
}
