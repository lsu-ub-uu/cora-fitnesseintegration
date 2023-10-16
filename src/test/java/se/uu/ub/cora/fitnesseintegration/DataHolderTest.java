/*
 * Copyright 2018, 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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
package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;

public class DataHolderTest {
	@Test
	public void testInit() {
		DataHolder recordHolder = new DataHolder();
		assertNotNull(recordHolder);
	}

	@Test
	public void testSetAndGetRecord() throws Exception {
		ClientDataRecord clientClientDataRecord = createRecordWithDataGroupUsingNameInData(
				"someName");

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
		ClientDataRecordGroup clientDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData(nameInData);
		return ClientDataProvider.createRecordWithDataRecordGroup(clientDataGroup);
	}
}
