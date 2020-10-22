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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;

public class ActionComparerFixtureTest {

	private ActionComparerFixture fixture;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private RecordHandlerSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		DependencyProvider.setComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.compare.ComparerFactorySpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		recordHandler = new RecordHandlerSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		fixture = new ActionComparerFixture();
		fixture.setRecordHandler(recordHandler);
		fixture.setJsonHandler(jsonHandler);

	}

	@Test
	public void testInit() {
		fixture = new ActionComparerFixture();
		assertTrue(fixture.getComparerFactory() instanceof ComparerFactorySpy);
		assertTrue(fixture.getJsonHandler() instanceof JsonHandlerImp);
		assertTrue(fixture.getJsonToDataRecordConverter() instanceof JsonToDataRecordConverterImp);
		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactorySpy);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(), fixture.getHttpHandlerFactory());
	}

	@Test
	public void testCallsAreMadeCorrectly() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		ComparerFactorySpy comparerFactory = (ComparerFactorySpy) fixture.getComparerFactory();

		String actions = "{\"actions\":[ \"read\", \"delete\",\"update\"]}";
		fixture.setActions(actions);

		fixture.testCheckActions();

		assertEquals(comparerFactory.type, "action");
		assertSame(comparerFactory.dataRecord, clientDataRecordSpy);

		ComparerSpy factoredComparer = comparerFactory.factoredComparer;
		assertEquals(jsonParser.jsonStringsSentToParser.get(0), actions);
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

}