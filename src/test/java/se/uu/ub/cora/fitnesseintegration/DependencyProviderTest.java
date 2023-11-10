/*
 * Copyright 2017, 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNotSame;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.compare.ComparerFactory;
import se.uu.ub.cora.fitnesseintegration.compare.ComparerFactoryImp;
import se.uu.ub.cora.fitnesseintegration.internal.ReadAndStoreRecord;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DependencyProviderTest {
	@Test
	public void testConstructor() {
		DependencyProvider dependencyProvider = new DependencyProvider();
		assertTrue(dependencyProvider instanceof DependencyProvider);
	}

	@Test
	public void testFactorHttpHandler() {
		DependencyProvider
				.setHttpHandlerFactoryClassName("se.uu.ub.cora.httphandler.HttpHandlerFactoryImp");
		HttpHandlerFactory factored = DependencyProvider.getHttpHandlerFactory();
		assertTrue(factored instanceof HttpHandlerFactoryImp);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorHttpHandlerNonExistingClassName() {
		DependencyProvider.setHttpHandlerFactoryClassName("se.uu.ub.cora.fitnesse.DoesNotExistImp");
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorHttpHandlerClassNameNotSet() {
		DependencyProvider.setHttpHandlerFactoryClassName(null);
	}

	@Test
	public void testChildComparer() {
		DependencyProvider.setChildComparerUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.ChildComparerImp");
		ChildComparer childComparer = DependencyProvider.getChildComparer();
		assertTrue(childComparer instanceof ChildComparerImp);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testChildComparerNonExistingClassName() {
		DependencyProvider.setChildComparerUsingClassName("se.uu.ub.cora.fitnesse.DoesNotExistImp");
	}

	@Test
	public void testGetJsonHandler() {
		JsonHandlerImp jsonHandler = (JsonHandlerImp) DependencyProvider.getJsonHandler();
		assertTrue(jsonHandler.onlyForTestGetJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testPermissionComparerFactory() {
		DependencyProvider.setComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.compare.ComparerFactoryImp");
		ComparerFactory permissionComparerFactory = DependencyProvider.getComparerFactory();
		assertTrue(permissionComparerFactory instanceof ComparerFactoryImp);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testPermissionComparerFacatoryNonExistingClassName() {
		DependencyProvider
				.setComparerFactoryUsingClassName("se.uu.ub.cora.fitnesse.DoesNotExistImp");
	}

	@Test
	public void testGetWaiter() throws Exception {
		Waiter waiter = DependencyProvider.getWaiter();
		assertTrue(waiter instanceof WaiterImp);
	}

	@Test
	public void testGetWaiterNewInstanceOnEachCall() throws Exception {
		Waiter waiter1 = DependencyProvider.getWaiter();
		Waiter waiter2 = DependencyProvider.getWaiter();
		assertNotSame(waiter1, waiter2);
	}

	@Test
	public void testSetWaiter() throws Exception {
		Waiter waiterSpy = new WaiterSpy();
		DependencyProvider.onlyForTestSetWaiter(waiterSpy);
		Waiter waiter = DependencyProvider.getWaiter();

		assertSame(waiter, waiterSpy);
	}

	@Test
	public void testFactorReadAndStoreRecord() throws Exception {

		JavaClientFactorySpy javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);

		String someBaseUrl = "someBaseUrl";
		String someAppTokenUrl = "someAppTokenUrl";
		SystemUrl.setUrl(someBaseUrl);
		SystemUrl.setAppTokenVerifierUrl(someAppTokenUrl);

		String authToken = "someAuthToken";
		String type = "someType";
		String id = "someId";

		ReadAndStoreRecord readAndStore = DependencyProvider.factorReadAndStoreRecord(authToken,
				type, id);

		assertNotNull(readAndStore);
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				someBaseUrl, someAppTokenUrl, authToken);
		javaClientFactory.MCR.methodWasCalled("factorDataClientUsingAuthTokenCredentials");
		javaClientFactory.MCR.assertParameterAsEqual("factorDataClientUsingAuthTokenCredentials", 0,
				"authTokenCredentials", authTokenCredentials);

		assertSame(readAndStore.onlyForTestGetDataClient(), javaClientFactory.MCR
				.getReturnValue("factorDataClientUsingAuthTokenCredentials", 0));
		assertSame(readAndStore.onlyForTestGetType(), type);
		assertSame(readAndStore.onlyForTestGetId(), id);

	}
}
