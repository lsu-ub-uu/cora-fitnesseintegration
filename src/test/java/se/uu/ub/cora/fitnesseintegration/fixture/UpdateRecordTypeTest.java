/*
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
package se.uu.ub.cora.fitnesseintegration.fixture;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class UpdateRecordTypeTest {
	private UpdateRecordType fixture;
	private ClientDataRecordGroupSpy dataRecordGroup;
	private ClientDataFactorySpy dataFactory;
	private DataClientSpy client;

	@BeforeMethod
	public void beforeMethod() {
		dataFactory = new ClientDataFactorySpy();
		ClientDataProvider.onlyForTestSetDataFactory(dataFactory);

		client = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, client);

		ClientDataRecordSpy updated = new ClientDataRecordSpy();
		client.MRV.setDefaultReturnValuesSupplier("update", () -> updated);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");

		createClientDataRecordGroup();
		RecordTypeProvider.setRecordGroupInInternalMap("someId", dataRecordGroup);

		fixture = new UpdateRecordType();
		fixture.setId("someId");
	}

	@AfterMethod
	public void afterMethod() {
		RecordTypeProvider.resetInternalHolder();
		ClientDataProvider.onlyForTestSetDataFactory(null);
	}

	private ClientDataRecordGroupSpy createClientDataRecordGroup() {
		dataRecordGroup = new ClientDataRecordGroupSpy();
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getType", () -> "someType");
		dataRecordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");
		setReturnValueForLinkWithNameAndValue("metadataId", "someDefinitionGroup");
		setReturnValueForAtomicWithNameAndValue("idSource", "userSupplied");
		setReturnValueForAtomicWithNameAndValue("public", "false");
		setReturnValueForAtomicWithNameAndValue("usePermissionUnit", "false");
		setReturnValueForAtomicWithNameAndValue("useVisibility", "false");
		setReturnValueForAtomicWithNameAndValue("useTrashBin", "false");
		setReturnValueForAtomicWithNameAndValue("storeInArchive", "false");
		return dataRecordGroup;
	}

	private void setReturnValueForLinkWithNameAndValue(String nameInData, String linkPointsTo) {
		ClientDataRecordLinkSpy metadataLink = new ClientDataRecordLinkSpy();
		metadataLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkPointsTo);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataLink, ClientDataRecordLink.class, nameInData);
	}

	private void setReturnValueForAtomicWithNameAndValue(String nameInData, String value) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
	}

	@Test
	public void testSetIdSource() {
		fixture.setIdSource("value");

		assertAtomicRemovedCreatedAndAdded("idSource", "value");
	}

	private void assertAtomicRemovedCreatedAndAdded(String nameInData, String value) {
		dataRecordGroup.MCR.assertCalledParameters("removeAllChildrenWithNameInData", nameInData);
		var newIdSource = dataFactory.MCR.assertCalledParametersReturn(
				"factorAtomicUsingNameInDataAndValue", nameInData, value);
		dataRecordGroup.MCR.assertCalledParameters("addChild", newIdSource);
	}

	@Test
	public void testSetPublic() {
		fixture.setPublic("value");

		assertAtomicRemovedCreatedAndAdded("public", "value");
	}

	@Test
	public void testSetUsePermissionUnit() {
		fixture.setUsePermissionUnit("value");

		assertAtomicRemovedCreatedAndAdded("usePermissionUnit", "value");
	}

	@Test
	public void testSetUseVisibility() {
		fixture.setUseVisibility("value");

		assertAtomicRemovedCreatedAndAdded("useVisibility", "value");
	}

	@Test
	public void testSetUseTrashBin() {
		fixture.setUseTrashBin("value");

		assertAtomicRemovedCreatedAndAdded("useTrashBin", "value");
	}

	@Test
	public void testSetStoreInArchive() {
		fixture.setStoreInArchive("value");

		assertAtomicRemovedCreatedAndAdded("storeInArchive", "value");
	}

	@Test
	public void testUpdateInStorageAndProvider() {
		String result = fixture.update();

		ClientDataRecordSpy updated = (ClientDataRecordSpy) client.MCR
				.assertCalledParametersReturn("update", "someType", "someId", dataRecordGroup);

		var updatedGroup = updated.MCR.getReturnValue("getDataRecordGroup", 0);
		assertSame(RecordTypeProvider.getRecordGroup("someId"), updatedGroup);

		assertEquals(result, "OK");
	}

}
