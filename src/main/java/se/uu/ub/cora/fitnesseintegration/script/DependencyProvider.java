/*
 * Copyright 2017, 2023 Uppsala University Library
 * Copyright 2023, 2025 Olov McKie
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

import java.lang.reflect.Constructor;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.ChildComparer;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.compare.DataComparer;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.internal.StandardFitnesseMethod;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactory;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactoryImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public final class DependencyProvider {

	private static HttpHandlerFactory httpHandlerFactory;
	private static ChildComparer childComparer;
	private static DependencyFactory dependencyFactory = new DependencyFactoryImp();

	public DependencyProvider() {
		// needs a public constructor for fitnesse to work
		super();
	}

	public static synchronized void setHttpHandlerFactoryClassName(
			String httpHandlerFactoryClassName) {
		Constructor<?> constructor;
		try {
			constructor = Class.forName(httpHandlerFactoryClassName).getConstructor();
			httpHandlerFactory = (HttpHandlerFactory) constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static HttpHandlerFactory getHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	public static synchronized void setChildComparerUsingClassName(String childComparerClassName) {
		Constructor<?> constructor;
		try {
			constructor = Class.forName(childComparerClassName).getConstructor();
			childComparer = (ChildComparer) constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static ChildComparer getChildComparer() {
		return childComparer;
	}

	public static JsonHandler getJsonHandler() {
		OrgJsonParser jsonParser = new OrgJsonParser();
		return JsonHandlerImp.usingJsonParser(jsonParser);
	}

	public static StandardFitnesseMethod factorReadAndStoreRecord(String authToken, String type,
			String id) {
		return dependencyFactory.factorReadAndStoreRecord(authToken, type, id);
	}

	public static StandardFitnesseMethod factorReadAndStoreRecordAsJson(String authToken,
			String type, String id) {
		return dependencyFactory.factorReadAndStoreRecordAsJson(authToken, type, id);
	}

	public static void onlyForTestSetDependencyFactory(DependencyFactory dependencyFactory) {
		DependencyProvider.dependencyFactory = dependencyFactory;
	}

	public static DependencyFactory onlyForTestGetDependencyFactory() {
		return DependencyProvider.dependencyFactory;
	}

	public static Waiter factorWaiter() {
		return dependencyFactory.factorWaiter();
	}

	public static DefinitionWriter factorDefinitionWriter() {
		return dependencyFactory.factorDefinitionWriter();
	}

	public static DataComparer factorPermissionComparer(ClientDataRecord dataRecord) {
		return dependencyFactory.factorPermissionComparer(dataRecord);
	}

	public static DataComparer factorActionComparer(ClientDataRecord dataRecord) {
		return dependencyFactory.factorActionComparer(dataRecord);
	}

}
