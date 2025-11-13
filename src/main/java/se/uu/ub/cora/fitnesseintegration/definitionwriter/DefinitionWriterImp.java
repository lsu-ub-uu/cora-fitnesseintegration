/*
 * Copyright 2024 Uppsala University Library
 * Copyright 2025 Olov McKie
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientDataChildFilter;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.fitnesseintegration.cache.MetadataHolder;
import se.uu.ub.cora.fitnesseintegration.script.MetadataProvider;

public class DefinitionWriterImp implements DefinitionWriter {

	private static final String NAME_IN_DATA = "nameInData";
	private static final String REF = "ref";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String REF_COLLECTION = "refCollection";
	private static final String FINAL_VALUE = "finalValue";
	private static final String SPACE = " ";
	private static final String COMMA_SPACE = ", ";
	private static final String TYPE = "type";
	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";

	private StringBuilder definition;
	private MetadataHolder metadataHolder;

	@Override
	public String writeDefinitionUsingRecordId(String authToken, String metadataId) {
		metadataHolder = MetadataProvider.getHolder(authToken);
		ClientDataRecord dataRecord = metadataHolder.getDataRecordById(metadataId);
		ClientDataRecordGroup dataRecordGroup = dataRecord.getDataRecordGroup();

		definition = new StringBuilder();
		writeDefinition(dataRecordGroup, Optional.empty(), 0);

		return definition.toString();
	}

	private void writeDefinition(ClientDataRecordGroup clientDataRecordGroup,
			Optional<ChildReferenceDetails> childReferenceDetails, int currentIndent) {
		addIndentation(currentIndent);
		writeElement(clientDataRecordGroup, childReferenceDetails);
		possiblyTraverseGroup(clientDataRecordGroup, currentIndent);
	}

	private void addIndentation(int level) {
		for (int i = 0; i < level; i++) {
			definition.append(TAB);
		}
	}

	private void writeElement(ClientDataRecordGroup clientDataRecordGroup,
			Optional<ChildReferenceDetails> details) {
		writeNameInData(clientDataRecordGroup);
		possiblyWriteFinalValue(clientDataRecordGroup);
		possiblyWriteAttributeReferences(clientDataRecordGroup);
		writeDetails(clientDataRecordGroup, details);
	}

	private void writeNameInData(ClientDataRecordGroup clientDataRecordGroup) {
		String metadataNameInData = clientDataRecordGroup
				.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		definition.append(metadataNameInData + SPACE);
	}

	private void possiblyWriteFinalValue(ClientDataRecordGroup clientDataRecordGroup) {
		if (hasFinalValue(clientDataRecordGroup)) {
			String finalValue = clientDataRecordGroup
					.getFirstAtomicValueWithNameInData(FINAL_VALUE);
			if (!finalValue.isBlank())
				definition.append("{" + finalValue + "}" + SPACE);
		}
	}

	private boolean hasFinalValue(ClientDataRecordGroup clientDataRecordGroup) {
		return clientDataRecordGroup.containsChildWithNameInData(FINAL_VALUE);
	}

	private void possiblyWriteAttributeReferences(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.containsChildWithNameInData(ATTRIBUTE_REFERENCES)) {
			List<Attribute> attributes = readAttributeReferences(clientDataRecordGroup);
			Iterator<Attribute> attributeIterator = attributes.iterator();
			while (attributeIterator.hasNext()) {
				Attribute attribute = attributeIterator.next();
				definition.append(MessageFormat.format("{0}:'{'{1}'}'", attribute.nameInData(),
						String.join(COMMA_SPACE, attribute.values())));
				addSpaceOrCommaSpace(attributeIterator);
			}
		}
	}

	private List<Attribute> readAttributeReferences(ClientDataRecordGroup clientDataRecordGroup) {
		List<Attribute> attributes = new ArrayList<>();
		List<ClientDataRecordLink> refs = getAttributeReferencesRefLinks(clientDataRecordGroup);
		for (ClientDataRecordLink ref : refs) {
			collectAttributes(attributes, ref);
		}
		return attributes;
	}

	private void addSpaceOrCommaSpace(Iterator<Attribute> attributeIterator) {
		if (attributeIterator.hasNext()) {
			definition.append(COMMA_SPACE);
		} else {
			definition.append(SPACE);
		}
	}

	private List<ClientDataRecordLink> getAttributeReferencesRefLinks(
			ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup attributeReferencesGroup = clientDataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, ATTRIBUTE_REFERENCES);
		return attributeReferencesGroup.getChildrenOfTypeAndName(ClientDataRecordLink.class, REF);
	}

	private void collectAttributes(List<Attribute> attributes, ClientDataRecordLink ref) {
		ClientDataRecordGroup collectionVar = readLink(ref.getLinkedRecordId());
		String attributeNameInData = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		if (hasFinalValue(collectionVar)) {
			String finalValue = collectionVar.getFirstAtomicValueWithNameInData(FINAL_VALUE);
			Attribute attribute = new Attribute(attributeNameInData, List.of(finalValue));
			attributes.add(attribute);
		} else {
			List<String> collectionItemValues = getCollectionItemValues(collectionVar);
			Attribute attribute = new Attribute(attributeNameInData, collectionItemValues);
			attributes.add(attribute);
		}
	}

	private ClientDataRecordGroup readLink(String linkedRecordId) {
		return metadataHolder.getDataRecordById(linkedRecordId).getDataRecordGroup();
	}

	private List<String> getCollectionItemValues(ClientDataRecordGroup collectionVariable) {
		List<ClientDataRecordLink> collectionItemLinks = getCollectionItemLinks(collectionVariable);
		List<String> attributeValues = new ArrayList<>();
		for (ClientDataRecordLink dataRecordLink : collectionItemLinks) {
			ClientDataRecordGroup collectionItem = readLink(dataRecordLink.getLinkedRecordId());
			String attributeValue = collectionItem.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
			attributeValues.add(attributeValue);
		}

		return attributeValues;
	}

	private List<ClientDataRecordLink> getCollectionItemLinks(
			ClientDataRecordGroup metadataCollectionVariable) {
		ClientDataRecordGroup refCollection = readRefCollection(metadataCollectionVariable);
		ClientDataGroup itemReferences = refCollection
				.getFirstGroupWithNameInData(COLLECTION_ITEM_REFERENCES);
		return itemReferences.getChildrenOfTypeAndName(ClientDataRecordLink.class, REF);
	}

	private ClientDataRecordGroup readRefCollection(
			ClientDataRecordGroup metadataCollectionVariable) {
		ClientDataRecordLink linkToCollection = metadataCollectionVariable
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, REF_COLLECTION);
		return readLink(linkToCollection.getLinkedRecordId());
	}

	private void writeDetails(ClientDataRecordGroup clientDataRecordGroup,
			Optional<ChildReferenceDetails> details) {
		definition.append("(");
		possiblyWriteMetadataType(clientDataRecordGroup);
		possiblyWriteChildReferenceDetails(details);
		definition.append(")");
	}

	private void possiblyWriteMetadataType(ClientDataRecordGroup clientDataRecordGroup) {
		Optional<String> attributeValue = clientDataRecordGroup.getAttributeValue(TYPE);
		definition.append(attributeValue.isPresent() ? attributeValue.get() : "");
	}

	private void writeChildReferenceDetails(ChildReferenceDetails details) {
		definition.append(MessageFormat.format(COMMA_SPACE + "{0}-{1}" + COMMA_SPACE + "{2}",
				details.repeatMin(), details.repeatMax(), details.constraints()));
		possiblyWriteCollectTerms(details);
	}

	private void possiblyWriteCollectTerms(ChildReferenceDetails details) {
		if (details.storageTerm()) {
			definition.append(COMMA_SPACE + "S");
		}
		if (details.permissionTerm()) {
			definition.append(COMMA_SPACE + "P");
		}
		if (details.indexTerm()) {
			definition.append(COMMA_SPACE + "I");
		}
	}

	private void possiblyTraverseGroup(ClientDataRecordGroup clientDataRecordGroup,
			int currentIndent) {
		if (isGroup(clientDataRecordGroup)) {
			possiblyTraverseChildren(clientDataRecordGroup, currentIndent);
		}
	}

	private boolean isGroup(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.hasAttributes()) {
			Optional<String> type = clientDataRecordGroup.getAttributeValue(TYPE);
			return type.isPresent() && "group".equals(type.get());
		}
		return false;
	}

	private void possiblyTraverseChildren(ClientDataRecordGroup clientDataRecordGroup,
			int currentIndent) {
		if (clientDataRecordGroup.hasChildren()) {
			List<ClientDataGroup> childReferences = getChildReferences(clientDataRecordGroup);
			for (ClientDataGroup child : childReferences) {
				addNewLine();
				ClientDataRecordGroup dataGroup = readRefLink(child);
				Optional<ChildReferenceDetails> details = collectChildReferenceDetailsAsRecord(
						child);
				writeDefinition(dataGroup, details, currentIndent + 1);
			}
		}
	}

	private List<ClientDataGroup> getChildReferences(ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup childReferencesGroup = clientDataRecordGroup
				.getFirstGroupWithNameInData("childReferences");
		return childReferencesGroup.getChildrenOfTypeAndName(ClientDataGroup.class,
				CHILD_REFERENCE);
	}

	private void addNewLine() {
		definition.append(NEW_LINE);
	}

	private void possiblyWriteChildReferenceDetails(Optional<ChildReferenceDetails> details) {
		if (details.isPresent()) {
			writeChildReferenceDetails(details.get());
		}
	}

	private ClientDataRecordGroup readRefLink(ClientDataGroup child) {
		ClientDataRecordLink refLink = child.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
				REF);
		return readLink(refLink.getLinkedRecordId());
	}

	private Optional<ChildReferenceDetails> collectChildReferenceDetailsAsRecord(
			ClientDataGroup dataGroup) {
		String repeatMin = dataGroup.getFirstAtomicValueWithNameInData("repeatMin");
		String repeatMax = dataGroup.getFirstAtomicValueWithNameInData("repeatMax");
		String constraints = getConstraint(dataGroup);
		boolean storageTerm = hasCollectTermType("storage", dataGroup);
		boolean permissionTerm = hasCollectTermType("permission", dataGroup);
		boolean indexTerm = hasCollectTermType("index", dataGroup);

		return Optional.of(new ChildReferenceDetails(repeatMin, repeatMax, constraints, storageTerm,
				permissionTerm, indexTerm));
	}

	private String getConstraint(ClientDataGroup dataGroup) {
		return dataGroup.containsChildWithNameInData("recordPartConstraint")
				? dataGroup.getFirstAtomicValueWithNameInData("recordPartConstraint")
				: "noConstraint";
	}

	private boolean hasCollectTermType(String type, ClientDataGroup dataGroup) {
		dataGroup.getChildrenOfTypeAndName(ClientDataRecordLink.class, "childRefCollectTerm");
		ClientDataChildFilter filter = ClientDataProvider
				.createDataChildFilterUsingChildNameInData("childRefCollectTerm");
		filter.addAttributeUsingNameInDataAndPossibleValues("type", Set.of(type));
		return !dataGroup.getAllChildrenMatchingFilter(filter).isEmpty();
	}

	private record ChildReferenceDetails(String repeatMin, String repeatMax, String constraints,
			boolean storageTerm, boolean permissionTerm, boolean indexTerm) {
	}

	private record Attribute(String nameInData, List<String> values) {
	}

}
