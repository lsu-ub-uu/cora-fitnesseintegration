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
package se.uu.ub.cora.fitnesseintegration.automator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class DefinitionAutomatorTest {
	private static final String CHECK_RECORD_TYPE = """
			!*< Setup record type definition

			!define recordTypeId {%s}
			!define recordTypeDefinition {!%s-!}

			#idSource (userSupplied/timestamp/sequence) others true/false
			!define recordTypeIdSource {%s}
			!define recordTypeIsPublic {%s}
			!define recordTypeUsePermissionUnit {%s}
			!define recordTypeUseVisibility {%s}
			!define recordTypeUseTrashBin {%s}
			!define recordTypeStoreInArchive {%s}
			*!
			!include -seamless .HelperPages.checkRecordType
			""";

	private static final String CHECK_VALIDATION_TYPE = """
			!*< Setup validation type definition

			!define validationTypeId {%s}
			!define recordTypeId {%s}
			!define createValidationTypeDefinition {!-%s-!}

			!define updateValidationTypeDefinition {!-%s-!}

			*!
			!include -seamless .HelperPages.checkValidationType

			----
			""";
	private DependencyFactorySpy dependencyFactory;
	private ClientDataRecordGroupSpy dataRecordGroup;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someRecordTypeId");

		createClientDataRecordGroup();
		RecordTypeProvider.onlyForTestAddRecordGroupToInternalMap("someRecordTypeId",
				dataRecordGroup);

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
	public void testTesting() {
		// SystemUrl.setUrl("");
		// SystemUrl.setAppTokenVerifierUrl("");
		// LoginToken.setFitnesseAdminLoginId("");
		// LoginToken.setFitnesseAdminAppToken("");

		// RecordTypeProvider.onlyForTestAddRecordGroupToInternalMap("id",
		// new ClientDataRecordGroupSpy());
		// ValidationTypeProvider.onlyForTestAddRecordGroupToInternalMap("id",
		// new ClientDataRecordGroupSpy());

		DefinitionAutomator dat = new DefinitionAutomator();
		String recordType = "someRecordTypeId";

		String out = dat.createTestForRecordType(recordType);

		String CHECK_RECORD_TYPE = """
				!*< Setup record type definition

				!define recordTypeId {someRecordTypeId}
				!define recordTypeDefinition {!%s-!}

				#idSource (userSupplied/timestamp/sequence) others true/false
				!define recordTypeIdSource {userSupplied}
				!define recordTypeIsPublic {%s}
				!define recordTypeUsePermissionUnit {%s}
				!define recordTypeUseVisibility {%s}
				!define recordTypeUseTrashBin {%s}
				!define recordTypeStoreInArchive {%s}
				*!
				!include -seamless .HelperPages.checkRecordType
				""";

		// System.out.println(out);
		assertEquals(dataRecordGroup.getFirstAtomicValueWithNameInData("idSource"), "adsfads");
		assertEquals(out, CHECK_RECORD_TYPE);
	}

}
