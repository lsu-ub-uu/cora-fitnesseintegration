package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import java.util.List;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

public class DefinitionWriter {

	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";
	private String definition = "";

	public String writeDefinitionFromUsingDataChild(DataChild dataChild) {
		if (isGroup(dataChild)) {
			DataGroup dataGroup = (DataGroup) dataChild;
			definition = definition + writeGroup(dataGroup);

			continueWritingDefinitionIfChildrenExists(dataGroup);
		}
		if (isAtomic(dataChild)) {
			DataAtomic dataAtomic = (DataAtomic) dataChild;
			definition = definition + writeAtomic(dataAtomic);
		}

		return definition;
	}

	private void continueWritingDefinitionIfChildrenExists(DataGroup dataGroup) {
		if (dataGroup.hasChildren()) {
			List<DataChild> children = dataGroup.getChildren();
			for (DataChild child : children) {
				addNewLine();
				addTab();
				writeDefinitionFromUsingDataChild(child);
			}
		}
	}

	private void addTab() {
		definition = definition + TAB;
	}

	private void addNewLine() {
		definition = definition + NEW_LINE;
	}

	private boolean isAtomic(DataChild dataChild) {
		return dataChild instanceof DataAtomic;
	}

	private String writeGroup(DataGroup dataGroup) {
		return dataGroup.getNameInData() + "(group)";
	}

	private String writeAtomic(DataAtomic dataAtomic) {
		return dataAtomic.getNameInData() + "(atomic, X-X, someConstarint)";
	}

	private boolean isGroup(DataChild dataChild) {
		return dataChild instanceof DataGroup;
	}

}
