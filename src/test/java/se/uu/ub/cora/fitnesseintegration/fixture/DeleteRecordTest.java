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

import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class DeleteRecordTest {

	private DeleteRecord fixture;
	private DataClientSpy dataClient;

	@BeforeMethod
	private void beforeMethod() {
		fixture = new DeleteRecord();
		dataClient = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient("fitnesseAdminJavaClient", dataClient);

		fixture.setRecordType("someRecordType");
		fixture.setRecordId("someId");
	}

	@Test
	public void testDeleteRecord() {
		String message = fixture.deleteRecord();

		dataClient.MCR.assertParameters("delete", 0, "someRecordType", "someId");
		assertEquals(message, "OK");
	}

	@Test
	public void testDeleteRecord_Exception() {
		dataClient.MRV.setAlwaysThrowException("delete",
				new RuntimeException("someExceptionMessage"));

		String message = fixture.deleteRecord();

		dataClient.MCR.assertParameters("delete", 0, "someRecordType", "someId");
		assertEquals(message, "someExceptionMessage");
	}

	@Test
	public void testDeleteAll() {
		var clientDataList = creatDataListUsingRecordIds("one", "two");
		dataClient.MRV.setDefaultReturnValuesSupplier("readList", () -> clientDataList);

		String message = fixture.deleteAllRecordsForRecordType();

		dataClient.MCR.assertParameters("readList", 0, "someRecordType");
		dataClient.MCR.assertParameters("delete", 0, "someRecordType", "one");
		dataClient.MCR.assertParameters("delete", 1, "someRecordType", "two");
		assertEquals(message, "OK ⋮ [one, two]");
	}

	@Test
	public void testDeleteAll_ExceptionOnSecondRead() {
		var clientDataList = creatDataListUsingRecordIds("one", "two");
		dataClient.MRV.setDefaultReturnValuesSupplier("readList", () -> clientDataList);
		dataClient.MRV.setDefaultReturnValuesSupplier("readList", () -> clientDataList);
		dataClient.MRV.setThrowException("delete", new RuntimeException("someException"),
				"someRecordType", "two");

		String message = fixture.deleteAllRecordsForRecordType();

		dataClient.MCR.assertParameters("readList", 0, "someRecordType");
		dataClient.MCR.assertParameters("delete", 0, "someRecordType", "one");
		dataClient.MCR.assertParameters("delete", 1, "someRecordType", "two");
		assertEquals(message, "FAILED ⋮ [one] ⋮ someException");
	}

	private ClientDataListSpy creatDataListUsingRecordIds(String... recordIds) {
		var clientDataListSpy = new ClientDataListSpy();
		clientDataListSpy.MRV.setDefaultReturnValuesSupplier("getDataList",
				() -> createDataList(recordIds));
		return clientDataListSpy;
	}

	private List<ClientData> createDataList(String... recordIds) {
		List<ClientData> dataList = new ArrayList<>();

		for (String recordId : recordIds) {
			ClientDataRecordSpy clientDataRecord = new ClientDataRecordSpy();
			clientDataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> recordId);
			dataList.add(clientDataRecord);
		}
		return dataList;
	}
}
