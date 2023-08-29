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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.fitnesseintegration.BasicHttpResponse;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy;
import se.uu.ub.cora.fitnesseintegration.IteratorSpy;
import se.uu.ub.cora.fitnesseintegration.JsonArraySpy;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.JsonObjectSpy;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestClientFactoryImp;

public class ComparerFixtureTest {

	private ComparerFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonToDataRecordConverterForComparerSpy jsonToDataConverter;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private String type;
	private String authToken;
	private String json;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		DependencyProvider.setChildComparerUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");

		fixture = new ComparerFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		recordHandler = new RecordHandlerSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		jsonToDataConverter = new JsonToDataRecordConverterForComparerSpy();

		type = "someRecordType";
		fixture.setType(type);
		fixture.setRecordHandler(recordHandler);
		fixture.onlyForTestSetJsonHandler(jsonHandler);
		fixture.onlyForTestSetJsonToDataRecordConverter(jsonToDataConverter);
	}

	@Test
	public void testInit() {
		fixture = new ComparerFixture();
		assertTrue(fixture.onlyForTestGetJsonHandler() instanceof JsonHandlerImp);
		assertTrue(fixture
				.onlyForTestGetJsonToDataRecordConverter() instanceof JsonToDataRecordConverterImp);
		assertTrue(fixture.onlyForTestGetHttpHandlerFactory() instanceof HttpHandlerFactorySpy);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(),
				fixture.onlyForTestGetHttpHandlerFactory());
		RestClientFactoryImp clientFactory = (RestClientFactoryImp) recordHandler
				.getRestClientFactory();
		assertEquals(clientFactory.getBaseUrl(), fixture.baseUrl);
	}

	@Test
	public void testReadRecordListAndStoreRecordsNoFilter() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.testReadRecordListAndStoreRecords();
		assertTrue(recordHandler.readRecordListWasCalled);

		assertEquals(recordHandler.recordType, type);
		assertEquals(fixture.getStoredListAsJson(), recordHandler.jsonToReturnDefault);
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

		assertEquals(recordHandler.recordType, type);
		assertEquals(fixture.getStoredListAsJson(), recordHandler.jsonToReturnDefault);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.filter, listFilter);
	}

	@Test
	public void testReadRecordListAndStoreRecordsInDataHolder()
			throws UnsupportedEncodingException {
		fixture.testReadRecordListAndStoreRecords();

		String jsonListFromRecordHandler = recordHandler.jsonToReturnDefault;
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
	public void testReadRecordListAndStoreRecordAsSpecifiedInIndex()
			throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.setIndexToStore(2);
		fixture.testReadRecordListAndStoreRecords();

		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(2));
	}

	@Test
	public void testReadRecordListAndStoreRecordWhenNoSpecifiedIndexUsingZeroAsDefault()
			throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.testReadRecordListAndStoreRecords();

		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(0));
	}

	@Test
	public void testReadRecordListAndStoreRecordByIdRecordNotFound() throws Exception {
		JsonToDataRecordConverterSpy jsonConverterSpy = new JsonToDataRecordConverterSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.onlyForTestSetJsonToDataRecordConverter(jsonConverterSpy);

		DataHolder.setRecord(new ClientDataRecordSpy());

		String response = fixture.testReadRecordListAndStoreRecordById();

		recordHandler.MCR.assertParameters("readRecordList", 0, authToken, type, null);

		DataRecord record = DataHolder.getRecord();

		assertNull(record);

		assertReturnValueForReadRecordListAndStore(response);
	}

	@Test
	public void testReadRecordListAndStoreRecordById1750() throws Exception {
		JsonToDataRecordConverterSpy jsonConverterSpy = new JsonToDataRecordConverterSpy();
		String authToken = "someAuthToken";
		String recordId = "1750";

		fixture.setIdToStore(recordId);
		fixture.setAuthToken(authToken);
		fixture.onlyForTestSetJsonToDataRecordConverter(jsonConverterSpy);

		jsonConverterSpy.returnRecordWithID(recordId);

		String response = fixture.testReadRecordListAndStoreRecordById();

		recordHandler.MCR.assertParameters("readRecordList", 0, authToken, type, null);

		DataRecord record = DataHolder.getRecord();

		jsonConverterSpy.MCR.assertMethodWasCalled("toInstance");

		String recordIdValue = record.getClientDataGroup().getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");

		assertEquals(recordIdValue, recordId);

		assertReturnValueForReadRecordListAndStore(response);
	}

	@Test
	public void testReadRecordListAndStoreRecordById3333() throws Exception {
		JsonToDataRecordConverterSpy jsonConverterSpy = new JsonToDataRecordConverterSpy();
		String authToken = "someAuthToken";
		String recordId = "3333";

		fixture.setIdToStore(recordId);
		fixture.setAuthToken(authToken);
		fixture.onlyForTestSetJsonToDataRecordConverter(jsonConverterSpy);

		jsonConverterSpy.returnRecordWithID(recordId);

		String response = fixture.testReadRecordListAndStoreRecordById();

		recordHandler.MCR.assertParameters("readRecordList", 0, authToken, type, null);

		DataRecord record = DataHolder.getRecord();

		jsonConverterSpy.MCR.assertMethodWasCalled("toInstance");

		String recordIdValue = record.getClientDataGroup().getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");

		assertEquals(recordIdValue, recordId);

		assertReturnValueForReadRecordListAndStore(response);
	}

	private void assertReturnValueForReadRecordListAndStore(String response) {
		String jsonListString = ((BasicHttpResponse) (recordHandler.MCR
				.getReturnValue("readRecordList", 0))).responseText;

		assertEquals(response, jsonListString);
	}

	@Test
	public void testReadRecordListAndStoreRecordByIdLoopList() throws Exception {
		JsonToDataRecordConverterSpy jsonConverterSpy = new JsonToDataRecordConverterSpy();
		JsonHandlerSpy jsonHandlerSpy = new JsonHandlerSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.onlyForTestSetJsonToDataRecordConverter(jsonConverterSpy);
		fixture.onlyForTestSetJsonHandler(jsonHandlerSpy);

		fixture.testReadRecordListAndStoreRecordById();

		recordHandler.MCR.getReturnValue("readRecordList", 0);

		String jsonListString = ((BasicHttpResponse) (recordHandler.MCR
				.getReturnValue("readRecordList", 0))).responseText;

		jsonHandlerSpy.MCR.assertMethodWasCalled("parseStringAsObject");
		jsonHandlerSpy.MCR.assertParameters("parseStringAsObject", 0, jsonListString);

		JsonObjectSpy jsonObject = (JsonObjectSpy) jsonHandlerSpy.MCR
				.getReturnValue("parseStringAsObject", 0);
		jsonObject.MCR.assertParameters("getValue", 0, "dataList");

		JsonObjectSpy dataListSpy = (JsonObjectSpy) jsonObject.MCR.getReturnValue("getValue", 0);
		dataListSpy.MCR.assertParameters("getValue", 0, "data");

		JsonArraySpy dataArraySpy = (JsonArraySpy) dataListSpy.MCR.getReturnValue("getValue", 0);
		dataArraySpy.MCR.assertMethodWasCalled("iterator");

		jsonConverterSpy.MCR.assertNumberOfCallsToMethod("toInstance", 4);

		IteratorSpy iteratorSpy = (IteratorSpy) dataArraySpy.MCR.getReturnValue("iterator", 0);

		jsonConverterSpy.MCR.assertParameters("toInstance", 1,
				iteratorSpy.MCR.getReturnValue("next", 1));
		jsonConverterSpy.MCR.assertParameters("toInstance", 2,
				iteratorSpy.MCR.getReturnValue("next", 2));

	}

	@Test
	public void testReadRecordAndStoreJson() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String id = "someId";
		fixture.setId(id);

		String responseText = fixture.testReadAndStoreRecord();
		assertTrue(recordHandler.readRecordWasCalled);

		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, authToken);

		assertCorrectDataPassedFromHandlerToConverter();
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testSearchAndStoreRecords() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.setSearchId("someSearch");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		fixture.testSearchAndStoreRecords();
		assertTrue(recordHandler.searchRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/searchResult/someSearch";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.json, json);
	}

	@Test
	public void testSearchAndStoreCorrectRecordsInDataHolder() throws UnsupportedEncodingException {
		fixture.testSearchAndStoreRecords();

		String jsonListFromRecordHandler = recordHandler.jsonToReturnDefault;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonListFromRecordHandler);

		JsonObjectSpy listObjectFromSpy = assertObjectForKeyDataListIsExtracted();

		JsonObjectSpy dataList = assertObjectForKeyDataIsExtracted(listObjectFromSpy);

		assertAllRecordsInDataAreConverted(dataList);

		assertConvertedRecordsAreAddedToRecordHolder();
	}

	@Test
	public void testSearchAndStoreRecordAsSpecifiedInIndex() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.setIndexToStore(2);
		fixture.testSearchAndStoreRecords();

		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(2));
	}

	@Test
	public void testSearchAndStoreRecordWhenNoSpecifiedIndexUsingZeroAsDefault()
			throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.testSearchAndStoreRecords();

		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(0));
	}

	@Test
	public void testUpdateAndStoreRecord() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);

		fixture.setId("someId");
		String responseText = fixture.testUpdateAndStoreRecord();
		assertTrue(recordHandler.updateRecordWasCalled);

		assertCorrectValuesSentToRecordHandler(authToken, json);

		assertCorrectDataPassedFromHandlerToConverter();
		assertCorrectValuesSentToRecordHandler(authToken, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	private void assertCorrectDataPassedFromHandlerToConverter() {
		String jsonFromRecordHandler = recordHandler.jsonToReturnDefault;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonFromRecordHandler);
		assertSame(jsonToDataConverter.jsonObjects.get(0), jsonParser.jsonObjectSpies.get(0));
		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(0));
	}

	@Test
	public void testUpdateAndStoreForbiddenRecord() throws UnsupportedEncodingException {
		DataHolder.setRecord(new ClientDataRecordSpy());
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "Forbidden answer from server";
		fixture.setJson(json);
		recordHandler.statusTypeReturned = 403;
		jsonParser.throwException = true;
		fixture.setId("someId");

		String responseText = fixture.testUpdateAndStoreRecord();

		assertTrue(recordHandler.updateRecordWasCalled);
		assertCorrectValuesSentToRecordHandler(authToken, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
		assertNull(DataHolder.getRecord());
	}

	@Test
	public void testCreateAndStoreRecord() throws UnsupportedEncodingException {
		setupForCreate();

		String responseText = fixture.testCreateAndStoreRecord();

		assertTrue(recordHandler.createRecordWasCalled);
		assertCorrectValuesSentToRecordHandler(authToken, json);
		assertCorrectDataPassedFromHandlerToConverter();
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	private void setupForCreate() {
		authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		json = "{\"name\":\"value\"}";
		fixture.setJson(json);
	}

	private void assertCorrectValuesSentToRecordHandler(String authToken, String json) {
		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.json, json);
	}

	@Test
	public void testGetStatusTypeOnCreateStatusCREATED() throws Exception {
		setupForCreate();
		recordHandler.statusTypeReturned = 201;

		fixture.testCreateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetStatusTypeOnCreateStatusBAD_REQUEST() throws Exception {
		setupForCreate();
		recordHandler.statusTypeReturned = 400;

		fixture.testCreateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testCreateAndStoreForbiddenRecord() throws UnsupportedEncodingException {
		DataHolder.setRecord(new ClientDataRecordSpy());
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "Forbidden answer from server";
		fixture.setJson(json);
		recordHandler.statusTypeReturned = 403;
		jsonParser.throwException = true;
		fixture.setId("someId");

		String responseText = fixture.testCreateAndStoreRecord();

		assertTrue(recordHandler.createRecordWasCalled);
		assertCorrectValuesSentToRecordHandler(authToken, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
		assertNull(DataHolder.getRecord());
	}

	@Test
	public void testGetStatusTypeOnUpdateStatusCREATED() throws Exception {
		setupForCreate();
		recordHandler.statusTypeReturned = 201;

		fixture.testUpdateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetStatusTypeOnUpdateStatusBAD_REQUEST() throws Exception {
		setupForCreate();
		recordHandler.statusTypeReturned = 400;

		fixture.testUpdateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetCreatedId() throws Exception {
		setupForCreate();
		String expectedCreatedId = "myCreatedId";
		recordHandler.createdId = expectedCreatedId;

		fixture.testCreateAndStoreRecord();

		assertEquals(fixture.getCreatedId(), expectedCreatedId);
	}

}
