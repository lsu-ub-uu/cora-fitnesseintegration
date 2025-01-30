package se.uu.ub.cora.fitnesseintegration.authentication;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.NoSuchElementException;
import java.util.Optional;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientAction;
import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientActionLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataAuthenticationSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.spies.HttpHandlerFactorySpy;
import se.uu.ub.cora.httphandler.spies.HttpHandlerSpy;

public class AuthenticationFixtureTest {

	private static final String BASE_LOGIN_URL = "http://localhost:8080/login/";
	private static final String IDP_LOGIN_URL = "http://localhost:8380/idplogin/";
	private static final String FITNESSE_ADMIN_LOGIN_ID = "fitnesseAdmin@system.cora.uu.se";
	private static final String FITNESSE_ADMIN_APPTOKEN = "29c30232-d514-4559-b60b-6de47175c1df";
	private static final String FITNESSE_USER_LOGIN_ID = "fitnesseUser@system.cora.uu.se";
	private static final String FITNESSE_USER_APPTOKEN = "bd699488-f9d1-419d-a79d-9fa8a0f3bb9d";
	private static final String NEW_LINE = "\n";
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final int CREATED = 201;
	private static final int UNAUTHORIZED = 401;
	private static final String SOME_RESPONSE_TEXT = "theResponseTextBody";
	private static final String SOME_ERROR_TEXT = "someErrorText";

	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private HttpHandlerSpy httpHandlerSpy;
	private ClientDataAuthenticationSpy clientDataAutentication;
	private JsonToClientDataConverterFactorySpy jsonToDataConverterFactory;
	private JsonToClientDataConverterSpy jsonToClientDataConverter;
	private AuthenticationFixture authenticationFixture;

	@BeforeMethod
	public void beforeMethod() {
		SystemUrl.setAppTokenVerifierUrl(BASE_LOGIN_URL);
		SystemUrl.setIdpLoginUrl(IDP_LOGIN_URL);
		setJsonToClientDataConverterFactory();

		JsonToClientDataConverterProvider.setJsonToDataConverterFactory(jsonToDataConverterFactory);
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.httphandler.spies.HttpHandlerFactorySpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();

		httpHandlerSpy = new HttpHandlerSpy();
		httpHandlerFactorySpy.MRV.setDefaultReturnValuesSupplier("factor", () -> httpHandlerSpy);

		authenticationFixture = new AuthenticationFixture();
	}

	private void setJsonToClientDataConverterFactory() {
		clientDataAutentication = new ClientDataAuthenticationSpy();
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> createClientActionLink(ClientAction.RENEW), ClientAction.RENEW);
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> createClientActionLink(ClientAction.DELETE), ClientAction.DELETE);

		jsonToClientDataConverter = new JsonToClientDataConverterSpy();
		jsonToClientDataConverter.MRV.setDefaultReturnValuesSupplier("toInstance",
				() -> clientDataAutentication);

		jsonToDataConverterFactory = new JsonToClientDataConverterFactorySpy();
		jsonToDataConverterFactory.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> jsonToClientDataConverter);
	}

	private Optional<ClientActionLinkSpy> createClientActionLink(ClientAction action) {
		Optional<ClientActionLinkSpy> actionLink = Optional.of(new ClientActionLinkSpy());
		actionLink.get().MRV.setDefaultReturnValuesSupplier("getURL",
				() -> "some" + action.name() + "Url");
		actionLink.get().MRV.setDefaultReturnValuesSupplier("getRequestMethod",
				() -> "some" + action.name() + "RequestMethod");
		actionLink.get().MRV.setDefaultReturnValuesSupplier("getContentType",
				() -> "some" + action.name() + "ContentType");
		actionLink.get().MRV.setDefaultReturnValuesSupplier("getAccept",
				() -> "some" + action.name() + "Accept");

		return actionLink;
	}

	@Test
	public void testFitnesseAdminAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> SOME_RESPONSE_TEXT);
		authenticationFixture.setLoginId(FITNESSE_ADMIN_LOGIN_ID);

		assertEquals(authenticationFixture.appTokenLogin(), SOME_RESPONSE_TEXT);

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/apptoken");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				FITNESSE_ADMIN_LOGIN_ID + NEW_LINE + FITNESSE_ADMIN_APPTOKEN);
		assertEquals(authenticationFixture.getResponseStatus(), Status.CREATED);
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testFitnesseUserAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> SOME_RESPONSE_TEXT);
		authenticationFixture.setLoginId(FITNESSE_USER_LOGIN_ID);

		assertEquals(authenticationFixture.appTokenLogin(), SOME_RESPONSE_TEXT);

		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				FITNESSE_USER_LOGIN_ID + NEW_LINE + FITNESSE_USER_APPTOKEN);
		assertEquals(authenticationFixture.getResponseStatus(), Status.CREATED);
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testFitnesseOtherUserAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> SOME_RESPONSE_TEXT);
		authenticationFixture.setLoginId("someOtherUser");
		authenticationFixture.setAppToken("123123");

		assertEquals(authenticationFixture.appTokenLogin(), SOME_RESPONSE_TEXT);

		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				"someOtherUser" + NEW_LINE + "123123");
		assertEquals(authenticationFixture.getResponseStatus(), Status.CREATED);
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testFitnesseAppTokenEmptyAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> UNAUTHORIZED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getErrorText", () -> SOME_ERROR_TEXT);
		authenticationFixture.setLoginId("someOtherUser");
		authenticationFixture.setAppToken("");

		assertEquals(authenticationFixture.appTokenLogin(), SOME_ERROR_TEXT);

		httpHandlerSpy.MCR.assertCalledParameters("setOutput", "someOtherUser" + NEW_LINE + "");
		assertEquals(authenticationFixture.getResponseStatus(), Status.UNAUTHORIZED);
		httpHandlerSpy.MCR.assertMethodNotCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodNotCalled("toInstance");
	}

	@Test
	public void testUnauthorizedAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> UNAUTHORIZED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getErrorText", () -> SOME_ERROR_TEXT);
		authenticationFixture.setLoginId("wackyLogin");
		authenticationFixture.setAppToken("123123");

		assertEquals(authenticationFixture.appTokenLogin(), SOME_ERROR_TEXT);

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/apptoken");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput", "wackyLogin\n123123");
		assertEquals(authenticationFixture.getResponseStatus(), Status.UNAUTHORIZED);
		httpHandlerSpy.MCR.assertMethodNotCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodNotCalled("toInstance");
	}

	@Test
	public void testGetDataFromApptokenAuthentication() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		authenticationFixture.appTokenLogin();
		assertAuthenticationData();
	}

	private void assertAuthenticationData() {
		clientDataAutentication.MCR.assertReturn("getToken", 0, authenticationFixture.getToken());
		clientDataAutentication.MCR.assertReturn("getLoginId", 0,
				authenticationFixture.getLoginId());
		clientDataAutentication.MCR.assertReturn("getUserId", 0, authenticationFixture.getUserId());
		clientDataAutentication.MCR.assertReturn("getValidUntil", 0,
				authenticationFixture.getValidUntil());
		clientDataAutentication.MCR.assertReturn("getRenewUntil", 0,
				authenticationFixture.getRenewUntil());
		clientDataAutentication.MCR.assertReturn("getFirstName", 0,
				authenticationFixture.getFirstName());
		clientDataAutentication.MCR.assertReturn("getLastName", 0,
				authenticationFixture.getLastName());
		assertClientActionLinks();
	}

	@SuppressWarnings("unchecked")
	private void assertClientActionLinks() {
		Optional<ClientActionLink> renewLink = (Optional<ClientActionLink>) clientDataAutentication.MCR
				.assertCalledParametersReturn("getActionLink", ClientAction.RENEW);
		assertEquals(renewLink.get().getURL(), authenticationFixture.getRenewUrl());
		assertEquals(renewLink.get().getRequestMethod(),
				authenticationFixture.getRenewRequestMethod());
		assertEquals(renewLink.get().getContentType(), authenticationFixture.getRenewContentType());
		assertEquals(renewLink.get().getAccept(), authenticationFixture.getRenewAccept());

		Optional<ClientActionLink> deleteLink = (Optional<ClientActionLink>) clientDataAutentication.MCR
				.assertCalledParametersReturn("getActionLink", ClientAction.DELETE);
		assertEquals(deleteLink.get().getURL(), authenticationFixture.getDeleteUrl());
		assertEquals(deleteLink.get().getRequestMethod(),
				authenticationFixture.getDeleteRequestMethod());
		assertEquals(deleteLink.get().getContentType(),
				authenticationFixture.getDeleteContentType());
		assertEquals(deleteLink.get().getAccept(), authenticationFixture.getDeleteAccept());
	}

	@Test
	public void testPasswordLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> SOME_RESPONSE_TEXT);
		authenticationFixture.setLoginId(FITNESSE_ADMIN_LOGIN_ID);
		authenticationFixture.setPassword("somePassword");

		assertEquals(authenticationFixture.passwordLogin(), SOME_RESPONSE_TEXT);

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/password");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				FITNESSE_ADMIN_LOGIN_ID + "\nsomePassword");
		assertEquals(authenticationFixture.getResponseStatus(), Status.CREATED);
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testGetDataFromPasswordAuthentication() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		authenticationFixture.passwordLogin();
		assertAuthenticationData();
	}

	@Test
	public void testUnauthorizedPasswordLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> UNAUTHORIZED);
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getErrorText", () -> SOME_ERROR_TEXT);
		authenticationFixture.setLoginId("wackyUser");
		authenticationFixture.setPassword("someBadPassword");

		assertEquals(authenticationFixture.passwordLogin(), SOME_ERROR_TEXT);

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/password");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput", "wackyUser" + "\nsomeBadPassword");
		assertEquals(authenticationFixture.getResponseStatus(), Status.UNAUTHORIZED);
		httpHandlerSpy.MCR.assertMethodNotCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodNotCalled("toInstance");

	}

	@Test
	public void testIdpLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				() -> SOME_RESPONSE_TEXT);
		authenticationFixture.setEPPN("someEppn");

		assertEquals(authenticationFixture.idpLogin(), SOME_RESPONSE_TEXT);

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", IDP_LOGIN_URL);
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", GET);
		httpHandlerSpy.MCR.assertCalledParameters("setRequestProperty", "eppn", "someEppn");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestProperty", "sn", "someLastName");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestProperty", "givenName",
				"someFirstName");

		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseCode");
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");

		StatusType responseStatus = authenticationFixture.getResponseStatus();
		assertEquals(responseStatus, Status.OK);
	}

	@Test
	public void testGetDataFromIdpAuthentication() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		authenticationFixture.idpLogin();
		assertAuthenticationData();
	}

	@Test
	public void testAuthenticationJsonIsExtractedCorrectlyForParsing() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseText",
				this::createExpectedHtml);
		authenticationFixture.idpLogin();

		String dataAsJson = (String) jsonToDataConverterFactory.MCR
				.getParameterForMethodAndCallNumberAndParameter("factorUsingString", 0, "json");
		assertEquals(compactString(dataAsJson), compactString(expectedJsonToParse()));
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

	private String expectedJsonToParse() {
		return """
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
	}

	private String compactString(String stringIn) {
		return stringIn.replace("\s", "").replace("\n", "").replace("\t", "");
	}

	@Test
	public void testGetStatusTypeIsFromServerError() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> 400);

		authenticationFixture.idpLogin();

		StatusType responseStatus = authenticationFixture.getResponseStatus();
		assertEquals(responseStatus, Status.BAD_REQUEST);
	}

	@Test
	public void testRenewLinkMissing() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> Optional.empty(), ClientAction.RENEW);

		try {
			authenticationFixture.passwordLogin();
		} catch (Exception e) {
			assertTrue(e instanceof NoSuchElementException);
			assertEquals(e.getMessage(), "Renew actionLink is missing");
		}
	}

	@Test
	public void testDeleteLinkMissing() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		clientDataAutentication.MRV.setSpecificReturnValuesSupplier("getActionLink",
				() -> Optional.empty(), ClientAction.DELETE);

		try {
			authenticationFixture.passwordLogin();
		} catch (Exception e) {
			assertTrue(e instanceof NoSuchElementException);
			assertEquals(e.getMessage(), "Delete actionLink is missing");
		}
	}
}
