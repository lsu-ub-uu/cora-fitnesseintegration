package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.DataRecord;

public class RecordHolderTest {
	@Test
	public void testInit() {
		RecordHolder recordHolder = new RecordHolder();
		assertNotNull(recordHolder);
	}

	@Test
	public void testSetAndGetRecord() throws Exception {
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("someName");
		DataRecord clientDataRecord = ClientDataRecord.withClientDataGroup(clientDataGroup);
		RecordHolder.setRecord(clientDataRecord);
		assertEquals(RecordHolder.getRecord(), clientDataRecord);
	}
}
