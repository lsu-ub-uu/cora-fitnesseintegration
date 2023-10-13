package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class DataHolderTest {
	@Test
	public void testInit() {
		DataHolder recordHolder = new DataHolder();
		assertNotNull(recordHolder);
	}

	@Test
	public void testSetAndGetRecord() throws Exception {
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("someName");
		ClientDataRecord clientClientDataRecord = ClientDataRecord.withClientDataGroup(clientDataGroup);
		DataHolder.setRecord(clientClientDataRecord);
		assertEquals(DataHolder.getRecord(), clientClientDataRecord);
	}

	@Test
	public void testSetAndGetListOfRecords() {
		List<ClientDataRecord> recordList = new ArrayList<>();
		recordList.add(createRecordWithDataGroupUsingNameInData("firstDataGroup"));
		recordList.add(createRecordWithDataGroupUsingNameInData("secondDataGroup"));
		DataHolder.setRecordList(recordList);
		List<ClientDataRecord> recordListFromHolder = DataHolder.getRecordList();
		assertEquals(recordListFromHolder.size(), 2);
		assertSame(recordListFromHolder.get(0), recordList.get(0));
		assertSame(recordListFromHolder.get(1), recordList.get(1));

	}

	private ClientDataRecord createRecordWithDataGroupUsingNameInData(String nameInData) {
		ClientDataGroup firstDataGroup = ClientDataGroup.withNameInData(nameInData);
		return ClientDataRecord.withClientDataGroup(firstDataGroup);
	}
}
