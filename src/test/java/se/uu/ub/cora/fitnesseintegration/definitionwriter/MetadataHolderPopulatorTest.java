package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class MetadataHolderPopulatorTest {

	private static final String SOME_ID = "someId";
	private static final String AUTH_TOKEN = "someAuthToken";
	private JavaClientFactorySpy javaClientFactory;
	private DataClientSpy dataClient;
	private MetadataHolderPopulatorImp populator;
	private ClientDataRecordSpy dataRecord;

	@BeforeMethod
	public void setup() {
		setupDataClient();
		setupClientDataList();
		populator = new MetadataHolderPopulatorImp();
	}

	@Test
	public void testDataClientIsFactored() throws Exception {
		populator.createAndPopulateHolder(AUTH_TOKEN);
		javaClientFactory.MCR
				.assertMethodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		assertEquals(dataClient, populator.onlyForTestGetDataClient());
	}

	@Test
	public void testCreateAndPopulateHolder() throws Exception {
		MetadataHolder metadataHolder = populator.createAndPopulateHolder(AUTH_TOKEN);

		dataClient.MCR.assertMethodWasCalled("readList");
		assertNotNull(metadataHolder);
		ClientDataRecord fetchedRecord = metadataHolder.getDataRecordById(SOME_ID);
		assertEquals(fetchedRecord, dataRecord);
	}

	private void setupDataClient() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		dataClient = new DataClientSpy();
		javaClientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAuthTokenCredentials", () -> dataClient);
	}

	private void setupClientDataList() {
		ClientDataListSpy clientDataListSpy = new ClientDataListSpy();
		List<ClientDataRecordSpy> dataList = new ArrayList<>();
		dataRecord = new ClientDataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> SOME_ID);
		dataList.add(dataRecord);
		clientDataListSpy.MRV.setDefaultReturnValuesSupplier("getDataList", () -> dataList);
		dataClient.MRV.setSpecificReturnValuesSupplier("readList", () -> clientDataListSpy,
				"metadata");
	}

}
