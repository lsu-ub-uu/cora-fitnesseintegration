/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.cache;

import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.fitnesseintegration.script.LoginToken;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.JavaClientAppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

/**
 * FitnesseJavaClientProvider provides javaclients for use in fitnesse.
 * <p>
 * For testing, as this class uses JavaClientProvider, mock the factory method for it:
 * 
 * <code>
   JavaClientFactorySpy clientFactory = new JavaClientFactorySpy();
   JavaClientProvider.onlyForTestSetJavaClientFactory(clientFactory);
 </code>
 * 
 */
public class FitnesseJavaClientProvider {
	public static final String FITNESSE_ADMIN_JAVA_CLIENT = "fitnesseAdminJavaClient";
	private static Map<String, DataClient> clients = new HashMap<>();

	private FitnesseJavaClientProvider() {
		throw new UnsupportedOperationException();
	}

	/**
	 * returns a DataClient for fitnesseAdmin, if first call one is created, later calls returns the
	 * same one. This is created with information from SystemUrl and LoginToken, and keept logged
	 * in.
	 * <p>
	 * 
	 * For testing, as this class uses JavaClientProvider, mock the factory method for it: <code>
	   JavaClientFactorySpy clientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(clientFactory);
		DataClientSpy client = new DataClientSpy();
		clientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAppTokenCredentials", () -> client);
	 </code>
	 * 
	 * @return A DataClient for fitnesseAdmin
	 */
	public static DataClient getFitnesseAdminDataClient() {
		return clients.computeIfAbsent(FITNESSE_ADMIN_JAVA_CLIENT,
				_ -> createFitnesseAdminDataClient());
	}

	private static DataClient createFitnesseAdminDataClient() {
		JavaClientAppTokenCredentials cred = new JavaClientAppTokenCredentials(
				SystemUrl.getRestUrl(), SystemUrl.getAppTokenVerifierRestUrl(),
				LoginToken.getFitnesseAdminLoginId(), LoginToken.getFitnesseAdminAppToken());
		return JavaClientProvider.createDataClientUsingJavaClientAppTokenCredentials(cred);
	}

	/**
	 * removeAllCreateClients removes all created clients from the internal holder, so that the next
	 * call to get a client creates it again.
	 */
	public static void removeAllCreateClients() {
		clients = new HashMap<>();
	}

}
