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
import se.uu.ub.cora.fitnesseintegration.cache.ValidationTypeProvider;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class CheckValidationType {
	private ClientDataRecordGroup dataRecordGroup;

	public CheckValidationType() {
		// needed by fitnesse
	}

	public void setId(String id) {
		dataRecordGroup = ValidationTypeProvider.getRecordGroup(id);
	}

	public String validatesRecordType() {
		return getLinkValueForNameInData("validatesRecordType");
	}

	public String createDefinitionIs() {
		String linkedRecordId = getLinkValueForNameInData("newMetadataId");
		DefinitionWriter writer = DependencyProvider.factorDefinitionWriter();
		return writer.writeDefinitionUsingRecordId(linkedRecordId);
	}

	private String getLinkValueForNameInData(String nameInData) {
		ClientDataRecordLink metadataLink = dataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, nameInData);
		return metadataLink.getLinkedRecordId();
	}

	public String updateDefinitionIs() {
		String linkedRecordId = getLinkValueForNameInData("metadataId");
		DefinitionWriter writer = DependencyProvider.factorDefinitionWriter();
		return writer.writeDefinitionUsingRecordId(linkedRecordId);
	}

}
