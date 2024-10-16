package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.clientdata.ClientDataRecord;

public class DataRecordHolder {

	private Map<String, ClientDataRecord> dataRecords = new HashMap<>();

	public void addDataRecord(ClientDataRecord dataRecord) {
		dataRecords.put(dataRecord.getId(), dataRecord);
	}

	public ClientDataRecord getDataRecordById(String recordId) {
		return dataRecords.get(recordId);
	}
}
