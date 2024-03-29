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

public class DataHolder {

	public DataHolder() {
		// needed by fitnesse
		super();
	}

	private static ClientDataRecord clientClientDataRecord;
	private static List<ClientDataRecord> dataRecords;
	private static String recordJson;

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

	public static void setRecordAsJson(String createdRecordJson) {
		DataHolder.recordJson = createdRecordJson;
	}

	public static String getRecordAsJson() {
		return recordJson;
	}
}