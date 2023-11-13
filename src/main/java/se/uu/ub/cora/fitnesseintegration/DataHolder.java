/*
 * Copyright 2018, 2023 Uppsala University Library
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;

public class DataHolder {

	public DataHolder() {
		// needed by fitnesse
		super();
	}

	private static ClientDataRecord clientClientDataRecord;
	private static List<ClientDataRecord> dataRecords;
	private static ClientDataRecord createdRecordData;
	private static String createdRecordJson;

	public static void setRecord(ClientDataRecord clientClientDataRecord) {
		DataHolder.clientClientDataRecord = clientClientDataRecord;
	}

	public static ClientDataRecord getRecord() {
		return clientClientDataRecord;
	}

	public static void setRecordList(List<ClientDataRecord> recordList) {
		dataRecords = new ArrayList<>();
		dataRecords.addAll(recordList);

	}

	public static List<ClientDataRecord> getRecordList() {
		return dataRecords;
	}

	public static void setCreatedRecordAsData(ClientDataRecord createdRecordData) {
		DataHolder.createdRecordData = createdRecordData;
		DataHolder.createdRecordJson = null;

	}

	public static Object getCreatedRecordAsData() {
		// if (createdRecordData) {
		// return createdRecordData;
		// }
		// if (createdRecordJson) {
		// createdRecordData = convert();
		// }

		return createdRecordData;
	}

	public static void setCreatedRecordAsJson(String createdRecordJson) {
		DataHolder.createdRecordData = null;
		DataHolder.createdRecordJson = createdRecordJson;
	}

	public static String getCreatedRecordAsJson() {
		if (createdRecordData != null) {
			// return "";
			ClientDataToJsonConverterFactory converterFactory = ClientDataToJsonConverterProvider
					.createImplementingFactory();
			ClientDataToJsonConverter converter = converterFactory
					.factorUsingConvertible(createdRecordData);
			return converter.toJson();

		}

		return createdRecordJson;
	}

	// protected ClientDataRecord convertJsonToClientDataRecord(String jsonText) {
	// JsonToClientDataConverter toClientConverter = JsonToClientDataConverterProvider
	// .getConverterUsingJsonString(jsonText);
	// return (ClientDataRecord) toClientConverter.toInstance();
	// }

}
