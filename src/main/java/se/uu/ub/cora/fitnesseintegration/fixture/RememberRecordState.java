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
package se.uu.ub.cora.fitnesseintegration.fixture;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class RememberRecordState {
	private static Map<String, ClientDataRecordGroup> memory = new HashMap<>();
	private String currentType;
	private String currentId;
	private String currentVersion = "";

	public RememberRecordState() {
		// needed by fitnesse
	}

	public void setType(String type) {
		currentType = type;
	}

	public void setId(String id) {
		currentId = id;
	}

	public void setVersion(String version) {
		currentVersion = version;
	}

	public String remember() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		ClientDataRecord read = client.read(currentType, currentId);

		memory.put(currentKey(), read.getDataRecordGroup());
		return "OK";
	}

	private String currentKey() {
		return currentType + currentId + currentVersion;
	}

	public String memoryToStorage() {
		ClientDataRecordGroup dataRecordGroup = memory.get(currentKey());
		addIgnoreOverwriteProtectionToMakeSureUpdateDoesNotGetConflictWithNewVersion(
				dataRecordGroup);

		ClientDataRecord updated = updateInStorage(dataRecordGroup);
		possiblyUpdateRecordTypeInRecordTypeProvider(updated);
		return "OK";
	}

	private void addIgnoreOverwriteProtectionToMakeSureUpdateDoesNotGetConflictWithNewVersion(
			ClientDataRecordGroup dataRecordGroup) {
		ClientDataGroup recordInfo = dataRecordGroup.getFirstGroupWithNameInData("recordInfo");
		recordInfo.addChild(ClientDataProvider
				.createAtomicUsingNameInDataAndValue("ignoreOverwriteProtection", "true"));
	}

	private ClientDataRecord updateInStorage(ClientDataRecordGroup dataRecordGroup) {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		return client.update(currentType, currentId, dataRecordGroup);
	}

	private void possiblyUpdateRecordTypeInRecordTypeProvider(ClientDataRecord updated) {
		if ("recordType".equals(currentType)) {
			RecordTypeProvider.setRecordGroupInInternalMap(currentId, updated.getDataRecordGroup());
		}
	}

	public static String forgetAllRecords() {
		memory = new HashMap<>();
		return "OK";
	}

}
