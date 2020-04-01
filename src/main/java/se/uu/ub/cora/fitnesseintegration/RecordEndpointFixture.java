/*
 * Copyright 2015, 2016, 2019 Uppsala University Library
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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverterFactory;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverterImp;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpMultiPartUploader;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class RecordEndpointFixture {
	private static final String AUTH_TOKEN = "authToken";
	private static final int DISTANCE_TO_START_OF_TOKEN = 24;
	private static final int DISTANCE_TO_START_OF_ID = 19;
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.uub.record+json";
	private static final String ACCEPT = "Accept";
	private String id;
	private String searchId;
	private String type;
	private String json;
	protected StatusType statusType;
	private String createdId;
	private String fileName;
	private String streamId;
	private String resourceName;
	private String contentLenght;
	private String contentDisposition;
	private String authToken;
	protected String baseUrl = SystemUrl.getUrl() + "rest/record/";
	private HttpHandlerFactory httpHandlerFactory;
	private String token;
	private JsonToDataConverterFactory jsonToDataConverterFactory;
	private JsonHandler jsonHandler;
	private JsonToDataRecordConverter jsonToDataRecordConverter;
	private ChildComparer childComparer;

	public RecordEndpointFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
		jsonToDataConverterFactory = DependencyProvider.getJsonToDataConverterFactory();
		childComparer = DependencyProvider.getChildComparer();
		jsonToDataRecordConverter = DependencyProvider.getJsonToDataRecordConverter();
		jsonHandler = DependencyProvider.getJsonHandler();
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
		String url = baseUrl + type + "/" + id;

		return getResponseTextOrErrorTextFromUrl(url);
	}

	private String getResponseTextOrErrorTextFromUrl(String url) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
		httpHandler.setRequestMethod("GET");

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			return httpHandler.getResponseText();
		}
		return httpHandler.getErrorText();
	}

	private HttpHandler createHttpHandlerWithAuthTokenAndUrl(String url) {
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		setAuthTokenInHeaderAsAuthTokenOrAdminAuthToken(httpHandler);
		return httpHandler;
	}

	private void setAuthTokenInHeaderAsAuthTokenOrAdminAuthToken(HttpHandler httpHandler) {
		if (null != authToken) {
			httpHandler.setRequestProperty(AUTH_TOKEN, authToken);
		} else {
			httpHandler.setRequestProperty(AUTH_TOKEN, AuthTokenHolder.getAdminAuthToken());
		}
	}

	public String testReadIncomingLinks() {
		String url = baseUrl + type + "/" + id + "/incomingLinks";
		return getResponseTextOrErrorTextFromUrl(url);
	}

	public String testReadRecordList() throws UnsupportedEncodingException {
		String url = baseUrl + type;
		if (json != null) {
			url += "?filter=" + URLEncoder.encode(json, StandardCharsets.UTF_8.name());
		}
		return getResponseTextOrErrorTextFromUrl(url);
	}

	public String testCreateRecord() {
		HttpHandler httpHandler = setUpHttpHandlerForCreate();

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType.equals(Response.Status.CREATED)) {
			String responseText = httpHandler.getResponseText();
			createdId = extractCreatedIdFromLocationHeader(httpHandler.getHeaderField("Location"));
			token = tryToExtractCreatedTokenFromResponseText(responseText);

			return responseText;
		}
		return httpHandler.getErrorText();
	}

	private HttpHandler setUpHttpHandlerForCreate() {
		String url = baseUrl + type;
		return createHttpHandlerForPostWithUrlAndContentType(url, APPLICATION_UUB_RECORD_JSON);
	}

	protected HttpHandler createHttpHandlerForPostWithUrlAndContentType(String url,
			String contentType) {
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
		httpHandler.setRequestMethod("POST");
		httpHandler.setRequestProperty(ACCEPT, APPLICATION_UUB_RECORD_JSON);
		httpHandler.setRequestProperty("Content-Type", contentType);
		httpHandler.setOutput(json);
		return httpHandler;
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
		int streamIdIndex = responseText.lastIndexOf("\"name\":\"token\"")
				+ DISTANCE_TO_START_OF_TOKEN;
		return responseText.substring(streamIdIndex, responseText.indexOf('"', streamIdIndex));
	}

	public String testCreateRecordCreatedType() {
		HttpHandler httpHandler = setUpHttpHandlerForCreate();

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType.equals(Response.Status.CREATED)) {
			String responseText = httpHandler.getResponseText();
			createdId = extractCreatedIdFromLocationHeader(httpHandler.getHeaderField("Location"));
			token = tryToExtractCreatedTokenFromResponseText(responseText);

			return getRecordTypeFromResponseText(responseText);
		}
		return httpHandler.getErrorText();

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
		JsonObject record = extractRecordAsJsonObjectFromResponseText(responseText);
		return record.getValueAsJsonObject("data");
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
		String url = baseUrl + type + "/" + id;
		HttpHandler httpHandler = createHttpHandlerForPostWithUrlAndContentType(url,
				APPLICATION_UUB_RECORD_JSON);
		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());

		if (responseIsOk()) {
			return httpHandler.getResponseText();
		}
		return httpHandler.getErrorText();
	}

	public String testDeleteRecord() {
		String url = baseUrl + type + "/" + id;
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
		httpHandler.setRequestMethod("DELETE");

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			return httpHandler.getResponseText();
		}
		return httpHandler.getErrorText();
	}

	public String testUpload() throws IOException {
		String url = baseUrl + type + "/" + id + "/master";
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
		httpHandler.addHeaderField(ACCEPT, APPLICATION_UUB_RECORD_JSON);
		InputStream fakeStream = new ByteArrayInputStream(
				"a string".getBytes(StandardCharsets.UTF_8));
		httpHandler.addFilePart("file", fileName, fakeStream);
	}

	protected boolean responseIsOk() {
		return statusType.equals(Response.Status.OK);
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

	public String testDownload() {
		HttpHandler httpHandler = setupHttpHandlerForDownload();
		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			return getDownloadResponseText(httpHandler);
		}
		return httpHandler.getErrorText();
	}

	private HttpHandler setupHttpHandlerForDownload() {
		String url = baseUrl + type + "/" + id + "/" + resourceName;
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

	public String getToken() {
		return token;
	}

	public String testSearchRecord() throws UnsupportedEncodingException {
		HttpHandler httpHandler = setupHttpHandlerForSearch();

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (responseIsOk()) {
			return httpHandler.getResponseText();
		}
		return httpHandler.getErrorText();
	}

	private HttpHandler setupHttpHandlerForSearch() throws UnsupportedEncodingException {
		String url = baseUrl + "searchResult" + "/" + searchId + "/";
		url += "?searchData=" + URLEncoder.encode(json, StandardCharsets.UTF_8.name());
		HttpHandler httpHandler = createHttpHandlerWithAuthTokenAndUrl(url);
		httpHandler.setRequestMethod("GET");
		return httpHandler;
	}

	public void testReadRecordAndStoreJson() {
		String responseText = testReadRecord();
		DataRecord clientDataRecord = convertJsonToClientDataRecord(responseText);

		DataHolder.setRecord(clientDataRecord);
	}

	protected DataRecord convertJsonToClientDataRecord(String responseText) {
		JsonObject recordJsonObject = createJsonObjectFromResponseText(responseText);

		JsonToDataRecordConverter converter = JsonToDataRecordConverterImp
				.usingConverterFactory(jsonToDataConverterFactory);
		return converter.toInstance(recordJsonObject);
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	public JsonToDataConverterFactory getJsonToDataConverterFactory() {
		return jsonToDataConverterFactory;
	}

	public ChildComparer getChildComparer() {
		// needed for test
		return childComparer;
	}

	JsonToDataRecordConverter getJsonToDataRecordConverter() {
		// needed for test
		return jsonToDataRecordConverter;
	}

	public JsonHandler getJsonHandler() {
		// needed for test
		return jsonHandler;
	}
}
