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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.RestClientSpy;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RecordHandlerTest {

	private static final String SOME_SEARCH_ID = "someSearchId";
	private static final String SOME_JSON = "someJson";
	private static final String SOME_FILTER = "someFilter";
	private String url = "http://localhost:8080/therest/rest/record/someType";
	private String filterAsJson = null;
	private String authToken = "someAuthToken";
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private RecordHandlerImp recordHandler;
	private RestClientFactorySpy restClientFactory;
	String recordType = "someType";
	String recordId = "someId";
	private JavaClientFactorySpy javaClientFactory;
	private String appTokenUrl = "someAppTokenUrl";
	private JavaClientAuthTokenCredentials authTokenCredentials;

	@BeforeMethod
	public void setUp() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		authTokenCredentials = new JavaClientAuthTokenCredentials(url, appTokenUrl, authToken);

		httpHandlerFactorySpy = new HttpHandlerFactorySpy();
		recordHandler = new RecordHandlerImp(authTokenCredentials);
		filterAsJson = null;

	}

	@Test
	public void testReadRecordRestClientSetUpCorrectly() {
		BasicHttpResponse basicResponse = recordHandler.readRecord(authToken, recordType, recordId);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordAsJson", 0, recordType, recordId);
		RestResponse response = (RestResponse) restClient.MCR.getReturnValue("readRecordAsJson", 0);
		assertEquals(basicResponse.statusCode, response.responseCode());
		assertEquals(basicResponse.responseText, response.responseText());
	}

	private RestClientSpy assertAndReturnRestClientIsFromProvider() {
		javaClientFactory.MCR.assertParameters("factorRestClientUsingAuthTokenCredentials", 0,
				authTokenCredentials);
		RestClientSpy restClient = (RestClientSpy) javaClientFactory.MCR
				.getReturnValue("factorRestClientUsingAuthTokenCredentials", 0);
		return restClient;
	}

	@Test
	public void testReadRecordListWithoutFilter() {
		BasicHttpResponse basicResponse = recordHandler.readRecordList(authToken, recordType, null);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordListAsJson", 0, recordType);
		RestResponse response = (RestResponse) restClient.MCR.getReturnValue("readRecordListAsJson",
				0);
		assertEquals(basicResponse.statusCode, response.responseCode());
		assertEquals(basicResponse.responseText, response.responseText());
	}

	@Test
	public void testReadRecordListWithFilter() {
		BasicHttpResponse basicResponse = recordHandler.readRecordList(authToken, recordType,
				SOME_FILTER);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordListWithFilterAsJson", 0, recordType,
				SOME_FILTER);
		RestResponse response = (RestResponse) restClient.MCR
				.getReturnValue("readRecordListWithFilterAsJson", 0);
		assertEquals(basicResponse.statusCode, response.responseCode());
		assertEquals(basicResponse.responseText, response.responseText());
	}

	@Test
	public void testSearchRecordHttpHandlerSetUpCorrectly() {
		BasicHttpResponse basicResponse = recordHandler.searchRecord(SOME_SEARCH_ID, SOME_JSON);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("searchRecordWithSearchCriteriaAsJson", 0, SOME_SEARCH_ID,
				SOME_JSON);

		RestResponse response = (RestResponse) restClient.MCR
				.getReturnValue("searchRecordWithSearchCriteriaAsJson", 0);
		assertEquals(basicResponse.statusCode, response.responseCode());
		assertEquals(basicResponse.responseText, response.responseText());

	}

	///////////////////
	@Test
	public void testCreateRecordRestClientSetUpCorrectly() {
		String json = "{\"name\":\"value\"}";
		recordHandler.createRecord(authToken, recordType, json);

		assertEquals(restClientFactory.authToken, authToken);

		OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		assertEquals(restClient.recordType, recordType);
	}

	@Test
	public void testCreateRecordOk() {

		// httpHandlerFactorySpy.setResponseCode(201);
		// String json = "{\"name\":\"value\"}";
		// ExtendedHttpResponse createResponse = recordHandler.createRecord(authToken, recordType,
		// json);
		//
		// OldRestClientSpy restClient = (OldRestClientSpy) restClientFactory.returnedRestClient;
		// assertEquals(createResponse.responseText, restClient.returnedJson);
		//
		// assertEquals(createResponse.statusCode, 201);
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
	public void testValidateRecord() {
		BasicHttpResponse basicResponse = recordHandler.validateRecord(SOME_JSON);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("validateRecordAsJson", 0, SOME_JSON);

		RestResponse response = (RestResponse) restClient.MCR.getReturnValue("validateRecordAsJson",
				0);
		assertEquals(basicResponse.statusCode, response.responseCode());
		assertEquals(basicResponse.responseText, response.responseText());
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
	public void testReadIncomingLinksRestClientSetUpCorrectly() {
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
