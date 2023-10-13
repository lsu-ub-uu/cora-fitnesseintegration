package se.uu.ub.cora.fitnesseintegration;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;

public class MetadataGroupFixture {

	private String nameInData;
	private String childDataGroupName;

	public void setChildNameInData(String nameInData) {
		this.nameInData = nameInData;
	}

	public int numberOfChildrenWithNameInData() {
		int numOfMatchingChildren = 0;
		ClientDataRecord dataRecord = DataHolder.getRecord();
		if (topLevelGroupExists(dataRecord)) {
			numOfMatchingChildren = findNumOfMatchingChildren(dataRecord);
		}
		return numOfMatchingChildren;
	}

	private int findNumOfMatchingChildren(ClientDataRecord dataRecord) {
		ClientDataRecordGroup topLevelDataGroup = dataRecord.getDataRecordGroup();
		if (shouldFindChildrenInTopLevelDataGroup()) {
			return getNumberOfMatchingChildren(topLevelDataGroup);
		}
		return possiblyGetNumOfMatchingChildrenFromChildDataGroup(topLevelDataGroup);
	}

	private boolean shouldFindChildrenInTopLevelDataGroup() {
		return childDataGroupName == null || "".equals(childDataGroupName);
	}

	private int possiblyGetNumOfMatchingChildrenFromChildDataGroup(
			ClientDataParent topLevelDataGroup) {
		if (childDataGroupExist(topLevelDataGroup)) {
			return getNumOfMatchingChildrenFromChildDataGroup(topLevelDataGroup);
		}
		return 0;
	}

	private boolean childDataGroupExist(ClientDataParent topLevelDataGroup) {
		return topLevelDataGroup.containsChildWithNameInData(childDataGroupName);
	}

	private int getNumOfMatchingChildrenFromChildDataGroup(ClientDataParent topLevelDataGroup) {
		ClientDataGroup childDataGroup = topLevelDataGroup
				.getFirstGroupWithNameInData(childDataGroupName);
		return getNumberOfMatchingChildren(childDataGroup);
	}

	private boolean topLevelGroupExists(ClientDataRecord dataRecord) {
		return dataRecord != null && dataRecord.getDataRecordGroup() != null;
	}

	private int getNumberOfMatchingChildren(ClientDataParent topLevelDataGroup) {
		List<ClientDataChild> matchingChildren = topLevelDataGroup
				.getAllChildrenWithNameInData(nameInData);
		return matchingChildren.size();
	}

	public void setChildDataGroup(String childDataGroup) {
		this.childDataGroupName = childDataGroup;
	}

}
