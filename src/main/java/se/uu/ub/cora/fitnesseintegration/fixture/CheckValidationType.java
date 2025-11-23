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
package se.uu.ub.cora.fitnesseintegration.fixture;

import se.uu.ub.cora.fitnesseintegration.cache.ValidationTypeProvider;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class CheckValidationType {
	private ValidationType validationType;
	private DefinitionWriter writer;

	public CheckValidationType() {
		// needed by fitnesse
	}

	public void setId(String id) {
		validationType = ValidationTypeProvider.getValidationType(id);
		writer = DependencyProvider.factorDefinitionWriter();
	}

	public String validatesRecordType() {
		return validationType.validatesRecordTypeId();
	}

	public String createDefinitionIs() {
		String linkedRecordId = validationType.createDefinitionId();
		return writer.writeDefinitionUsingRecordId(linkedRecordId);
	}

	public String updateDefinitionIs() {
		String linkedRecordId = validationType.updateDefinitionId();
		return writer.writeDefinitionUsingRecordId(linkedRecordId);
	}
}
