package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import java.util.List;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

public class DefinitionWriter {

	public String writeDefinitionFromUsingDataChild(DataChild dataChild) {
		String definition = "";
		if (isGroup(dataChild)) {
			DataGroup dataGroup = (DataGroup) dataChild;
			definition = definition + writeGroup(dataGroup);

			if (dataGroup.hasChildren()) {
				definition = definition + "\\n\\t";

				List<DataChild> children = dataGroup.getChildren();
				for (DataChild child : children) {
					writeDefinitionFromUsingDataChild(child);
				}
			}
		}
		if (isAtomic(dataChild)) {
			DataAtomic dataAtomic = (DataAtomic) dataChild;
			definition = definition + writeAtomic(dataAtomic);
		}

		return definition;
	}

	private boolean isAtomic(DataChild dataChild) {
		return dataChild instanceof DataAtomic;
	}

	private String writeAtomic(DataAtomic dataAtomic) {
		return dataAtomic.getNameInData() + "(atomic, X-X, someConstarint)";
	}

	private boolean isGroup(DataChild dataChild) {
		return dataChild instanceof DataGroup;
	}

	private String writeGroup(DataGroup dataGroup) {

		// dataGroup.getallc§
		return dataGroup.getNameInData() + "(group)";
	}

}
