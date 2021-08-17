package se.uu.ub.cora.fitnesseintegration;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;

public class MetadataGroupFixture {

	private String nameInData;
	private String childDataGroupName;

	public void setChildNameInData(String nameInData) {
		this.nameInData = nameInData;
	}

	public int numberOfChildrenWithNameInData() {
		int numOfMatchingChildren = 0;
		DataRecord dataRecord = DataHolder.getRecord();
		if (topLevelGroupExists(dataRecord)) {
			numOfMatchingChildren = findNumOfMatchingChildren(dataRecord);
		}
		return numOfMatchingChildren;
	}

	private int findNumOfMatchingChildren(DataRecord dataRecord) {
		ClientDataGroup topLevelDataGroup = dataRecord.getClientDataGroup();
		if (shouldFindChildrenInTopLevelDataGroup()) {
			return getNumberOfMatchingChildren(topLevelDataGroup);
		}
		return possiblyGetNumOfMatchingChildrenFromChildDataGroup(topLevelDataGroup);
	}

	private boolean shouldFindChildrenInTopLevelDataGroup() {
		return childDataGroupName == null || "".equals(childDataGroupName);
	}

	private int possiblyGetNumOfMatchingChildrenFromChildDataGroup(
			ClientDataGroup topLevelDataGroup) {
		if (childDataGroupExist(topLevelDataGroup)) {
			return getNumOfMatchingChildrenFromChildDataGroup(topLevelDataGroup);
		}
		return 0;
	}

	private boolean childDataGroupExist(ClientDataGroup topLevelDataGroup) {
		return topLevelDataGroup.containsChildWithNameInData(childDataGroupName);
	}

	private int getNumOfMatchingChildrenFromChildDataGroup(ClientDataGroup topLevelDataGroup) {
		ClientDataGroup childDataGroup = topLevelDataGroup
				.getFirstGroupWithNameInData(childDataGroupName);
		return getNumberOfMatchingChildren(childDataGroup);
	}

	private boolean topLevelGroupExists(DataRecord dataRecord) {
		return dataRecord != null && dataRecord.getClientDataGroup() != null;
	}

	private int getNumberOfMatchingChildren(ClientDataGroup topLevelDataGroup) {
		List<ClientDataElement> matchingChildren = topLevelDataGroup
				.getAllChildrenWithNameInData(nameInData);
		return matchingChildren.size();
	}

	public void setChildDataGroup(String childDataGroup) {
		this.childDataGroupName = childDataGroup;
	}

}
