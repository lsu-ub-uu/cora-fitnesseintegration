package se.uu.ub.cora.fitnesseintegration;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;

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
		DataRecord record = DataHolder.getRecord();
		if (recordHasDataGroup(record)) {
			return possiblyGetNumberOfMatchingChildren(record);
		}
		return 0;
	}

	private int possiblyGetNumberOfMatchingChildren(DataRecord record) {
		ClientDataGroup topLevelDataGroup = record.getClientDataGroup();
		if (groupHasChildren(topLevelDataGroup)) {
			return getNumberOfMatchingChildren(topLevelDataGroup);
		}
		return 0;
	}

	private int getNumberOfMatchingChildren(ClientDataGroup topLevelDataGroup) {
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

	private boolean recordHasDataGroup(DataRecord record) {
		return record != null && record.getClientDataGroup() != null;
	}

	private boolean groupHasChildren(ClientDataGroup topLevelDataGroup) {
		return topLevelDataGroup.containsChildWithNameInData("childReferences");
	}

	private List<ClientDataGroup> extractChildReferences(ClientDataGroup topLevelDataGroup) {
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
