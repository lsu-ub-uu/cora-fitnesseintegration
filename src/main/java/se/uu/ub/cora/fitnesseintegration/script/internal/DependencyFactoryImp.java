/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.script.internal;

import se.uu.ub.cora.fitnesseintegration.internal.ReadAndStoreRecord;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.internal.WaiterImp;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;
import se.uu.ub.cora.javaclient.rest.RestClient;

public class DependencyFactoryImp implements DependencyFactory {
	@Override
	public ReadAndStoreRecord factorReadAndStoreRecord(String authToken, String type, String id) {
		String baseUrl = SystemUrl.getUrl() + "rest/";
		String appTokenVerifierUrl = SystemUrl.getAppTokenVerifierUrl() + "rest/";
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenVerifierUrl, authToken);

		DataClient dataClient = JavaClientProvider
				.createDataClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
		return ReadAndStoreRecord.usingDataClientAndTypeAndId(dataClient, type, id);
	}

	// TODO: add tests and code
	public ReadAndStoreRecord factorReadAndStoreRecordAsJson(String authToken, String type,
			String id) {
		String baseUrl = SystemUrl.getUrl() + "rest/";
		String appTokenVerifierUrl = SystemUrl.getAppTokenVerifierUrl() + "rest/";
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenVerifierUrl, authToken);

		// DataClient dataClient = JavaClientProvider
		// .createDataClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
		// return ReadAndStoreRecord.usingDataClientAndTypeAndId(dataClient, type, id);
		RestClient restClient = JavaClientProvider
				.createRestClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
		return ReadAndStoreRecordAsJson.usingRestClientAndTypeAndId(restClient, type, id);
	}

	@Override
	public Waiter factorWaiter() {
		return new WaiterImp();
	}

}
