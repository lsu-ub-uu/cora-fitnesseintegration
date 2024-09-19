package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import java.util.List;

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
			definition = definition + writeGroup(dataGroup);

			possiblyTraverseAndWriteChildren(dataGroup);
		}

		possiblyWriteDataChild(dataChild);

		return definition;
	}

	private void possiblyWriteDataChild(DataChild dataChild) {
		if (isAtomic(dataChild)) {
			DataAtomic dataAtomic = (DataAtomic) dataChild;
			definition = definition + writeAtomic(dataAtomic);
		}

		if (isRecordLink(dataChild)) {
			DataRecordLink recordLink = (DataRecordLink) dataChild;
			definition = definition + writeRecordLink(recordLink);
		}

		if (isResourceLink(dataChild)) {
			DataResourceLink resourceLink = (DataResourceLink) dataChild;
			definition = definition + writeResourceLink(resourceLink);
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
		definition = definition + NEW_LINE;
	}

	private void addTab() {
		for (int i = 0; i < childDepth; i++) {
			definition = definition + TAB;
		}
	}

	private boolean isAtomic(DataChild dataChild) {
		return dataChild instanceof DataAtomic;
	}

	private String writeAtomic(DataAtomic dataAtomic) {
		return dataAtomic.getNameInData() + "(textVariable, 1-1, noConstraint)";
	}

	private String writeRecordLink(DataRecordLink dataRecordLink) {
		return dataRecordLink.getNameInData() + "(recordLink, 1-1, noConstraint)";
	}

	private String writeResourceLink(DataResourceLink dataResourcedLink) {
		return dataResourcedLink.getNameInData() + "(resourceLink, 1-1, noConstraint)";
	}
}
