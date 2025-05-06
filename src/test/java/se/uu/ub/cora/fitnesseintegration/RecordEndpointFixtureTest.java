/*
 * Copyright 2015, 2016, 2019, 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.script.AuthTokenHolder;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RecordEndpointFixtureTest {
	private static final String SOME_FILTER = "some filter";
	private static final String SOME_RECORD_TYPE = "someType";
	private static final String SOME_AUTH_TOKEN = "someToken";
	private JsonToClientDataConverterFactorySpy converterToClientFactorySpy;
	private RecordEndpointFixture fixture;
	private HttpHandlerFactoryOldSpy httpHandlerFactorySpy;
	private RecordHandlerOLDSpy oldRecordHandlerSpy;
	private RecordHandlerSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		converterToClientFactorySpy = new JsonToClientDataConverterFactorySpy();
		JsonToClientDataConverterProvider
				.setJsonToDataConverterFactory(converterToClientFactorySpy);

		SystemUrl.setUrl("http://localhost:8080/therest/");
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8080/appTokenVerifier/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");

		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactoryOldSpy");
		DependencyProvider.setChildComparerUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
		httpHandlerFactorySpy = (HttpHandlerFactoryOldSpy) DependencyProvider.getHttpHandlerFactory();

		recordHandler = new RecordHandlerSpy();
		oldRecordHandlerSpy = new RecordHandlerOLDSpy();
		fixture = new RecordEndpointFixture();
		fixture.onlyForTestSetRecordHandler(oldRecordHandlerSpy);
	}

	@Test
	public void testInit() {
		fixture = new RecordEndpointFixture();

		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactoryOldSpy);
		assertTrue(fixture.getChildComparer() instanceof ChildComparerSpy);

		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertEquals(recordHandler.onlyForTestGetBaseUrl(), SystemUrl.getUrl() + "rest/");
		assertEquals(recordHandler.onlyForTestGetAppTokenUrl(), SystemUrl.getAppTokenVerifierUrl());
	}

	@Test
	public void testReadRecordClientDataRecordHandlerIsOk() {
		String type = SOME_RECORD_TYPE;
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String responseText = fixture.testReadRecord();

		assertEquals(oldRecordHandlerSpy.recordType, type);
		assertEquals(oldRecordHandlerSpy.recordId, id);
		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);

	}

	@Test
	public void testReadRecordStatusTypeFromRecordHandlerUsed() {
		assertEquals(fixture.testReadRecord(), "some json returned from spy");
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testReadRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testReadRecord();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testReadIncomingLinksDataForRecordHandlerIsOk() {
		String type = SOME_RECORD_TYPE;
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String responseText = fixture.testReadIncomingLinks();

		assertEquals(oldRecordHandlerSpy.recordType, type);
		assertEquals(oldRecordHandlerSpy.recordId, id);
		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);
	}

	@Test
	public void testReadIncomingLinksStatusTypeFromRecordHandlerUsed() {
		assertEquals(fixture.testReadIncomingLinks(), "some json returned from spy");
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testReadIncomingLinksAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testReadIncomingLinks();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testReadRecordListClientDataRecordHandlerIsOk()
			throws UnsupportedEncodingException {
		String type = SOME_RECORD_TYPE;
		fixture.setType(type);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = SOME_FILTER;
		fixture.setJson(json);

		String responseText = fixture.testReadRecordList();
		assertEquals(oldRecordHandlerSpy.recordType, type);
		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(oldRecordHandlerSpy.filter, json);
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);

	}

	@Test
	public void testReadRecordListStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testReadRecordListAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testCreateRecordDataOk() {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();

		RestResponse restResponseToReturn = new RestResponse(201, "someJson", Optional.empty(),
				Optional.of("someCreatedId"));
		recordHandler.MRV.setDefaultReturnValuesSupplier("createRecord",
				() -> restResponseToReturn);
		fixture.onlyForTestSetRecordHandler(recordHandler);

		String type = "autogeneratedIdType";
		fixture.setType(type);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);

		String createdRecord = fixture.testCreateRecord();

		recordHandler.MCR.assertParameters("createRecord", 0, SOME_AUTH_TOKEN, type, json);
		RestResponse restResponse = (RestResponse) recordHandler.MCR.getReturnValue("createRecord",
				0);
		assertEquals(createdRecord, restResponse.responseText());
		assertEquals(fixture.getCreatedId(), "someCreatedId");
	}

	@Test
	public void testCreateRecordNotOk() {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();
		fixture.onlyForTestSetRecordHandler(recordHandler);

		fixture.setType(SOME_RECORD_TYPE);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);

		String createdRecord = fixture.testCreateRecord();

		recordHandler.MCR.assertParameters("createRecord", 0, SOME_AUTH_TOKEN, SOME_RECORD_TYPE,
				json);
		RestResponse restResponse = (RestResponse) recordHandler.MCR.getReturnValue("createRecord",
				0);
		assertEquals(createdRecord, restResponse.responseText());
		assertNull(fixture.getCreatedId());
	}

	@Test
	public void testCreatRecordStoreInDataHolder() throws Exception {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();

		RestResponse restResponseToReturn = new RestResponse(201, "someJson", Optional.empty(),
				Optional.of("someCreatedId"));
		recordHandler.MRV.setDefaultReturnValuesSupplier("createRecord",
				() -> restResponseToReturn);
		fixture.onlyForTestSetRecordHandler(recordHandler);

		String type = "autogeneratedIdType";
		fixture.setType(type);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);

		String createdRecord = fixture.testCreateRecord();

		assertEquals(DataHolder.getRecordAsJson(), createdRecord);

	}

	@Test
	public void testCreateRecordReturnedResponseTextSameAsInRecordHandler() {
		String responseText = fixture.testCreateRecord();
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);
	}

	@Test
	public void testCreateRecordAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testCreateRecord();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testCreateRecordCreatedTypeOk() {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();
		String jsonToReturnDefault = """
				{
				  "record": {
				    "data": {
				      "children": [
				        {
				          "children": [
				            {
				              "children": [
				                {
				                  "name": "linkedRecordType",
				                  "value": "recordType"
				                },
				                {
				                  "name": "linkedRecordId",
				                  "value": "someRecordType"
				                }
				              ],
				              "name": "type"
				            }
				          ],
				          "name": "recordInfo"
				        }
				      ],
				      "name": "binary"
				    }
				  }
				}
								""";

		RestResponse restResponseToReturn = new RestResponse(201, jsonToReturnDefault,
				Optional.empty(), Optional.of("someCreatedId"));
		recordHandler.MRV.setDefaultReturnValuesSupplier("createRecord",
				() -> restResponseToReturn);

		fixture.onlyForTestSetRecordHandler(recordHandler);

		fixture.setType("someRecordType");
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);

		String createdType = fixture.testCreateRecordCreatedType();

		recordHandler.MCR.assertParameters("createRecord", 0, SOME_AUTH_TOKEN, "someRecordType",
				json);
		assertEquals(createdType, "someRecordType");
		assertEquals(fixture.getCreatedId(), "someCreatedId");
	}

	@Test
	public void testBatchIndexingFetchesDataFromRecordHandle3333r() {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();
		RestResponse restResponseToReturn = new RestResponse(200, "someResponseText",
				Optional.empty(), Optional.of("someCreatedId"));
		recordHandler.MRV.setDefaultReturnValuesSupplier("batchIndex", () -> restResponseToReturn);
		fixture.onlyForTestSetRecordHandler(recordHandler);

		String authToken = SOME_AUTH_TOKEN;
		String recordType = SOME_RECORD_TYPE;
		String filterAsJson = SOME_FILTER;

		fixture.setAuthToken(authToken);
		fixture.setType(recordType);
		fixture.setJson(filterAsJson);
		String responseBatchIndex = fixture.testBatchIndexing();

		recordHandler.MCR.assertParameters("batchIndex", 0, authToken, recordType, filterAsJson);
		assertEquals(fixture.getCreatedId(), "someCreatedId");
		assertEquals(fixture.getStatusType(), Response.Status.fromStatusCode(200));
		assertEquals(responseBatchIndex, restResponseToReturn.responseText());
	}

	@Test
	public void testCreateRecordCreatedTypeNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		oldRecordHandlerSpy.statusTypeReturned = 401;

		assertEquals(fixture.testCreateRecordCreatedType(),
				oldRecordHandlerSpy.jsonToReturnDefault);
	}

	@Test
	public void testCreateRecordCreatedTypeNotFoundInJson() {
		oldRecordHandlerSpy.jsonToReturnDefault = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.cora.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"someId\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"someRecordTypeAttribute\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.cora.record+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.cora.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.cora.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\"}}}}";
		fixture.setType("someWrongRecordTypeWrongJson");
		String createdType = fixture.testCreateRecordCreatedType();
		assertEquals(createdType, "");
	}

	@Test
	public void testUpdateRecordDataForRecordHandlerIsOk() {
		String type = SOME_RECORD_TYPE;
		String id = "someId";

		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		fixture.testUpdateRecord();

		assertTrue(oldRecordHandlerSpy.updateRecordWasCalled);

		assertEquals(oldRecordHandlerSpy.recordType, type);
		assertEquals(oldRecordHandlerSpy.recordId, id);
		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(oldRecordHandlerSpy.json, json);

	}

	@Test
	public void testUpdateRecordReturnedResponseTextSameAsInRecordHandler() {
		String responseText = fixture.testUpdateRecord();
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);
	}

	@Test
	public void testUpdateRecordSetsStatusType() {
		fixture.testUpdateRecord();
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testUpdateRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testUpdateRecord();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testDeleteRecordClientDataRecordHandlerIsOk() {
		String type = SOME_RECORD_TYPE;
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		String responseText = fixture.testDeleteRecord();

		assertTrue(oldRecordHandlerSpy.deleteRecordWasCalled);
		assertEquals(oldRecordHandlerSpy.recordType, type);
		assertEquals(oldRecordHandlerSpy.recordId, id);
		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);
	}

	@Test
	public void testDeleteRecordStatusTypeFromRecordHandlerUsed() {
		fixture.testDeleteRecord();
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testDeleteRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testDeleteRecord();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testDownloadDataForFactoryIsOk() {
		fixture.setType(SOME_RECORD_TYPE);
		fixture.setId("someId");
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setResourceName("someResourceName");
		fixture.testDownload();
		HttpHandlerOldSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), SOME_AUTH_TOKEN);
		assertEquals(httpHandlerSpy.requestProperties.size(), 1);

		assertEquals(fixture.getContentLength(), "9999");
		assertEquals(fixture.getContentDisposition(),
				"form-data; name=\"file\"; filename=\"adele.png\"\n");

		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId/someResourceName");
	}

	@Test
	public void testDownloadOk() {
		assertEquals(fixture.testDownload(), "Everything ok");
	}

	@Test
	public void testDownloadNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testDownload(), "bad things happend");
	}

	@Test
	public void testSearchRecordDataForRecordHandlerIsOk() throws UnsupportedEncodingException {
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setSearchId("aSearchId");

		String json = "{\"name\":\"search\",\"children\":[{\"name\":\"include\",\"children\":["
				+ "{\"name\":\"includePart\",\"children\":[{\"name\":\"text\",\"value\":\"\"}]}]}]}";
		fixture.setJson(json);

		String responseText = fixture.testSearchRecord();

		assertEquals(oldRecordHandlerSpy.authToken, SOME_AUTH_TOKEN);
		assertEquals(oldRecordHandlerSpy.json, json);
		assertEquals(responseText, oldRecordHandlerSpy.jsonToReturnDefault);

	}

	@Test
	public void testSearchRecordStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testSearchRecord();
		assertEquals(fixture.getStatusType().getStatusCode(),
				oldRecordHandlerSpy.statusTypeReturned);
	}

	@Test
	public void testSearchRecordAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testSearchRecord();
		assertEquals(oldRecordHandlerSpy.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testSetJsonObject() {
		fixture.setType("metadataGroup");
		fixture.setId("someMetadataGroupId");
		fixture.setAuthToken(SOME_AUTH_TOKEN);

		setupConverterToClientFactorySpyToReturnClientDataRecordSpy();

		fixture.testReadRecordAndStoreJson();

		converterToClientFactorySpy.MCR.assertParameters("factorUsingString", 0,
				"some json returned from spy");
		JsonToClientDataConverterSpy toClientConverter = (JsonToClientDataConverterSpy) converterToClientFactorySpy.MCR
				.getReturnValue("factorUsingString", 0);
		toClientConverter.MCR.assertReturn("toInstance", 0, DataHolder.getRecord());
	}

	private void setupConverterToClientFactorySpyToReturnClientDataRecordSpy() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance", () -> clientDataRecordSpy);
		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	@Test
	public void testBatchIndexingFetchesDataFromRecordHandler() {
		RecordHandlerSpy recordHandler = new RecordHandlerSpy();
		RestResponse restResponseToReturn = new RestResponse(200, "someResponseText",
				Optional.empty(), Optional.of("someCreatedId"));
		recordHandler.MRV.setDefaultReturnValuesSupplier("batchIndex", () -> restResponseToReturn);
		fixture.onlyForTestSetRecordHandler(recordHandler);

		String authToken = SOME_AUTH_TOKEN;
		String recordType = SOME_RECORD_TYPE;
		String filterAsJson = SOME_FILTER;

		fixture.setAuthToken(authToken);
		fixture.setType(recordType);
		fixture.setJson(filterAsJson);
		String responseBatchIndex = fixture.testBatchIndexing();

		recordHandler.MCR.assertParameters("batchIndex", 0, authToken, recordType, filterAsJson);
		assertEquals(fixture.getCreatedId(), "someCreatedId");
		assertEquals(fixture.getStatusType(), Response.Status.fromStatusCode(200));
		assertEquals(responseBatchIndex, restResponseToReturn.responseText());
	}

	@Test
	public void testBatchIndexingAdminAuthTokenUsedWhenNoAuthTokenSet() {
		recordHandler.MRV.setDefaultReturnValuesSupplier("batchIndex",
				() -> new RestResponse(500, "someText", Optional.empty(), Optional.empty()));
		fixture.onlyForTestSetRecordHandler(recordHandler);

		fixture.testBatchIndexing();

		recordHandler.MCR.assertParameter("batchIndex", 0, "authToken",
				AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testBatchIndexingReturnsResponseText() {

		recordHandler.MRV.setDefaultReturnValuesSupplier("batchIndex",
				() -> new RestResponse(201, "someText", Optional.empty(), Optional.of("someId")));
		fixture.onlyForTestSetRecordHandler(recordHandler);

		String responseText = fixture.testBatchIndexing();

		RestResponse response = (RestResponse) recordHandler.MCR.getReturnValue("batchIndex", 0);
		assertEquals(responseText, response.responseText());
	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedReadsRecord() throws InterruptedException {
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setType("indexBatchJob");
		fixture.setId("indexBatchJob:12345");
		fixture.setSleepTime(0);
		fixture.setMaxNumberOfReads(1);

		setupConverterToClientFactorySpyToReturnClientDataRecordSpy();

		fixture.waitUntilIndexBatchJobIsFinished();

		assertEquals(oldRecordHandlerSpy.MCR.getNumberOfCallsToMethod("readRecord"), 1);
		Map<String, Object> parametersForReadRecord = oldRecordHandlerSpy.MCR
				.getParametersForMethodAndCallNumber("readRecord", 0);

		assertSame(parametersForReadRecord.get("authToken"), SOME_AUTH_TOKEN);
		assertSame(parametersForReadRecord.get("recordType"), "indexBatchJob");
		assertSame(parametersForReadRecord.get("recordId"), "indexBatchJob:12345");

	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedStoresJson() throws InterruptedException {
		fixture.setSleepTime(0);
		fixture.setMaxNumberOfReads(1);

		setUpFixtureAndSpiesWithCallsUntilFinished(7);

		fixture.waitUntilIndexBatchJobIsFinished();

		converterToClientFactorySpy.MCR.assertParameters("factorUsingString", 0,
				"some json returned from spy");
		JsonToClientDataConverterSpy toClientConverter = (JsonToClientDataConverterSpy) converterToClientFactorySpy.MCR
				.getReturnValue("factorUsingString", 0);
		toClientConverter.MCR.assertReturn("toInstance", 0, DataHolder.getRecord());
	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedStopsAtMaxNumberOfReads()
			throws InterruptedException {
		setUpFixtureAndSpiesWithCallsUntilFinished(7);
		int sleepTime = 1;
		fixture.setSleepTime(sleepTime);
		int maxNumberOfReads = 5;
		fixture.setMaxNumberOfReads(maxNumberOfReads);
		String expectedResponseText = "Tried to read indexBatchJob " + maxNumberOfReads
				+ " times, waiting " + sleepTime
				+ " milliseconds between each read, but it was still not finished.";
		oldRecordHandlerSpy.jsonToReturnDefault = expectedResponseText;

		String responseText = fixture.waitUntilIndexBatchJobIsFinished();

		assertEquals(oldRecordHandlerSpy.MCR.getNumberOfCallsToMethod("readRecord"), 5);
		assertFalse(oldRecordHandlerSpy.deleteRecordWasCalled);
		assertEquals(responseText, expectedResponseText);
	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedStopsWhenJobFinished() throws InterruptedException {
		setUpFixtureAndSpiesWithCallsUntilFinished(3);
		fixture.setSleepTime(0);
		fixture.setMaxNumberOfReads(5);

		fixture.waitUntilIndexBatchJobIsFinished();

		assertEquals(oldRecordHandlerSpy.MCR.getNumberOfCallsToMethod("readRecord"), 3);
	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedDoesNotDeleteJobWhenFinished()
			throws InterruptedException {
		setUpFixtureAndSpiesWithCallsUntilFinished(1);
		fixture.setSleepTime(0);
		fixture.setMaxNumberOfReads(5);

		String responseText = fixture.waitUntilIndexBatchJobIsFinished();

		assertEquals(oldRecordHandlerSpy.MCR.getNumberOfCallsToMethod("readRecord"), 1);
		assertFalse(oldRecordHandlerSpy.deleteRecordWasCalled);
		assertEquals(responseText, "finished");
	}

	@Test
	public void waitUntilIndexBatchJobIsFinishedWaitsSleepTimeInEachLoopIfNotFinished()
			throws InterruptedException {
		setUpFixtureAndSpiesWithCallsUntilFinished(2);

		int sleepTime = 1000;
		int maxNumberOfReads = 3;
		fixture.setSleepTime(sleepTime);
		fixture.setMaxNumberOfReads(maxNumberOfReads);

		Instant start = Instant.now();
		fixture.waitUntilIndexBatchJobIsFinished();
		Instant end = Instant.now();
		Duration timeElapsed = Duration.between(start, end);

		assertEquals(oldRecordHandlerSpy.MCR.getNumberOfCallsToMethod("readRecord"), 2);
		assertEquals(timeElapsed.toSeconds(), sleepTime / 1000);
	}

	private void setUpFixtureAndSpiesWithCallsUntilFinished(int callsUntilFinished) {
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setType("indexBatchJob");
		fixture.setId("indexBatchJob:12345");

		setUpConverterFromJsonToReturnNameInDataForClientDataRecordGroup(callsUntilFinished);
	}

	private void setUpConverterFromJsonToReturnNameInDataForClientDataRecordGroup(
			int callsUntilFinished) {
		ClientDataRecordSpy clientDataRecordFinishedSpy = setupConverterFromJsonToREturnStatusForClientDataRecordGroup(
				"finished");

		ClientDataRecordSpy clientDataRecordNotFinishedSpy = setupConverterFromJsonToREturnStatusForClientDataRecordGroup(
				"notFinished");

		List<Object> of = new ArrayList<>();
		while (of.size() + 1 < callsUntilFinished) {
			of.add(clientDataRecordNotFinishedSpy);
		}
		of.add(clientDataRecordFinishedSpy);

		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setReturnValues("toInstance", of);
		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	private ClientDataRecordSpy setupConverterFromJsonToREturnStatusForClientDataRecordGroup(
			String status) {
		ClientDataRecordGroupSpy clientDataRecordGroupFinished = new ClientDataRecordGroupSpy();
		clientDataRecordGroupFinished.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", () -> status, "status");
		ClientDataRecordSpy clientDataRecordFinishedSpy = new ClientDataRecordSpy();
		clientDataRecordFinishedSpy.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> clientDataRecordGroupFinished);
		return clientDataRecordFinishedSpy;
	}

	// @Test
	// public void testWaitUntilImageIsAnalyzed() throws Exception {
	// WaiterSpy waiter = new WaiterSpy();
	// DependencyProvider.onlyForTestSetWaiter(waiter);
	//
	// fixture.waitUntilImageIsAnalyzed();
	//
	// waiter.MCR.assertMethodWasCalled("waitUntilReadGetsTrueForSupplier");
	// var methodToRun = waiter.MCR.getValueForMethodNameAndCallNumberAndParameterName(
	// "waitUntilReadGetsTrueForSupplier", 0, "methodToRun");
	//
	// assertTrue(methodToRun instanceof MethodToRunImp);
	// System.out.println(methodToRun.getClass());
	//
	// DependencyProvider.onlyForTestSetWaiter(null);
	// }

}
