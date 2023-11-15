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

package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.ChildComparer;
import se.uu.ub.cora.fitnesseintegration.ChildComparerImp;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.compare.ComparerFactory;
import se.uu.ub.cora.fitnesseintegration.compare.ComparerFactoryImp;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactory;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactoryImp;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.StandardFitnesseMethodSpy;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DependencyProviderTest {
	private static final String SOME_TYPE = "someType";
	private static final String SOME_ID = "someId";
	private static final String SOME_AUTH_TOKEN = "someAuthToken";

	@AfterMethod
	private void afterMethod() {
		DependencyFactory dependencyFactory = new DependencyFactoryImp();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

	}

	@Test
	public void testConstructor() {
		DependencyProvider dependencyProvider = new DependencyProvider();
		assertTrue(dependencyProvider instanceof DependencyProvider);
	}

	@Test
	public void testDependencyFactory() throws Exception {
		DependencyFactory dependencyFactory = DependencyProvider.onlyForTestGetDependencyFactory();
		assertTrue(dependencyFactory instanceof DependencyFactoryImp);
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
	public void testFactorReadAndStoreRecord() throws Exception {
		DependencyFactorySpy dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		StandardFitnesseMethodSpy readAndStore = (StandardFitnesseMethodSpy) DependencyProvider
				.factorReadAndStoreRecord(SOME_AUTH_TOKEN, SOME_TYPE, SOME_ID);

		dependencyFactory.MCR.assertReturn("factorReadAndStoreRecord", 0, readAndStore);
	}

	@Test
	public void testFactorReadAndStoreRecordAsJson() throws Exception {
		DependencyFactorySpy dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		StandardFitnesseMethodSpy readAndStore = (StandardFitnesseMethodSpy) DependencyProvider
				.factorReadAndStoreRecordAsJson(SOME_AUTH_TOKEN, SOME_TYPE, SOME_ID);

		dependencyFactory.MCR.assertReturn("factorReadAndStoreRecordAsJson", 0, readAndStore);
	}

	@Test
	public void testFactorWaiter() throws Exception {
		DependencyFactorySpy dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		Waiter waiter = DependencyProvider.factorWaiter();

		dependencyFactory.MCR.assertReturn("factorWaiter", 0, waiter);
	}
}
