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

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.fitnesseintegration.ChildComparer;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.ReadResponse;
import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
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
	private ChildComparer childComparer;
	private String childrenToCompare;
	private int indexToCompareTo;
	private HttpHandlerFactory httpHandlerFactory;
	private String authToken;
	private String listFilter;
	private String id;
	private String searchId;
	private String json;

	public ComparerFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
		recordHandler = new RecordHandlerImp(httpHandlerFactory);
		childComparer = DependencyProvider.getChildComparer();
		jsonHandler = DependencyProvider.getJsonHandler();
		jsonToDataRecordConverter = DependencyProvider.getJsonToDataRecordConverter();
	}

	public void testReadAndStoreRecord() {
		String baseUrl = createBaseUrl();
		ReadResponse readResponse = recordHandler.readRecord(baseUrl + type + "/" + id, authToken);
		JsonObject recordJsonObject = jsonHandler.parseStringAsObject(readResponse.responseText);
		DataRecord record = jsonToDataRecordConverter.toInstance(recordJsonObject);
		DataHolder.setRecord(record);
	}

	private String createBaseUrl() {
		return SystemUrl.getUrl() + "rest/record/";
	}

	public void testReadRecordListAndStoreRecords() throws UnsupportedEncodingException {
		String baseUrl = createBaseUrl();
		storedListAsJson = recordHandler.readRecordList(baseUrl + type, authToken,
				listFilter).responseText;

		List<DataRecord> convertedRecords = convertToRecords();
		DataHolder.setRecordList(convertedRecords);
	}

	private List<DataRecord> convertToRecords() {
		JsonArray data = extractListOfRecords();
		List<DataRecord> convertedRecords = new ArrayList<>();
		Iterator<JsonValue> iterator = data.iterator();
		while (iterator.hasNext()) {
			JsonObject record = (JsonObject) iterator.next();
			convertAndAddRecord(record, convertedRecords);
		}
		return convertedRecords;
	}

	private JsonArray extractListOfRecords() {
		JsonObject list = jsonHandler.parseStringAsObject(storedListAsJson);
		JsonObject dataList = (JsonObject) list.getValue("dataList");
		return (JsonArray) dataList.getValue("data");
	}

	private void convertAndAddRecord(JsonObject recordJsonObject,
			List<DataRecord> convertedRecords) {
		DataRecord record = jsonToDataRecordConverter.toInstance(recordJsonObject);
		convertedRecords.add(record);
	}

	public String testCheckContain() {
		try {
			ClientDataGroup readDataGroup = DataHolder.getRecord().getClientDataGroup();
			return compareChildrenUsingDataGroup(readDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private String compareChildrenUsingDataGroup(ClientDataGroup clientDataGroup) {
		JsonObject childrenObject = jsonHandler.parseStringAsObject(childrenToCompare);
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(clientDataGroup,
				childrenObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	protected String joinErrorMessages(List<String> errorMessages) {
		StringJoiner compareError = new StringJoiner(" ");
		for (String errorMessage : errorMessages) {
			compareError.add(errorMessage);
		}
		return compareError.toString();
	}

	public String testCheckContainWithValues() {
		try {
			ClientDataGroup readDataGroup = DataHolder.getRecord().getClientDataGroup();
			return compareChildrenWithValuesUsingDataGroup(readDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private String compareChildrenWithValuesUsingDataGroup(ClientDataGroup clientDataGroup) {
		JsonObject childrenObject = jsonHandler.parseStringAsObject(childrenToCompare);
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(clientDataGroup, childrenObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public String testReadFromListCheckContain() {
		try {
			ClientDataGroup clientDataGroup = getDataGroupFromRecordHolderUsingIndex();
			return compareChildrenUsingDataGroup(clientDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private ClientDataGroup getDataGroupFromRecordHolderUsingIndex() {
		int index = getListIndexToCompareTo();
		return DataHolder.getRecordList().get(index).getClientDataGroup();
	}

	public String testReadFromListCheckContainWithValues() {
		try {
			ClientDataGroup clientDataGroup = getDataGroupFromRecordHolderUsingIndex();
			return compareChildrenWithValuesUsingDataGroup(clientDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	public void testSearchAndStoreRecords() throws UnsupportedEncodingException {
		String baseUrl = createBaseUrl();
		String url = baseUrl + "searchResult" + "/" + searchId;
		storedListAsJson = recordHandler.searchRecord(url, authToken, json).responseText;

		List<DataRecord> convertedRecords = convertToRecords();
		DataHolder.setRecordList(convertedRecords);
	}

	private int getListIndexToCompareTo() {
		return indexToCompareTo;
	}

	public void setListIndexToCompareTo(int index) {
		this.indexToCompareTo = index;

	}

	void setJsonHandler(JsonHandler jsonHandler) {
		// needed for test
		this.jsonHandler = jsonHandler;
	}

	void setJsonToDataRecordConverter(JsonToDataRecordConverter jsonToDataConverter) {
		// needed for test
		this.jsonToDataRecordConverter = jsonToDataConverter;
	}

	public void setChildren(String children) {
		childrenToCompare = children;

	}

	public ChildComparer getChildComparer() {
		// needed for test
		return childComparer;
	}

	public HttpHandlerFactory getHttpHandlerFactory() {
		// needed for test
		return httpHandlerFactory;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setListFilter(String listFilter) {
		this.listFilter = listFilter;
	}

	public JsonHandler getJsonHandler() {
		// needed for test
		return jsonHandler;
	}

	public JsonToDataRecordConverter getJsonToDataRecordConverter() {
		// needed for test
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

}
