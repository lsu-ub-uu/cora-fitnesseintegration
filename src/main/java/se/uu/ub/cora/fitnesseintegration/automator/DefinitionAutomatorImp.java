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

import java.util.Collection;

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.cache.ValidationTypeProvider;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class DefinitionAutomatorImp implements DefinitionAutomator {
	private static final String CHECK_RECORD_TYPE = """
			!*< Setup record type definition

			!define recordTypeId {%s}
			!define recordTypeDefinition {!-%s-!}

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

			""";

	private ClientDataRecordGroup dataRecordGroup;
	private DefinitionWriter writer;

	public DefinitionAutomatorImp() {
		writer = DependencyProvider.factorDefinitionWriter();
	}

	@Override
	public String createTestForRecordType(String recordType) {
		dataRecordGroup = RecordTypeProvider.getRecordGroup(recordType);
		ClientDataRecordLink metadataLink = dataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "metadataId");
		String linkedRecordId = metadataLink.getLinkedRecordId();
		String definition = writer.writeDefinitionUsingRecordId(linkedRecordId);
		return CHECK_RECORD_TYPE.formatted(recordType, definition, idSourceIs(), isPublic(),
				usePermissionUnit(), useVisibility(), useTrashBin(), storeInArchive());
	}

	private String idSourceIs() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("idSource");
	}

	private String isPublic() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("public");
	}

	private String usePermissionUnit() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("usePermissionUnit");
	}

	private String useVisibility() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("useVisibility");
	}

	private String useTrashBin() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("useTrashBin");
	}

	private String storeInArchive() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("storeInArchive");
	}

	@Override
	public String createTestForValidationType(String validationTypeId) {
		ValidationType validationType = ValidationTypeProvider.getValidationType(validationTypeId);
		return createTestForValidationType(validationType);
	}

	private String createTestForValidationType(ValidationType validationType) {
		String validatesRecordType = validationType.validatesRecordTypeId();
		String newDefinition = createDefinitionIs(validationType);
		String updateDefinition = updateDefinitionIs(validationType);
		return CHECK_VALIDATION_TYPE.formatted(validationType.id(), validatesRecordType,
				newDefinition, updateDefinition);
	}

	private String createDefinitionIs(ValidationType validationType) {
		return writer.writeDefinitionUsingRecordId(validationType.createDefinitionId());
	}

	private String updateDefinitionIs(ValidationType validationType) {
		return writer.writeDefinitionUsingRecordId(validationType.updateDefinitionId());
	}

	@Override
	public String createTestForRecordAndValidationType(String recordType) {
		StringBuilder out = new StringBuilder();
		out.append(createStartOfTest(recordType));
		out.append(createTestForRecordType(recordType));
		appendTestsForValidationTypes(recordType, out);
		return out.toString();
	}

	private Object createStartOfTest(String recordType) {
		String start = """
				---
				Test
				---
				!1 %s
				Some text about the recordType.

				There are a total of %s validation types for this record type.

				""";
		String validationPart = """
				!2 %s
				Some text about the validation type.

				""";
		StringBuilder out = new StringBuilder();
		Collection<ValidationType> validationTypes = ValidationTypeProvider
				.getValidationTypesThatValidatesRecordType(recordType);
		out.append(start.formatted(recordType, validationTypes.size()));
		for (ValidationType validationType : validationTypes) {
			out.append(validationPart.formatted(validationType.id()));
		}
		return out;
	}

	private void appendTestsForValidationTypes(String recordType, StringBuilder out) {
		for (ValidationType validationType : ValidationTypeProvider
				.getValidationTypesThatValidatesRecordType(recordType)) {
			out.append(createTestForValidationType(validationType));
		}
	}

}
