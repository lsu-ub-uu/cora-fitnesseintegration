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

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class DeleteRecord {

	private String recordId;
	private String recordType;

	public DeleteRecord() {
		// needed by fitnesse
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;

	}

	public String deleteRecord() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		try {
			client.delete(recordType, recordId);
		} catch (Exception e) {
			return e.getMessage();
		}
		return "OK";
	}

	public String deleteAllRecordsForRecordType() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		ClientDataList list = client.readList(recordType);
		for (ClientData clientData : list.getDataList()) {
			ClientDataRecord clientDataAsRecord = (ClientDataRecord) clientData;
			client.delete(recordType, clientDataAsRecord.getId());
		}
		return "OK";
	}
}
