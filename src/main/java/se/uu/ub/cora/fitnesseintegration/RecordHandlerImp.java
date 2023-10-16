/*
 * Copyright 2020, 2023 Uppsala University Library
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
import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestClientFactory;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RecordHandlerImp implements RecordHandler {

	private static final int CREATED = 201;
	private HttpHandlerFactory httpHandlerFactory;
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final int DISTANCE_TO_START_OF_TOKEN = 24;
	private RestClientFactory restClientFactory;

	public RecordHandlerImp(HttpHandlerFactory httpHandlerFactory,
			RestClientFactory restClientFactory) {
		this.httpHandlerFactory = httpHandlerFactory;
		this.restClientFactory = restClientFactory;
	}

	@Override
	public BasicHttpResponse readRecordList(String authToken, String recordType,
			String filterAsJson) throws UnsupportedEncodingException {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = getRecordListResponse(restClient, recordType, filterAsJson);
		return new BasicHttpResponse(response.responseCode(), response.responseText());
	}

	private RestResponse getRecordListResponse(RestClient restClient, String recordType,
			String filterAsJson) throws UnsupportedEncodingException {
		if (filterAsJson != null) {
			return restClient.readRecordListWithFilterAsJson(recordType, filterAsJson);
		}
		return restClient.readRecordListAsJson(recordType);
	}

	private BasicHttpResponse createCommonHttpResponseFromHttpHandler(HttpHandler httpHandler) {
		StatusType statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		String responseText = statusIsOk(statusType) ? httpHandler.getResponseText()
				: httpHandler.getErrorText();
		return new BasicHttpResponse(httpHandler.getResponseCode(), responseText);
	}

	protected boolean statusIsOk(StatusType statusType) {
		return statusType.equals(Response.Status.OK);
	}

	private HttpHandler createHttpHandlerWithAuthTokenAndUrl(String url, String authToken) {
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestProperty("authToken", authToken);
		return httpHandler;
	}

	@Override
	public BasicHttpResponse readRecord(String authToken, String recordType, String recordId) {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.readRecordAsJson(recordType, recordId);
		return createBasicHttpResponseFromRestResponse(response);
	}

	private BasicHttpResponse createBasicHttpResponseFromRestResponse(RestResponse response) {
		String json = response.responseText();
		return new BasicHttpResponse(response.responseCode(), json);
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
	public ExtendedHttpResponse createRecord(String authToken, String recordType, String json) {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.createRecordFromJson(recordType, json);

		return createExtendedHttpResponse(response);

	}

	private ExtendedHttpResponse createExtendedHttpResponse(RestResponse response) {
		BasicHttpResponse basicHttpResponse = new BasicHttpResponse(response.responseCode(),
				response.responseText());

		return response.responseCode() == CREATED
				? createCreateResponse(response, basicHttpResponse)
				: new ExtendedHttpResponse(basicHttpResponse);
	}

	protected void addPropertiesToHttpHandler(HttpHandler httpHandler, String json,
			String contentType) {
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty("Accept", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", contentType);
		httpHandler.setOutput(json);
	}

	private ExtendedHttpResponse createCreateResponse(RestResponse response,
			BasicHttpResponse readResponse) {
		String responseText = readResponse.responseText;
		String createdId = response.createdId().get();
		String token = tryToExtractCreatedTokenFromResponseText(responseText);
		return new ExtendedHttpResponse(readResponse, createdId, token);
	}

	@Override
	public BasicHttpResponse validateRecord(String url, String authToken, String json,
			String contentType) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url, authToken);
		addPropertiesToHttpHandler(httpHandler, json, contentType);
		return createCommonHttpResponseFromHttpHandler(httpHandler);
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

	@Override
	public BasicHttpResponse updateRecord(String authToken, String recordType, String recordId,
			String json) {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.updateRecordFromJson(recordType, recordId, json);
		return createBasicHttpResponseFromRestResponse(response);
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for test
		return httpHandlerFactory;
	}

	@Override
	public BasicHttpResponse deleteRecord(String authToken, String recordType, String recordId) {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.deleteRecord(recordType, recordId);
		return createBasicHttpResponseFromRestResponse(response);
	}

	public RestClientFactory getRestClientFactory() {
		// needed for test
		return restClientFactory;
	}

	@Override
	public BasicHttpResponse readIncomingLinks(String authToken, String recordType,
			String recordId) {
		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.readIncomingLinksAsJson(recordType, recordId);
		return createBasicHttpResponseFromRestResponse(response);

	}

	@Override
	public ExtendedHttpResponse batchIndex(String authToken, String recordType,
			String filterAsJson) {

		RestClient restClient = restClientFactory.factorUsingAuthToken(authToken);
		RestResponse response = restClient.batchIndexWithFilterAsJson(recordType, filterAsJson);

		return createExtendedHttpResponse(response);
	}

}
