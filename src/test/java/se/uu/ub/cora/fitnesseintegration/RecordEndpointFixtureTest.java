/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.javaclient.rest.RestClientFactoryImp;

public class RecordEndpointFixtureTest {
	private RecordEndpointFixture fixture;
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private RecordHandlerSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		DependencyProvider.setJsonToDataFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.JsonToDataConverterFactorySpy");
		DependencyProvider.setChildComparerUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();

		recordHandler = new RecordHandlerSpy();
		fixture = new RecordEndpointFixture();
		fixture.setRecordHandler(recordHandler);
	}

	@Test
	public void testInit() {
		fixture = new RecordEndpointFixture();
		assertTrue(
				fixture.getJsonToDataConverterFactory() instanceof JsonToDataConverterFactorySpy);
		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactorySpy);
		assertTrue(fixture.getChildComparer() instanceof ChildComparerSpy);
		assertTrue(fixture.getJsonToDataRecordConverter() instanceof JsonToDataRecordConverterImp);
		assertTrue(fixture.getJsonHandler() instanceof JsonHandlerImp);
		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.getRecordHandler();
		assertSame(recordHandler.getHttpHandlerFactory(), fixture.getHttpHandlerFactory());
		RestClientFactoryImp clientFactory = (RestClientFactoryImp) recordHandler
				.getRestClientFactory();
		assertEquals(clientFactory.getBaseUrl(), fixture.baseUrl);
	}

	@Test
	public void testReadRecordDataRecordHandlerIsOk() {
		String type = "someType";
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken("someToken");
		String responseText = fixture.testReadRecord();

		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(responseText, recordHandler.jsonToReturnDefault);

	}

	@Test
	public void testReadRecordStatusTypeFromRecordHandlerUsed() {
		assertEquals(fixture.testReadRecord(), "some json returned from spy");
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testReadRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testReadRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testReadIncomingLinksDataForRecordHandlerIsOk() {
		String type = "someType";
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken("someToken");
		String responseText = fixture.testReadIncomingLinks();

		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testReadIncomingLinksStatusTypeFromRecordHandlerUsed() {
		assertEquals(fixture.testReadIncomingLinks(), "some json returned from spy");
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testReadIncomingLinksAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testReadIncomingLinks();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testReadRecordListDataRecordHandlerIsOk() throws UnsupportedEncodingException {
		String type = "someType";
		fixture.setType(type);
		fixture.setAuthToken("someToken");
		String json = "some filter";
		fixture.setJson(json);

		String responseText = fixture.testReadRecordList();
		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.filter, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);

	}

	@Test
	public void testReadRecordListStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testReadRecordListAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testCreateRecordDataForRecordHandlerIsOk() {
		String type = "autogeneratedIdType";
		fixture.setType(type);
		fixture.setAuthToken("someToken");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		fixture.testCreateRecord();

		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);
		assertEquals(recordHandler.recordType, type);
		assertEquals(fixture.getCreatedId(), recordHandler.createdId);
		assertEquals(fixture.getToken(), recordHandler.token);
	}

	@Test
	public void testCreateRecordReturnedResponseTextSameAsInRecordHandler() {
		String responseText = fixture.testCreateRecord();
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testCreateRecordAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testCreateRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testCreateRecordCreatedType() {
		fixture.setType("someRecordType");
		fixture.setAuthToken("someToken");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		recordHandler.jsonToReturnDefault = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"someId\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/recordType/someRecordType\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"type\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"someRecordTypeAttribute\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\"}}}}";

		String createdType = fixture.testCreateRecordCreatedType();
		assertEquals(createdType, "someRecordType");

		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);
		assertEquals(fixture.getCreatedId(), recordHandler.createdId);
		assertEquals(fixture.getToken(), recordHandler.token);
	}

	@Test
	public void testCreateRecordCreatedTypeNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		recordHandler.statusTypeReturned = 401;

		assertEquals(fixture.testCreateRecordCreatedType(), recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testCreateRecordCreatedTypeNotFoundInJson() {
		recordHandler.jsonToReturnDefault = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"someId\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"someRecordTypeAttribute\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\"}}}}";
		fixture.setType("someWrongRecordTypeWrongJson");
		String createdType = fixture.testCreateRecordCreatedType();
		assertEquals(createdType, "");
	}

	@Test
	public void testUpdateRecordDataForRecordHandlerIsOk() {
		String type = "someType";
		String id = "someId";

		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken("someToken");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		fixture.testUpdateRecord();

		assertTrue(recordHandler.updateRecordWasCalled);

		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);

	}

	@Test
	public void testUpdateRecordReturnedResponseTextSameAsInRecordHandler() {
		String responseText = fixture.testUpdateRecord();
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testUpdateRecordSetsStatusType() {
		fixture.testUpdateRecord();
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testUpdateRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testUpdateRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testDeleteRecordDataRecordHandlerIsOk() {
		String type = "someType";
		String id = "someId";
		fixture.setType(type);
		fixture.setId(id);
		fixture.setAuthToken("someToken");
		String responseText = fixture.testDeleteRecord();

		assertTrue(recordHandler.deleteRecordWasCalled);
		assertEquals(recordHandler.recordType, type);
		assertEquals(recordHandler.recordId, id);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testDeleteRecordStatusTypeFromRecordHandlerUsed() {
		fixture.testDeleteRecord();
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testDeleteRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testDeleteRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testUploadDataForFactoryIsOk() throws ClientProtocolException, IOException {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		fixture.setFileName("correctFileAnswer");
		fixture.testUpload();

		HttpMultiPartUploaderSpy httpHandlerSpy = httpHandlerFactorySpy.httpMultiPartUploaderSpy;
		assertEquals(httpHandlerSpy.headerFields.get("Accept"), "application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.headerFields.size(), 1);

		assertEquals(httpHandlerSpy.fieldName, "file");
		assertEquals(httpHandlerSpy.fileName, "correctFileAnswer");

		assertTrue(httpHandlerSpy.doneIsCalled);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId/master?authToken=someToken");
		assertEquals(fixture.getStreamId(), "soundBinary:23310456970967");
	}

	@Test
	public void testUploadDataForFactoryIsOkUsingDefaultAuthToken()
			throws ClientProtocolException, IOException {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setFileName("correctFileAnswer");
		fixture.testUpload();

		HttpMultiPartUploaderSpy httpHandlerSpy = httpHandlerFactorySpy.httpMultiPartUploaderSpy;
		assertEquals(httpHandlerSpy.headerFields.get("Accept"), "application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.headerFields.size(), 1);

		assertEquals(httpHandlerSpy.fieldName, "file");
		assertEquals(httpHandlerSpy.fileName, "correctFileAnswer");

		assertTrue(httpHandlerSpy.doneIsCalled);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId/master?authToken=someAdminToken");
		assertEquals(fixture.getStreamId(), "soundBinary:23310456970967");
	}

	@Test
	public void testUploadOk() throws ClientProtocolException, IOException {
		assertEquals(fixture.testUpload(), "Everything ok");
	}

	@Test
	public void testUploadNotOk() throws ClientProtocolException, IOException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testUpload(), "bad things happend");
	}

	@Test
	public void testDownloadDataForFactoryIsOk() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		fixture.setResourceName("someResourceName");
		fixture.testDownload();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someToken");
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
		fixture.setAuthToken("someToken");
		fixture.setSearchId("aSearchId");

		String json = "{\"name\":\"search\",\"children\":[{\"name\":\"include\",\"children\":["
				+ "{\"name\":\"includePart\",\"children\":[{\"name\":\"text\",\"value\":\"\"}]}]}]}";
		fixture.setJson(json);
		String responseText = fixture.testSearchRecord();
		String expectedUrl = SystemUrl.getUrl() + "rest/record/searchResult/aSearchId";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);

		assertEquals(responseText, recordHandler.jsonToReturnDefault);

	}

	@Test
	public void testSearchRecordStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testSearchRecord();
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testSearchRecordAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testSearchRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testSetJsonObject() {
		fixture.setType("metadataGroup");
		fixture.setId("someMetadataGroupId");
		fixture.setAuthToken("someToken");
		JsonToDataRecordConverterSpy jsonToDataRecordConverterSpy = new JsonToDataRecordConverterSpy();
		fixture.setJsonToDataRecordConverter(jsonToDataRecordConverterSpy);
		JsonParserSpy jsonParser = new JsonParserSpy();
		JsonHandler jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		fixture.setJsonHandler(jsonHandler);
		fixture.testReadRecordAndStoreJson();

		assertEquals(jsonParser.jsonStringSentToParser, recordHandler.json);
		assertEquals(jsonToDataRecordConverterSpy.jsonObjects.get(0),
				jsonParser.jsonObjectSpies.get(0));

		assertSame(DataHolder.getRecord(), jsonToDataRecordConverterSpy.clientDataRecordSpy);
	}

}
