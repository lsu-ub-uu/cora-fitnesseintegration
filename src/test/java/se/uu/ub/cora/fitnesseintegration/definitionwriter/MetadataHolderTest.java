package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;

public class MetadataHolderTest {

	private static final String SOME_ID = "someId";
	MetadataHolderImp holder = new MetadataHolderImp();
	private ClientDataRecordSpy dataRecord;

	@BeforeMethod
	public void setup() {
		dataRecord = new ClientDataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> SOME_ID);
	}

	@Test
	public void testAddDataRecord() {
		holder.addDataRecord(dataRecord);
		var record = holder.onlyForTestGetMetadata().get(SOME_ID);
		assertEquals(record, dataRecord);
	}

	@Test
	public void testGetDataRecordById() throws Exception {
		holder.addDataRecord(dataRecord);
		ClientDataRecord record = holder.getDataRecordById(SOME_ID);
		assertEquals(record, dataRecord);
	}
}
