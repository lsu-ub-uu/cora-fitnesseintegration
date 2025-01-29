package se.uu.ub.cora.fitnesseintegration.authentication;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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

	private static final String FITNESSE_ADMIN_LOGIN_ID = "fitnesseAdmin@system.cora.uu.se";
	private static final String POST = "POST";
	private static final int CREATED = 201;
	private static final int UNAUTHORIZED = 401;

	private String BASE_LOGIN_URL = "http://localhost:8080/login/";
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private HttpHandlerSpy httpHandlerSpy;
	private ClientDataAuthenticationSpy clientDataAutentication;
	private JsonToClientDataConverterFactorySpy jsonToDataConverterFactory;
	private JsonToClientDataConverterSpy jsonToClientDataConverter;
	private AuthenticationFixture authenticationFixture;

	@BeforeMethod
	public void beforeMethod() {
		SystemUrl.setAppTokenVerifierUrl(BASE_LOGIN_URL);
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

		jsonToClientDataConverter = new JsonToClientDataConverterSpy();
		jsonToClientDataConverter.MRV.setDefaultReturnValuesSupplier("toInstance",
				() -> clientDataAutentication);

		jsonToDataConverterFactory = new JsonToClientDataConverterFactorySpy();
		jsonToDataConverterFactory.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> jsonToClientDataConverter);
	}

	@Test
	public void testFitnesseAdminAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		authenticationFixture.setLoginId(FITNESSE_ADMIN_LOGIN_ID);

		authenticationFixture.appTokenLogin();

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/apptoken");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				FITNESSE_ADMIN_LOGIN_ID + "\n29c30232-d514-4559-b60b-6de47175c1df");
		assertEquals(authenticationFixture.getResponseStatus(), "CREATED");
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	@Test
	public void testUnauthorizedAppTokenLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> UNAUTHORIZED);
		authenticationFixture.setLoginId("wackyLogin");
		authenticationFixture.setAppToken("123123");

		authenticationFixture.appTokenLogin();

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/apptoken");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput", "wackyLogin\n123123");
		assertEquals(authenticationFixture.getResponseStatus(), "UNAUTHORIZED");
		httpHandlerSpy.MCR.assertMethodNotCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodNotCalled("toInstance");
	}

	@Test
	public void testGetDataFromApptokenAuthentication() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		ClientActionLinkSpy renewActionLink = new ClientActionLinkSpy();
		clientDataAutentication.MRV.setDefaultReturnValuesSupplier("getRenewLink",
				() -> renewActionLink);

		ClientActionLinkSpy deleteActionLink = new ClientActionLinkSpy();
		clientDataAutentication.MRV.setDefaultReturnValuesSupplier("getDeleteLink",
				() -> deleteActionLink);

		authenticationFixture.appTokenLogin();

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
		// clientDataAutentication.MCR.assertReturn("getRenewLink", 0,
		// authenticationFixture.getRenewLink());
		// clientDataAutentication.MCR.assertReturn("getDeleteLink", 0,
		// authenticationFixture.getDeleteLink());
	}

	@Test
	public void testPasswordLogin() {
		httpHandlerSpy.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> CREATED);
		authenticationFixture.setLoginId(FITNESSE_ADMIN_LOGIN_ID);

		authenticationFixture.appTokenLogin();

		httpHandlerSpy = (HttpHandlerSpy) httpHandlerFactorySpy.MCR
				.assertCalledParametersReturn("factor", BASE_LOGIN_URL + "rest/apptoken");
		httpHandlerSpy.MCR.assertCalledParameters("setRequestMethod", POST);
		httpHandlerSpy.MCR.assertCalledParameters("setOutput",
				FITNESSE_ADMIN_LOGIN_ID + "\n29c30232-d514-4559-b60b-6de47175c1df");
		assertEquals(authenticationFixture.getResponseStatus(), "CREATED");
		httpHandlerSpy.MCR.assertMethodWasCalled("getResponseText");
		jsonToClientDataConverter.MCR.assertMethodWasCalled("toInstance");
	}

	// private String getAuthTokenJson() {
	// return """
	// {
	// "authentication": {
	// "data": {
	// "children": [
	// {
	// "name": "token",
	// "value": "some-AuthToken"
	// },
	// {
	// "name": "validUntil",
	// "value": "600000"
	// },
	// {
	// "name": "renewUntil",
	// "value": "86400000"
	// },
	// {
	// "name": "userId",
	// "value": "someIdInUserStorage"
	// },
	// {
	// "name": "loginId",
	// "value": "someLoginId"
	// },
	// {
	// "name": "firstName",
	// "value": "someFirst&Name"
	// },
	// {
	// "name": "lastName",
	// "value": "someLastName"
	// }
	// ],
	// "name": "authToken"
	// },
	// "actionLinks": {
	// "renew": {
	// "requestMethod": "POST",
	// "rel": "renew",
	// "url": "http://localhost:8080/login/rest/authToken/someTokenId",
	// "accept": "application/vnd.uub.authentication+json"
	// },
	// "delete": {
	// "requestMethod": "DELETE",
	// "rel": "delete",
	// "url": "http://localhost:8080/login/rest/authToken/someTokenId"
	// }
	// }
	// }
	// }""";
	// }
}
