/*
 * Copyright 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.script;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterFactoryCreatorSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.script.StoredData;

public class StoredDataTest {
	JsonToClientDataConverterFactorySpy converterToClientFactorySpy;
	ClientDataToJsonConverterFactoryCreatorSpy toJsonFactoryCreator;

	private StoredData storedData;
	private ClientDataRecordSpy clientClientDataRecord;

	@BeforeMethod
	public void beforeMethod() {
		toJsonFactoryCreator = new ClientDataToJsonConverterFactoryCreatorSpy();
		ClientDataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(toJsonFactoryCreator);

		ClientDataRecordGroupSpy clientDataRecordGroupSpy = new ClientDataRecordGroupSpy();
		clientClientDataRecord = new ClientDataRecordSpy();
		clientClientDataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> clientDataRecordGroupSpy);

		DataHolder.setRecord(clientClientDataRecord);
	}

	@Test
	public void testReturnedValueIsConvertedFromRecordStoredInDataHolder() throws Exception {
		storedData = new StoredData();

		String json = storedData.getStoredRecordDataGroupAsJsonWithoutLinks();

		ClientDataToJsonConverterFactorySpy toJsonConverterFacotory = (ClientDataToJsonConverterFactorySpy) toJsonFactoryCreator.MCR
				.getReturnValue("createFactory", 0);
		toJsonConverterFacotory.MCR.assertParameters("factorUsingConvertible", 0,
				DataHolder.getRecord().getDataRecordGroup());

		ClientDataToJsonConverterSpy toJsonConverter = (ClientDataToJsonConverterSpy) toJsonConverterFacotory.MCR
				.getReturnValue("factorUsingConvertible", 0);
		toJsonConverter.MCR.assertReturn("toJson", 0, json);
	}

	@Test
	public void testMultipleReturnedValueUsesTheSameClientDataToJsonConverterFactory()
			throws Exception {
		storedData = new StoredData();
		toJsonFactoryCreator.MCR.assertNumberOfCallsToMethod("createFactory", 0);

		storedData.getStoredRecordDataGroupAsJsonWithoutLinks();

		toJsonFactoryCreator.MCR.assertNumberOfCallsToMethod("createFactory", 1);

		storedData.getStoredRecordDataGroupAsJsonWithoutLinks();
		storedData.getStoredRecordDataGroupAsJsonWithoutLinks();
		storedData.getStoredRecordDataGroupAsJsonWithoutLinks();

		toJsonFactoryCreator.MCR.assertNumberOfCallsToMethod("createFactory", 1);
	}
}
