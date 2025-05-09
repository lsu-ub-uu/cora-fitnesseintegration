/*
 * Copyright 2015, 2016, 2019, 2020, 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.fitnesseintegration.script.AuthTokenHolder;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpMultiPartUploader;
import se.uu.ub.cora.javaclient.rest.RestResponse;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class RecordEndpointFixture {
	private static final String AUTH_TOKEN = "authToken";
	private static final int DISTANCE_TO_START_OF_ID = 19;
	protected static final String APPLICATION_VND_CORA_RECORD_JSON = "application/vnd.cora.record+json";
	protected static final String ACCEPT = "Accept";
	private String id;
	private String searchId;
	private String type;
	protected String json;
	protected StatusType statusType;
	private String createdId;
	private String fileName;
	private String streamId;
	private String resourceName;
	private String contentLenght;
	private String contentDisposition;
	private String authToken;
	private HttpHandlerFactory httpHandlerFactory;
	protected String baseUrl = SystemUrl.getUrl() + "rest/";
	protected String baseRecordUrl = baseUrl + "record/";
	private ChildComparer childComparer;
	private RecordHandler recordHandler;
	private int maxNumberOfReads;
	private int sleepTime;

	public RecordEndpointFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
		childComparer = DependencyProvider.getChildComparer();
		recordHandler = new RecordHandlerImp(baseUrl, SystemUrl.getAppTokenVerifierUrl());
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public String getCreatedId() {
		return createdId;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getStreamId() {
		return streamId;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getContentDisposition() {
		return contentDisposition;
	}

	public String getContentLength() {
		return contentLenght;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public String testReadRecord() {
		String readAuthToken = getSetAuthTokenOrAdminAuthToken();

		RestResponse readResponse = recordHandler.readRecord(readAuthToken, type, id);
		statusType = Response.Status.fromStatusCode(readResponse.responseCode());
		return readResponse.responseText();
	}

	protected String getSetAuthTokenOrAdminAuthToken() {
		return authToken != null ? authToken : AuthTokenHolder.getAdminAuthToken();
	}

	protected HttpHandler createHttpHandlerWithAuthTokenAndUrl(String url) {
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestProperty(AUTH_TOKEN, getSetAuthTokenOrAdminAuthToken());
		return httpHandler;
	}

	public String testReadIncomingLinks() {
		String readAuthToken = getSetAuthTokenOrAdminAuthToken();

		RestResponse readResponse = recordHandler.readIncomingLinks(readAuthToken, type, id);
		statusType = Response.Status.fromStatusCode(readResponse.responseCode());
		return readResponse.responseText();
	}

	public String testReadRecordList() {
		RestResponse readResponse = recordHandler.readRecordList(getSetAuthTokenOrAdminAuthToken(),
				type, json);
		statusType = Response.Status.fromStatusCode(readResponse.responseCode());
		return readResponse.responseText();
	}

	public String testCreateRecord() {
		return createRecordAndSetValuesFromResponse();
	}

	private String createRecordAndSetValuesFromResponse() {
		RestResponse createResponse = recordHandler.createRecord(getSetAuthTokenOrAdminAuthToken(),
				type, json);

		setStatusTypeAndCreatedIdFromResponse(createResponse);
		String createdRecordAsJson = createResponse.responseText();
		DataHolder.setRecordAsJson(createdRecordAsJson);
		return createdRecordAsJson;
	}

	private void setStatusTypeAndCreatedIdFromResponse(RestResponse response) {
		statusType = Response.Status.fromStatusCode(response.responseCode());
		if (response.createdId().isPresent()) {
			createdId = response.createdId().get();
			DataHolder.setRecord(null);
		}
		// token = response.token;
	}

	public String testCreateRecordCreatedType() {
		String responseText = createRecordAndSetValuesFromResponse();
		if (statusIsCreated()) {
			return getRecordTypeFromResponseText(responseText);
		}
		return responseText;
	}

	private boolean statusIsCreated() {
		return statusType.getStatusCode() == Response.Status.CREATED.getStatusCode();
	}

	private String getRecordTypeFromResponseText(String responseText) {
		JsonObject data = extractDataAsJsonObjectFromResponseText(responseText);
		try {
			return getRecordTypeFromData(data);
		} catch (ChildNotFoundException e) {
			return "";
		}
	}

	private JsonObject extractDataAsJsonObjectFromResponseText(String responseText) {
		JsonObject dataRecord = extractRecordAsJsonObjectFromResponseText(responseText);
		return dataRecord.getValueAsJsonObject("data");
	}

	private JsonObject extractRecordAsJsonObjectFromResponseText(String responseText) {
		JsonObject textAsJsonObject = createJsonObjectFromResponseText(responseText);
		return textAsJsonObject.getValueAsJsonObject("record");
	}

	private JsonObject createJsonObjectFromResponseText(String responseText) {
		JsonParser jsonParser = new OrgJsonParser();
		JsonValue jsonValue = jsonParser.parseString(responseText);
		return (JsonObject) jsonValue;
	}

	private String getRecordTypeFromData(JsonObject data) {
		JsonObject recordInfo = tryToGetChildFromChildrenArrayByNameInData(data, "recordInfo");
		JsonObject typeObject = tryToGetChildFromChildrenArrayByNameInData(recordInfo, "type");

		JsonObject linkedRecordId = tryToGetChildFromChildrenArrayByNameInData(typeObject,
				"linkedRecordId");
		return linkedRecordId.getValueAsJsonString("value").getStringValue();
	}

	private JsonObject tryToGetChildFromChildrenArrayByNameInData(JsonObject jsonObject,
			String nameInData) {
		JsonArray children = jsonObject.getValueAsJsonArray("children");
		for (JsonValue child : children) {
			JsonObject jsonChildObject = (JsonObject) child;
			String name = jsonChildObject.getValueAsJsonString("name").getStringValue();
			if (nameInData.equals(name)) {
				return jsonChildObject;
			}
		}
		throw new ChildNotFoundException("child with name: " + nameInData + "not found");
	}

	public String testUpdateRecord() {
		RestResponse response = recordHandler.updateRecord(getSetAuthTokenOrAdminAuthToken(), type,
				id, json);
		statusType = Response.Status.fromStatusCode(response.responseCode());
		return response.responseText();
	}

	public String testDeleteRecord() {
		RestResponse response = recordHandler.deleteRecord(getSetAuthTokenOrAdminAuthToken(), type,
				id);
		statusType = Response.Status.fromStatusCode(response.responseCode());
		return response.responseText();
	}

	public String testUpload() throws IOException {
		String url = baseRecordUrl + type + "/" + id + "/master";
		url = addAuthTokenToUrl(url);

		HttpMultiPartUploader httpHandler = httpHandlerFactory.factorHttpMultiPartUploader(url);
		addStreamInfoToHttpHandler(httpHandler);
		httpHandler.done();

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			String responseText = httpHandler.getResponseText();
			streamId = tryToFindStreamId(responseText);
			return responseText;
		}
		return httpHandler.getErrorText();
	}

	private void addStreamInfoToHttpHandler(HttpMultiPartUploader httpHandler) throws IOException {
		httpHandler.addHeaderField(ACCEPT, APPLICATION_VND_CORA_RECORD_JSON);
		Path path = Path.of("FitNesseRoot/files/testResources/" + fileName);
		InputStream fileStream = Files.newInputStream(path);
		httpHandler.addFilePart("file", fileName, fileStream);
	}

	protected boolean responseIsOk() {
		return statusType.getStatusCode() == Response.Status.OK.getStatusCode();
	}

	private String addAuthTokenToUrl(String urlIn) {
		String authTokenToUse = authToken != null ? authToken : AuthTokenHolder.getAdminAuthToken();
		return urlIn + "?" + AUTH_TOKEN + "=" + authTokenToUse;
	}

	private String tryToFindStreamId(String entity) {
		try {
			return findStreamId(entity);
		} catch (Exception e) {
			return "";
		}
	}

	private String findStreamId(String entity) {
		int streamIdIndex = entity.lastIndexOf("streamId") + DISTANCE_TO_START_OF_ID;
		return entity.substring(streamIdIndex, entity.indexOf('"', streamIdIndex));
	}

	@Deprecated
	public String testDownload() {
		HttpHandler httpHandler = setupHttpHandlerForDownload();
		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			return getDownloadResponseText(httpHandler);
		}
		return httpHandler.getErrorText();
	}

	private HttpHandler setupHttpHandlerForDownload() {
		String url = baseRecordUrl + type + "/" + id + "/" + resourceName;
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	private String getDownloadResponseText(HttpHandler httpHandler) {
		String responseText = httpHandler.getResponseText();
		contentLenght = httpHandler.getHeaderField("Content-Length");
		contentDisposition = httpHandler.getHeaderField("Content-Disposition");
		streamId = tryToFindStreamId(responseText);
		return responseText;
	}

	public String testSearchRecord() {
		RestResponse readResponse = recordHandler.searchRecord(getSetAuthTokenOrAdminAuthToken(),
				searchId, json);
		statusType = Response.Status.fromStatusCode(readResponse.responseCode());
		return readResponse.responseText();
	}

	public void testReadRecordAndStoreJson() {
		String responseText = testReadRecord();
		ClientDataRecord clientClientDataRecord = convertJsonToClientDataRecord(responseText);

		DataHolder.setRecord(clientClientDataRecord);
	}

	protected ClientDataRecord convertJsonToClientDataRecord(String jsonText) {
		JsonToClientDataConverter toClientConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(jsonText);
		return (ClientDataRecord) toClientConverter.toInstance();
	}

	private void setValuesFromResponse(RestResponse createResponse) {
		statusType = Response.Status.fromStatusCode(createResponse.responseCode());
		if (createResponse.createdId().isPresent()) {
			createdId = createResponse.createdId().get();
		} else {
			createdId = null;
		}
	}

	public String testBatchIndexing() {
		RestResponse response = recordHandler.batchIndex(getSetAuthTokenOrAdminAuthToken(), type,
				json);

		setValuesFromResponse(response);

		return response.responseText();
	}

	public String waitUntilIndexBatchJobIsFinished() throws InterruptedException {
		int numberOfReads = 0;
		boolean continueToReadIndexBatchJob = true;
		while (continueToReadIndexBatchJob) {
			numberOfReads++;

			testReadRecordAndStoreJson();

			if (storedIndexBatchJobIsFinished() || numberOfReads == maxNumberOfReads) {
				continueToReadIndexBatchJob = false;
			} else {
				Thread.sleep(sleepTime);
			}
		}
		return generateResponseBasedOnIndexBatchJobStatus();
	}

	private boolean storedIndexBatchJobIsFinished() {
		String status = extractStatusFromIndexBatchJob();
		return "finished".equals(status);
	}

	private String extractStatusFromIndexBatchJob() {
		ClientDataRecord dataRecord = DataHolder.getRecord();
		ClientDataRecordGroup clientDataGroup = dataRecord.getDataRecordGroup();
		return clientDataGroup.getFirstAtomicValueWithNameInData("status");
	}

	private String generateResponseBasedOnIndexBatchJobStatus() {
		if (storedIndexBatchJobIsFinished()) {
			return "finished";
		}
		return "Tried to read indexBatchJob " + maxNumberOfReads + " times, waiting " + sleepTime
				+ " milliseconds between each read, but it was still not finished.";
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	public ChildComparer getChildComparer() {
		// needed for test
		return childComparer;
	}

	public RecordHandler getRecordHandler() {
		return recordHandler;
	}

	public void setMaxNumberOfReads(int maxRepeatCount) {
		this.maxNumberOfReads = maxRepeatCount;
	}

	public void setSleepTime(int sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void onlyForTestSetRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}
}
