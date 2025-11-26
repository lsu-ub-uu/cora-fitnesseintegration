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
import static org.testng.Assert.assertSame;
import static org.testng.Assert.fail;

import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataAtomicSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class RememberRecordStateTest {
	private RememberRecordState fixture;
	private ClientDataFactorySpy dataFactory;
	private DataClientSpy client;

	@BeforeMethod
	public void beforeMethod() {
		dataFactory = new ClientDataFactorySpy();
		ClientDataProvider.onlyForTestSetDataFactory(dataFactory);

		client = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, client);

		ClientDataRecordSpy read = createRecordSpy();
		ClientDataRecordSpy read2 = createRecordSpy();
		ClientDataRecordSpy read3 = createRecordSpy();

		client.MRV.setReturnValues("read", List.of(read, read2, read3), "someType", "someId");

		fixture = new RememberRecordState();
	}

	private ClientDataRecordSpy createRecordSpy() {
		ClientDataRecordSpy dataRecord = new ClientDataRecordSpy();

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> recordGroup);

		ClientDataGroupSpy recordInfo = new ClientDataGroupSpy();
		recordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> recordInfo, "recordInfo");

		return dataRecord;
	}

	@AfterMethod
	public void afterMethod() {
		ClientDataProvider.onlyForTestSetDataFactory(null);
		RememberRecordState.forgetAllRecords();
	}

	@Test
	public void testRememberRecordState() {
		fixture.setType("someType");
		fixture.setId("someId");

		String result = fixture.remember();

		ClientDataRecordSpy read = (ClientDataRecordSpy) client.MCR
				.assertCalledParametersReturn("read", "someType", "someId");

		ClientDataRecordGroupSpy readRecordGroup = (ClientDataRecordGroupSpy) read
				.getDataRecordGroup();

		assertEquals(result, "OK");

		fixture = new RememberRecordState();
		fixture.setType("someType");
		fixture.setId("someId");

		String resultUpdate = fixture.memoryToStorage();
		ClientDataAtomicSpy ignoreOverwriteProtectionAtomic = (ClientDataAtomicSpy) dataFactory.MCR
				.assertCalledParametersReturn("factorAtomicUsingNameInDataAndValue",
						"ignoreOverwriteProtection", "true");
		ClientDataGroupSpy recordInfo = (ClientDataGroupSpy) readRecordGroup
				.getFirstGroupWithNameInData("recordInfo");
		recordInfo.MCR.assertCalledParameters("addChild", ignoreOverwriteProtectionAtomic);

		client.MCR.assertCalledParameters("update", "someType", "someId", readRecordGroup);

		assertEquals(resultUpdate, "OK");
	}

	@Test
	public void testRememberRecordState_withVersion() {
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setVersion("someVersion");
		fixture.remember();

		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setVersion("");
		fixture.remember();

		ClientDataRecordSpy recordWith = (ClientDataRecordSpy) client.MCR.getReturnValue("read", 0);
		var readWithVersion = recordWith.getDataRecordGroup();

		ClientDataRecordSpy recordNo = (ClientDataRecordSpy) client.MCR.getReturnValue("read", 1);
		var readNoVersion = recordNo.getDataRecordGroup();

		fixture = new RememberRecordState();
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.memoryToStorage();

		fixture = new RememberRecordState();
		fixture.setType("someType");
		fixture.setId("someId");
		fixture.setVersion("someVersion");
		fixture.memoryToStorage();

		var updatedNoVersion = client.MCR.getParameterForMethodAndCallNumberAndParameter("update",
				0, "dataRecordGroup");
		var updatedWithVersion = client.MCR.getParameterForMethodAndCallNumberAndParameter("update",
				1, "dataRecordGroup");

		assertSame(updatedWithVersion, readWithVersion);
		assertSame(updatedNoVersion, readNoVersion);
	}

	@Test
	public void testForgetAllRecords() {
		fixture.setType("someType");
		fixture.setId("someId");

		fixture.remember();

		String result = RememberRecordState.forgetAllRecords();
		assertEquals(result, "OK");
		try {
			fixture.memoryToStorage();
			fail();
		} catch (Exception _) {

		}

	}
}
