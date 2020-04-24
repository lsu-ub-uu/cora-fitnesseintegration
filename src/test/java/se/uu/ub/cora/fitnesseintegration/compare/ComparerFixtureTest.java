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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy;
import se.uu.ub.cora.fitnesseintegration.IteratorSpy;
import se.uu.ub.cora.fitnesseintegration.JsonArraySpy;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.JsonObjectSpy;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.JsonToDataRecordConverterSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;

public class ComparerFixtureTest {

	private ComparerFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonToDataRecordConverterSpy jsonToDataConverter;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;

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
		jsonToDataConverter = new JsonToDataRecordConverterSpy();

		fixture.setType("someRecordType");
		fixture.setRecordHandler(recordHandler);
		fixture.setJsonHandler(jsonHandler);
		fixture.setJsonToDataRecordConverter(jsonToDataConverter);
	}

	@Test
	public void testInit() {
		fixture = new ComparerFixture();
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
	public void testReadRecordAndStoreJson() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.setId("someId");

		String responseText = fixture.testReadAndStoreRecord();
		assertTrue(recordHandler.readRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType/someId";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, authToken);

		assertCorrectDataPassedFromHandlerToConverter();
		assertEquals(responseText, recordHandler.jsonToReturn);
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
		assertCorrectValuesSentToRecordHandler(authToken, json, expectedUrl);
	}

	@Test
	public void testSearchAndStoreCorrectRecordsInDataHolder() throws UnsupportedEncodingException {
		fixture.testSearchAndStoreRecords();

		String jsonListFromRecordHandler = recordHandler.jsonToReturn;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonListFromRecordHandler);

		JsonObjectSpy listObjectFromSpy = assertObjectForKeyDataListIsExtracted();

		JsonObjectSpy dataList = assertObjectForKeyDataIsExtracted(listObjectFromSpy);

		assertAllRecordsInDataAreConverted(dataList);

		assertConvertedRecordsAreAddedToRecordHolder();
	}

	@Test
	public void testUpdateAndStoreRecord() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "{\"name\":\"value\"}";
		fixture.setId("someId");
		fixture.setJson(json);
		String responseText = fixture.testUpdateAndStoreRecord();
		assertTrue(recordHandler.updateRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType/someId";
		assertCorrectValuesSentToRecordHandler(authToken, json, expectedUrl);

		assertCorrectDataPassedFromHandlerToConverter();
		assertCorrectValuesSentToRecordHandler(authToken, json, expectedUrl);
		assertEquals(responseText, recordHandler.jsonToReturn);
	}

	private void assertCorrectDataPassedFromHandlerToConverter() {
		String jsonFromRecordHandler = recordHandler.jsonToReturn;
		String jsonListSentToParser = jsonParser.jsonStringsSentToParser.get(0);
		assertEquals(jsonListSentToParser, jsonFromRecordHandler);
		assertSame(jsonToDataConverter.jsonObjects.get(0), jsonParser.jsonObjectSpies.get(0));
		assertSame(DataHolder.getRecord(), jsonToDataConverter.returnedSpies.get(0));
	}

	@Test
	public void testCreateAndStoreRecord() throws UnsupportedEncodingException {
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		String responseText = fixture.testCreateAndStoreRecord();
		assertTrue(recordHandler.createRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someRecordType";
		assertCorrectValuesSentToRecordHandler(authToken, json, expectedUrl);

		assertCorrectDataPassedFromHandlerToConverter();
		assertEquals(responseText, recordHandler.jsonToReturn);
	}

	private void assertCorrectValuesSentToRecordHandler(String authToken, String json,
			String expectedUrl) {
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.json, json);
	}

}
