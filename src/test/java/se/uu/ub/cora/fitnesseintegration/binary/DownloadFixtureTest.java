/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.binary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class DownloadFixtureTest {

	private static final String SOME_REPRESENTATION = "someRepresentation";
	private static final String SOME_TYPE = "someType";
	private static final String SOME_ID = "someId";
	private static final String SOME_AUTH_TOKEN = "someAuthToken";
	private DownloadFixture fixture;
	private RecordHandlerSpy recordHandlerSpy;

	@BeforeMethod
	private void beforeMethod() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8080/appTokenVerifier/");
		recordHandlerSpy = new RecordHandlerSpy();

		fixture = new DownloadFixture();
	}

	@Test
	public void testValidateRecordHandler() throws Exception {
		RecordHandlerImp recordHandler = (RecordHandlerImp) fixture.onlyForTestgetRecordHandler();
		assertTrue(recordHandler instanceof RecordHandlerImp);
		assertEquals(recordHandler.onlyForTestGetBaseUrl(), SystemUrl.getUrl() + "rest/");
		assertEquals(recordHandler.onlyForTestGetAppTokenUrl(), SystemUrl.getAppTokenVerifierUrl());
	}

	@Test
	public void testDownloadOk() throws Exception {
		fixture.onlyForTestSetRecordHandler(recordHandlerSpy);

		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setRecordType(SOME_TYPE);
		fixture.setRecordId(SOME_ID);
		fixture.setRepresentation(SOME_REPRESENTATION);

		String status = fixture.testDownload();

		recordHandlerSpy.MCR.assertParameters("download", 0, SOME_AUTH_TOKEN, SOME_TYPE, SOME_ID,
				SOME_REPRESENTATION);

		assertEquals(fixture.getStatusType().toString(), "OK");
		assertEquals(status, "someResponseText");
	}

	@Test
	public void testDownloadNotOk() throws Exception {
		fixture.onlyForTestSetRecordHandler(recordHandlerSpy);

		setErrorResponseFromDownload(500);

		String status = fixture.testDownload();

		assertEquals(fixture.getStatusType().toString(), "Internal Server Error");
		assertEquals(status, "someErrorText");
	}

	private void setErrorResponseFromDownload(int code) {
		RestResponse errorResponse = new RestResponse(code, "someErrorText", Optional.empty(),
				Optional.empty());

		recordHandlerSpy.MRV.setDefaultReturnValuesSupplier("download", () -> errorResponse);
	}
}
