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
		ReadResponse readResponse = recordHandler.readRecord(url, authToken);
		assertTrue(readResponse.statusType.getStatusCode() == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testReadRecordNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		ReadResponse readResponse = recordHandler.readRecord(url, authToken);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertNotNull(readResponse.responseText);
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
	}

	@Test
	public void testReadRecordListHttpHandlerSetUpCorrectly() throws UnsupportedEncodingException {
		recordHandler.readRecordList(url, filterAsJson, authToken);
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");

		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType");
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAuthToken");

	}

	@Test
	public void testReadRecordListWithFilter() throws UnsupportedEncodingException {
		filterAsJson = "{\"name\":\"filter\",\"children\":[{\"name\":\"part\",\"children\":[{\"name\":\"key\",\"value\":\"idFromLogin\"},{\"name\":\"value\",\"value\":\"someId\"}],\"repeatId\":\"0\"}]}";

		recordHandler.readRecordList(url, filterAsJson, authToken);
		String encodedJson = URLEncoder.encode(filterAsJson, "UTF-8");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/someType?filter=" + encodedJson);

	}

	@Test
	public void testReadRecordListOk() throws UnsupportedEncodingException {
		ReadResponse readResponse = recordHandler.readRecordList(url, filterAsJson, authToken);

		assertTrue(readResponse.statusType.getStatusCode() == 200);
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.responseText);
	}

	@Test
	public void testReadRecordListNotOk() throws UnsupportedEncodingException {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		ReadResponse readResponse = recordHandler.readRecordList(url, filterAsJson, authToken);

		HttpHandlerInvalidSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerInvalidSpy;
		assertEquals(readResponse.responseText, httpHandlerSpy.returnedErrorText);
	}
}
