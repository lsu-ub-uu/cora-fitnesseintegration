/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.automator;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.cache.ValidationTypeProvider;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DefinitionWriterSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class DefinitionAutomatorTest {
	private static final String THIRD_VALIDATION_TYPE_ID = "thirdValidationTypeId";
	private static final String SECOND_VALIDATION_TYPE_ID = "secondValidationTypeId";
	private static final String FIRST_VALIDATION_TYPE_ID = "firstValidationTypeId";
	private static final String RECORD_TYPE_ID = "someRecordTypeId";
	private DependencyFactorySpy dependencyFactory;
	private ClientDataRecordGroupSpy dataRecordGroup;
	private ValidationType validationType;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		DefinitionWriterSpy writer = new DefinitionWriterSpy();
		dependencyFactory.MRV.setDefaultReturnValuesSupplier("factorDefinitionWriter",
				() -> writer);
		writer.MRV.setSpecificReturnValuesSupplier("writeDefinitionUsingRecordId",
				() -> "{fake someDefinitionGroup}", "someDefinitionGroup");
		writer.MRV.setSpecificReturnValuesSupplier("writeDefinitionUsingRecordId",
				() -> "{fake someNewDefinitionGroup}", "someNewDefinitionGroup");
		writer.MRV.setSpecificReturnValuesSupplier("writeDefinitionUsingRecordId",
				() -> "{fake someUpdateDefinitionGroup}", "someUpdateDefinitionGroup");

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> RECORD_TYPE_ID);

		dataRecordGroup = createClientDataRecordGroup();
		RecordTypeProvider.onlyForTestAddRecordGroupToInternalMap(RECORD_TYPE_ID, dataRecordGroup);

		validationType = createValidationType(FIRST_VALIDATION_TYPE_ID, RECORD_TYPE_ID);
		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationType);

		ValidationType validationRecordGroup2 = createValidationType(SECOND_VALIDATION_TYPE_ID,
				"someOtherRecordType");
		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationRecordGroup2);

		ValidationType validationRecordGroup3 = createValidationType(THIRD_VALIDATION_TYPE_ID,
				RECORD_TYPE_ID);
		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationRecordGroup3);
	}

	@AfterMethod
	public void afterMethod() {
		RecordTypeProvider.resetInternalHolder();
		ValidationTypeProvider.resetInternalHolder();
	}

	private ClientDataRecordGroupSpy createClientDataRecordGroup() {
		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		setReturnValueForLinkWithNameAndValue(recordGroup, "metadataId", "someDefinitionGroup");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "idSource", "userSupplied");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "public", "false");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "usePermissionUnit", "true");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "useVisibility", "false");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "useTrashBin", "true");
		setReturnValueForAtomicWithNameAndValue(recordGroup, "storeInArchive", "false");
		return recordGroup;
	}

	private ValidationType createValidationType(String id, String recordType) {
		return new ValidationType(id, recordType, "someNewDefinitionGroup",
				"someUpdateDefinitionGroup");
	}

	private void setReturnValueForLinkWithNameAndValue(
			ClientDataRecordGroupSpy clientDataRecordGroup, String nameInData,
			String linkPointsTo) {
		ClientDataRecordLinkSpy metadataLink = new ClientDataRecordLinkSpy();
		metadataLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkPointsTo);
		clientDataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataLink, ClientDataRecordLink.class, nameInData);
	}

	private void setReturnValueForAtomicWithNameAndValue(ClientDataRecordGroupSpy dataRecordGroup,
			String nameInData, String value) {
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> value, nameInData);
	}

	@Test
	public void testCreateTestForRecordType() {
		DefinitionAutomator dat = new DefinitionAutomatorImp();
		String out = dat.createTestForRecordType(RECORD_TYPE_ID);

		String recordTypeDefinitionTemplate = """
				!*< Setup record type definition

				!define recordTypeId {someRecordTypeId}
				!define recordTypeDefinition {!-%s-!}

				#idSource (userSupplied/timestamp/sequence) others true/false
				!define recordTypeIdSource {userSupplied}
				!define recordTypeIsPublic {false}
				!define recordTypeUsePermissionUnit {true}
				!define recordTypeUseVisibility {false}
				!define recordTypeUseTrashBin {true}
				!define recordTypeStoreInArchive {false}
				*!
				!include -seamless .HelperPages.checkRecordType

				""";

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		String definition = (String) writer.MCR.assertCalledParametersReturn(
				"writeDefinitionUsingRecordId", "someDefinitionGroup");
		assertEquals(out, recordTypeDefinitionTemplate.formatted(definition));
	}

	@Test
	public void testCreateTestForValidationType() {
		DefinitionAutomator dat = new DefinitionAutomatorImp();
		String out = dat.createTestForValidationType(FIRST_VALIDATION_TYPE_ID);

		String validationTypeDefinitionTemplate = """
				!*< Setup validation type definition

				!define validationTypeId {firstValidationTypeId}
				!define recordTypeId {someRecordTypeId}
				!define createValidationTypeDefinition {!-%s-!}

				!define updateValidationTypeDefinition {!-%s-!}

				*!
				!include -seamless .HelperPages.checkValidationType

					""";

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		String newDefinition = (String) writer.MCR.assertCalledParametersReturn(
				"writeDefinitionUsingRecordId", "someNewDefinitionGroup");
		String updateDefinition = (String) writer.MCR.assertCalledParametersReturn(
				"writeDefinitionUsingRecordId", "someUpdateDefinitionGroup");
		assertEquals(out,
				validationTypeDefinitionTemplate.formatted(newDefinition, updateDefinition));
	}

	@Test
	public void testCreateTestForRecordAndValidationType() {
		String start = """
				---
				Test
				---
				!1 someRecordTypeId
				Some text about the recordType.

				There are a total of 2 validation types for this record type.

				!2 firstValidationTypeId
				Some text about the validation type.

				!2 thirdValidationTypeId
				Some text about the validation type.

				""";
		DefinitionAutomator dat = new DefinitionAutomatorImp();

		String recordTypeTest = dat.createTestForRecordType(RECORD_TYPE_ID);
		String firstValidationTypeTest = dat.createTestForValidationType(FIRST_VALIDATION_TYPE_ID);
		String thirdValidationTypeTest = dat.createTestForValidationType(THIRD_VALIDATION_TYPE_ID);

		String combined = dat.createTestForRecordAndValidationType(RECORD_TYPE_ID);

		assertEquals(combined,
				start + recordTypeTest + firstValidationTypeTest + thirdValidationTypeTest);
	}

}
