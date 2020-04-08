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
		DependencyProvider
				.setChildComparerClassName("se.uu.ub.cora.fitnesseintegration.ChildComparerSpy");
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
	}

	@Test
	public void testReadRecordDataForFactoryIsOk() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		String responseText = fixture.testReadRecord();

		String expectedUrl = SystemUrl.getUrl() + "rest/record/someType/someId";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(responseText, recordHandler.jsonToReturn);

	}

	@Test
	public void testReadRecordStatusTypeFromRecordHandlerUsed() {
		assertEquals(fixture.testReadRecord(), "some json returned from spy");
		assertEquals(fixture.getStatusType(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testReadRecordAdminAuthTokenUsedWhenNoAuthTokenSet() {
		fixture.testReadRecord();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testReadIncomingLinksDataForFactoryIsOk() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		fixture.testReadIncomingLinks();
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId/incomingLinks");
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestProperties.get("authToken"),
				"someToken");
	}

	@Test
	public void testReadIncomingLinksOk() {
		assertEquals(fixture.testReadIncomingLinks(), "Everything ok");
	}

	@Test
	public void testReadIncomingLinksNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testReadIncomingLinks(), "bad things happend");
	}

	@Test
	public void testReadRecordListDataForRecordHandlerIsOk() throws UnsupportedEncodingException {
		fixture.setType("someType");
		fixture.setAuthToken("someToken");
		String json = "some filter";
		fixture.setJson(json);

		String responseText = fixture.testReadRecordList();
		String expectedUrl = SystemUrl.getUrl() + "rest/record/someType";
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.filter, json);
		assertEquals(responseText, recordHandler.jsonToReturn);

	}

	@Test
	public void testReadRecordListStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(fixture.getStatusType(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testReadRecordListAdminAuthTokenUsedWhenNoAuthTokenSet()
			throws UnsupportedEncodingException {
		fixture.testReadRecordList();
		assertEquals(recordHandler.authToken, AuthTokenHolder.getAdminAuthToken());
	}

	@Test
	public void testCreateRecordDataForFactoryIsOk() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setType("autogeneratedIdType");
		fixture.setAuthToken("someToken");
		fixture.setJson("{\"name\":\"value\"}");
		fixture.testCreateRecord();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "{\"name\":\"value\"}");
		assertEquals(httpHandlerSpy.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("Content-Type"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someToken");
		assertEquals(httpHandlerSpy.requestProperties.size(), 3);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/autogeneratedIdType");
		assertEquals(fixture.getCreatedId(), "someRecordType:35824453170224822");
	}

	@Test
	public void testCreateAppTokenDataForFactoryIsOk() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setType("appToken");
		fixture.setAuthToken("someToken");
		fixture.setJson("{\"name\":\"value\"}");
		fixture.testCreateRecord();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "{\"name\":\"value\"}");
		assertEquals(httpHandlerSpy.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("Content-Type"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someToken");
		assertEquals(httpHandlerSpy.requestProperties.size(), 3);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/appToken");
		assertEquals(fixture.getCreatedId(), "someRecordType:35824453170224822");
		assertEquals(fixture.getToken(), "ba064c86-bd7c-4283-a5f3-86ba1dade3f3");
	}

	@Test
	public void testCreateRecordOk() {
		httpHandlerFactorySpy.setResponseCode(201);
		assertEquals(fixture.testCreateRecord(), "Everything ok");
	}

	@Test
	public void testCreateRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testCreateRecord(), "bad things happend");
	}

	@Test
	public void testCreateRecordCreatedType() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setType("someRecordType");
		String createdType = fixture.testCreateRecordCreatedType();
		assertEquals(createdType, "someRecordType");
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAdminToken");
	}

	@Test
	public void testCreateRecordCreatedTypeNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testCreateRecordCreatedType(), "bad things happend");
	}

	@Test
	public void testCreateRecordCreatedTypeNotFoundInJson() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setType("someWrongRecordTypeWrongJson");
		String createdType = fixture.testCreateRecordCreatedType();
		assertEquals(createdType, "");
	}

	@Test
	public void testUpdateRecordDataForFactoryIsOk() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		fixture.setJson("{\"name\":\"value\"}");
		fixture.testUpdateRecord();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "{\"name\":\"value\"}");
		assertEquals(httpHandlerSpy.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("Content-Type"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someToken");
		assertEquals(httpHandlerSpy.requestProperties.size(), 3);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId");
	}

	@Test
	public void testUpdateRecordOk() {
		assertEquals(fixture.testUpdateRecord(), "Everything ok");
	}

	@Test
	public void testUpdateRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testUpdateRecord(), "bad things happend");
	}

	@Test
	public void testDeleteRecordDataForFactoryIsOk() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setAuthToken("someToken");
		fixture.testDeleteRecord();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "DELETE");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someToken");
		assertEquals(httpHandlerSpy.requestProperties.size(), 1);
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId");
	}

	@Test
	public void testDeleteRecordOk() {
		assertEquals(fixture.testDeleteRecord(), "Everything ok");
	}

	@Test
	public void testDeleteRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.testDeleteRecord(), "bad things happend");
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

		assertEquals(responseText, recordHandler.jsonToReturn);

	}

	@Test
	public void testSearchRecordStatusTypeFromRecordHandlerUsed()
			throws UnsupportedEncodingException {
		fixture.testSearchRecord();
		assertEquals(fixture.getStatusType(), recordHandler.statusTypeReturned);
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
