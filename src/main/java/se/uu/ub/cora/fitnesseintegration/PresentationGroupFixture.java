/*
 * Copyright 2018 Uppsala University Library
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

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class PresentationGroupFixture extends MetadataLinkFixture {

	@Override
	public void setLinkedRecordType(String linkedRecordType) {
		this.linkedRecordType = linkedRecordType;
	}

	@Override
	public void setLinkedRecordId(String linkedRecordId) {
		this.linkedRecordId = linkedRecordId;
	}

	public int numberOfRefs() {
		ClientDataRecord dataRecord = DataHolder.getRecord();
		if (recordHasDataGroup(dataRecord)) {
			return possiblyGetNumberOfMatchingChildren(dataRecord);
		}
		return 0;
	}

	private int possiblyGetNumberOfMatchingChildren(ClientDataRecord dataRecord) {
		ClientDataParent topLevelDataGroup = dataRecord.getDataRecordGroup();
		if (groupHasChildren(topLevelDataGroup)) {
			return getNumberOfMatchingChildren(topLevelDataGroup);
		}
		return 0;
	}

	private int getNumberOfMatchingChildren(ClientDataParent topLevelDataGroup) {
		List<ClientDataGroup> childReferenceGroups = extractChildReferences(topLevelDataGroup);
		int children = 0;
		for (ClientDataGroup childReference : childReferenceGroups) {

			if (childReferenceMatches(childReference)) {
				children++;
			}
		}
		return children;
	}

	private boolean childReferenceMatches(ClientDataGroup childReference) {
		String childLinkedRecordType = extractValueFromReferenceUsingNameInData(childReference,
				"linkedRecordType");
		String childLinkedRecordId = extractValueFromReferenceUsingNameInData(childReference,
				"linkedRecordId");

		return childReferenceMatchesTypeAndId(childLinkedRecordType, childLinkedRecordId);
	}

	private boolean recordHasDataGroup(ClientDataRecord dataRecord) {
		return dataRecord != null && dataRecord.getDataRecordGroup() != null;
	}

	private boolean groupHasChildren(ClientDataParent topLevelDataGroup) {
		return topLevelDataGroup.containsChildWithNameInData("childReferences");
	}

	private List<ClientDataGroup> extractChildReferences(ClientDataParent topLevelDataGroup) {
		ClientDataGroup childReferences = topLevelDataGroup
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference");
	}

	@Override
	protected String extractValueFromReferenceUsingNameInData(ClientDataGroup childReference,
			String childNameInData) {
		ClientDataGroup refGroup = childReference.getFirstGroupWithNameInData("refGroup");
		ClientDataGroup ref = refGroup.getFirstGroupWithNameInData("ref");
		return ref.getFirstAtomicValueWithNameInData(childNameInData);
	}
}
