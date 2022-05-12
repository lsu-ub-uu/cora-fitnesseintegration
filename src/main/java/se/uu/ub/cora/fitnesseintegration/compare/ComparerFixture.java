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
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.fitnesseintegration.BasicHttpResponse;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.ExtendedHttpResponse;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.javaclient.rest.RestClientFactoryImp;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class ComparerFixture {

	private RecordHandler recordHandler;
	private String type;
	private String storedListAsJson;
	protected JsonHandler jsonHandler;
	private JsonToDataRecordConverter jsonToDataRecordConverter;
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

	public ComparerFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
		RestClientFactoryImp restClientFactory = new RestClientFactoryImp(baseUrl);
		recordHandler = new RecordHandlerImp(httpHandlerFactory, restClientFactory);
		jsonHandler = DependencyProvider.getJsonHandler();
		jsonToDataRecordConverter = DependencyProvider.getJsonToDataRecordConverter();
	}

	public String testReadAndStoreRecord() {
		BasicHttpResponse readResponse = recordHandler.readRecord(authToken, type, id);
		JsonObject recordJsonObject = jsonHandler.parseStringAsObject(readResponse.responseText);
		DataRecord dataRecord = jsonToDataRecordConverter.toInstance(recordJsonObject);
		DataHolder.setRecord(dataRecord);
		return readResponse.responseText;
	}

	public void testReadRecordListAndStoreRecords() throws UnsupportedEncodingException {
		storedListAsJson = recordHandler.readRecordList(authToken, type, listFilter).responseText;
		List<DataRecord> convertedRecords = convertToRecords();
		DataHolder.setRecord(convertedRecords.get(indexToStore));
		DataHolder.setRecordList(convertedRecords);
	}

	private List<DataRecord> convertToRecords() {
		Iterator<JsonValue> iterator = getIteratorFromListOfRecords();
		List<DataRecord> convertedRecords = new ArrayList<>();
		while (iterator.hasNext()) {
			JsonObject dataRecord = (JsonObject) iterator.next();
			convertAndAddRecord(dataRecord, convertedRecords);
		}
		return convertedRecords;
	}

	private JsonArray extractListOfRecords() {
		JsonObject list = jsonHandler.parseStringAsObject(storedListAsJson);
		JsonObject dataList = (JsonObject) list.getValue("dataList");
		return (JsonArray) dataList.getValue("data");
	}

	public String testReadRecordListAndStoreRecordById() throws UnsupportedEncodingException {
		DataHolder.setRecord(null);
		storedListAsJson = recordHandler.readRecordList(authToken, type, listFilter).responseText;
		findAndStoreRecord();
		return storedListAsJson;
	}

	private void findAndStoreRecord() {
		Iterator<JsonValue> iterator = getIteratorFromListOfRecords();
		boolean recordFound = false;
		while (!recordFound && iterator.hasNext()) {
			recordFound = compareAndStoreIfFound(iterator, recordFound);
		}
	}

	private Iterator<JsonValue> getIteratorFromListOfRecords() {
		JsonArray dataArray = extractListOfRecords();
		return dataArray.iterator();
	}

	private boolean compareAndStoreIfFound(Iterator<JsonValue> iterator, boolean recordFound) {
		JsonObject jsonRecord = (JsonObject) iterator.next();
		DataRecord recordToStore = jsonToDataRecordConverter.toInstance(jsonRecord);
		return storeRecordIfFound(recordFound, recordToStore);
	}

	private boolean storeRecordIfFound(boolean recordFound, DataRecord recordToStore) {
		if (idToStoreEqualsIdIn(recordToStore)) {
			recordFound = true;
			DataHolder.setRecord(recordToStore);
		}
		return recordFound;
	}

	private boolean idToStoreEqualsIdIn(DataRecord recordToStore) {
		return getRecordIdValue(recordToStore).equals(idToStore);
	}

	private String getRecordIdValue(DataRecord recordToStore) {
		return recordToStore.getClientDataGroup().getFirstGroupWithNameInData("recordInfo")
				.getFirstAtomicValueWithNameInData("id");
	}

	private void convertAndAddRecord(JsonObject recordJsonObject,
			List<DataRecord> convertedRecords) {
		DataRecord dataRecord = jsonToDataRecordConverter.toInstance(recordJsonObject);
		convertedRecords.add(dataRecord);
	}

	protected String joinErrorMessages(List<String> errorMessages) {
		StringJoiner compareError = new StringJoiner(" ");
		for (String errorMessage : errorMessages) {
			compareError.add(errorMessage);
		}
		return compareError.toString();
	}

	protected ClientDataGroup getDataGroupFromRecordHolderUsingIndex() {
		return DataHolder.getRecordList().get(indexToCompareTo).getClientDataGroup();
	}

	protected DataRecord getDataRecordFromRecordHolderUsingIndex() {
		return DataHolder.getRecordList().get(indexToCompareTo);
	}

	public void testSearchAndStoreRecords() throws UnsupportedEncodingException {
		String url = baseRecordUrl + "searchResult" + "/" + searchId;
		storedListAsJson = recordHandler.searchRecord(url, authToken, json).responseText;

		List<DataRecord> convertedRecords = convertToRecords();
		DataHolder.setRecord(convertedRecords.get(indexToStore));
		DataHolder.setRecordList(convertedRecords);
	}

	public String testUpdateAndStoreRecord() {
		BasicHttpResponse response = recordHandler.updateRecord(authToken, type, id, json);
		tryToCreateRecordFromResponseAndSetInDataHolder(response);
		statusType = Response.Status.fromStatusCode(response.statusCode);
		return response.responseText;
	}

	private void tryToCreateRecordFromResponseAndSetInDataHolder(BasicHttpResponse response) {
		try {
			createRecordFromResponseAndSetInDataHolder(response);
		} catch (JsonParseException e) {
			DataHolder.setRecord(null);
		}
	}

	private void createRecordFromResponseAndSetInDataHolder(BasicHttpResponse response) {
		DataRecord dataRecord = createRecordFromResponseText(response.responseText);
		DataHolder.setRecord(dataRecord);
	}

	public String testCreateAndStoreRecord() {
		ExtendedHttpResponse response = recordHandler.createRecord(authToken, type, json);
		tryToCreateRecordFromExtendedResponseAndSetInDataHolder(response);
		statusType = Response.Status.fromStatusCode(response.statusCode);
		return response.responseText;
	}

	private void tryToCreateRecordFromExtendedResponseAndSetInDataHolder(
			ExtendedHttpResponse response) {
		try {
			createRecordFromExtendedResponseAndSetInDataHolder(response);
		} catch (JsonParseException e) {
			DataHolder.setRecord(null);
		}
	}

	private void createRecordFromExtendedResponseAndSetInDataHolder(ExtendedHttpResponse response) {
		DataRecord dataRecord = createRecordFromResponseText(response.responseText);
		DataHolder.setRecord(dataRecord);
	}

	private DataRecord createRecordFromResponseText(String responseText) {
		JsonObject recordJsonObject = jsonHandler.parseStringAsObject(responseText);
		return jsonToDataRecordConverter.toInstance(recordJsonObject);
	}

	public void setListIndexToCompareTo(int index) {
		this.indexToCompareTo = index;
	}

	void onlyForTestSetJsonHandler(JsonHandler jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

	void onlyForTestSetJsonToDataRecordConverter(JsonToDataRecordConverter jsonToDataConverter) {
		this.jsonToDataRecordConverter = jsonToDataConverter;
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

	public JsonToDataRecordConverter onlyForTestGetJsonToDataRecordConverter() {
		return jsonToDataRecordConverter;
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
}
