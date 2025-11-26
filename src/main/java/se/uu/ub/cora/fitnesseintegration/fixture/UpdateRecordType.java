/*
 * Copyright 2025 Olov McKie
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

import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class UpdateRecordType {
	private ClientDataRecordGroup dataRecordGroup;

	public UpdateRecordType() {
		// needed by fitnesse
	}

	public void setId(String id) {
		dataRecordGroup = RecordTypeProvider.getRecordGroup(id);
	}

	public void setIdSource(String idSource) {
		removeCreateNewAndAddToDataRecordGroup("idSource", idSource);
	}

	private void removeCreateNewAndAddToDataRecordGroup(String nameInData, String idSource) {
		dataRecordGroup.removeAllChildrenWithNameInData(nameInData);
		var newIdSource = ClientDataProvider.createAtomicUsingNameInDataAndValue(nameInData,
				idSource);
		dataRecordGroup.addChild(newIdSource);
	}

	public void setPublic(String publicValue) {
		removeCreateNewAndAddToDataRecordGroup("public", publicValue);
	}

	public void setUsePermissionUnit(String usePermissionUnit) {
		removeCreateNewAndAddToDataRecordGroup("usePermissionUnit", usePermissionUnit);
	}

	public void setUseVisibility(String useVisibility) {
		removeCreateNewAndAddToDataRecordGroup("useVisibility", useVisibility);
	}

	public void setUseTrashBin(String useTrashBin) {
		removeCreateNewAndAddToDataRecordGroup("useTrashBin", useTrashBin);
	}

	public void setStoreInArchive(String storeInArchive) {
		removeCreateNewAndAddToDataRecordGroup("storeInArchive", storeInArchive);
	}

	public String update() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		ClientDataRecord updated = client.update(dataRecordGroup.getType(), dataRecordGroup.getId(),
				dataRecordGroup);
		RecordTypeProvider.setRecordGroupInInternalMap(dataRecordGroup.getId(),
				updated.getDataRecordGroup());
		return "OK";
	}
}
