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
	private String filterAsJson = null;
	private String authToken = "someAuthToken";
	private RecordHandlerImp recordHandler;
	String recordType = "someType";
	String recordId = "someId";
	private JavaClientFactorySpy javaClientFactory;
	private String baseUrl = "someBaseUrl";
	private String appTokenUrl = "someAppTokenUrl";

	@BeforeMethod
	public void setUp() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		recordHandler = new RecordHandlerImp(baseUrl, appTokenUrl);
		filterAsJson = null;
	}

	@Test
	public void testOnlyForTestGetBaseUrl() throws Exception {
		assertEquals(recordHandler.onlyForTestGetBaseUrl(), baseUrl);
	}

	@Test
	public void testOnlyForTestGetAppTokenUrl() throws Exception {
		assertEquals(recordHandler.onlyForTestGetAppTokenUrl(), appTokenUrl);
	}

	@Test
	public void testReadRecord() {
		RestResponse restResponse = recordHandler.readRecord(authToken, recordType, recordId);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordAsJson", 0, recordType, recordId);
		restClient.MCR.assertReturn("readRecordAsJson", 0, restResponse);
	}

	private RestClientSpy assertAndReturnRestClientIsFromProvider() {
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenUrl, authToken);
		javaClientFactory.MCR.assertParameterAsEqual(
				"factorRestClientUsingJavaClientAuthTokenCredentials", 0,
				"javaClientAuthTokenCredentials", authTokenCredentials);
		RestClientSpy restClient = (RestClientSpy) javaClientFactory.MCR
				.getReturnValue("factorRestClientUsingJavaClientAuthTokenCredentials", 0);
		return restClient;
	}

	@Test
	public void testReadRecordListWithoutFilter() {
		RestResponse restResponse = recordHandler.readRecordList(authToken, recordType, null);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordListAsJson", 0, recordType);
		restClient.MCR.assertReturn("readRecordListAsJson", 0, restResponse);
	}

	@Test
	public void testReadRecordListWithFilter() {
		RestResponse restResponse = recordHandler.readRecordList(authToken, recordType,
				SOME_FILTER);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readRecordListWithFilterAsJson", 0, recordType,
				SOME_FILTER);
		restClient.MCR.assertReturn("readRecordListWithFilterAsJson", 0, restResponse);
	}

	@Test
	public void testSearchRecord() {
		RestResponse restResponse = recordHandler.searchRecord(authToken, SOME_SEARCH_ID,
				SOME_JSON);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("searchRecordWithSearchCriteriaAsJson", 0, SOME_SEARCH_ID,
				SOME_JSON);
		restClient.MCR.assertReturn("searchRecordWithSearchCriteriaAsJson", 0, restResponse);
	}

	@Test
	public void testCreateRecord() {
		RestResponse restResponse = recordHandler.createRecord(authToken, SOME_SEARCH_ID,
				SOME_JSON);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("createRecordFromJson", 0, SOME_SEARCH_ID, SOME_JSON);
		restClient.MCR.assertReturn("createRecordFromJson", 0, restResponse);
	}

	@Test
	public void testValidateRecord() {
		RestResponse restResponse = recordHandler.validateRecord(authToken, SOME_JSON);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("validateRecordAsJson", 0, SOME_JSON);
		restClient.MCR.assertReturn("validateRecordAsJson", 0, restResponse);
	}

	@Test
	public void testUpdateRecord() {
		String json = "{\"name\":\"value\"}";
		RestResponse restResponse = recordHandler.updateRecord(authToken, recordType, recordId,
				json);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("updateRecordFromJson", 0, recordType, recordId, json);
		restClient.MCR.assertReturn("updateRecordFromJson", 0, restResponse);
	}

	@Test
	public void testDeleteRecord() {
		RestResponse restResponse = recordHandler.deleteRecord(authToken, recordType, recordId);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("deleteRecord", 0, recordType, recordId);
		restClient.MCR.assertReturn("deleteRecord", 0, restResponse);
	}

	@Test
	public void testReadIncomingLinks() {
		RestResponse restResponse = recordHandler.readIncomingLinks(authToken, recordType,
				recordId);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();
		restClient.MCR.assertParameters("readIncomingLinksAsJson", 0, recordType, recordId);
		restClient.MCR.assertReturn("readIncomingLinksAsJson", 0, restResponse);
	}

	@Test
	public void testBatchIndex() {
		RestResponse restResponse = recordHandler.batchIndex(authToken, recordType, filterAsJson);

		RestClientSpy restClient = assertAndReturnRestClientIsFromProvider();

		restClient.MCR.assertParameters("batchIndexWithFilterAsJson", 0, recordType, filterAsJson);
		restClient.MCR.assertReturn("batchIndexWithFilterAsJson", 0, restResponse);
	}

}
