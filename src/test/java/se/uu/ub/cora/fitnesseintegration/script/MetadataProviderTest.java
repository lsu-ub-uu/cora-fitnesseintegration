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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.cache.MetadataHolder;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.MetadataHolderSpy;

public class MetadataProviderTest {
	private DataClientSpy dataClient;

	@BeforeMethod
	public void setup() {
		MetadataProvider.onlyForTestSetHolder(null);
		setupDataClient();
	}

	@AfterMethod
	public void afterMethod() {
		FitnesseJavaClientProvider.removeAllCreateClients();
	}

	private void setupDataClient() {
		dataClient = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, dataClient);
	}

	@Test
	public void testHolderPopulated() {
		MetadataHolder holder = MetadataProvider.getHolder();
		assertTrue(holder instanceof MetadataHolder);
	}

	@Test
	public void testGetHolderTwicePopulateOnlyOnce() {
		MetadataHolder holder = MetadataProvider.getHolder();
		MetadataProvider.getHolder();

		assertTrue(holder instanceof MetadataHolder);
		dataClient.MCR.assertNumberOfCallsToMethod("readList", 1);
	}

	@Test
	public void testOnlyForTestHolder() {
		MetadataHolderSpy holder = new MetadataHolderSpy();
		MetadataProvider.onlyForTestSetHolder(holder);
		MetadataHolder fetchedHolder = MetadataProvider.getHolder();

		dataClient.MCR.assertNumberOfCallsToMethod("readList", 0);
		assertEquals(fetchedHolder, holder);
	}

}
