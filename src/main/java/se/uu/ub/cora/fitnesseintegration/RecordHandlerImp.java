/*
 * Copyright 2020 Uppsala University Library
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class RecordHandlerImp implements RecordHandler {

	protected StatusType statusType;
	private HttpHandlerFactory httpHandlerFactory;

	public RecordHandlerImp(HttpHandlerFactory httpHandlerFactory) {
		this.httpHandlerFactory = httpHandlerFactory;
	}

	@Override
	public ReadResponse readRecordList(String url, String filterAsJson, String authToken)
			throws UnsupportedEncodingException {
		if (filterAsJson != null) {
			url += "?filter=" + URLEncoder.encode(filterAsJson, StandardCharsets.UTF_8.name());
		}
		return getResponseTextOrErrorTextFromUrl(url, authToken);
	}

	private ReadResponse getResponseTextOrErrorTextFromUrl(String url, String authToken) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		httpHandler.setRequestMethod("GET");

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		String responseText = responseIsOk() ? httpHandler.getResponseText()
				: httpHandler.getErrorText();
		return new ReadResponse(statusType, responseText);
	}

	protected boolean responseIsOk() {
		return statusType.equals(Response.Status.OK);
	}

	private HttpHandler createHttpHandlerWithAuthTokenAndUrl(String url, String authToken) {
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		setAuthTokenInHeaderAsAuthTokenOrAdminAuthToken(httpHandler, authToken);
		return httpHandler;
	}

	private void setAuthTokenInHeaderAsAuthTokenOrAdminAuthToken(HttpHandler httpHandler,
			String authToken) {
		httpHandler.setRequestProperty("authToken", authToken);
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for test
		return httpHandlerFactory;
	}

	@Override
	public ReadResponse readRecord(String url, String authToken) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		httpHandler.setRequestMethod("GET");

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		String responseText = responseIsOk() ? httpHandler.getResponseText()
				: httpHandler.getErrorText();
		return new ReadResponse(Response.Status.OK, responseText);
	}

}
