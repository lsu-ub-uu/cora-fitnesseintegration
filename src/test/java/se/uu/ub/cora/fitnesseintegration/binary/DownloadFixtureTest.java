package se.uu.ub.cora.fitnesseintegration.binary;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerSpy;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.spies.HttpHandlerSpy;

public class DownloadFixtureTest {

	private static final String SOME_TYPE = "someType";
	private static final String SOME_ID = "someId";
	private static final String SOME_AUTH_TOKEN = "someAuthToken";
	private HttpHandlerSpy httpHandler;
	private DownloadFixture fixture;
	private RecordHandlerSpy recordHandler;

	@BeforeMethod
	private void beforeMethod() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8080/appTokenVerifier/");
		recordHandler = new RecordHandlerSpy();

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
		fixture.setAuthToken(SOME_AUTH_TOKEN);
		fixture.setType(SOME_TYPE);
		fixture.setId(SOME_ID);
		fixture.setRepresentation("someRepresentation");

		String status = fixture.testDownload();

		assertEquals(status, "200");

		// assertEquals(fixture.getMimeType(), "someMimeType");
		// assertEquals(fixture.getContentLength(), "100");
		// assertEquals(fixture.getContentDisposition(), "someContentDisposition");
	}

	@Test
	public void testDownloadNotOk() throws Exception {
		recordHandler.MRV.setDefaultReturnValuesSupplier("getResponseCode", () -> 500);

		String status = fixture.testDownload();

		assertEquals(status, "500");

	}
}
