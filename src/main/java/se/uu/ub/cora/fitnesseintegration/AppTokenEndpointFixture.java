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
	private static final int DISTANCE_TO_START_OF_TOKEN = 21;
	private String appToken;
	private HttpHandlerFactory factory;
	private String baseUrlApptoken = SystemUrl.getAppTokenVerifierUrl() + "rest/apptoken/";
	private String baseUrlAuthToken = SystemUrl.getAppTokenVerifierUrl() + "rest/authToken/";
	private String userId;
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
		String url = baseUrlApptoken + userId;

		HttpHandler httpHandler = factory.factor(url);
		httpHandler.setRequestMethod("POST");
		if (appToken == null || "".equals(appToken)) {
			if ("131313".equals(userId)) {
				appToken = "44c17361-ead7-43b5-a938-038765873037";
			} else if ("121212".equals(userId)) {
				appToken = "a5b9871f-1610-44e1-b838-c37ace6757d6";
			}
		}
		httpHandler.setOutput(appToken);

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType == Response.Status.CREATED) {
			String responseText = httpHandler.getResponseText();
			authToken = extractCreatedTokenFromResponseText(responseText);

			return responseText;
		}
		return httpHandler.getErrorText();
	}

	private String extractCreatedTokenFromResponseText(String responseText) {
		int idIndex = responseText.lastIndexOf("\"name\":\"id\"") + DISTANCE_TO_START_OF_TOKEN;
		return responseText.substring(idIndex, responseText.indexOf('"', idIndex));
	}

	public void setUserId(String userId) {
		this.userId = userId;
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
		String url = baseUrlAuthToken + userId;

		HttpHandler httpHandler = factory.factor(url);
		httpHandler.setRequestMethod("DELETE");
		httpHandler.setOutput(authTokenToLogOut);

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
	}
}
