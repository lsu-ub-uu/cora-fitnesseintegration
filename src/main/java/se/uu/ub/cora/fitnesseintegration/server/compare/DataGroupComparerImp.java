/*
 * Copyright 2022 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.server.compare;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.DataRecordLink;
import se.uu.ub.cora.data.DataResourceLink;

public class DataGroupComparerImp implements DataGroupComparer {

	@Override
	public List<String> compareDataGroupToDataGroup(DataGroup compareWith,
			DataGroup compareAgainst) {
		if (groupsDoNotHaveTheSameNameAndAttributes(compareWith, compareAgainst)) {
			return List.of(createCompareMessageForChild(compareWith));
		}
		return compareChildren(compareWith, compareAgainst);
	}

	private boolean groupsDoNotHaveTheSameNameAndAttributes(DataGroup compareWith,
			DataGroup compareAgainst) {
		return !namesAndAttributesAreTheSame(compareWith, compareAgainst);
	}

	private boolean namesAndAttributesAreTheSame(DataChild compareWith, DataChild compareAgainst) {
		if (namesAreDifferent(compareWith, compareAgainst)) {
			return false;
		}
		if (classesAreDifferent(compareWith, compareAgainst)) {
			return false;
		}
		return attributesAreDifferent(compareWith, compareAgainst);
	}

	private boolean namesAreDifferent(DataChild compareWith, DataChild compareAgainst) {
		return !compareWith.getNameInData().equals(compareAgainst.getNameInData());
	}

	private boolean classesAreDifferent(DataChild compareWith, DataChild compareAgainst) {
		return compareWith.getClass() != compareAgainst.getClass();
	}

	private boolean attributesAreDifferent(DataChild compareWith, DataChild compareAgainst) {
		if (differentNumberOfAttributes(compareWith, compareAgainst)) {
			return false;
		}
		return differentAttributeNamesOrValues(compareWith, compareAgainst);
	}

	private boolean differentNumberOfAttributes(DataChild compareWith, DataChild compareAgainst) {
		return compareWith.getAttributes().size() != compareAgainst.getAttributes().size();
	}

	private boolean differentAttributeNamesOrValues(DataChild compareWith,
			DataChild compareAgainst) {
		for (DataAttribute attributeWith : compareWith.getAttributes()) {
			if (!childHasAttribute(compareAgainst, attributeWith)) {
				return false;
			}
		}
		return true;
	}

	private boolean childHasAttribute(DataChild dataChild, DataAttribute attribute) {
		boolean attributeFound = false;
		for (DataAttribute childAttribute : dataChild.getAttributes()) {
			if (attribute.getNameInData().equals(childAttribute.getNameInData())
					&& attribute.getValue().equals(childAttribute.getValue())) {
				attributeFound = true;
			}
		}
		return attributeFound;
	}

	private List<String> compareChildren(DataGroup compareWith, DataGroup compareAgainst) {
		List<String> compareMessages = new ArrayList<>();
		for (DataChild childCompareWith : compareWith.getChildren()) {
			compareMessages.addAll(compareChild(childCompareWith, compareAgainst));
		}
		return compareMessages;
	}

	private List<String> compareChild(DataChild childCompareWith, DataGroup compareAgainst) {
		Optional<DataChild> matchedChild = findChildMatchingOnNameInDataAndAttributes(
				childCompareWith, compareAgainst);
		if (matchedChild.isPresent()) {
			return compareTypeSpecifics(childCompareWith, matchedChild.get());
		}
		return List.of(createCompareMessageForChild(childCompareWith));
	}

	private List<String> compareTypeSpecifics(DataChild compareWith, DataChild compareAgainst) {
		if (compareWith instanceof DataAtomic) {
			return compareAtomicSpecifics(compareWith, compareAgainst);
		}
		if (compareWith instanceof DataRecordLink) {
			return compareRecordLinkSpecifics(compareWith, compareAgainst);
		}
		if (compareWith instanceof DataResourceLink) {
			return compareResourceLinkSpecifics(compareWith, compareAgainst);
		}
		if (compareWith instanceof DataGroup) {
			return compareDataGroupToDataGroup((DataGroup) compareWith, (DataGroup) compareAgainst);
		}
		return Collections.emptyList();
	}

	private List<String> compareAtomicSpecifics(DataChild compareWith, DataChild compareAgainst) {
		DataAtomic atomicWith = (DataAtomic) compareWith;
		DataAtomic atomicAgainst = (DataAtomic) compareAgainst;
		if (atomicValuesAreDifferent(atomicWith, atomicAgainst)) {
			return List.of(createCompareMessageForChild(compareWith));
		}
		return Collections.emptyList();
	}

	private boolean atomicValuesAreDifferent(DataAtomic atomicWith, DataAtomic atomicAgainst) {
		return !atomicWith.getValue().equals(atomicAgainst.getValue());
	}

	private List<String> compareRecordLinkSpecifics(DataChild compareWith,
			DataChild compareAgainst) {
		DataRecordLink recordLinkChild = (DataRecordLink) compareWith;
		DataRecordLink recordLinkAgainst = (DataRecordLink) compareAgainst;
		if (recordLinksAreDifferent(recordLinkChild, recordLinkAgainst)) {
			return List.of(createCompareMessageForChild(compareWith));
		}
		return Collections.emptyList();
	}

	private boolean recordLinksAreDifferent(DataRecordLink recordLinkChild,
			DataRecordLink recordLinkAgainst) {
		return !recordLinkChild.getLinkedRecordType()
				.equals(recordLinkAgainst.getLinkedRecordType())
				|| !recordLinkChild.getLinkedRecordId()
						.equals(recordLinkAgainst.getLinkedRecordId());
	}

	private Optional<DataChild> findChildMatchingOnNameInDataAndAttributes(DataChild compareWith,
			DataGroup compareAgainst) {
		for (DataChild against : compareAgainst.getChildren()) {
			if (namesAndAttributesAreTheSame(compareWith, against)) {
				return Optional.of(against);
			}
		}
		return Optional.empty();
	}

	private List<String> compareResourceLinkSpecifics(DataChild compareWith,
			DataChild compareAgainst) {
		DataResourceLink resourceLinkChild = (DataResourceLink) compareWith;
		DataResourceLink resourceLinkAgainst = (DataResourceLink) compareAgainst;
		if (resourceLinksAreDifferent(resourceLinkChild, resourceLinkAgainst)) {
			return List.of(createCompareMessageForChild(compareWith));
		}
		return Collections.emptyList();
	}

	private boolean resourceLinksAreDifferent(DataResourceLink resourceLinkChild,
			DataResourceLink resourceLinkAgainst) {
		// return !resourceLinkChild.getStreamId().equals(resourceLinkAgainst.getStreamId())
		// || !resourceLinkChild.getFileName().equals(resourceLinkAgainst.getFileName())
		// || !resourceLinkChild.getFileSize().equals(resourceLinkAgainst.getFileSize())
		// || !resourceLinkChild.getMimeType().equals(resourceLinkAgainst.getMimeType());
		return !resourceLinkChild.getMimeType().equals(resourceLinkAgainst.getMimeType());
	}

	private String createCompareMessageForChild(DataChild dataChild) {
		String nameInData = dataChild.getNameInData();
		String attribute = createAttributeMessagePart(dataChild);
		if (dataChild instanceof DataAtomic) {
			return createErrorMessageForAtomic(dataChild, nameInData, attribute);
		}
		if (dataChild instanceof DataRecordLink) {
			return createErrorMessageForRecordLink(dataChild, nameInData, attribute);
		}
		if (dataChild instanceof DataResourceLink) {
			return createErrorMessageForResourceLink(dataChild, nameInData, attribute);
		}

		return createErrorMessageForGroup(dataChild, attribute);
	}

	private String createAttributeMessagePart(DataChild compareWith) {
		if (!compareWith.hasAttributes()) {
			return "";
		}
		String pattern = "name: {0} and value: {1}";
		StringJoiner sj = new StringJoiner(", ", " and attribute/s [", "]");
		for (DataAttribute attribute : compareWith.getAttributes()) {
			sj.add(MessageFormat.format(pattern, attribute.getNameInData(), attribute.getValue()));
		}
		return sj.toString();
	}

	private String createErrorMessageForAtomic(DataChild dataChild, String nameInData,
			String attribute) {
		DataAtomic atomicChild = (DataAtomic) dataChild;
		String value = atomicChild.getValue();
		return MessageFormat.format("Atomic with name: {0}{1} and value: {2} not found.",
				nameInData, attribute, value);
	}

	private String createErrorMessageForRecordLink(DataChild dataChild, String nameInData,
			String attribute) {
		DataRecordLink recordLinkChild = (DataRecordLink) dataChild;
		String type = recordLinkChild.getLinkedRecordType();
		String id = recordLinkChild.getLinkedRecordId();
		return MessageFormat.format(
				"RecordLink with name: {0}{1}, linkType: {2} and " + "linkId: {3} not found.",
				nameInData, attribute, type, id);
	}

	private String createErrorMessageForResourceLink(DataChild dataChild, String nameInData,
			String attribute) {
		DataResourceLink resourceLinkChild = (DataResourceLink) dataChild;
		// String streamId = resourceLinkChild.getStreamId();
		// String fileName = resourceLinkChild.getFileName();
		// String fileSize = resourceLinkChild.getFileSize();
		String mimeType = resourceLinkChild.getMimeType();
		// return MessageFormat.format(
		// "ResourceLink with name: {0}{1}, streamId: {2}, fileName: {3}"
		// + ", fileSize: {4} and mimeType: {5} not found.",
		// nameInData, attribute, streamId, fileName, fileSize, mimeType);
		return MessageFormat.format("ResourceLink with name: {0}{1}, and mimeType: {2} not found.",
				nameInData, attribute, mimeType);
	}

	private String createErrorMessageForGroup(DataChild dataChild, String attribute) {
		return MessageFormat.format("Group with name: {0}{1} not found.", dataChild.getNameInData(),
				attribute);
	}

}
