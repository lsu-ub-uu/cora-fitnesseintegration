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

	private HttpHandlerFactory httpHandlerFactory;
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final int DISTANCE_TO_START_OF_TOKEN = 24;

	public RecordHandlerImp(HttpHandlerFactory httpHandlerFactory) {
		this.httpHandlerFactory = httpHandlerFactory;
	}

	@Override
	public BasicHttpResponse readRecordList(String url, String authToken, String filterAsJson)
			throws UnsupportedEncodingException {
		if (filterAsJson != null) {
			url += "?filter=" + URLEncoder.encode(filterAsJson, StandardCharsets.UTF_8.name());
		}
		return getResponseTextOrErrorTextFromUrl(url, authToken);
	}

	private BasicHttpResponse getResponseTextOrErrorTextFromUrl(String url, String authToken) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		httpHandler.setRequestMethod("GET");

		return createCommonHttpResponseFromHttpHandler(httpHandler);
	}

	private BasicHttpResponse createCommonHttpResponseFromHttpHandler(HttpHandler httpHandler) {
		StatusType statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		String responseText = responseIsOk(statusType) ? httpHandler.getResponseText()
				: httpHandler.getErrorText();
		return new BasicHttpResponse(statusType, responseText);
	}

	protected boolean responseIsOk(StatusType statusType) {
		return statusType.equals(Response.Status.OK);
	}

	private HttpHandler createHttpHandlerWithAuthTokenAndUrl(String url, String authToken) {
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestProperty("authToken", authToken);
		return httpHandler;
	}

	@Override
	public BasicHttpResponse readRecord(String url, String authToken) {
		return getResponseTextOrErrorTextFromUrl(url, authToken);
	}

	@Override
	public BasicHttpResponse searchRecord(String url, String authToken, String json)
			throws UnsupportedEncodingException {
		url += "?searchData=" + URLEncoder.encode(json, StandardCharsets.UTF_8.name());
		HttpHandler httpHandler = setupHttpHandlerForSearch(url, authToken);

		return createCommonHttpResponseFromHttpHandler(httpHandler);
	}

	private HttpHandler setupHttpHandlerForSearch(String url, String authToken) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	@Override
	public ExtendedHttpResponse createRecord(String url, String authToken, String json) {
		HttpHandler httpHandler = createHttpHandlerForPostWithUrlAndContentType(url, authToken,
				json);
		StatusType statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());

		BasicHttpResponse readResponse = createReadResponseForCreated(httpHandler, statusType);
		return statusCreated(statusType) ? createCreateResponse(httpHandler, readResponse)
				: createCreateResponseForErrorResponse(readResponse);
	}

	protected HttpHandler createHttpHandlerForPostWithUrlAndContentType(String url,
			String authToken, String json) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty("Accept", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setOutput(json);
		return httpHandler;
	}

	private BasicHttpResponse createReadResponseForCreated(HttpHandler httpHandler,
			StatusType statusType) {
		String responseText = statusCreated(statusType) ? httpHandler.getResponseText()
				: httpHandler.getErrorText();
		return new BasicHttpResponse(statusType, responseText);
	}

	protected boolean statusCreated(StatusType statusType) {
		return statusType.equals(Response.Status.CREATED);
	}

	private ExtendedHttpResponse createCreateResponse(HttpHandler httpHandler,
			BasicHttpResponse readResponse) {
		String responseText = readResponse.responseText;
		String createdId = extractCreatedIdFromLocationHeader(
				httpHandler.getHeaderField("Location"));
		String token = tryToExtractCreatedTokenFromResponseText(responseText);
		return new ExtendedHttpResponse(readResponse, createdId, token);
	}

	private String extractCreatedIdFromLocationHeader(String locationHeader) {
		return locationHeader.substring(locationHeader.lastIndexOf('/') + 1);
	}

	private String tryToExtractCreatedTokenFromResponseText(String responseText) {
		try {
			return extractCreatedTokenFromResponseText(responseText);
		} catch (Exception e) {
			return "";
		}
	}

	private String extractCreatedTokenFromResponseText(String responseText) {
		int tokenIdIndex = responseText.lastIndexOf("\"name\":\"token\"")
				+ DISTANCE_TO_START_OF_TOKEN;
		return responseText.substring(tokenIdIndex, responseText.indexOf('"', tokenIdIndex));
	}

	private ExtendedHttpResponse createCreateResponseForErrorResponse(
			BasicHttpResponse readResponse) {
		return new ExtendedHttpResponse(readResponse, "", "");
	}

	@Override
	public BasicHttpResponse updateRecord(String url, String authToken, String json) {
		HttpHandler httpHandler = createHttpHandlerForPostWithUrlAndContentType(url, authToken,
				json);
		return createCommonHttpResponseFromHttpHandler(httpHandler);
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for test
		return httpHandlerFactory;
	}

}
