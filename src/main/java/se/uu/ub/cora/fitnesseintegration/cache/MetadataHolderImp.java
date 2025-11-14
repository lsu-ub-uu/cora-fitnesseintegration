/*
 * Copyright 2024 Uppsala University Library
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
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataRecord;

public class MetadataHolderImp implements MetadataHolder {

	private Map<String, ClientDataRecord> dataRecords = new HashMap<>();

	@Override
	public void addDataRecord(ClientDataRecord dataRecord) {
		dataRecords.put(dataRecord.getId(), dataRecord);
	}

	@Override
	public ClientDataRecord getDataRecordById(String recordId) {
		return dataRecords.get(recordId);
	}

	public Map<String, ClientDataRecord> onlyForTestGetMetadata() {
		return dataRecords;
	}
}
