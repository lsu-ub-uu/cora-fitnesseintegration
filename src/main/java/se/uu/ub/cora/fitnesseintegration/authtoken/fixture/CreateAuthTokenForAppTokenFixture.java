/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.authtoken.fixture;

import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.AppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.token.TokenClient;

public class CreateAuthTokenForAppTokenFixture {
	private String appToken;
	private String userId;

	public CreateAuthTokenForAppTokenFixture() {
		// needed by fitnesse
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setAppToken(String token) {
		this.appToken = token;
	}

	public String getAuthTokenUsingUserIdAndAppToken() {
		try {
			String appTokenVerifierUrl = SystemUrl.getAppTokenVerifierUrl();
			AppTokenCredentials appTokenCredentials = new AppTokenCredentials(appTokenVerifierUrl,
					userId, appToken);
			TokenClient tokenClient = JavaClientProvider
					.createTokenClientUsingAppTokenCredentials(appTokenCredentials);
			return tokenClient.getAuthToken();
		} catch (Exception e) {
			return "Failed to get authToken using user id and appToken: " + e.getMessage();
		}
	}
}
