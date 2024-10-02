/*
 * Copyright 2019 Uppsala University Library
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class IdpLoginServletFixture {
	private String eppn;
	private HttpHandlerFactory factory;
	private HttpHandler httpHandler;
	private String loginId;
	private String authToken;
	private String answer;
	private String validForNoSeconds;
	private String deleteUrl;

	public IdpLoginServletFixture() {
		factory = DependencyProvider.getHttpHandlerFactory();
	}

	public void setEPPN(String eppn) {
		this.eppn = eppn;
	}

	public String getAuthTokenForEPPN() {
		callIdpLogin();
		parseInformationFromAnswer();
		return answer;
	}

	private void callIdpLogin() {
		httpHandler = factory.factor(SystemUrl.getIdpLoginUrl() + "login");
		httpHandler.setRequestProperty("eppn", eppn);
		httpHandler.setRequestMethod("GET");
		answer = httpHandler.getResponseText();
	}

	private void parseInformationFromAnswer() {
		loginId = tryToGetFirstMatchFromAnswerUsingRegEx("userId\" : \"");
		authToken = tryToGetFirstMatchFromAnswerUsingRegEx("token\" : \"");
		validForNoSeconds = tryToGetFirstMatchFromAnswerUsingRegEx("validForNoSeconds\" : \"");
		deleteUrl = tryToGetFirstMatchFromAnswerUsingRegEx("url\" : \"");
		decodeJavascriptEncoded();
	}

	private String tryToGetFirstMatchFromAnswerUsingRegEx(String regEx) {
		try {
			return getFirstMatchFromAnswerUsingRegEx(regEx);
		} catch (Exception e) {
			return "Not parseable";
		}
	}

	private String getFirstMatchFromAnswerUsingRegEx(String regEx) {
		String nonGreedyMatchingGroupUntilQuote = "(.*?)\"";
		Pattern pattern = Pattern.compile(regEx + nonGreedyMatchingGroupUntilQuote);
		Matcher matcher = pattern.matcher(answer);
		matcher.find();
		int regExGroupMatchingValue = 1;
		return matcher.group(regExGroupMatchingValue);
	}

	private void decodeJavascriptEncoded() {
		authToken = authToken.replace("\\", "");
		deleteUrl = deleteUrl.replace("\\", "");
	}

	public String getLoginId() {
		return loginId;
	}

	public StatusType getResponseCode() {
		return Response.Status.fromStatusCode(httpHandler.getResponseCode());
	}

	public String getAuthToken() {
		return authToken;
	}

	public String getValidForNoSeconds() {
		return validForNoSeconds;
	}

	public String getDeleteUrl() {
		return deleteUrl;
	}

}
