/*
 * Copyright 2017 Uppsala University Library
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

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.fitnesseintegration.compare.PermissionComparerFactory;
import se.uu.ub.cora.fitnesseintegration.compare.PermissionComparerFactoryImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
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
	public void testFactorJsonToDataConverterFactory() {
		DependencyProvider.setJsonToDataFactoryClassName(
				"se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactoryImp");
		JsonToDataConverterFactory factored = DependencyProvider.getJsonToDataConverterFactory();
		assertTrue(factored instanceof JsonToDataConverterFactoryImp);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorJsonToDataConverterFactoryNonExistingClassName() {
		DependencyProvider.setJsonToDataFactoryClassName("se.uu.ub.cora.fitnesse.DoesNotExistImp");
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testFactorJsonToDataConverterFactoryClassNameNotSet() {
		DependencyProvider.setJsonToDataFactoryClassName(null);
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
	public void testGetJsonToDataRecordConverter() {
		JsonToDataRecordConverterImp jsonToDataRecordConverter = (JsonToDataRecordConverterImp) DependencyProvider
				.getJsonToDataRecordConverter();
		assertSame(jsonToDataRecordConverter.getConverterFactory(),
				DependencyProvider.getJsonToDataConverterFactory());
	}

	@Test
	public void testGetJsonHandler() {
		JsonHandlerImp jsonHandler = (JsonHandlerImp) DependencyProvider.getJsonHandler();
		assertTrue(jsonHandler.getJsonParser() instanceof OrgJsonParser);
	}

	@Test
	public void testPermissionComparerFactory() {
		DependencyProvider.setPermissionComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesseintegration.compare.PermissionComparerFactoryImp");
		PermissionComparerFactory permissionComparerFactory = DependencyProvider
				.getPermissionsComparerFactory();
		assertTrue(permissionComparerFactory instanceof PermissionComparerFactoryImp);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testPermissionComparerFacatoryNonExistingClassName() {
		DependencyProvider.setPermissionComparerFactoryUsingClassName(
				"se.uu.ub.cora.fitnesse.DoesNotExistImp");
	}
}
