package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class AppTokenEndpointFixtureTest {
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private AppTokenEndpointFixture fixture;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8080/login/");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();
		fixture = new AppTokenEndpointFixture();
	}

	@Test
	public void testGetAuthTokenForAppToken() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setUserId("someUserId");
		fixture.setAppToken("02a89fd5-c768-4209-9ecc-d80bd793b01e");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "02a89fd5-c768-4209-9ecc-d80bd793b01e");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/login/rest/apptoken/someUserId");
		assertEquals(json,
				"{\"children\":[{\"name\":\"id\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
						+ ",{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}");
		assertEquals(fixture.getAuthToken(), "a1acff95-5849-4e10-9ee9-4b192aef17fd");
		assertEquals(fixture.getStatusType(), Response.Status.CREATED);
	}

	@Test
	public void testGetAuthTokenForFitnesseAdmin() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setUserId("131313");
		fixture.setAppToken("");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "44c17361-ead7-43b5-a938-038765873037");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/login/rest/apptoken/131313");
		assertEquals(json,
				"{\"children\":[{\"name\":\"id\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
						+ ",{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}");
		assertEquals(fixture.getAuthToken(), "a1acff95-5849-4e10-9ee9-4b192aef17fd");
		assertEquals(fixture.getStatusType(), Response.Status.CREATED);
	}

	@Test
	public void testGetAuthTokenForFitnesseUser() {
		httpHandlerFactorySpy.setResponseCode(201);
		fixture.setUserId("121212");
		fixture.setAppToken("");
		String json = fixture.getAuthTokenForAppToken();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "POST");
		assertEquals(httpHandlerSpy.outputString, "a5b9871f-1610-44e1-b838-c37ace6757d6");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/login/rest/apptoken/121212");
		assertEquals(json,
				"{\"children\":[{\"name\":\"id\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"}"
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
		fixture.setUserId("someUserId22");
		fixture.setAuthTokenToLogOut("02a89fd5-c768-4209-9ecc-d80bd793b01e");
		fixture.removeAuthTokenForUser();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "DELETE");
		assertEquals(httpHandlerSpy.outputString, "02a89fd5-c768-4209-9ecc-d80bd793b01e");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/login/rest/apptoken/someUserId22");
		assertEquals(fixture.getStatusType(), Response.Status.OK);
	}

	@Test
	public void testRemoveAuthTokenForUserNotOk() {
		httpHandlerFactorySpy.setResponseCode(404);
		fixture.setUserId("someUserId22");
		fixture.setAuthTokenToLogOut("02a89fd5-c768-4209-9ecc-d80bd793b01e");
		fixture.removeAuthTokenForUser();
		HttpHandlerSpy httpHandlerSpy = httpHandlerFactorySpy.httpHandlerSpy;
		assertEquals(httpHandlerSpy.requestMetod, "DELETE");
		assertEquals(httpHandlerSpy.outputString, "02a89fd5-c768-4209-9ecc-d80bd793b01e");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/login/rest/apptoken/someUserId22");
		assertEquals(fixture.getStatusType(), Response.Status.NOT_FOUND);
	}
}
