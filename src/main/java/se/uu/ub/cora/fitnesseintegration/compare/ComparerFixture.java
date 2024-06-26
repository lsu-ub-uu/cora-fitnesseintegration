/*
 * Copyright 2020, 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.compare;

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
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class ComparerFixture {

	private RecordHandler recordHandler;
	private String type;
	private String storedListAsJson;
	protected int indexToCompareTo;
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
		recordHandler = new RecordHandlerImp(baseUrl, SystemUrl.getAppTokenVerifierUrl());
	}

	public String testReadAndStoreRecord() {
		RestResponse response = recordHandler.readRecord(authToken, type, id);
		setStatusTypeUsingResponseCode(response.responseCode());
		return handleResponseText(response.responseText());
	}

	private String handleResponseText(String responseText) {
		if (readNotOk()) {
			return responseText;
		}
		return transformRecordToClientDataAndStoreItInDataHolder(responseText);
	}

	private boolean readNotOk() {
		return statusType.getStatusCode() != 200;
	}

	private void setStatusTypeUsingResponseCode(int responseCode) {
		statusType = Response.Status.fromStatusCode(responseCode);
	}

	private String transformRecordToClientDataAndStoreItInDataHolder(String recordAsJson) {
		ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
		DataHolder.setRecord(dataRecord);
		return recordAsJson;
	}

	protected ClientDataRecord convertJsonToClientDataRecord(String jsonText) {
		JsonToClientDataConverter toClientConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(jsonText);
		return (ClientDataRecord) toClientConverter.toInstance();
	}

	public void testReadRecordListAndStoreRecords() {
		String recordListAsJson = readRecordListAsJsonText();
		storedListAsJson = recordListAsJson;

		List<ClientDataRecord> convertedRecords = convertJsonTextToListOfRecords(recordListAsJson);
		DataHolder.setRecord(convertedRecords.get(indexToStore));
		DataHolder.setRecordList(convertedRecords);
	}

	private String readRecordListAsJsonText() {
		RestResponse httpResponseForList = recordHandler.readRecordList(authToken, type,
				listFilter);
		return httpResponseForList.responseText();
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

	public String testReadRecordListAndStoreRecordById() {
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

	public void testSearchAndStoreRecords() {
		RestResponse searchRecord = recordHandler.searchRecord(authToken, searchId, json);
		storedListAsJson = searchRecord.responseText();

		List<ClientDataRecord> convertedRecords = convertJsonTextToListOfRecords(storedListAsJson);
		DataHolder.setRecordList(convertedRecords);
		DataHolder.setRecord(convertedRecords.get(indexToStore));
	}

	public String testUpdateAndStoreRecord() {
		DataHolder.setRecord(null);
		RestResponse response = recordHandler.updateRecord(authToken, type, id, json);
		setStatusTypeUsingResponseCode(response.responseCode());
		if (200 == (response.responseCode())) {
			String recordAsJson = response.responseText();
			ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
			DataHolder.setRecord(dataRecord);
		}
		return response.responseText();
	}

	public String testCreateAndStoreRecord() {
		DataHolder.setRecord(null);
		RestResponse response = recordHandler.createRecord(authToken, type, json);
		setStatusTypeUsingResponseCode(response.responseCode());
		if (201 == (response.responseCode())) {
			if (response.createdId().isPresent()) {
				createdId = response.createdId().get();
			}
			String recordAsJson = response.responseText();
			ClientDataRecord dataRecord = convertJsonToClientDataRecord(recordAsJson);
			DataHolder.setRecord(dataRecord);
		}
		return response.responseText();
	}

	public void setListIndexToCompareTo(int index) {
		this.indexToCompareTo = index;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setListFilter(String listFilter) {
		this.listFilter = listFilter;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RecordHandler onlyForTestGetRecordHandler() {
		return recordHandler;
	}

	void onlyForTestSetRecordHandler(RecordHandler recordHandler) {
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
