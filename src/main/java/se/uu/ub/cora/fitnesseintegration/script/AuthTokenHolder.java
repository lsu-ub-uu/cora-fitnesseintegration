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

public final class AuthTokenHolder {
	private static String adminAuthToken;
	private static String userAuthToken;

	public AuthTokenHolder() {
		// needed by fitnesse
		super();
	}

	public static synchronized String getAdminAuthToken() {
		return adminAuthToken;
	}

	public static synchronized void setAdminAuthToken(String authTokenIn) {
		adminAuthToken = authTokenIn;
	}

	public static synchronized void setUserAuthToken(String authTokenIn) {
		userAuthToken = authTokenIn;
	}

	public static synchronized String getUserAuthToken() {
		return userAuthToken;
	}
}
