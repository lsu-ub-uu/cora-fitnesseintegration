/*
 * Copyright 2019, 2025 Uppsala University Library
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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.clientdata.ClientDataAuthentication;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class IdpLoginServletFixture {
	private String eppn;
	private HttpHandlerFactory factory;
	private HttpHandler httpHandler;
	private String answer;
	private ClientDataAuthentication authentication;
	private String jsonPart;

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
		httpHandler.setRequestProperty("sn", "someLastName");
		httpHandler.setRequestProperty("givenName", "someFirstName");
		answer = httpHandler.getResponseText();
	}

	private void parseInformationFromAnswer() {
		jsonPart = tryToGetFirstMatchFromAnswerUsingRegEx();
		jsonPart = decodeJavascriptEncoded(jsonPart);
		JsonToClientDataConverter jsonToClientDataConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(jsonPart);
		authentication = (ClientDataAuthentication) jsonToClientDataConverter.toInstance();
	}

	private String decodeJavascriptEncoded(String stingToDecode) {
		Map<String, String> decodePairs = new HashMap<>();
		decodePairs.put("\\x27", "'");
		decodePairs.put("\\x26", "&");
		decodePairs.put("\\x22", "\"");
		decodePairs.put("\\-", "-");
		decodePairs.put("\\/", "/");
		decodePairs.put("\\\\", "\\");

		for (Entry<String, String> entry : decodePairs.entrySet()) {
			stingToDecode = stingToDecode.replace(entry.getKey(), entry.getValue());
		}
		return stingToDecode;
	}

	private String tryToGetFirstMatchFromAnswerUsingRegEx() {
		try {
			return getFirstMatchFromAnswerUsingRegEx();
		} catch (Exception e) {
			return "Not parseable";
		}
	}

	private String getFirstMatchFromAnswerUsingRegEx() {
		Pattern pattern = Pattern.compile("authentication\\s=\\s([\\s\\S]*?});");
		Matcher matcher = pattern.matcher(answer);
		matcher.find();
		int regExGroupMatchingValue = 1;
		return matcher.group(regExGroupMatchingValue);
	}

	public String getLoginId() {
		return authentication.getLoginId();
	}

	public StatusType getResponseCode() {
		return Response.Status.fromStatusCode(httpHandler.getResponseCode());
	}

	public String getAuthToken() {
		return authentication.getToken();
	}

	public String getUserId() {
		return authentication.getUserId();
	}

	public String getValidUntil() {
		return authentication.getValidUntil();
	}

	public String getRenewUntil() {
		return authentication.getRenewUntil();
	}

	// TODO:
	// Vi behöver ändra den till getDeleteUrl
	// Vi behöver lägga till getRenewUrl också.
	public String getTokenIdUrl() {
		// SPIKE
		// Optional<ClientActionLink> actionLink = authentication.getActionLink(ClientAction.RENEW);
		// if (actionLink.isPresent()) {
		// return actionLink.get().getURL();
		// }
		return "No url available";
	}

	public String getFirstName() {
		return authentication.getFirstName();
	}

	public String getLastName() {
		return authentication.getLastName();
	}

}
