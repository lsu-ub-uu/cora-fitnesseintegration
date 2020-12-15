package se.uu.ub.cora.fitnesseintegration.compare;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.fitnesseintegration.spy.MethodCallRecorder;
import se.uu.ub.cora.json.parser.JsonObject;

public class JsonToDataRecordConverterSpy implements JsonToDataRecordConverter {

	MethodCallRecorder MCR = new MethodCallRecorder();
	private String startRecordId = null;

	@Override
	public DataRecord toInstance(JsonObject jsonObject) {
		MCR.addCall("jsonObject", jsonObject);

		DataRecord returnRecord = null;

		if (startRecordId != null) {
			returnRecord = createDataRecord(startRecordId);
			addNumberOfCallstoId();
		} else {
			returnRecord = createDataRecord("AnotherRecordIDNotToBeFound");
		}

		MCR.addReturned(returnRecord);
		return returnRecord;
	}

	private void addNumberOfCallstoId() {
		int counter = MCR.getNumberOfCallsToMethod("toInstance");
		startRecordId = startRecordId + counter;
	}

	private DataRecord createDataRecord(String recordId) {
		DataRecord returnRecord;
		ClientDataGroup withNameInData = ClientDataGroup.withNameInData("something");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		ClientDataAtomic atomicID = ClientDataAtomic.withNameInDataAndValue("id", recordId);

		withNameInData.addChild(recordInfo);
		recordInfo.addChild(atomicID);

		returnRecord = ClientDataRecord.withClientDataGroup(withNameInData);
		return returnRecord;
	}

	public void returnRecordWithID(String recordId) {
		this.startRecordId = recordId;

	}

}
