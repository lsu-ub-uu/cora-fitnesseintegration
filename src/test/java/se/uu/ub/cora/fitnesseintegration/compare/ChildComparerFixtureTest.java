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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToClientDataRecordConverterImp;
import se.uu.ub.cora.fitnesseintegration.ChildComparerSpy;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.JsonToClientDataRecordConverterSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;

public class ChildComparerFixtureTest {

	private ChildComparerFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonToClientDataRecordConverterSpy jsonToClientDataRecordConverter;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		DependencyProvider.setChildComparerUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");

		fixture = new ChildComparerFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		recordHandler = new RecordHandlerSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		jsonToClientDataRecordConverter = new JsonToClientDataRecordConverterSpy();

		fixture.setType("someRecordType");
		fixture.setRecordHandler(recordHandler);
		fixture.onlyForTestSetJsonHandler(jsonHandler);
		fixture.onlyForTestSetJsonToClientDataRecordConverter(jsonToClientDataRecordConverter);
	}

	@Test
	public void testInit() {
		fixture = new ChildComparerFixture();
		assertTrue(fixture.onlyForTestGetChildComparer() instanceof ChildComparerSpy);
		assertTrue(fixture.onlyForTestGetJsonHandler() instanceof JsonHandlerImp);
		assertTrue(fixture
				.onlyForTestGetJsonToClientDataRecordConverter() instanceof JsonToClientDataRecordConverterImp);
		assertTrue(fixture.onlyForTestGetHttpHandlerFactory() instanceof HttpHandlerFactorySpy);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(),
				fixture.onlyForTestGetHttpHandlerFactory());
	}

	private void addRecordsToDataHolder() {
		List<ClientDataRecord> dataRecords = new ArrayList<>();
		dataRecords.add(new ClientDataRecordSpy());
		dataRecords.add(new ClientDataRecordSpy());
		DataHolder.setRecordList(dataRecords);
	}

	@Test
	public void testReadFromListCheckContainWithValuesOK() {
		fixture.setListIndexToCompareTo(0);
		String result = fixture.testReadFromListCheckContainWithValues();

		assertEquals(result, "OK");
	}

	@Test
	public void testReadFromListCheckContainWithValuesResultNotOK() {
		addRecordsToDataHolder();
		String childrenToLookFor = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setListIndexToCompareTo(0);
		fixture.setChildren(childrenToLookFor);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testReadFromListCheckContainWithValues(),
				"From spy: Child with number 0 has incorrect value. "
						+ "From spy: Child with number 1 has incorrect value. "
						+ "From spy: Child with number 2 has incorrect value.");
	}

	@Test
	public void testReadFromListCheckContainWithValuesComparesCorrectData() {
		addRecordsToDataHolder();

		String children = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setChildren(children);
		fixture.setListIndexToCompareTo(0);
		fixture.testReadFromListCheckContainWithValues();

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		assertEquals(jsonParser.jsonStringsSentToParser.get(0), children);

		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecordList().get(0);
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);

		fixture.setListIndexToCompareTo(1);
		fixture.testReadFromListCheckContainWithValues();
		ClientDataRecordSpy recordSpy2 = (ClientDataRecordSpy) DataHolder.getRecordList().get(1);
		ClientDataGroup dataGroup = comparerSpy.dataGroup;
		ClientDataGroup clientDataGroup = recordSpy2.clientDataGroup;
		assertSame(dataGroup, clientDataGroup);

	}

	@Test
	public void testReadFromListCheckContainWithValuesComparerThrowsError() {
		addRecordsToDataHolder();
		String childrenToLookFor = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setListIndexToCompareTo(0);
		fixture.setChildren(childrenToLookFor);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testReadFromListCheckContainWithValues(), childComparer.errorMessage);
	}

	@Test
	public void testReadFromListCheckContainOK() {
		addRecordsToDataHolder();
		fixture.setListIndexToCompareTo(0);
		String result = fixture.testReadFromListCheckContain();

		assertEquals(result, "OK");
	}

	@Test
	public void testReadFromListCheckContainResultNotOK() {
		addRecordsToDataHolder();
		String childrenToLookFor = "{\"children\":[{\"name\":\"instructorId\"},{\"name\":\"popularity\"}]}";
		fixture.setListIndexToCompareTo(0);
		fixture.setChildren(childrenToLookFor);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testReadFromListCheckContain(),
				"From spy: Child with number 0 is missing. "
						+ "From spy: Child with number 1 is missing. "
						+ "From spy: Child with number 2 is missing.");
	}

	@Test
	public void testReadListCheckContainComparesCorrectData() {
		addRecordsToDataHolder();

		String childrenToLookFor = "{\"children\":[{\"name\":\"instructorId\"},{\"name\":\"popularity\"}]}";
		fixture.setChildren(childrenToLookFor);
		fixture.setListIndexToCompareTo(0);
		fixture.testReadFromListCheckContain();

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		assertEquals(jsonParser.jsonStringsSentToParser.get(0), childrenToLookFor);

		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecordList().get(0);
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);

		fixture.setListIndexToCompareTo(1);
		fixture.testReadFromListCheckContain();
		ClientDataRecordSpy recordSpy2 = (ClientDataRecordSpy) DataHolder.getRecordList().get(1);
		ClientDataGroup dataGroup = comparerSpy.dataGroup;
		ClientDataGroup clientDataGroup = recordSpy2.clientDataGroup;
		assertSame(dataGroup, clientDataGroup);

	}

	@Test
	public void testReadListCheckContainComparerThrowsError() {
		addRecordsToDataHolder();
		String childrenToLookFor = "{\"children\":[{\"name\":\"instructorId\"},{\"name\":\"popularity\"}]}";
		fixture.setListIndexToCompareTo(0);
		fixture.setChildren(childrenToLookFor);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testReadFromListCheckContain(), childComparer.errorMessage);
	}

	@Test
	public void testCheckContainOK() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);
		String result = fixture.testCheckContain();
		assertEquals(result, "OK");
	}

	@Test
	public void testCheckContainResultNotOK() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testCheckContain(),
				"From spy: Child with number 0 is missing. "
						+ "From spy: Child with number 1 is missing. "
						+ "From spy: Child with number 2 is missing.");
	}

	@Test
	public void testCheckContainSendsResultBetweenObjectsCorrectly() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);
		String childrenToLookFor = "{\"children\":[{\"name\":\"instructorId\"},{\"name\":\"popularity\"}]}";
		fixture.setChildren(childrenToLookFor);
		fixture.testCheckContain();

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), childrenToLookFor);

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecord();
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);
	}

	@Test
	public void testCheckContainComparerThrowsError() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testCheckContain(), childComparer.errorMessage);
	}

	@Test
	public void testCheckContainWithValuesResultOK() {
		DataHolder.setRecord(new ClientDataRecordSpy());
		String childrenToLookFor = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setChildren(childrenToLookFor);
		assertEquals(fixture.testCheckContainWithValues(), "OK");
	}

	@Test
	public void testCheckContainWithValuesResultNotOK() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testCheckContainWithValues(),
				"From spy: Child with number 0 has incorrect value. "
						+ "From spy: Child with number 1 has incorrect value. "
						+ "From spy: Child with number 2 has incorrect value.");
	}

	@Test
	public void testCheckContainWithValuesSendsResultBetweenObjectsCorrectly() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);
		String childrenToLookFor = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setChildren(childrenToLookFor);
		fixture.testCheckContainWithValues();

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), childrenToLookFor);

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecord();
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);
	}

	@Test
	public void testCheckContainWithValuesThrowsError() {
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecordSpy);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.onlyForTestGetChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testCheckContainWithValues(), childComparer.errorMessage);
	}

	@Test
	public void testCountChildrenOK() {
		ClientDataGroup clientDataGroup = createDataGroupWithTwoChildReferences();
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		clientClientDataRecordSpy.clientDataGroup = clientDataGroup;
		DataHolder.setRecord(clientClientDataRecordSpy);

		fixture.setExpectedNumberOfChildren(2);
		String result = fixture.testCheckNumberOfChildren();
		assertEquals(result, "OK");
	}

	private ClientDataGroup createDataGroupWithTwoChildReferences() {
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("clientDataGroupSpy");
		ClientDataGroup childReferences = ClientDataGroup.withNameInData("childReferences");
		addChildRefWithRepeatId(childReferences, "0");
		addChildRefWithRepeatId(childReferences, "1");
		clientDataGroup.addChild(childReferences);
		return clientDataGroup;
	}

	private void addChildRefWithRepeatId(ClientDataGroup childReferences, String repeatId) {
		ClientDataGroup childRef1 = ClientDataGroup.withNameInData("childReference");
		childRef1.setRepeatId(repeatId);
		childReferences.addChild(childRef1);
	}

	@Test
	public void testCountChildrenNotOK() {
		ClientDataGroup clientDataGroup = createDataGroupWithTwoChildReferences();
		ClientDataRecordSpy clientClientDataRecordSpy = new ClientDataRecordSpy();
		clientClientDataRecordSpy.clientDataGroup = clientDataGroup;
		DataHolder.setRecord(clientClientDataRecordSpy);

		fixture.setExpectedNumberOfChildren(3);
		String result = fixture.testCheckNumberOfChildren();
		assertEquals(result, "Expected 3 but found 2");
	}

}
