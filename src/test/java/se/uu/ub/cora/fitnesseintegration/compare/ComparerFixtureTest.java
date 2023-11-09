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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy;
import se.uu.ub.cora.fitnesseintegration.JsonParserSpy;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestClientFactoryImp;

public class ComparerFixtureTest {
	JsonToClientDataConverterFactorySpy converterToClientFactorySpy;

	private ComparerFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonParserSpy jsonParser;
	private String type;
	private String authToken;
	private String json;

	@BeforeMethod
	public void setUp() {
		converterToClientFactorySpy = new JsonToClientDataConverterFactorySpy();
		JsonToClientDataConverterProvider
				.setJsonToDataConverterFactory(converterToClientFactorySpy);

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

		type = "someRecordType";
		fixture.setType(type);
		fixture.setRecordHandler(recordHandler);
	}

	@Test
	public void testInit() {
		fixture = new ComparerFixture();
		assertTrue(fixture.onlyForTestGetHttpHandlerFactory() instanceof HttpHandlerFactorySpy);
		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(),
				fixture.onlyForTestGetHttpHandlerFactory());

		RestClientFactoryImp clientFactory = (RestClientFactoryImp) recordHandler
				.getRestClientFactory();
		assertEquals(clientFactory.onlyForTestGetBaseUrl(), fixture.baseUrl);
	}

	@Test
	public void testReadRecordListAndStoreRecordsNoFilter() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.testReadRecordListAndStoreRecords();

		assertTrue(recordHandler.readRecordListWasCalled);
		assertEquals(recordHandler.recordType, type);
		assertEquals(fixture.getStoredListAsJson(), recordHandler.jsonToReturnDefault);
		assertEquals(recordHandler.authToken, authToken);
		assertNull(recordHandler.filter);
		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
	}

	private ClientDataListSpy setupConverterToClientFactorySpyToReturnClientDataListSpy() {
		ClientDataRecord clientDataRecord1 = new ClientDataRecordSpy();
		ClientDataRecord clientDataRecord2 = new ClientDataRecordSpy();
		ClientDataRecord clientDataRecord3 = new ClientDataRecordSpy();

		return setupConverterToClientFactorySpyToReturnClientDataListSpyWithRecords(
				clientDataRecord1, clientDataRecord2, clientDataRecord3);
	}

	private ClientDataListSpy setupConverterToClientFactorySpyToReturnClientDataListSpyWithRecords(
			ClientDataRecord... clientDataRecords) {
		ClientDataListSpy clientDataListSpy = new ClientDataListSpy();
		clientDataListSpy.MRV.setDefaultReturnValuesSupplier("getDataList",
				() -> List.of(clientDataRecords));

		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance", () -> clientDataListSpy);

		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
		return clientDataListSpy;
	}

	private List<ClientData> assertListOfRecordsAreConvertedAndStoredInDataHolder(
			ClientDataListSpy clientDataListSpy) {
		List<ClientDataRecord> recordList = DataHolder.getRecordList();
		assertEquals(recordList.size(), 3);
		List<ClientData> listFromSpy = (List<ClientData>) clientDataListSpy.MCR
				.getReturnValue("getDataList", 0);
		assertSame(recordList.get(0), listFromSpy.get(0));
		assertSame(recordList.get(1), listFromSpy.get(1));
		assertSame(recordList.get(2), listFromSpy.get(2));
		return listFromSpy;
	}

	@Test
	public void testReadRecordListAndStoreRecordsWithFilter() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
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
		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
	}

	@Test
	public void testReadRecordListAndStoreRecordsInDataHolder() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();

		fixture.testReadRecordListAndStoreRecords();

		String jsonListFromRecordHandler = recordHandler.jsonToReturnDefault;
		converterToClientFactorySpy.MCR.assertParameters("factorUsingString", 0,
				jsonListFromRecordHandler);
		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);

		assertEquals(DataHolder.getRecord(), listFromSpy.get(0));
	}

	@Test
	public void testReadRecordListAndStoreRecordAsSpecifiedInIndex() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.setIndexToStore(2);
		fixture.testReadRecordListAndStoreRecords();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);

		assertEquals(DataHolder.getRecord(), listFromSpy.get(2));
	}

	@Test
	public void testReadRecordListAndStoreRecordWhenNoSpecifiedIndexUsingZeroAsDefault()
			throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.testReadRecordListAndStoreRecords();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);

		assertEquals(DataHolder.getRecord(), listFromSpy.get(0));
	}

	@Test
	public void testReadRecordListAndStoreRecordByIdRecordNotFound() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		DataHolder.setRecord(new ClientDataRecordSpy());
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		String response = fixture.testReadRecordListAndStoreRecordById();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
		ClientDataRecord record = DataHolder.getRecord();
		assertNull(record);
	}

	@Test
	public void testReadRecordListAndStoreRecordById1750() throws Exception {
		ClientDataRecord clientDataRecord1 = new ClientDataRecordSpy();
		ClientDataRecord clientDataRecord2 = new ClientDataRecordSpy();
		ClientDataRecordSpy clientDataRecord3 = new ClientDataRecordSpy();
		clientDataRecord3.MRV.setDefaultReturnValuesSupplier("getId", () -> "1750");
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpyWithRecords(
				clientDataRecord1, clientDataRecord2, clientDataRecord3);
		String authToken = "someAuthToken";
		String recordId = "1750";

		fixture.setIdToStore(recordId);
		fixture.setAuthToken(authToken);
		String recordListAsJson = fixture.testReadRecordListAndStoreRecordById();

		recordHandler.MCR.assertParameters("readRecordList", 0, authToken, type, null);

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
		ClientDataRecord record = DataHolder.getRecord();
		assertSame(clientDataRecord3, record);
	}

	@Test
	public void testReadRecordAndStoreJson() throws Exception {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		String authToken = "someAuthToken";
		String id = "someId";

		fixture.setAuthToken(authToken);
		fixture.setId(id);
		String responseText = fixture.testReadAndStoreRecord();

		assertTrue(recordHandler.readRecordWasCalled);
		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);

		assertEquals(clientDataRecord, DataHolder.getRecord());
	}

	private void setupConverterToClientFactorySpyToReturnClientRecordSpy(
			ClientDataRecordSpy clientDataRecord) {
		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance", () -> clientDataRecord);

		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	@Test
	public void testSearchAndStoreRecords() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";
		String json = "{\"name\":\"value\"}";

		fixture.setAuthToken(authToken);
		fixture.setSearchId("someSearch");
		fixture.setJson(json);
		fixture.testSearchAndStoreRecords();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
		assertTrue(recordHandler.searchRecordWasCalled);

		String expectedUrl = SystemUrl.getUrl() + "rest/record/searchResult/someSearch";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, authToken);
		assertEquals(recordHandler.json, json);
	}

	@Test
	public void testSearchAndStoreRecordAsSpecifiedInIndex() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";

		fixture.setAuthToken(authToken);
		fixture.setIndexToStore(2);
		fixture.testSearchAndStoreRecords();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
		assertSame(DataHolder.getRecord(), listFromSpy.get(2));
	}

	@Test
	public void testSearchAndStoreRecordWhenNoSpecifiedIndexUsingZeroAsDefault() throws Exception {
		ClientDataListSpy clientDataListSpy = setupConverterToClientFactorySpyToReturnClientDataListSpy();
		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		fixture.testSearchAndStoreRecords();

		List<ClientData> listFromSpy = assertListOfRecordsAreConvertedAndStoredInDataHolder(
				clientDataListSpy);
		assertSame(DataHolder.getRecord(), listFromSpy.get(0));
	}

	@Test
	public void testUpdateAndStoreRecord() throws UnsupportedEncodingException {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		String authToken = "someAuthToken";
		String json = "{\"name\":\"value\"}";

		fixture.setAuthToken(authToken);
		fixture.setJson(json);
		fixture.setId("someId");
		String responseText = fixture.testUpdateAndStoreRecord();

		assertTrue(recordHandler.updateRecordWasCalled);

		assertCorrectValuesSentToRecordHandler(authToken, json);

		assertEquals(responseText, recordHandler.jsonToReturnDefault);

		assertEquals(clientDataRecord, DataHolder.getRecord());
	}

	@Test
	public void testUpdateAndStoreForbiddenRecord() throws UnsupportedEncodingException {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		DataHolder.setRecord(clientDataRecord);
		String authToken = "someAuthToken";
		String json = "Forbidden answer from server";
		recordHandler.statusTypeReturned = 403;

		fixture.setAuthToken(authToken);
		fixture.setJson(json);
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
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);

		String responseText = fixture.testCreateAndStoreRecord();

		assertTrue(recordHandler.createRecordWasCalled);
		assertCorrectValuesSentToRecordHandler(authToken, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
		assertEquals(clientDataRecord, DataHolder.getRecord());
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
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		recordHandler.statusTypeReturned = 201;

		fixture.testCreateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetStatusTypeOnCreateStatusBAD_REQUEST() throws Exception {
		setupForCreate();
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		recordHandler.statusTypeReturned = 400;

		fixture.testCreateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
		assertNull(DataHolder.getRecord());
	}

	@Test
	public void testCreateAndStoreForbiddenRecord() throws UnsupportedEncodingException {
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		DataHolder.setRecord(clientDataRecord);

		String authToken = "someAuthToken";
		fixture.setAuthToken(authToken);
		String json = "Forbidden answer from server";
		fixture.setJson(json);
		recordHandler.statusTypeReturned = 403;
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
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		recordHandler.statusTypeReturned = 201;

		fixture.testUpdateAndStoreRecord();

		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetStatusTypeOnUpdateStatusBAD_REQUEST() throws Exception {
		setupForCreate();
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		recordHandler.statusTypeReturned = 400;

		fixture.testUpdateAndStoreRecord();
		StatusType statusType = fixture.getStatusType();

		assertEquals(statusType.getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testGetCreatedId() throws Exception {
		setupForCreate();
		ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
		setupConverterToClientFactorySpyToReturnClientRecordSpy(clientDataRecord);
		String expectedCreatedId = "myCreatedId";
		recordHandler.createdId = expectedCreatedId;

		fixture.testCreateAndStoreRecord();

		assertEquals(fixture.getCreatedId(), expectedCreatedId);
	}

}
