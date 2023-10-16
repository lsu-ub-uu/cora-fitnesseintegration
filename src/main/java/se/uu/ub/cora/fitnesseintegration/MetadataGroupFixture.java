/*
 * Copyright 2023 Uppsala University Library
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
