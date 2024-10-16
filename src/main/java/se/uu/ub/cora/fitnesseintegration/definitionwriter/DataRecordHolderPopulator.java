package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class DataRecordHolderPopulator {

	private static final String METADATA = "metadata";
	private String baseUrl = SystemUrl.getUrl() + "rest/";
	private String appTokenUrl = SystemUrl.getAppTokenVerifierUrl();

	public DataRecordHolder createAndPopulateHolder(String authToken) {
		DataClient dataClient = createDataClientUsingAuthToken(authToken);
		ClientDataList readList = dataClient.readList(METADATA);
		DataRecordHolder holder = new DataRecordHolder();
		for (ClientData dataRecord : readList.getDataList()) {
			holder.addDataRecord((ClientDataRecord) dataRecord);
		}
		return holder;
	}

	private DataClient createDataClientUsingAuthToken(String authToken) {
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenUrl, authToken);
		return JavaClientProvider
				.createDataClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
	}

}
