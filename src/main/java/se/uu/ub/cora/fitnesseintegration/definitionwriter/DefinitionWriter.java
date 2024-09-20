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
	private int childDepth = 0;

	public String writeDefinitionFromUsingDataChild(DataChild dataChild) {
		if (isGroup(dataChild)) {
			// todo: collect all child groups (per level) and process them indiviually to handle all
			// nested group levels
			childDepth += 1;
			DataGroup dataGroup = (DataGroup) dataChild;
			definition += writeGroup(dataGroup);

			possiblyTraverseAndWriteChildren(dataGroup);
		}

		possiblyWriteDataChild(dataChild);

		return definition;
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

	private boolean isGroup(DataChild dataChild) {
		return dataChild instanceof DataGroup;
	}

	private boolean isRecordLink(DataChild dataChild) {
		return dataChild instanceof DataRecordLink;
	}

	private boolean isResourceLink(DataChild dataChild) {
		return dataChild instanceof DataResourceLink;
	}

	private String writeGroup(DataGroup dataGroup) {
		return dataGroup.getNameInData() + "(group)";
	}

	private void possiblyTraverseAndWriteChildren(DataGroup dataGroup) {
		if (dataGroup.hasChildren()) {
			List<DataChild> children = dataGroup.getChildren();
			for (DataChild child : children) {
				addNewLine();
				addTab();
				writeDefinitionFromUsingDataChild(child);
			}
			childDepth = 0;
		}
	}

	private void addNewLine() {
		definition += NEW_LINE;
	}

	private void addTab() {
		for (int i = 0; i < childDepth; i++) {
			definition += TAB;
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

	private String writeRecordLink(DataRecordLink dataRecordLink) {
		return dataRecordLink.getNameInData() + "(recordLink, 1-1, noConstraint)";
	}

	private String writeResourceLink(DataResourceLink dataResourcedLink) {
		return dataResourcedLink.getNameInData() + "(resourceLink, 1-1, noConstraint)";
	}
}
