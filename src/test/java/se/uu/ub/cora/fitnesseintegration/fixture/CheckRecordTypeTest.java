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

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DefinitionWriterSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class CheckRecordTypeTest {
	private CheckRecordType fixture;
	private DependencyFactorySpy dependencyFactory;
	private ClientDataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");

		createClientDataRecordGroup();
		RecordTypeProvider.onlyForTestAddRecordGroupToInternalMap("someId", dataRecordGroup);

		fixture = new CheckRecordType();
		fixture.setId("someId");
	}

	@AfterMethod
	public void afterMethod() {
		RecordTypeProvider.resetInternalHolder();
	}

	private ClientDataRecordGroupSpy createClientDataRecordGroup() {
		dataRecordGroup = new ClientDataRecordGroupSpy();
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
	public void testDefinitionIs() {
		String definition = fixture.definitionIs();

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId",
				"someDefinitionGroup");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definition);
	}

	@Test
	public void testIdSourceIs() {
		String value = fixture.idSourceIs();

		assertEquals(value, dataRecordGroup.MCR
				.assertCalledParametersReturn("getFirstAtomicValueWithNameInData", "idSource"));
	}

	@Test
	public void testIsPublic() {
		String value = fixture.isPublic();

		assertEquals(value, dataRecordGroup.MCR
				.assertCalledParametersReturn("getFirstAtomicValueWithNameInData", "public"));
	}

	@Test
	public void testUsePermissionUnit() {
		String value = fixture.usePermissionUnit();

		assertEquals(value, dataRecordGroup.MCR.assertCalledParametersReturn(
				"getFirstAtomicValueWithNameInData", "usePermissionUnit"));
	}

	@Test
	public void testUseVisibility() {
		String value = fixture.useVisibility();

		assertEquals(value, dataRecordGroup.MCR.assertCalledParametersReturn(
				"getFirstAtomicValueWithNameInData", "useVisibility"));
	}

	@Test
	public void testUseTrashBin() {
		String value = fixture.useTrashBin();

		assertEquals(value, dataRecordGroup.MCR
				.assertCalledParametersReturn("getFirstAtomicValueWithNameInData", "useTrashBin"));
	}

	@Test
	public void testStoreInArchive() {
		String value = fixture.storeInArchive();

		assertEquals(value, dataRecordGroup.MCR.assertCalledParametersReturn(
				"getFirstAtomicValueWithNameInData", "storeInArchive"));
	}

}
