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

	@BeforeMethod
	public void setUp() {
		httpHandlerFactorySpy = new HttpHandlerFactorySpy();
		recordHandler = new RecordHandlerImp(httpHandlerFactorySpy);

	}

	@Test
	public void testReadRecordHttpHandlerSetUpCorrectly() throws UnsupportedEncodingException {
		recordHandler.readRecord(url + "/someId", authToken);
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");

		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType/someId");
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAuthToken");

	}

	@Test
	public void testReadRecordOk() {
		BasicHttpResponse readResponse = recordHandler.readRecord(url, authToken);
		assertTrue(readResponse.statusType.getStatusCode() == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testReadRecordNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		BasicHttpResponse readResponse = recordHandler.readRecord(url, authToken);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertNotNull(readResponse.responseText);
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
	}

	@Test
	public void testReadRecordListHttpHandlerSetUpCorrectly() throws UnsupportedEncodingException {
		recordHandler.readRecordList(url, authToken, filterAsJson);
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");

		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType");
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAuthToken");

	}

	@Test
	public void testReadRecordListWithFilter() throws UnsupportedEncodingException {
		filterAsJson = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"idFromLogin\"},{\"name\":\"value\",\"value\":\"someId\"}],\"repeatId\":\"0\"}]}";

		recordHandler.readRecordList(url, authToken, filterAsJson);
		String encodedJson = URLEncoder.encode(filterAsJson, "UTF-8");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType?filter=" + encodedJson);

	}

	@Test
	public void testReadRecordListOk() throws UnsupportedEncodingException {
		BasicHttpResponse readResponse = recordHandler.readRecordList(url, authToken, filterAsJson);

		assertTrue(readResponse.statusType.getStatusCode() == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testReadRecordListNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		BasicHttpResponse readResponse = recordHandler.readRecordList(url, authToken, filterAsJson);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
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

		assertTrue(readResponse.statusType.getStatusCode() == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testSearchRecordNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		BasicHttpResponse readResponse = recordHandler.searchRecord(url, authToken, "some json");

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertTrue(readResponse.statusType.getStatusCode() != 200);
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
	}

	@Test
	public void testCreateRecordHttpHandlerSetUpCorrectly() {
		String json = "{\"name\":\"value\"}";
		recordHandler.createRecord(url, authToken, json);
		String expectedUrl = "http://localhost:8080/therest/rest/record/someType";
		String contentType = "application/vnd.uub.record+json";
		assertCorrectHttpHandlerForPost(json, expectedUrl, contentType);
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
		ExtendedHttpResponse createResponse = recordHandler.createRecord(url, authToken, json);

		assertEquals(createResponse.statusType.getStatusCode(), 201);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(createResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testCreateRecordOkWithCreatedIdAndToken() {
		String apptokenUrl = "http://localhost:8080/therest/rest/record/appToken";
		httpHandlerFactorySpy.setResponseCode(201);
		String json = "{\"name\":\"value\"}";
		ExtendedHttpResponse createResponse = recordHandler.createRecord(apptokenUrl, authToken,
				json);

		assertEquals(createResponse.statusType.getStatusCode(), 201);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		String returnedHeaderFromSpy = httpHandlerSpy.returnedHeaderField;
		assertEquals(createResponse.createdId,
				returnedHeaderFromSpy.substring(returnedHeaderFromSpy.lastIndexOf('/') + 1));
		assertEquals(createResponse.token, "ba064c86-bd7c-4283-a5f3-86ba1dade3f3");
	}

	@Test
	public void testCreateRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		String json = "{\"name\":\"value\"}";
		ExtendedHttpResponse createResponse = recordHandler.createRecord(url, authToken, json);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertNotNull(createResponse.responseText);
		assertEquals(createResponse.responseText, httpHandlerSpy.returnedErrorText);
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

		assertEquals(response.statusType.getStatusCode(), 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(response.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testValidateRecordNotOk() {
		httpHandlerFactorySpy.setResponseCode(401);
		String json = "{\"name\":\"value\"}";
		BasicHttpResponse response = recordHandler.validateRecord(url, authToken, json,
				"application/vnd.uub.workorder+json");

		assertEquals(response.statusType.getStatusCode(), 401);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(response.responseText, httpHandlerSpy.errorText);
	}

	@Test
	public void testUpdateRecordHttpHandlerSetUpCorrectly() {
		String json = "{\"name\":\"value\"}";
		String updateUrl = url + "/someId";
		recordHandler.updateRecord(updateUrl, authToken, json);

		assertCorrectHttpHandlerForPost(json, updateUrl, "application/vnd.uub.record+json");
	}

	@Test
	public void testUpdateRecordOk() {
		httpHandlerFactorySpy.setResponseCode(200);
		String json = "{\"name\":\"value\"}";
		String updateUrl = url + "/someId";
		BasicHttpResponse updateResponse = recordHandler.updateRecord(updateUrl, authToken, json);

		assertEquals(updateResponse.statusType.getStatusCode(), 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(updateResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testUpdateRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		String json = "{\"name\":\"value\"}";
		String updateUrl = url + "/someId";
		BasicHttpResponse createResponse = recordHandler.updateRecord(updateUrl, authToken, json);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertNotNull(createResponse.responseText);
		assertEquals(createResponse.responseText, httpHandlerSpy.returnedErrorText);
	}

}
