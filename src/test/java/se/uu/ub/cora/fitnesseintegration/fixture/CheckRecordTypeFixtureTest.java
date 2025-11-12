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
package se.uu.ub.cora.fitnesseintegration.fixture;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DefinitionWriterSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class CheckRecordTypeFixtureTest {

	private static final String NO_VALID_TOKEN = "noValidToken";
	private CheckRecordTypeFixture fixture;
	private JavaClientFactorySpy clientFactory;
	private DependencyFactorySpy dependencyFactory;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		clientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(clientFactory);
	}

	@Test
	public void testInit() {
		fixture = new CheckRecordTypeFixture();

		clientFactory.MCR
				.assertMethodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		JavaClientAuthTokenCredentials credentials = new JavaClientAuthTokenCredentials(
				SystemUrl.getRestUrl(), "no renew url", NO_VALID_TOKEN, false);
		clientFactory.MCR.assertParameterAsEqual(
				"factorDataClientUsingJavaClientAuthTokenCredentials", 0,
				"javaClientAuthTokenCredentials", credentials);

		DataClientSpy client = (DataClientSpy) clientFactory.MCR
				.getReturnValue("factorDataClientUsingJavaClientAuthTokenCredentials", 0);

	}

	@Test
	public void testAssertDefinitionIs() {
		DataClientSpy dataClient = setUpDataClient();
		ClientDataRecordGroupSpy dataRecordGroup = new ClientDataRecordGroupSpy();

		ClientDataRecordSpy dataRecord = new ClientDataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		dataClient.MRV.setDefaultReturnValuesSupplier("read", () -> dataRecord);

		ClientDataRecordLinkSpy metadataLink = new ClientDataRecordLinkSpy();
		metadataLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someDefinitionGroup");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataLink, ClientDataRecordLink.class, "metadataId");

		fixture = new CheckRecordTypeFixture();
		fixture.setId("someId");

		String definition = fixture.assertDefinitionIs();

		dataClient.MCR.assertCalledParameters("read", "recordType", "someId");

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId", NO_VALID_TOKEN,
				"someDefinitionGroup");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definition);

	}

	private DataClientSpy setUpDataClient() {
		DataClientSpy dataClient = new DataClientSpy();
		clientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAuthTokenCredentials", () -> dataClient);
		return dataClient;
	}
}
