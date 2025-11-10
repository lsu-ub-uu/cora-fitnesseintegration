/*
 * Copyright 2020, 2023 Uppsala University Library
 * Copyright 2023, 2025 Olov McKie
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
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class PermissionComparerFixtureTest {

	private PermissionComparerFixture fixture;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private RecordHandlerOLDSpy recordHandler;
	private DependencyFactorySpy dependencyFactory;

	@BeforeMethod
	public void setUp() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);
		SystemUrl.setUrl("http://localhost:8080/therest/");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactoryOldSpy");

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

		ComparerOldSpy factoredComparer = (ComparerOldSpy) dependencyFactory.MCR
				.assertCalledParametersReturn("factorPermissionComparer",
						clientClientDataRecordSpy);

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), permissions);

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
		ComparerOldSpy comparerSpy = new ComparerOldSpy();
		comparerSpy.type = "permission";
		comparerSpy.numberOfErrorsToReturn = 3;
		dependencyFactory.MRV.setDefaultReturnValuesSupplier("factorPermissionComparer",
				() -> comparerSpy);
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);

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
		ComparerOldSpy comparerSpy = new ComparerOldSpy();
		comparerSpy.type = "permission";
		comparerSpy.numberOfErrorsToReturn = 3;
		dependencyFactory.MRV.setDefaultReturnValuesSupplier("factorPermissionComparer",
				() -> comparerSpy);
		addRecordsToDataHolder();
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		fixture.setListIndexToCompareTo(0);

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

		ClientDataRecordOLDSpy recordSpy = (ClientDataRecordOLDSpy) DataHolder.getRecordList()
				.get(0);

		ComparerOldSpy factoredComparer = (ComparerOldSpy) dependencyFactory.MCR
				.assertCalledParametersReturn("factorPermissionComparer", recordSpy);

		assertSame(factoredComparer.jsonValue, jsonParser.jsonObjectSpies.get(0));
		assertEquals(jsonParser.jsonStringsSentToParser.get(0), permissions);

		fixture.setListIndexToCompareTo(1);
		fixture.testReadFromListCheckPermissions();
		ClientDataRecordOLDSpy recordSpy2 = (ClientDataRecordOLDSpy) DataHolder.getRecordList()
				.get(1);
		ComparerOldSpy factoredComparer2 = (ComparerOldSpy) dependencyFactory.MCR
				.assertCalledParametersReturn("factorPermissionComparer", recordSpy2);
		assertSame(factoredComparer2.jsonValue, jsonParser.jsonObjectSpies.get(1));
		assertEquals(jsonParser.jsonStringsSentToParser.get(1), permissions);

	}

	@Test
	public void testReadListCheckPermissionComparerThrowsError() {
		ComparerOldSpy comparerSpy = new ComparerOldSpy();
		comparerSpy.spyShouldThrowError = true;
		dependencyFactory.MRV.setDefaultReturnValuesSupplier("factorPermissionComparer",
				() -> comparerSpy);
		addRecordsToDataHolder();
		String permissions = "{\"read\":[\"readPermissionOne\"]}";
		fixture.setPermissions(permissions);
		fixture.setListIndexToCompareTo(0);

		String responseText = fixture.testReadFromListCheckPermissions();

		assertEquals(responseText, comparerSpy.errorMessage);
	}
}
