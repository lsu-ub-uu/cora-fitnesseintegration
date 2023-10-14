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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.fitnesseintegration.BasicHttpResponse;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.ExtendedHttpResponse;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.javaclient.rest.RestClientFactory;
import se.uu.ub.cora.javaclient.rest.RestClientFactoryImp;

public class ComparerFixture {

	private RecordHandler recordHandler;
	private String type;
	private String storedListAsJson;
	protected JsonHandler jsonHandler;
	protected int indexToCompareTo;
	private HttpHandlerFactory httpHandlerFactory;
	private String authToken;
	private String listFilter;
	private String id;
	private String searchId;
	private String json;
	protected String baseUrl = SystemUrl.getUrl() + "rest/";
	protected String baseRecordUrl = baseUrl + "record/";
	private StatusType statusType;
	private int indexToStore = 0;
	private String idToStore;
	private String createdId;

	public ComparerFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
		RestClientFactory restClientFactory = RestClientFactoryImp
				.usingBaseUrlAndAppTokenVerifierUrl(baseUrl, SystemUrl.getAppTokenVerifierUrl());
		recordHandler = new RecordHandlerImp(httpHandlerFactory, restClientFactory);
	}

	public String testReadAndStoreRecord() {
		String recordAsJson = readRecordAsJsonText();
		ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
		DataHolder.setRecord(dataRecord);
		return recordAsJson;
	}

	private String readRecordAsJsonText() {
		BasicHttpResponse readResponse = recordHandler.readRecord(authToken, type, id);
		return readResponse.responseText;
	}

	protected ClientDataRecord convertJsonToClientDataRecord(String jsonText) {
		JsonToClientDataConverter toClientConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(jsonText);
		return (ClientDataRecord) toClientConverter.toInstance();
	}

	public void testReadRecordListAndStoreRecords() throws UnsupportedEncodingException {
		String recordListAsJson = readRecordListAsJsonText();
		storedListAsJson = recordListAsJson;

		List<ClientDataRecord> convertedRecords = convertJsonTextToListOfRecords(recordListAsJson);
		DataHolder.setRecord(convertedRecords.get(indexToStore));
		DataHolder.setRecordList(convertedRecords);
	}

	private String readRecordListAsJsonText() throws UnsupportedEncodingException {
		BasicHttpResponse httpResponseForList = recordHandler.readRecordList(authToken, type,
				listFilter);
		return httpResponseForList.responseText;
	}

	private List<ClientDataRecord> convertJsonTextToListOfRecords(String recordsAsJson) {
		ClientDataList clientDataList = convertJsonToClientDataList(recordsAsJson);
		List<ClientDataRecord> convertedRecords = new ArrayList<>();
		for (ClientData clientData : clientDataList.getDataList()) {
			convertedRecords.add((ClientDataRecord) clientData);
		}
		return convertedRecords;
	}

	protected ClientDataList convertJsonToClientDataList(String jsonText) {
		JsonToClientDataConverter toClientConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(jsonText);
		return (ClientDataList) toClientConverter.toInstance();
	}

	public String testReadRecordListAndStoreRecordById() throws UnsupportedEncodingException {
		DataHolder.setRecord(null);
		String recordListAsJson = readRecordListAsJsonText();
		storedListAsJson = recordListAsJson;

		List<ClientDataRecord> convertedRecords = convertJsonTextToListOfRecords(recordListAsJson);
		DataHolder.setRecordList(convertedRecords);

		ClientDataRecord foundRecord = findRecordWithIdInList(convertedRecords, idToStore);
		DataHolder.setRecord(foundRecord);
		return storedListAsJson;
	}

	private ClientDataRecord findRecordWithIdInList(List<ClientDataRecord> convertedRecords,
			String idToFind) {
		for (ClientDataRecord clientRecord : convertedRecords) {
			if (clientRecord.getId().equals(idToFind)) {
				return clientRecord;
			}
		}
		return null;
	}

	protected String joinErrorMessages(List<String> errorMessages) {
		StringJoiner compareError = new StringJoiner(" ");
		for (String errorMessage : errorMessages) {
			compareError.add(errorMessage);
		}
		return compareError.toString();
	}

	protected ClientDataRecord getClientDataRecordFromRecordHolderUsingIndex() {
		return DataHolder.getRecordList().get(indexToCompareTo);
	}

	public void testSearchAndStoreRecords() throws UnsupportedEncodingException {
		String url = baseRecordUrl + "searchResult" + "/" + searchId;
		String recordListAsJson = recordHandler.searchRecord(url, authToken, json).responseText;
		storedListAsJson = recordListAsJson;

		List<ClientDataRecord> convertedRecords = convertJsonTextToListOfRecords(recordListAsJson);
		DataHolder.setRecordList(convertedRecords);
		DataHolder.setRecord(convertedRecords.get(indexToStore));
	}

	public String testUpdateAndStoreRecord() {
		DataHolder.setRecord(null);
		BasicHttpResponse response = recordHandler.updateRecord(authToken, type, id, json);
		statusType = Response.Status.fromStatusCode(response.statusCode);
		if (200 == (response.statusCode)) {
			String recordAsJson = response.responseText;
			ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
			DataHolder.setRecord(dataRecord);
		}
		return response.responseText;
	}

	public String testCreateAndStoreRecord() {
		DataHolder.setRecord(null);
		ExtendedHttpResponse response = recordHandler.createRecord(authToken, type, json);
		statusType = Response.Status.fromStatusCode(response.statusCode);
		createdId = response.createdId;
		if (201 == (response.statusCode)) {
			String recordAsJson = response.responseText;
			ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
			DataHolder.setRecord(dataRecord);
		}
		return response.responseText;
	}

	public void setListIndexToCompareTo(int index) {
		this.indexToCompareTo = index;
	}

	void onlyForTestSetJsonHandler(JsonHandler jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

	public HttpHandlerFactory onlyForTestGetHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setListFilter(String listFilter) {
		this.listFilter = listFilter;
	}

	public JsonHandler onlyForTestGetJsonHandler() {
		return jsonHandler;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RecordHandler getRecordHandler() {
		return recordHandler;
	}

	void setRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}

	public String getStoredListAsJson() {
		return storedListAsJson;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public void setJson(String json) {
		this.json = json;
	}

	public StatusType getStatusType() {
		return statusType;
	}

	public void setIndexToStore(int indexToStore) {
		this.indexToStore = indexToStore;
	}

	public void setIdToStore(String idToStore) {
		this.idToStore = idToStore;
	}

	public String getCreatedId() {
		return createdId;
	}
}
