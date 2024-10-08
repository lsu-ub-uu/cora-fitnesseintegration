/*
 * Copyright 2018 Uppsala University Library
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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class AppTokenEndpointFixture {
	private static final String NEW_LINE = "\n";
	private static final int DISTANCE_TO_START_OF_TOKEN = 24;
	private String appToken;
	private HttpHandlerFactory factory;
	private String baseUrlApptoken = SystemUrl.getAppTokenVerifierUrl() + "rest/apptoken";
	private String baseUrlAuthToken = SystemUrl.getAppTokenVerifierUrl() + "rest/authToken";
	private String loginId;
	private Status statusType;
	private String authToken;
	private String authTokenToLogOut;

	public AppTokenEndpointFixture() {
		factory = DependencyProvider.getHttpHandlerFactory();
	}

	public void setAppToken(String token) {
		this.appToken = token;
	}

	public String getAuthTokenForAppToken() {
		String url = baseUrlApptoken;

		HttpHandler httpHandler = factory.factor(url);
		httpHandler.setRequestMethod("POST");
		if (appToken == null || "".equals(appToken)) {
			if ("fitnesseAdmin@system.cora.uu.se".equals(loginId)) {
				appToken = "29c30232-d514-4559-b60b-6de47175c1df";
			} else if ("fitnesseUser@system.cora.uu.se".equals(loginId)) {
				appToken = "bd699488-f9d1-419d-a79d-9fa8a0f3bb9d";
			}
		}
		httpHandler.setOutput(loginId + NEW_LINE + appToken);

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType == Response.Status.CREATED) {
			String responseText = httpHandler.getResponseText();
			authToken = extractCreatedTokenFromResponseText(responseText);

			return responseText;
		}
		return httpHandler.getErrorText();
	}

	private String extractCreatedTokenFromResponseText(String responseText) {
		int idIndex = responseText.lastIndexOf("\"name\":\"token\"") + DISTANCE_TO_START_OF_TOKEN;
		return responseText.substring(idIndex, responseText.indexOf('"', idIndex));
	}

	public void setUserId(String userId) {
		this.loginId = userId;
	}

	public String getAuthToken() {
		return authToken;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public void setAuthTokenToLogOut(String authTokenToLogOut) {
		this.authTokenToLogOut = authTokenToLogOut;
	}

	public void removeAuthTokenForUser() {
		String url = baseUrlAuthToken + "/" + loginId;

		HttpHandler httpHandler = factory.factor(url);
		httpHandler.setRequestMethod("DELETE");
		httpHandler.setOutput(authTokenToLogOut);

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
	}
}
