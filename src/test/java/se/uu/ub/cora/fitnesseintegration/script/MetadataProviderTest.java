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
package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.cache.MetadataHolder;
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
	public void testHolderPopulated() {
		MetadataHolder holder = MetadataProvider.getHolder(SOME_AUTH_TOKEN);
		javaClientFactory.MCR
				.assertMethodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		assertTrue(holder instanceof MetadataHolder);
	}

	@Test
	public void testGetHolderTwicePopulateOnlyOnce() {
		MetadataHolder holder = MetadataProvider.getHolder(SOME_AUTH_TOKEN);
		MetadataProvider.getHolder(SOME_AUTH_TOKEN);

		assertTrue(holder instanceof MetadataHolder);
		javaClientFactory.MCR.assertNumberOfCallsToMethod(
				"factorDataClientUsingJavaClientAuthTokenCredentials", 1);
	}

	@Test
	public void testOnlyForTestHolder() {
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
