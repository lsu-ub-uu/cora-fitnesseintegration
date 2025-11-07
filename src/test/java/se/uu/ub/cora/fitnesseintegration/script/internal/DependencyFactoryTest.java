/*
 * Copyright 2023 Uppsala University Library
 * Copyright 2025 Olov McKie
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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.compare.ActionComparer;
import se.uu.ub.cora.fitnesseintegration.compare.PermissionComparer;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriterImp;
import se.uu.ub.cora.fitnesseintegration.internal.ReadAndStoreRecord;
import se.uu.ub.cora.fitnesseintegration.internal.ReadAndStoreRecordAsJson;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.internal.WaiterImp;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class DependencyFactoryTest {

	private static final String SOME_BASE_URL = "someBaseUrl/";
	private static final String SOME_APP_TOKEN_URL = "someAppTokenUrl/";
	private static final String SOME_TYPE = "someType";
	private static final String SOME_AUTH_TOKEN = "someAuthToken";
	private static final String SOME_ID = "someId";
	private DependencyFactory factory;

	@BeforeMethod
	private void beforeMethod() {
		SystemUrl.setUrl(SOME_BASE_URL);
		SystemUrl.setAppTokenVerifierUrl(SOME_APP_TOKEN_URL);

		factory = new DependencyFactoryImp();
	}

	@Test
	public void testFactorReadAndStoreRecord() {

		JavaClientFactorySpy javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);

		ReadAndStoreRecord readAndStore = (ReadAndStoreRecord) factory
				.factorReadAndStoreRecord(SOME_AUTH_TOKEN, SOME_TYPE, SOME_ID);

		assertNotNull(readAndStore);
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				SOME_BASE_URL + "rest/", SOME_APP_TOKEN_URL + "rest/", SOME_AUTH_TOKEN, false);
		javaClientFactory.MCR
				.methodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		javaClientFactory.MCR.assertParameterAsEqual(
				"factorDataClientUsingJavaClientAuthTokenCredentials", 0,
				"javaClientAuthTokenCredentials", authTokenCredentials);

		assertSame(readAndStore.onlyForTestGetDataClient(), javaClientFactory.MCR
				.getReturnValue("factorDataClientUsingJavaClientAuthTokenCredentials", 0));
		assertSame(readAndStore.onlyForTestGetType(), SOME_TYPE);
		assertSame(readAndStore.onlyForTestGetId(), SOME_ID);
	}

	@Test
	public void testFactorReadAndStoreRecordAsJson() {

		JavaClientFactorySpy javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);

		ReadAndStoreRecordAsJson readAndStoreAsJson = (ReadAndStoreRecordAsJson) factory
				.factorReadAndStoreRecordAsJson(SOME_AUTH_TOKEN, SOME_TYPE, SOME_ID);

		assertNotNull(readAndStoreAsJson);
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				SOME_BASE_URL + "rest/", SOME_APP_TOKEN_URL + "rest/", SOME_AUTH_TOKEN, false);
		javaClientFactory.MCR
				.methodWasCalled("factorRestClientUsingJavaClientAuthTokenCredentials");
		javaClientFactory.MCR.assertParameterAsEqual(
				"factorRestClientUsingJavaClientAuthTokenCredentials", 0,
				"javaClientAuthTokenCredentials", authTokenCredentials);

		assertSame(readAndStoreAsJson.onlyForTestGetRestClient(), javaClientFactory.MCR
				.getReturnValue("factorRestClientUsingJavaClientAuthTokenCredentials", 0));
		assertSame(readAndStoreAsJson.onlyForTestGetType(), SOME_TYPE);
		assertSame(readAndStoreAsJson.onlyForTestGetId(), SOME_ID);
	}

	@Test
	public void testFactorWaiter() {
		Waiter waiter = factory.factorWaiter();
		assertTrue(waiter instanceof WaiterImp);
	}

	@Test
	public void testFactorDefinitionWriter() {
		DefinitionWriter writer = factory.factorDefinitionWriter();
		assertTrue(writer instanceof DefinitionWriterImp);
	}

	@Test
	public void testFactorPermissionComparer() {
		ClientDataRecord dataRecord = new ClientDataRecordSpy();
		PermissionComparer comparer = (PermissionComparer) factory
				.factorPermissionComparer(dataRecord);
		assertSame(comparer.onlyForTestGetClientDataRecord(), dataRecord);

	}

	@Test
	public void testFactorActionComparer() {
		ClientDataRecord dataRecord = new ClientDataRecordSpy();
		ActionComparer comparer = (ActionComparer) factory.factorActionComparer(dataRecord);
		assertSame(comparer.onlyForTestGetClientDataRecord(), dataRecord);
	}
}
