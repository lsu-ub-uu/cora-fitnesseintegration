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
package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;

public class CompararerFixtureTest {

	private ComparerFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonToDataRecordConverterSpy jsonToDataConverter;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		DependencyProvider
				.setChildComparerClassName("se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");

		fixture = new ComparerFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		recordHandler = new RecordHandlerSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		jsonToDataConverter = new JsonToDataRecordConverterSpy();

		fixture.setType("someRecordType");
		fixture.setRecordHandler(recordHandler);
		fixture.setJsonHandler(jsonHandler);
		fixture.setJsonToDataRecordConverter(jsonToDataConverter);
	}

	@Test
	public void testInit() {
		fixture = new ComparerFixture();
		assertTrue(fixture.getChildComparer() instanceof ChildComparerSpy);
		assertTrue(fixture.getJsonHandler() instanceof JsonHandlerImp);
		assertTrue(fixture.getJsonToDataRecordConverter() instanceof JsonToDataRecordConverterImp);
		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactorySpy);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(), fixture.getHttpHandlerFactory());
	}

	@Test
	public void testReadRecordListAndStoreRecordsNoFilter() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.testReadRecordListAndStoreRecords();
		assertTrue(recordHandler.readRecordListWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(fixture.getStoredListAsJson(), recordHandler.jsonToReturn);
		assertEquals(recordHandler.authToken, authToken);
		assertNull(recordHandler.filter);
	}

	@Test
	public void testReadRecordListAndStoreRecordsWithFilter() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		String listFilter = "someFilter";
		fixture.setAuthToken(authToken);
		fixture.setListFilter(listFilter);
		fixture.testReadRecordListAndStoreRecords();
		assertTrue(recordHandler.readRecordListWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(fixture.getStoredListAsJson(), recordHandler.jsonToReturn);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.filter, listFilter);
	}

	private void addRecordsToDataHolder() {
		List<DataRecord> dataRecords = new ArrayList<>();
		dataRecords.add(new ClientDataRecordSpy());
		dataRecords.add(new ClientDataRecordSpy());
		DataHolder.setRecordList(dataRecords);
	}

	@Test
	public void testReadRecordListAndStoreRecordsInDataHolder()
			throws UnsupportedEncodingException {
		fixture.testReadRecordListAndStoreRecords();

		String jsonListFromRecordHandler = recordHandler.jsonToReturn;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonListFromRecordHandler);

		JsonObjectSpy listObjectFromSpy = assertObjectForKeyDataListIsExtracted();

		JsonObjectSpy dataList = assertObjectForKeyDataIsExtracted(listObjectFromSpy);

		assertAllRecordsInDataAreConverted(dataList);

		assertConvertedRecordsAreAddedToRecordHolder();
	}

	private JsonObjectSpy assertObjectForKeyDataListIsExtracted() {
		JsonObjectSpy listObjectFromSpy = jsonParser.jsonObjectSpies.get(0);
		assertEquals(listObjectFromSpy.getValueKeys.get(0), "dataList");
		return listObjectFromSpy;
	}

	private JsonObjectSpy assertObjectForKeyDataIsExtracted(JsonObjectSpy listObjectFromSpy) {
		JsonObjectSpy dataList = listObjectFromSpy.getValueObjectsReturned.get(0);
		assertEquals(dataList.getValueKeys.get(0), "data");
		return dataList;
	}

	private void assertAllRecordsInDataAreConverted(JsonObjectSpy dataList) {
		JsonArraySpy data = dataList.getValueArraysReturned.get(0);
		IteratorSpy returnedIterator = data.returnedIterator;
		assertTrue(returnedIterator.hasNextWasCalled);

		List<JsonObjectSpy> objectsReturnedFromNext = returnedIterator.objectsReturnedFromNext;
		assertSame(jsonToDataConverter.jsonObjects.get(0), objectsReturnedFromNext.get(0));
		assertSame(jsonToDataConverter.jsonObjects.get(1), objectsReturnedFromNext.get(1));
	}

	private void assertConvertedRecordsAreAddedToRecordHolder() {
		List<ClientDataRecordSpy> returnedSpies = jsonToDataConverter.returnedSpies;
		assertSame(DataHolder.getRecordList().get(0), returnedSpies.get(0));
		assertSame(DataHolder.getRecordList().get(1), returnedSpies.get(1));
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

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
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

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.getChildComparer();
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
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
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

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
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

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.getChildComparer();
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
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testReadFromListCheckContain(), childComparer.errorMessage);
	}

	@Test
	public void testReadRecordAndStoreJson() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.setId("someId");
		fixture.testReadAndStoreRecord();
		assertTrue(recordHandler.readRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType/someId";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, authToken);

		String jsonListFromRecordHandler = recordHandler.jsonToReturn;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonListFromRecordHandler);
		assertSame(jsonToDataConverter.jsonObjects.get(0), jsonParser.jsonObjectSpies.get(0));
		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(0));
	}

	@Test
	public void testCheckContainOK() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		String result = fixture.testCheckContain();
		assertEquals(result, "OK");
	}

	@Test
	public void testCheckContainResultNotOK() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testCheckContain(),
				"From spy: Child with number 0 is missing. "
						+ "From spy: Child with number 1 is missing. "
						+ "From spy: Child with number 2 is missing.");
	}

	@Test
	public void testCheckContainSendsResultBetweenObjectsCorrectly() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		String childrenToLookFor = "{\"children\":[{\"name\":\"instructorId\"},{\"name\":\"popularity\"}]}";
		fixture.setChildren(childrenToLookFor);
		fixture.testCheckContain();

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), childrenToLookFor);

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.getChildComparer();
		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecord();
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);
	}

	@Test
	public void testCheckContainComparerThrowsError() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
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
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);

		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
		childComparer.numberOfErrorsToReturn = 3;

		assertEquals(fixture.testCheckContainWithValues(),
				"From spy: Child with number 0 has incorrect value. "
						+ "From spy: Child with number 1 has incorrect value. "
						+ "From spy: Child with number 2 has incorrect value.");
	}

	@Test
	public void testCheckContainWithValuesSendsResultBetweenObjectsCorrectly() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		String childrenToLookFor = "{\"children\":[{\"type\":\"atomic\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}";
		fixture.setChildren(childrenToLookFor);
		fixture.testCheckContainWithValues();

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), childrenToLookFor);

		ChildComparerSpy comparerSpy = (ChildComparerSpy) fixture.getChildComparer();
		assertSame(comparerSpy.jsonValue, jsonParser.jsonObjectSpies.get(0));
		ClientDataRecordSpy recordSpy = (ClientDataRecordSpy) DataHolder.getRecord();
		assertSame(comparerSpy.dataGroup, recordSpy.clientDataGroup);
	}

	@Test
	public void testCheckContainWithValuesThrowsError() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		DataHolder.setRecord(clientDataRecordSpy);
		ChildComparerSpy childComparer = (ChildComparerSpy) fixture.getChildComparer();
		childComparer.spyShouldThrowError = true;

		assertEquals(fixture.testCheckContainWithValues(), childComparer.errorMessage);
	}

}
