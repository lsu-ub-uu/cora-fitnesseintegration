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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordOLDSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerOLDSpy;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class PermissionComparerFixtureTest {

	private PermissionComparerFixture fixture;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private RecordHandlerOLDSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		DependencyProvider.setComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.compare.ComparerFactorySpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");

		fixture = new PermissionComparerFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		recordHandler = new RecordHandlerOLDSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);

		fixture.setType("someRecordType");
		fixture.onlyForTestSetRecordHandler(recordHandler);
		fixture.onlyForTestSetJsonHandler(jsonHandler);
	}

	@Test
	public void testInit() {
		fixture = new PermissionComparerFixture();
		assertTrue(fixture.getComparerFactory() instanceof ComparerFactorySpy);
		assertTrue(fixture.onlyForTestGetJsonHandler() instanceof JsonHandlerImp);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.onlyForTestGetRecordHandler();
	}

	@Test
	public void testCheckContainPermissionValuesPassedCorrectly() {
		ClientDataRecordOLDSpy clientClientDataRecordSpy = new ClientDataRecordOLDSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);

		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		fixture.testCheckPermissions();

		ComparerFactorySpy permissionComparerFactory = (ComparerFactorySpy) fixture
				.getComparerFactory();
		assertSame(permissionComparerFactory.dataRecord, clientClientDataRecordSpy);

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), permissions);

		ComparerSpy factoredComparer = permissionComparerFactory.factoredComparer;
		assertSame(factoredComparer.jsonValue, jsonParser.jsonObjectSpies.get(0));
	}

	@Test
	public void testCheckContainPermissionOk() {

		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		String responseText = fixture.testCheckPermissions();
		assertEquals(responseText, "OK");
	}

	@Test
	public void testCheckPermissionNotOk() {
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);

		ComparerFactorySpy permissionComparerFactory = (ComparerFactorySpy) fixture
				.getComparerFactory();
		permissionComparerFactory.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testCheckPermissions(),
				"From spy: permission with number 0 is missing. "
						+ "From spy: permission with number 1 is missing. "
						+ "From spy: permission with number 2 is missing.");
	}

	@Test
	public void testReadFromListCheckPermissionsWithValuesOK() {
		fixture.setListIndexToCompareTo(0);
		String result = fixture.testReadFromListCheckPermissions();

		assertEquals(result, "OK");
	}

	@Test
	public void testReadFromListCheckPermissionResultNotOK() {
		addRecordsToDataHolder();
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		fixture.setListIndexToCompareTo(0);

		ComparerFactorySpy permissionComparerFactory = (ComparerFactorySpy) fixture
				.getComparerFactory();
		permissionComparerFactory.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testReadFromListCheckPermissions(),
				"From spy: permission with number 0 is missing. "
						+ "From spy: permission with number 1 is missing. "
						+ "From spy: permission with number 2 is missing.");
	}

	private void addRecordsToDataHolder() {
		List<ClientDataRecord> dataRecords = new ArrayList<>();
		dataRecords.add(new ClientDataRecordOLDSpy());
		dataRecords.add(new ClientDataRecordOLDSpy());
		DataHolder.setRecordList(dataRecords);
	}

	@Test
	public void testReadListCheckPermissionComparesCorrectDataTwoComparisons() {
		addRecordsToDataHolder();
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);

		fixture.setListIndexToCompareTo(0);
		fixture.testReadFromListCheckPermissions();

		ComparerFactorySpy permissionComparerFactory = (ComparerFactorySpy) fixture
				.getComparerFactory();

		ClientDataRecordOLDSpy recordSpy = (ClientDataRecordOLDSpy) DataHolder.getRecordList()
				.get(0);
		assertSame(permissionComparerFactory.dataRecords.get(0), recordSpy);

		ComparerSpy factoredComparer = permissionComparerFactory.factoredComparers.get(0);
		assertSame(factoredComparer.jsonValue, jsonParser.jsonObjectSpies.get(0));
		assertEquals(jsonParser.jsonStringsSentToParser.get(0), permissions);

		fixture.setListIndexToCompareTo(1);
		fixture.testReadFromListCheckPermissions();
		ComparerSpy factoredComparer2 = permissionComparerFactory.factoredComparers.get(1);
		ClientDataRecordOLDSpy recordSpy2 = (ClientDataRecordOLDSpy) DataHolder.getRecordList()
				.get(1);
		assertSame(permissionComparerFactory.dataRecords.get(1), recordSpy2);
		assertSame(factoredComparer2.jsonValue, jsonParser.jsonObjectSpies.get(1));
		assertEquals(jsonParser.jsonStringsSentToParser.get(1), permissions);

	}

	@Test
	public void testReadListCheckPermissionComparerThrowsError() {
		addRecordsToDataHolder();
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		fixture.setListIndexToCompareTo(0);

		ComparerFactorySpy permissionComparerFactory = (ComparerFactorySpy) fixture
				.getComparerFactory();
		permissionComparerFactory.spyShouldThrowError = true;

		String responseText = fixture.testReadFromListCheckPermissions();

		ComparerSpy factoredComparer = permissionComparerFactory.factoredComparers.get(0);

		assertEquals(responseText, factoredComparer.errorMessage);
	}

}
