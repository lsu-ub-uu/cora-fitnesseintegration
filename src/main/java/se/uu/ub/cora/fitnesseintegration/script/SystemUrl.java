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

package se.uu.ub.cora.fitnesseintegration.script;

public final class SystemUrl {

	private static String url;
	private static String appTokenVerifierUrl;
	private static String idpLoginUrl;
	private static String gatekeeperServerUrl;

	public SystemUrl() {
		// needed by fitnesse
		super();
	}

	public static synchronized void setUrl(String urlIn) {
		url = urlIn;
	}

	public static synchronized String getUrl() {
		return url;
	}

	public static synchronized void setAppTokenVerifierUrl(String url) {
		appTokenVerifierUrl = url;
	}

	public static synchronized String getAppTokenVerifierUrl() {
		return appTokenVerifierUrl;
	}

	public static void setIdpLoginUrl(String urlIn) {
		idpLoginUrl = urlIn;
	}

	public static String getIdpLoginUrl() {
		return idpLoginUrl;
	}

	public static void setGatekeeperServerUrl(String urlIn) {
		gatekeeperServerUrl = urlIn;
	}

	public static String getGatekeeperServerUrl() {
		return gatekeeperServerUrl;
	}

}
