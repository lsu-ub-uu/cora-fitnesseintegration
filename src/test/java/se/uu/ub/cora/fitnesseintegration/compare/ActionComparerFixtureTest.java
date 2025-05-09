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

public class ActionComparerFixtureTest {

	private ActionComparerFixture fixture;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private RecordHandlerOLDSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		DependencyProvider.setComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.compare.ComparerFactorySpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactoryOldSpy");
		recordHandler = new RecordHandlerOLDSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		fixture = new ActionComparerFixture();
		fixture.onlyForTestSetRecordHandler(recordHandler);
		fixture.onlyForTestSetJsonHandler(jsonHandler);

	}

	@Test
	public void testInit() {
		fixture = new ActionComparerFixture();
		assertTrue(fixture.getComparerFactory() instanceof ComparerFactorySpy);
		assertTrue(fixture.onlyForTestGetJsonHandler() instanceof JsonHandlerImp);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.onlyForTestGetRecordHandler();
	}

	@Test
	public void testCallsAreMadeCorrectly() {
		ClientDataRecordOLDSpy clientClientDataRecordSpy = new ClientDataRecordOLDSpy();

		DataHolder.setRecord(clientClientDataRecordSpy);
		ComparerFactorySpy comparerFactory = (ComparerFactorySpy) fixture.getComparerFactory();
		fixture.setActions("{\"actions\":[ \"read\", \"delete\",\"update\"]}");

		fixture.testCheckActions();

		assertEquals(comparerFactory.type, "action");
		assertSame(comparerFactory.dataRecord, clientClientDataRecordSpy);

		ComparerSpy factoredComparer = comparerFactory.factoredComparer;
		assertEquals(jsonParser.jsonStringsSentToParser.get(0),
				"{\"actions\":[ \"read\", \"delete\",\"update\"]}");
		assertSame(factoredComparer.jsonValue, jsonParser.jsonObjectSpy);

	}

	@Test
	public void testCheckPermissionOk() {
		fixture.setActions("{\"actions\":[ \"read\", \"delete\",\"update\"]}");
		String result = fixture.testCheckActions();
		assertEquals(result, "OK");
	}

	@Test
	public void testPermissionNotOk() {
		fixture.setActions("{\"actions\":[ \"read\", \"delete\"]}");
		ComparerFactorySpy comparerFactory = (ComparerFactorySpy) fixture.getComparerFactory();
		comparerFactory.numberOfErrorsToReturn = 3;

		String testCheckActions = fixture.testCheckActions();

		assertEquals(testCheckActions,
				"From spy: action with number 0 is missing. "
						+ "From spy: action with number 1 is missing. "
						+ "From spy: action with number 2 is missing.");
	}

	@Test
	public void testTestCheckActionsFromListPermissionsOK() {
		ClientDataRecord dataRecord = new ClientDataRecordOLDSpy();
		List<ClientDataRecord> dataRecordList = List.of(dataRecord);

		ComparerFactorySpy comparerFactory = (ComparerFactorySpy) fixture.getComparerFactory();

		DataHolder.setRecordList(dataRecordList);
		fixture.setActions("{\"actions\":[ \"read\", \"delete\",\"update\"]}");
		fixture.setListIndexToCompareTo(0);
		String result = fixture.testCheckActionsFromList();

		assertEquals(comparerFactory.type, "action");
		assertSame(comparerFactory.dataRecord, dataRecord);

		ComparerSpy factoredComparer = comparerFactory.factoredComparer;
		assertEquals(jsonParser.jsonStringsSentToParser.get(0),
				"{\"actions\":[ \"read\", \"delete\",\"update\"]}");
		assertSame(factoredComparer.jsonValue, jsonParser.jsonObjectSpy);

		assertEquals(result, "OK");
	}

	@Test
	public void testTestCheckActionsFromListPermissionsNotOK() {
		ClientDataRecord dataRecord = new ClientDataRecordOLDSpy();
		List<ClientDataRecord> dataRecordList = List.of(dataRecord);

		ComparerFactorySpy comparerFactory = (ComparerFactorySpy) fixture.getComparerFactory();
		comparerFactory.numberOfErrorsToReturn = 3;

		fixture.setActions("{\"actions\":[ \"read\", \"delete\"]}");
		DataHolder.setRecordList(dataRecordList);
		fixture.setListIndexToCompareTo(0);
		String result = fixture.testCheckActionsFromList();

		assertEquals(result,
				"From spy: action with number 0 is missing. "
						+ "From spy: action with number 1 is missing. "
						+ "From spy: action with number 2 is missing.");

	}

}
