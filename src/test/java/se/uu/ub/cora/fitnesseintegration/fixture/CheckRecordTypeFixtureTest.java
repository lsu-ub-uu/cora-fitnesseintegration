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

public class CheckRecordTypeFixtureTest {
	private CheckRecordTypeFixture fixture;
	private DependencyFactorySpy dependencyFactory;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");

		ClientDataRecordGroupSpy dataRecordGroup = createClientDataRecordGroup();
		RecordTypeProvider.onlyForTestAddRecordGroupToInternalMap("someId", dataRecordGroup);

		fixture = new CheckRecordTypeFixture();
	}

	@AfterMethod
	public void afterMethod() {
		RecordTypeProvider.resetInternalHolder();
	}

	private ClientDataRecordGroupSpy createClientDataRecordGroup() {
		ClientDataRecordGroupSpy dataRecordGroup = new ClientDataRecordGroupSpy();
		ClientDataRecordLinkSpy metadataLink = new ClientDataRecordLinkSpy();
		metadataLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId",
				() -> "someDefinitionGroup");
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataLink, ClientDataRecordLink.class, "metadataId");
		return dataRecordGroup;
	}

	@Test
	public void testAssertDefinitionIs() {
		fixture.setId("someId");

		String definition = fixture.assertDefinitionIs();

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId",
				"someDefinitionGroup");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definition);
	}
	// idSource (userSupplied/timestamp/sequence)
	// public (false/true)
	// usePermissionUnit (false/true)
	// useVisibility (false/true)
	// useTrashBin (false/true)
	// storeInArchive (false/true)

}
