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

package se.uu.ub.cora.fitnesseintegration.script;

public final class LoginToken {

	private static String fitnesseAdminLoginId;
	private static String fitnesseAdminAppToken;
	private static String fitnesseUserLoginId;
	private static String fitnesseUserAppToken;

	public LoginToken() {
		// needed by fitnesse
		super();
	}

	public static synchronized void setFitnesseAdminLoginId(String fitnesseAdminLoginIdIn) {
		fitnesseAdminLoginId = fitnesseAdminLoginIdIn;
	}

	public static synchronized String getFitnesseAdminLoginId() {
		return fitnesseAdminLoginId;
	}

	public static synchronized void setFitnesseAdminAppToken(String fitnesseAdminAppTokenIn) {
		fitnesseAdminAppToken = fitnesseAdminAppTokenIn;
	}

	public static synchronized String getFitnesseAdminAppToken() {
		return fitnesseAdminAppToken;
	}

	public static synchronized void setFitnesseUserLoginId(String fitnesseUserLoginIdIn) {
		fitnesseUserLoginId = fitnesseUserLoginIdIn;
	}

	public static synchronized String getFitnesseUserLoginId() {
		return fitnesseUserLoginId;
	}

	public static synchronized void setFitnesseUserAppToken(String fitnesseUserAppTokenIn) {
		fitnesseUserAppToken = fitnesseUserAppTokenIn;
	}

	public static synchronized String getFitnesseUserAppToken() {
		return fitnesseUserAppToken;
	}
}
