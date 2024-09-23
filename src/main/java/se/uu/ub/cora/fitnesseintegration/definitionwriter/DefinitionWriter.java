/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import java.text.MessageFormat;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class DefinitionWriter {

	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";
	private String definition = "";

	public String writeDefinitionFromUsingDataChild(DataChild dataChild) {
		writeDefinition(dataChild, 0);
		return definition;
	}

	public void writeDefinition(DataChild dataChild, int currentLevel) {
		addTab(currentLevel);
		possiblyTraverseAndWriteGroup(dataChild, currentLevel);
		possiblyWriteDataChild(dataChild);
	}

	private void addTab(int level) {
		for (int i = 0; i < level; i++) {
			definition += TAB;
		}
	}

	private void possiblyTraverseAndWriteGroup(DataChild dataChild, int currentLevel) {
		if (isGroup(dataChild)) {
			DataGroup dataGroup = (DataGroup) dataChild;
			definition += writeGroup(dataGroup);
			possiblyTraverseChildren(dataGroup, currentLevel);
		}
	}

	private boolean isGroup(DataChild dataChild) {
		return dataChild instanceof DataGroup;
	}

	private String writeGroup(DataGroup dataGroup) {
		return dataGroup.getNameInData() + "(group)";
	}

	private void possiblyTraverseChildren(DataGroup dataGroup, int currentLevel) {
		if (dataGroup.hasChildren()) {
			List<DataChild> children = dataGroup.getChildren();
			for (DataChild child : children) {
				addNewLine();
				writeDefinition(child, currentLevel + 1);
			}
		}
	}

	private void addNewLine() {
		definition += NEW_LINE;
	}

	private void possiblyWriteDataChild(DataChild dataChild) {
		if (isAtomic(dataChild)) {
			DataAtomic dataAtomic = (DataAtomic) dataChild;
			definition += writeAtomic(dataAtomic);
		}

		if (isRecordLink(dataChild)) {
			DataRecordLink recordLink = (DataRecordLink) dataChild;
			definition += writeRecordLink(recordLink);
		}

		if (isResourceLink(dataChild)) {
			DataResourceLink resourceLink = (DataResourceLink) dataChild;
			definition += writeResourceLink(resourceLink);
		}
	}

	private boolean isAtomic(DataChild dataChild) {
		return dataChild instanceof DataAtomic;
	}

	private String writeAtomic(DataAtomic dataAtomic) {
		String nameInData = dataAtomic.getNameInData();
		Optional<String> attributeType = dataAtomic.getAttributeValue("type");
		return MessageFormat.format("{0}({1}, 1-1, noConstraint)", nameInData, attributeType.get());
	}

	private boolean isRecordLink(DataChild dataChild) {
		return dataChild instanceof DataRecordLink;
	}

	private String writeRecordLink(DataRecordLink dataRecordLink) {
		return dataRecordLink.getNameInData() + "(recordLink, 1-1, noConstraint)";
	}

	private boolean isResourceLink(DataChild dataChild) {
		return dataChild instanceof DataResourceLink;
	}

	private String writeResourceLink(DataResourceLink dataResourcedLink) {
		return dataResourcedLink.getNameInData() + "(resourceLink, 1-1, noConstraint)";
	}

}
