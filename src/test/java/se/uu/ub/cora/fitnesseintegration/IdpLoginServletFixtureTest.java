/*
 * Copyright 2019 Uppsala University Library
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

import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class IdpLoginServletFixtureTest {

	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private IdpLoginServletFixture idpFixture;

	@BeforeMethod
	public void beforeMethod() {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/idplogin/");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();
		idpFixture = new IdpLoginServletFixture();
	}

	@Test
	public void testRequestMethod() throws Exception {
		idpFixture.getAuthTokenForEPPN();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "GET");
	}

	@Test
	public void testThatIdpLoginUrlFromSystemUrlIsUsed() throws Exception {
		idpFixture.getAuthTokenForEPPN();
		assertEquals(httpHandlerFactorySpy.urlString, SystemUrl.getIdpLoginUrl() + "login");
	}

	@Test
	public void testThatOtherIdpLoginUrlFromSystemUrlIsUsed() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.getAuthTokenForEPPN();
		assertEquals(httpHandlerFactorySpy.urlString, SystemUrl.getIdpLoginUrl() + "login");
	}

	@Test
	public void testEPPNIsSentOnToServer() throws Exception {
		idpFixture.setEPPN("someuser@user.domain.org");

		idpFixture.getAuthTokenForEPPN();

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("eppn"), "someuser@user.domain.org");
	}

	@Test
	public void testOtherEPPNIsSentOnToServer() throws Exception {
		idpFixture.setEPPN("other@user.domain.org");

		idpFixture.getAuthTokenForEPPN();

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestProperties.get("eppn"), "other@user.domain.org");
	}

	@Test
	public void testGetAuthTokenForEPPNRetrunsHtmlFromServer() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.setEPPN("other@user.domain.org");

		String html = idpFixture.getAuthTokenForEPPN();

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.getResponseText(), html);
	}

	@Test
	public void testGetAuthTokenForEPPNRetrunsHtmlFromServerOther() throws Exception {
		idpFixture.setEPPN("other@user.domain.org");

		String html = idpFixture.getAuthTokenForEPPN();

		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.getResponseText(), html);
	}

	@Test
	public void testGetStatusTypeIsFromServer() throws Exception {
		idpFixture.getAuthTokenForEPPN();

		StatusType responseCode = idpFixture.getResponseCode();
		assertEquals(responseCode.toString(), "OK");
	}

	@Test
	public void testGetStatusTypeIsFromServerError() throws Exception {
		httpHandlerFactorySpy.setResponseCode(400);

		idpFixture.getAuthTokenForEPPN();

		StatusType responseCode = idpFixture.getResponseCode();
		assertEquals(responseCode.toString(), "Bad Request");
	}

	@Test
	public void testLoginIdIsFromServerAnswer() throws Exception {
		idpFixture.getAuthTokenForEPPN();

		String loginId = idpFixture.getLoginId();
		assertEquals(loginId, "other@user.domain.org");

	}

	@Test
	public void testNotParseableIdpLoginAnswer() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.getAuthTokenForEPPN();

		String loginId = idpFixture.getLoginId();
		assertEquals(loginId, "Not parseable");
	}

	@Test
	public void testAuthTokenIsFromServerAnswer() throws Exception {
		idpFixture.getAuthTokenForEPPN();
		String authToken = idpFixture.getAuthToken();
		assertEquals(authToken, "a8675062-a00d-4f6b-ada3-510934ad779d");
	}

	@Test
	public void testNotParseableAuthTokenIsFromServerAnswer() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.getAuthTokenForEPPN();
		String authToken = idpFixture.getAuthToken();
		assertEquals(authToken, "Not parseable");
	}

	@Test
	public void testValidForNumberOfSecondsIsFromServerAnswer() throws Exception {
		idpFixture.getAuthTokenForEPPN();
		String validForNoSeconds = idpFixture.getValidForNoSeconds();
		assertEquals(validForNoSeconds, "600");
	}

	@Test
	public void testNotParseableValidForNumberOfSecondsIsFromServerAnswer() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.getAuthTokenForEPPN();
		String validForNoSeconds = idpFixture.getValidForNoSeconds();
		assertEquals(validForNoSeconds, "Not parseable");
	}

	@Test
	public void testIdInUserStorageIsFromServerAnswer() throws Exception {
		idpFixture.getAuthTokenForEPPN();
		String deleteURL = idpFixture.getDeleteUrl();
		assertEquals(deleteURL, "http://localhost:8180/login/rest/apptoken/141414");
	}

	@Test
	public void testNotParseableIdInUserStorageIsFromServerAnswer() throws Exception {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/notthesameurl/");
		idpFixture.getAuthTokenForEPPN();
		String idInUserStorage = idpFixture.getDeleteUrl();
		assertEquals(idInUserStorage, "Not parseable");
	}
}
