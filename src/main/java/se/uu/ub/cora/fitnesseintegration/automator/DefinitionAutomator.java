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

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;

public class DefinitionAutomator {
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

			----
			""";

	private ClientDataRecordGroup dataRecordGroup;

	public String createTestForRecordType(String recordType) {
		// TODO Auto-generated method stub
		dataRecordGroup = RecordTypeProvider.getRecordGroup(recordType);
		return CHECK_RECORD_TYPE.formatted(recordType, recordType, idSourceIs(), recordType,
				recordType, recordType, recordType, recordType);
	}

	public String idSourceIs() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("idSource");
	}

	public String isPublic() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("public");
	}

	public String usePermissionUnit() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("usePermissionUnit");
	}

	public String useVisibility() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("useVisibility");
	}

	public String useTrashBin() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("useTrashBin");
	}

	public String storeInArchive() {
		return dataRecordGroup.getFirstAtomicValueWithNameInData("storeInArchive");
	}

}
