package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.definitionwriter.MetadataHolder;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.MetadataHolderSpy;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class MetadataProviderTest {

	private static final String SOME_AUTH_TOKEN = "someToken";
	private JavaClientFactorySpy javaClientFactory;
	private DataClientSpy dataClient;

	@BeforeMethod
	public void setup() {
		MetadataProvider.onlyForTestSetHolder(null);
		setupDataClient();
	}

	@Test
	public void testHolderPopulated() throws Exception {
		MetadataHolder holder = MetadataProvider.getHolder(SOME_AUTH_TOKEN);
		javaClientFactory.MCR
				.assertMethodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		assertTrue(holder instanceof MetadataHolder);
	}

	@Test
	public void testGetHolderTwicePopulateOnlyOnce() throws Exception {
		MetadataHolder holder = MetadataProvider.getHolder(SOME_AUTH_TOKEN);
		MetadataProvider.getHolder(SOME_AUTH_TOKEN);

		assertTrue(holder instanceof MetadataHolder);
		javaClientFactory.MCR.assertNumberOfCallsToMethod(
				"factorDataClientUsingJavaClientAuthTokenCredentials", 1);
	}

	@Test
	public void testOnlyForTestHolder() throws Exception {
		MetadataHolderSpy holder = new MetadataHolderSpy();
		MetadataProvider.onlyForTestSetHolder(holder);
		MetadataHolder fetchedHolder = MetadataProvider.getHolder(SOME_AUTH_TOKEN);

		javaClientFactory.MCR.assertNumberOfCallsToMethod(
				"factorDataClientUsingJavaClientAuthTokenCredentials", 0);
		assertEquals(fetchedHolder, holder);
	}

	private void setupDataClient() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		dataClient = new DataClientSpy();
		javaClientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAuthTokenCredentials", () -> dataClient);
	}

}
