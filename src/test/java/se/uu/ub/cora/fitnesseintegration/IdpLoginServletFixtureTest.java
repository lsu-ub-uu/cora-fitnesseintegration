/*
 * Copyright 2019, 2025 Uppsala University Library
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

import java.util.Optional;

import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientAction;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientActionLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataAuthenticationSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.spies.HttpHandlerFactorySpy;
import se.uu.ub.cora.httphandler.spies.HttpHandlerSpy;

public class IdpLoginServletFixtureTest {

	private static final String DEFAULT_URL = "http://localhost:8380/idplogin/";
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private HttpHandlerSpy httpHandlerSpy;
	private IdpLoginServletFixture idpFixture;
	private JsonToClientDataConverterFactorySpy jsonToDataConverterFactory;
	private ClientDataAuthenticationSpy clientDataAutentication;
	private JsonToClientDataConverterSpy jsonToClientDataConverter;

	@BeforeMethod
	public void beforeMethod() {
		setJsonToClientDataConverterFactory();

		JsonToClientDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactory);
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.httphandler.spies.HttpHandlerFactorySpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();

		SystemUrl.setIdpLoginUrl(DEFAULT_URL);
		httpHandlerSpy = new HttpHandlerSpy();
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> createExpectedHtml());
		httpHandlerFactorySpy.MRV.setDefaultReturnValuesSupplier("factor", () -> httpHandlerSpy);

		idpFixture = new IdpLoginServletFixture();
	}

	private void setJsonToClientDataConverterFactory() {
		clientDataAutentication = new ClientDataAuthenticationSpy();

		jsonToClientDataConverter = new JsonToClientDataConverterSpy();
		jsonToClientDataConverter.MRV.setDefaultReturnValuesSupplier("toInstance",
				() -> clientDataAutentication);
		jsonToDataConverterFactory = new JsonToClientDataConverterFactorySpy();
		jsonToDataConverterFactory.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> jsonToClientDataConverter);
	}

	private String createExpectedHtml() {
		return """
				<!DOCTYPE html>
				<html>
					<head>
						<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
						<script type="text/javascript">
							window.onload = start;
							function start() {
								var authentication = {
									"authentication" : {
										"data" : {
											"children" : [
												{"name" : "token", "value" : "some\\-AuthToken"},
												{"name" : "validUntil", "value" : "600000"},
												{"name" : "renewUntil", "value" : "86400000"},
												{"name" : "userId", "value" : "someIdInUser\\x27Storage"},
												{"name" : "loginId", "value" : "someLogin\\x22Id"},
												{"name" : "firstName", "value" : "someFirst\\x26Name"},
												{"name" : "lastName", "value" : "someLast\\\\Name"}
											],
											"name" : "authToken"
										},
										"actionLinks" : {
											"renew" : {
												"requestMethod" : "POST",
												"rel" : "renew",
												"url" : "http:\\/\\/localhost:8080\\/login\\/rest\\/authToken\\/someTokenId",
												"accept": "application/vnd.uub.authentication+json"
											},
											"delete" : {
												"requestMethod" : "DELETE",
												"rel" : "delete",
												"url" : "http:\\/\\/localhost:8080\\/login\\/rest\\/authToken\\/someTokenId"
											}
										}
									}
								};
								if(null!=window.opener){
									window.opener.postMessage(authentication, "http:\\/\\/localhost:8080");
									window.opener.focus();
									window.close();
								}
							};
						</script>
					</head>
					<body>
						token: someAuthToken
					</body>
				</html>
				""";
	}

	@Test
	public void testRequestMethod() {
		idpFixture.setEPPN("someuser@user.domain.org");

		String answer = idpFixture.getAuthTokenForEPPN();

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", DEFAULT_URL + "login");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", "GET");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestProperty", "eppn",
				"someuser@user.domain.org");
		httpHandlerSpy.MCR.assertReturn("getResponseText", 0, answer);
	}

	@Test
	public void testRequestMethodOther() {
		String otherUrl = "http://localhost:8380/notthesameurl/";
		SystemUrl.setIdpLoginUrl(otherUrl);
		idpFixture.setEPPN("other@user.domain.org");

		String answer = idpFixture.getAuthTokenForEPPN();

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", otherUrl + "login");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", "GET");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestProperty", "eppn",
				"other@user.domain.org");
		httpHandlerSpy.MCR.assertReturn("getResponseText", 0, answer);

	}

	@Test
	public void testGetStatusTypeIsFromServer() {
		idpFixture.getAuthTokenForEPPN();

		StatusType responseCode = idpFixture.getResponseCode();

		assertEquals(responseCode.toString(), "OK");
	}

	@Test
	public void testGetStatusTypeIsFromServerError() {
		HttpHandlerSpy errorHttpHandler = new HttpHandlerSpy();
		errorHttpHandler.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> 400);
		httpHandlerFactorySpy.MRV.setDefaultReturnValuesSupplier("factor", () -> errorHttpHandler);

		idpFixture.getAuthTokenForEPPN();

		StatusType responseCode = idpFixture.getResponseCode();
		assertEquals(responseCode.toString(), "Bad Request");
	}

	@Test
	public void testFactorConvertJsonToBasicAuthentication() {
		idpFixture.getAuthTokenForEPPN();

		String expectedDataToConvert = """
				{
					"authentication" : {
						"data" : {
							"children" : [
								{"name" : "token", "value" : "some-AuthToken"},
								{"name" : "validUntil", "value" : "600000"},
								{"name" : "renewUntil", "value" : "86400000"},
								{"name" : "userId", "value" : "someIdInUser'Storage"},
								{"name" : "loginId", "value" : "someLogin"Id"},
								{"name" : "firstName", "value" : "someFirst&Name"},
								{"name" : "lastName", "value" : "someLast\\Name"}
							],
							"name" : "authToken"
						},
						"actionLinks" : {
							"renew" : {
								"requestMethod" : "POST",
								"rel" : "renew",
								"url" : "http://localhost:8080/login/rest/authToken/someTokenId",
								"accept": "application/vnd.uub.authentication+json"
							},
							"delete" : {
								"requestMethod" : "DELETE",
								"rel" : "delete",
								"url" : "http://localhost:8080/login/rest/authToken/someTokenId"
							}
						}
					}
				}""";

		String dataAsJson = (String) jsonToDataConverterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter("factorUsingString", 0, "json");
		assertEquals(compactString(dataAsJson), compactString(expectedDataToConvert));

	}

	private String compactString(String stringIn) {
		return stringIn.replace("\s", "").replace("\n", "").replace("\t", "");
	}

	@Test
	public void testCallConverter() {
		idpFixture.getAuthTokenForEPPN();

		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testFetchDataFromDataAuthentication() {
		idpFixture.getAuthTokenForEPPN();

		clientDataAutentication.MCR.assertReturn("getToken", 0, idpFixture.getAuthToken());
		clientDataAutentication.MCR.assertReturn("getLoginId", 0, idpFixture.getLoginId());
		clientDataAutentication.MCR.assertReturn("getUserId", 0, idpFixture.getUserId());
		clientDataAutentication.MCR.assertReturn("getValidUntil", 0, idpFixture.getValidUntil());
		clientDataAutentication.MCR.assertReturn("getRenewUntil", 0, idpFixture.getRenewUntil());
		clientDataAutentication.MCR.assertReturn("getFirstName", 0, idpFixture.getFirstName());
		clientDataAutentication.MCR.assertReturn("getLastName", 0, idpFixture.getLastName());
	}

	@Test
	public void testGetDeleteUrlFromServerAnswer() {
		ClientActionLinkSpy clientActionLink = new ClientActionLinkSpy();
		clientActionLink.MRV.setDefaultReturnValuesSupplier("getURL", () -> "someDeleteUrl");
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> Optional.of(clientActionLink), ClientAction.DELETE);
		idpFixture.getAuthTokenForEPPN();

		String deleteURL = idpFixture.getDeleteUrl();

		assertEquals(deleteURL, "someDeleteUrl");
	}

	@Test
	public void testDeleteUrlFromServerAnswerForMissingURL() {
		idpFixture.getAuthTokenForEPPN();
		String idInUserStorage = idpFixture.getDeleteUrl();

		assertEquals(idInUserStorage, "Delete URL missing.");
	}

	@Test
	public void testGetRenewUrlFromServerAnswer() {
		ClientActionLinkSpy clientActionLink = new ClientActionLinkSpy();
		clientActionLink.MRV.setDefaultReturnValuesSupplier("getURL", () -> "someRenewUrl");
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> Optional.of(clientActionLink), ClientAction.RENEW);
		idpFixture.getAuthTokenForEPPN();

		String renewURL = idpFixture.getRenewUrl();

		assertEquals(renewURL, "someRenewUrl");
	}

	@Test
	public void testRenewUrlFromServerAnswerForMissingURL() {
		idpFixture.getAuthTokenForEPPN();
		String idInUserStorage = idpFixture.getRenewUrl();

		assertEquals(idInUserStorage, "Renew URL missing.");
	}
}
