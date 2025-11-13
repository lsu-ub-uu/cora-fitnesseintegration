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

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class RecordTypeProviderTest {
	private DataClientSpy client;

	@BeforeMethod
	public void beforeMethod() {
		setUpUserProviderToReturnClientSpy();
		List<ClientDataRecordSpy> list = createListOfRecordTypes();
		client.MRV.setSpecificReturnValuesSupplier("readList", () -> list, "someRecordType");
	}

	private List<ClientDataRecordSpy> createListOfRecordTypes() {
		List<ClientDataRecordSpy> list = new ArrayList<>();
		String recordTypeId = "someRecordType";

		ClientDataRecordSpy record = new ClientDataRecordSpy();
		record.MRV.setDefaultReturnValuesSupplier("getId", () -> recordTypeId);
		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.list.add(record);
		return list;
	}

	private void setUpUserProviderToReturnClientSpy() {
		// JavaClientFactorySpy clientFactory = new JavaClientFactorySpy();
		// JavaClientProvider.onlyForTestSetJavaClientFactory(clientFactory);
		client = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, client);
		// clientFactory.MRV.setDefaultReturnValuesSupplier(
		// "factorDataClientUsingJavaClientAppTokenCredentials", () -> client);
	}

	@Test
	public void testName() {
		String id = "someRecordType";

		ClientDataRecordGroup recordType = RecordTypeProvider.getRecordTypeRecordGroup(id);

		client.MCR.assertMethodWasCalled("readList");
		client.MCR.assertParameters("readList", 0, id);
	}
}
