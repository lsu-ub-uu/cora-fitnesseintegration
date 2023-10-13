package se.uu.ub.cora.fitnesseintegration.compare;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToClientDataRecordConverter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class JsonToClientDataRecordConverterSpy implements JsonToClientDataRecordConverter {

	MethodCallRecorder MCR = new MethodCallRecorder();
	private String startRecordId = null;

	@Override
	public ClientDataRecord toInstance(JsonObject jsonObject) {
		MCR.addCall("jsonObject", jsonObject);

		ClientDataRecord returnRecord = null;

		if (startRecordId != null) {
			returnRecord = createClientDataRecord(startRecordId);
			addNumberOfCallstoId();
		} else {
			returnRecord = createClientDataRecord("AnotherRecordIDNotToBeFound");
		}

		MCR.addReturned(returnRecord);
		return returnRecord;
	}

	private void addNumberOfCallstoId() {
		int counter = MCR.getNumberOfCallsToMethod("toInstance");
		startRecordId = startRecordId + counter;
	}

	private ClientDataRecord createClientDataRecord(String recordId) {
		ClientDataRecord returnRecord;
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
