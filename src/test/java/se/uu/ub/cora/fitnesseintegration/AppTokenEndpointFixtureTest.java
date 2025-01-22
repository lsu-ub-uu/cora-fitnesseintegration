package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class AppTokenEndpointFixtureTest {
	private HttpHandlerFactoryOldSpy httpHandlerFactorySpy;
	private AppTokenEndpointFixture fixture;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8080/login/");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactoryOldSpy");
		httpHandlerFactorySpy = (HttpHandlerFactoryOldSpy) DependencyProvider.getHttpHandlerFactory();
		fixture = new AppTokenEndpointFixture();
	}

	@Test
	public void testGetAuthTokenForAppToken() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setLoginId("someUserId");
		fixture.setAppToken("02a89fd5-c768-4209-9ecc-d80bd793b01e");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerOldSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		String credentials = """
				someUserId
				02a89fd5-c768-4209-9ecc-d80bd793b01e""";
		assertEquals(httpHandlerSpy.outputString, credentials);
		assertEquals(httpHandlerFactorySpy.urlString, "http://localhost:8080/login/rest/apptoken");
		assertEquals(json,
				"{\"children\":[{\"name\":\"token\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
						+ ",{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}");
		assertEquals(fixture.getAuthToken(), "a1acff95-5849-4e10-9ee9-4b192aef17fd");
		assertEquals(fixture.getStatusType(), Response.Status.CREATED);
	}

	@Test
	public void testGetAuthTokenForFitnesseAdmin() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setLoginId("fitnesseAdmin@system.cora.uu.se");
		fixture.setAppToken("");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerOldSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		String credentials = """
				fitnesseAdmin@system.cora.uu.se
				29c30232-d514-4559-b60b-6de47175c1df""";
		assertEquals(httpHandlerSpy.outputString, credentials);
		assertEquals(httpHandlerFactorySpy.urlString, "http://localhost:8080/login/rest/apptoken");
		assertEquals(json,
				"{\"children\":[{\"name\":\"token\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
						+ ",{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}");
		assertEquals(fixture.getAuthToken(), "a1acff95-5849-4e10-9ee9-4b192aef17fd");
		assertEquals(fixture.getStatusType(), Response.Status.CREATED);
	}

	@Test
	public void testGetAuthTokenForFitnesseUser() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setLoginId("fitnesseUser@system.cora.uu.se");
		fixture.setAppToken("");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerOldSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		String credentials = """
				fitnesseUser@system.cora.uu.se
				bd699488-f9d1-419d-a79d-9fa8a0f3bb9d""";
		assertEquals(httpHandlerSpy.outputString, credentials);
		assertEquals(httpHandlerFactorySpy.urlString, "http://localhost:8080/login/rest/apptoken");
		assertEquals(json,
				"{\"children\":[{\"name\":\"token\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
						+ ",{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}");
		assertEquals(fixture.getAuthToken(), "a1acff95-5849-4e10-9ee9-4b192aef17fd");
		assertEquals(fixture.getStatusType(), Response.Status.CREATED);
	}

	@Test
	public void testCreateRecordNotOk() {
		httpHandlerFactorySpy.changeFactoryToFactorInvalidHttpHandlers();
		assertEquals(fixture.getAuthTokenForAppToken(), "bad things happend");
	}

	@Test
	public void testRemoveAuthTokenForUser() {
		fixture.setAuthTokenToLogOut("someAuthToken");
		fixture.setDeleteUrl("http://localhost:8180/login/rest/someTokenId");

		fixture.removeAuthTokenForUser();

		assertCallToDeleteAuthToken();
		assertEquals(fixture.getStatusType(), Response.Status.OK);
	}

	@Test
	public void testRemoveAuthTokenForUserNotOk() {
		httpHandlerFactorySpy.setResponseCode(404);
		fixture.setAuthTokenToLogOut("someAuthToken");
		fixture.setDeleteUrl("http://localhost:8180/login/rest/someTokenId");

		fixture.removeAuthTokenForUser();

		assertCallToDeleteAuthToken();
		assertEquals(fixture.getStatusType(), Response.Status.NOT_FOUND);
	}

	private void assertCallToDeleteAuthToken() {
		HttpHandlerOldSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "DELETE");
		assertEquals(httpHandlerSpy.requestProperties.get("authToken"), "someAuthToken");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8180/login/rest/someTokenId");
	}
}
