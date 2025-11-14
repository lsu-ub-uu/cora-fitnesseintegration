/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.javaclient.data.DataClient;

public class ValidationTypeProvider {
	private static Map<String, ClientDataRecordGroup> recordGroupMap = new HashMap<>();

	private ValidationTypeProvider() {
		throw new UnsupportedOperationException();
	}

	public static ClientDataRecordGroup getRecordGroup(String id) {
		possiblyLoadValidationTypesFromServer();
		return recordGroupMap.get(id);
	}

	private static void possiblyLoadValidationTypesFromServer() {
		if (recordTypesNotLoaded()) {
			loadValidationTypesFromServer();
		}
	}

	private static boolean recordTypesNotLoaded() {
		return recordGroupMap.size() == 0;
	}

	private static void loadValidationTypesFromServer() {
		List<ClientData> listOfRecords = loadListOfValidationTypesFromServer();
		for (ClientData recordItem : listOfRecords) {
			ClientDataRecord clientDataRecord = (ClientDataRecord) recordItem;
			recordGroupMap.put(clientDataRecord.getId(), clientDataRecord.getDataRecordGroup());
		}
	}

	private static List<ClientData> loadListOfValidationTypesFromServer() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		ClientDataList dataList = client.readList("validationType");
		return dataList.getDataList();
	}

	public static void resetInternalHolder() {
		recordGroupMap = new HashMap<>();
	}

	public static void onlyForTestAddRecordGroupToInternalMap(String id,
			ClientDataRecordGroup clientDataRecordGroup) {
		recordGroupMap.put(id, clientDataRecordGroup);
	}
}
