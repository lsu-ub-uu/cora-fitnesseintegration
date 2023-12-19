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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RecordHandlerImp implements RecordHandler {
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private String baseUrl;
	private String appTokenUrl;

	public RecordHandlerImp(String baseUrl, String appTokenUrl) {
		this.baseUrl = baseUrl;
		this.appTokenUrl = appTokenUrl;
	}

	@Override
	public RestResponse readRecord(String authToken, String recordType, String recordId) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.readRecordAsJson(recordType, recordId);
	}

	private RestClient createRestClientForAuthToken(String authToken) {
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenUrl, authToken);
		return JavaClientProvider
				.createRestClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
	}

	@Override
	// TODO do not pass null (filterAsJson)
	public RestResponse readRecordList(String authToken, String recordType, String filterAsJson) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return getRecordListResponse(restClient, recordType, filterAsJson);
	}

	private RestResponse getRecordListResponse(RestClient restClient, String recordType,
			String filterAsJson) {
		if (filterAsJson != null) {
			return restClient.readRecordListWithFilterAsJson(recordType, filterAsJson);
		}
		return restClient.readRecordListAsJson(recordType);
	}

	protected boolean statusIsOk(StatusType statusType) {
		return statusType.equals(Response.Status.OK);
	}

	@Override
	public RestResponse searchRecord(String authToken, String searchId, String json) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.searchRecordWithSearchCriteriaAsJson(searchId, json);
	}

	protected void addPropertiesToHttpHandler(HttpHandler httpHandler, String json,
			String contentType) {
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty("Accept", APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", contentType);
		httpHandler.setOutput(json);
	}

	@Override
	public RestResponse createRecord(String authToken, String recordType, String json) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.createRecordFromJson(recordType, json);
	}

	@Override
	public RestResponse validateRecord(String authToken, String json) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.validateRecordAsJson(json);
	}

	@Override
	public RestResponse updateRecord(String authToken, String recordType, String recordId,
			String json) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.updateRecordFromJson(recordType, recordId, json);
	}

	@Override
	public RestResponse deleteRecord(String authToken, String recordType, String recordId) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.deleteRecord(recordType, recordId);
	}

	@Override
	public RestResponse readIncomingLinks(String authToken, String recordType, String recordId) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.readIncomingLinksAsJson(recordType, recordId);
	}

	@Override
	public RestResponse batchIndex(String authToken, String recordType, String filterAsJson) {

		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.batchIndexWithFilterAsJson(recordType, filterAsJson);
	}

	public String onlyForTestGetBaseUrl() {
		return baseUrl;
	}

	public String onlyForTestGetAppTokenUrl() {
		return appTokenUrl;
	}

	@Override
	public RestResponse download(String authToken, String recordType, String recordId,
			String representation) {
		RestClient restClient = createRestClientForAuthToken(authToken);
		return restClient.download(recordType, recordId, representation);
	}
}
