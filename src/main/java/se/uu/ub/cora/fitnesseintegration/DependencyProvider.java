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

import java.lang.reflect.Constructor;

import se.uu.ub.cora.fitnesseintegration.compare.ComparerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public final class DependencyProvider {

	private static HttpHandlerFactory httpHandlerFactory;
	// private static JsonToDataConverterFactory jsonToDataConverterFactory;
	private static ChildComparer childComparer;
	private static ComparerFactory permissionComparerFactory;

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

	// public static synchronized void setJsonToDataFactoryClassName(
	// String jsonToDataConverterFactoryClassName) {
	// Constructor<?> constructor;
	// try {
	// constructor = Class.forName(jsonToDataConverterFactoryClassName).getConstructor();
	// jsonToDataConverterFactory = (JsonToDataConverterFactory) constructor.newInstance();
	// } catch (Exception e) {
	// throw new RuntimeException(e);
	// }
	//
	// }

	// public static JsonToDataConverterFactory getJsonToDataConverterFactory() {
	// return jsonToDataConverterFactory;
	// }

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

	// public static JsonToClientDataConverter getJsonToClientDataRecordConverter() {
	// return new JsonToClientDataRecordConverterImp(getJsonToDataConverterFactory());
	// }

	// public static JsonHandler getJsonHandler() {
	// OrgJsonParser jsonParser = new OrgJsonParser();
	// return JsonHandlerImp.usingJsonParser(jsonParser);
	// }

	public static void setComparerFactoryUsingClassName(String className) {
		Constructor<?> constructor;
		try {
			constructor = Class.forName(className).getConstructor();
			permissionComparerFactory = (ComparerFactory) constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public static ComparerFactory getComparerFactory() {
		return permissionComparerFactory;
	}
}
