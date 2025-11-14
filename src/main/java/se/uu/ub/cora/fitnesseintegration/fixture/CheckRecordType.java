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

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class CheckRecordType {
	private ClientDataRecordGroup dataRecordGroup;

	public CheckRecordType() {
		// needed by fitnesse
	}

	public void setId(String id) {
		dataRecordGroup = RecordTypeProvider.getRecordGroup(id);
	}

	public String definitionIs() {
		ClientDataRecordLink metadataLink = dataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "metadataId");
		String linkedRecordId = metadataLink.getLinkedRecordId();
		DefinitionWriter writer = DependencyProvider.factorDefinitionWriter();

		return writer.writeDefinitionUsingRecordId(linkedRecordId);
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
