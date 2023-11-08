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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class RecordHandlerTest {

	private String url = "http://localhost:8080/therest/rest/record/someType";
	private String filterAsJson = null;
	private String authToken = "someAuthToken";
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private RecordHandlerImp recordHandler;
	private RestClientFactorySpy restClientFactory;
	String recordType = "someType";
	String recordId = "someId";

	@BeforeMethod
	public void setUp() {
		restClientFactory = new RestClientFactorySpy();
		httpHandlerFactorySpy = new HttpHandlerFactorySpy();
		recordHandler = new RecordHandlerImp(httpHandlerFactorySpy, restClientFactory);
		filterAsJson = null;

	}

	@Test
	public void testReadRecordRestClientSetUpCorrectly() {
		recordHandler.readRecord(authToken, recordType, recordId);

		assertEquals(restClientFactory.authToken, authToken);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertTrue(restClient.readWasCalled);
		assertEquals(restClient.recordType, recordType);
		assertEquals(restClient.recordId, recordId);
	}

	@Test
	public void testReadRecordOk() {
		BasicHttpResponse readResponse = recordHandler.readRecord(authToken, recordType, recordId);
		assertTrue(readResponse.statusCode == 200);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(readResponse.responseText, restClient.returnedJson);
	}

	@Test
	public void testReadRecordNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		BasicHttpResponse readResponse = recordHandler.readRecord(authToken, recordType, recordId);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(readResponse.responseText);
		assertTrue(readResponse.statusCode == 500);
		assertEquals(readResponse.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testReadRecordListRestClientSetUpCorrectly() throws UnsupportedEncodingException {
		recordHandler.readRecordList(authToken, recordType, filterAsJson);

		assertEquals(restClientFactory.authToken, authToken);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
	}

	@Test
	public void testReadRecordListOk() throws UnsupportedEncodingException {
		BasicHttpResponse readResponse = recordHandler.readRecordList(authToken, null,
				filterAsJson);
		assertTrue(readResponse.statusCode == 200);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(readResponse.responseText, restClient.returnedJson);
		assertNull(restClient.filter);
	}

	@Test
	public void testReadRecordListWithFilterOk() throws UnsupportedEncodingException {
		filterAsJson = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"idFromLogin\"},{\"name\":\"value\",\"value\":\"someId\"}],\"repeatId\":\"0\"}]}";

		BasicHttpResponse readResponse = recordHandler.readRecordList(authToken, recordType,
				filterAsJson);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(readResponse.responseText, restClient.returnedJson);
		assertEquals(restClient.filter, filterAsJson);
	}

	@Test
	public void testReadRecordListNotOk() throws UnsupportedEncodingException {
		restClientFactory.factorInvalidRestClient = true;
		BasicHttpResponse readResponse = recordHandler.readRecordList(authToken, null,
				filterAsJson);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(readResponse.responseText);
		assertTrue(readResponse.statusCode == 500);
		assertEquals(readResponse.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testSearchRecordHttpHandlerSetUpCorrectly() throws UnsupportedEncodingException {
		String apptokenUrl = "http://localhost:8080/therest/rest/record/searchResult/aSearchId";
		String json = "{\"name\":\"search\",\"children\":[{\"name\":\"include\",\"children\":["
				+ "{\"name\":\"includePart\",\"children\":[{\"name\":\"text\",\"value\":\"\"}]}]}]}";

		recordHandler.searchRecord(apptokenUrl, authToken, json);

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), authToken);
		assertEquals(httpHandlerSpy.requestProperties.size(), 1);

		String encodedJson = URLEncoder.encode(json, "UTF-8");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/searchResult/aSearchId?" + "searchData="
						+ encodedJson);
	}

	@Test
	public void testSearchRecordOk() throws UnsupportedEncodingException {
		BasicHttpResponse readResponse = recordHandler.searchRecord(url, authToken, "some json");

		assertTrue(readResponse.statusCode == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testSearchRecordNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		BasicHttpResponse readResponse = recordHandler.searchRecord(url, authToken, "some json");

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertTrue(readResponse.statusCode != 200);
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
	}

	@Test
	public void testCreateRecordRestClientSetUpCorrectly() {
		String json = "{\"name\":\"value\"}";
		recordHandler.createRecord(authToken, recordType, json);

		assertEquals(restClientFactory.authToken, authToken);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
	}

	private void assertCorrectHttpHandlerForPost(String json, String expectedUrl,
			String contentType) {
		assertEquals(httpHandlerFactorySpy.urlString, expectedUrl);

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAuthToken");
		assertEquals(httpHandlerSpy.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
		assertEquals(httpHandlerSpy.requestProperties.get("Content-Type"), contentType);
		assertEquals(httpHandlerSpy.requestProperties.size(), 3);

		assertEquals(httpHandlerSpy.outputString, json);
	}

	@Test
	public void testCreateRecordOk() {
		httpHandlerFactorySpy.setResponseCode(201);
		String json = "{\"name\":\"value\"}";
		ExtendedHttpResponse createResponse = recordHandler.createRecord(authToken, recordType,
				json);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(createResponse.responseText, restClient.returnedJson);

		assertEquals(createResponse.statusCode, 201);
	}

	@Test
	public void testCreateRecordOkWithCreatedIdAndToken() {
		httpHandlerFactorySpy.setResponseCode(201);
		restClientFactory.jsonToReturn = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"appToken:7053734211763\"},{\"name\":\"type\",\"value\":\"appToken\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"},{\"name\":\"note\",\"value\":\"My  device\"},{\"name\":\"token\",\"value\":\"ba064c86-bd7c-4283-a5f3-86ba1dade3f3\"}],\"name\":\"appToken\"},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"},\"read_incoming_links\":{\"requestMethod\":\"GET\",\"rel\":\"read_incoming_links\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763/incomingLinks\",\"accept\":\"application/vnd.uub.recordList+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"}}}}";
		String json = "{\"name\":\"value\"}";
		ExtendedHttpResponse createResponse = recordHandler.createRecord(authToken, recordType,
				json);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;

		assertEquals(createResponse.statusCode, 201);
		assertEquals(createResponse.createdId, restClient.createdId);
		assertEquals(createResponse.token, "ba064c86-bd7c-4283-a5f3-86ba1dade3f3");
	}

	@Test
	public void testCreateRecordNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		String json = "{\"name\":\"value\"}";
		ExtendedHttpResponse createResponse = recordHandler.createRecord(authToken, recordType,
				json);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(createResponse.responseText);
		assertTrue(createResponse.statusCode == 500);
		assertEquals(createResponse.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testValidateRecordHttpHandlerSetUpCorrectly() {
		httpHandlerFactorySpy.setResponseCode(200);
		String json = "{\"name\":\"value\"}";
		String contentType = "application/vnd.uub.workorder+json";
		recordHandler.validateRecord(url, authToken, json, contentType);
		String expectedUrl = "http://localhost:8080/therest/rest/record/someType";
		assertCorrectHttpHandlerForPost(json, expectedUrl, contentType);
	}

	@Test
	public void testValidateRecordOk() {
		httpHandlerFactorySpy.setResponseCode(200);
		String json = "{\"name\":\"value\"}";
		BasicHttpResponse response = recordHandler.validateRecord(url, authToken, json,
				"application/vnd.uub.workorder+json");

		assertEquals(response.statusCode, 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(response.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testValidateRecordNotOk() {
		httpHandlerFactorySpy.setResponseCode(401);
		String json = "{\"name\":\"value\"}";
		BasicHttpResponse response = recordHandler.validateRecord(url, authToken, json,
				"application/vnd.uub.workorder+json");

		assertEquals(response.statusCode, 401);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(response.responseText, httpHandlerSpy.errorText);
	}

	@Test
	public void testUpdateRecordRestClientSetUpCorrectly() {
		String json = "{\"name\":\"value\"}";
		recordHandler.updateRecord(authToken, recordType, recordId, json);

		assertEquals(restClientFactory.authToken, authToken);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
		assertEquals(restClient.recordId, recordId);

	}

	@Test
	public void testUpdateRecordOk() {
		httpHandlerFactorySpy.setResponseCode(200);
		String json = "{\"name\":\"value\"}";
		BasicHttpResponse updateResponse = recordHandler.updateRecord(authToken, recordType,
				recordId, json);

		assertTrue(updateResponse.statusCode == 200);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(updateResponse.responseText, restClient.returnedJson);
	}

	@Test
	public void testUpdateRecordNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		String json = "{\"name\":\"value\"}";
		BasicHttpResponse response = recordHandler.updateRecord(authToken, recordType, recordId,
				json);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(response.responseText);
		assertTrue(response.statusCode == 500);
		assertEquals(response.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testDeleteRecordHttpHandlerSetUpCorrectly() {
		recordHandler.deleteRecord(authToken, recordType, recordId);

		assertEquals(restClientFactory.authToken, authToken);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
		assertEquals(restClient.recordId, recordId);
	}

	@Test
	public void testDeleteRecordOk() {
		BasicHttpResponse response = recordHandler.deleteRecord(authToken, recordType, recordId);

		assertTrue(response.statusCode == 200);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(response.responseText, restClient.returnedJson);
	}

	@Test
	public void testDeleteRecordNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		BasicHttpResponse response = recordHandler.deleteRecord(authToken, null, null);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(response.responseText);
		assertTrue(response.statusCode == 500);
		assertEquals(response.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testReadIncomingLinksRestClientSetUpCorrectly()
			throws UnsupportedEncodingException {
		recordHandler.readIncomingLinks(authToken, recordType, recordId);

		assertEquals(restClientFactory.authToken, authToken);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
		assertEquals(restClient.recordId, recordId);
	}

	@Test
	public void testReadIncomingLinksOk() {
		BasicHttpResponse readResponse = recordHandler.readIncomingLinks(authToken, recordType,
				recordId);
		assertTrue(readResponse.statusCode == 200);
		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(readResponse.responseText, restClient.returnedJson);
	}

	@Test
	public void testReadIncomingLinksNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		BasicHttpResponse readResponse = recordHandler.readIncomingLinks(authToken, recordType,
				recordId);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertNotNull(readResponse.responseText);
		assertTrue(readResponse.statusCode == 500);
		assertEquals(readResponse.responseText, restClient.returnedErrorMessage);
	}

	@Test
	public void testBatchIndexRestClientSetUpCorrectly() {
		filterAsJson = "someFilter";
		recordHandler.batchIndex(authToken, recordType, filterAsJson);

		assertEquals(restClientFactory.authToken, authToken);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;

		assertEquals(restClient.recordType, recordType);
		assertEquals(restClient.filter, filterAsJson);
	}

	@Test
	public void testBatchIndexNotOk() {
		restClientFactory.factorInvalidRestClient = true;
		filterAsJson = "someFilter";

		ExtendedHttpResponse response = recordHandler.batchIndex(authToken, recordType,
				filterAsJson);

		assertNotNull(response.responseText);
		assertTrue(response.statusCode == 500);

		RestClientInvalidSpy restClient = (RestClientInvalidSpy) restClientFactory.returnedRestClient;
		assertEquals(response.responseText, restClient.returnedErrorMessage);

	}

	@Test
	public void testBatchIndexOk() {
		httpHandlerFactorySpy.setResponseCode(201);
		filterAsJson = "someFilter";
		restClientFactory.jsonToReturn = "some json";

		ExtendedHttpResponse response = recordHandler.batchIndex(authToken, recordType,
				filterAsJson);

		assertNotNull(response);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(response.responseText, restClient.returnedJson);

		assertEquals(response.statusCode, 201);

	}

	@Test
	public void testBatchIndexOkWithCreatedIdAndToken() {
		httpHandlerFactorySpy.setResponseCode(201);
		filterAsJson = "someFilter";
		restClientFactory.jsonToReturn = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"appToken:7053734211763\"},{\"name\":\"type\",\"value\":\"appToken\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"},{\"name\":\"note\",\"value\":\"My  device\"},{\"name\":\"token\",\"value\":\"ba064c86-bd7c-4283-a5f3-86ba1dade3f3\"}],\"name\":\"appToken\"},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"},\"read_incoming_links\":{\"requestMethod\":\"GET\",\"rel\":\"read_incoming_links\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763/incomingLinks\",\"accept\":\"application/vnd.uub.recordList+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"}}}}";

		ExtendedHttpResponse response = recordHandler.batchIndex(authToken, recordType,
				filterAsJson);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;

		assertEquals(response.statusCode, 201);
		assertEquals(response.createdId, restClient.createdId);
		assertEquals(response.createdId, restClient.createdId);
		assertEquals(response.token, "ba064c86-bd7c-4283-a5f3-86ba1dade3f3");

	}

}
