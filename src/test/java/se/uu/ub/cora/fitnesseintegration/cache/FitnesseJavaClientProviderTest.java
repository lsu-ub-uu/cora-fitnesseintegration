/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.cache;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.LoginToken;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class FitnesseJavaClientProviderTest {
	private static final String FACTOR_DATA_CLIENT_METHOD_NAME = "factorDataClientUsingJavaClientAppTokenCredentials";
	private JavaClientFactorySpy clientFactory;

	@BeforeMethod
	public void beforeMethod() {
		clientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(clientFactory);
		SystemUrl.setUrl("someBaseURL");
		SystemUrl.setAppTokenVerifierUrl("someAppTokenURL");
		LoginToken.setFitnesseAdminLoginId("someFitnesseAdminLoginId");
		LoginToken.setFitnesseAdminAppToken("someFitnesseAdminAppToken");
	}

	@AfterMethod
	public void afterMethod() {
		FitnesseJavaClientProvider.removeAllCreateClients();
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<FitnesseJavaClientProvider> constructor = FitnesseJavaClientProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<FitnesseJavaClientProvider> constructor = FitnesseJavaClientProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testGetFitnesseAdminDataClientFirstCall_ClientCreated() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();

		assertCredentialsUsedForCreatedUserIsFromSystemUrlAndLoginToken();
		assertReturnedClientIsFromClientProvider(client);
	}

	@Test
	public void testGetFitnesseAdminDataClientSecondCall_ClientReturned() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		DataClient client2 = FitnesseJavaClientProvider.getFitnesseAdminDataClient();

		clientFactory.MCR.assertNumberOfCallsToMethod(FACTOR_DATA_CLIENT_METHOD_NAME, 1);
		assertSame(client2, client);
	}

	@Test
	public void onlyForTestSetClient() throws Exception {
		DataClientSpy spyClient = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, spyClient);

		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		assertSame(spyClient, client);
	}

	private void assertReturnedClientIsFromClientProvider(DataClient client) {
		clientFactory.MCR.assertReturn(FACTOR_DATA_CLIENT_METHOD_NAME, 0, client);
	}

	private void assertCredentialsUsedForCreatedUserIsFromSystemUrlAndLoginToken() {
		JavaClientAppTokenCredentials cred = createAppTokenCredentialsFromSystemUrlAndLoginToken();
		clientFactory.MCR.assertParameterAsEqual(FACTOR_DATA_CLIENT_METHOD_NAME, 0,
				"javaClientAppTokenCredentials", cred);
	}

	private JavaClientAppTokenCredentials createAppTokenCredentialsFromSystemUrlAndLoginToken() {
		return new JavaClientAppTokenCredentials(SystemUrl.getRestUrl(),
				SystemUrl.getAppTokenVerifierRestUrl(), LoginToken.getFitnesseAdminLoginId(),
				LoginToken.getFitnesseAdminAppToken());
	}
}
