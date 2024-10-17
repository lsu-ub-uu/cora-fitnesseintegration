package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import se.uu.ub.cora.clientdata.ClientDataRecord;

public interface MetadataHolder {

	void addDataRecord(ClientDataRecord dataRecord);

	ClientDataRecord getDataRecordById(String recordId);

}
