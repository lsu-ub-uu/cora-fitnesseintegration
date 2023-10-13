/*
 * Copyright 2022 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.converter.DataToJsonConverterFactory;
import se.uu.ub.cora.fitnesseintegration.compare.ClientDataRecordSpy;

public class StoredDataTest {

	private StoredDataOnlyForTest storedData;
	private DataToJsonConverterFactorySpy dataToJsonConverterFactorySpy;
	private ClientDataRecordSpy clientClientDataRecord;

	@BeforeMethod
	public void beforeMethod() {
		storedData = new StoredDataOnlyForTest();
		dataToJsonConverterFactorySpy = new DataToJsonConverterFactorySpy();
		clientClientDataRecord = new ClientDataRecordSpy();
		DataHolder.setRecord(clientClientDataRecord);
	}

	@Test
	public void testDefaultConverter() throws Exception {
		StoredData storedData = new StoredData();
		String json = storedData.getStoredRecordDataGroupAsJsonWithoutLinks();
		assertEquals(json, "{\"name\":\"clientDataGroupSpy\"}");
	}

	@Test
	public void testCallToFactory() throws Exception {
		storedData.onlyForTestSetDataToJsonConverterFactory(dataToJsonConverterFactorySpy);

		storedData.getStoredRecordDataGroupAsJsonWithoutLinks();

		dataToJsonConverterFactorySpy.MCR.assertParameters(
				"createForClientDataChildIncludingClientActionLinks", 0,
				clientClientDataRecord.getDataRecordGroup(), false);
	}

	@Test
	public void testCallToConvert() throws Exception {
		storedData.onlyForTestSetDataToJsonConverterFactory(dataToJsonConverterFactorySpy);

		String json = storedData.getStoredRecordDataGroupAsJsonWithoutLinks();

		DataToJsonConverterSpy converterSpy = (DataToJsonConverterSpy) dataToJsonConverterFactorySpy.MCR
				.getReturnValue("createForClientDataChildIncludingClientActionLinks", 0);
		converterSpy.MCR.assertReturn("toJson", 0, json);
	}

	class StoredDataOnlyForTest extends StoredData {
		void onlyForTestSetDataToJsonConverterFactory(
				DataToJsonConverterFactory dataToJsonConverterFactory) {
			this.dataToJsonConverterFactory = dataToJsonConverterFactory;
		}
	}
}
