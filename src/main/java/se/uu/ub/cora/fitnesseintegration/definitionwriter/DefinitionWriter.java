/*
 * Copyright 2024 Uppsala University Library
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
import java.util.List;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientDataChildFilter;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class DefinitionWriter {

	private static final String NAME_IN_DATA = "nameInData";
	private static final String REF = "ref";
	private static final String CHILD_REFERENCE = "childReference";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String COLLECTION_ITEM_REFERENCES = "collectionItemReferences";
	private static final String REF_COLLECTION = "refCollection";
	private static final String FINAL_VALUE = "finalValue";
	private static final String METADATA = "metadata";
	private static final String SPACE = " ";
	private static final String COMMA_SPACE = ", ";
	private static final String TYPE = "type";
	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";

	private String definition = "";
	private String baseUrl;
	private String appTokenUrl;
	private DataClient dataClient;
	private StringBuilder stringBuilder = new StringBuilder();

	public DefinitionWriter(String baseUrl, String appTokenUrl) {
		this.baseUrl = baseUrl;
		this.appTokenUrl = appTokenUrl;
	}

	public String writeDefinitionFromUsingDataChild(String authToken, String recordId) {
		dataClient = createDataClientUsingAuthToken(authToken);
		ClientDataRecord dataRecord = dataClient.read(METADATA, recordId);
		ClientDataRecordGroup dataRecordGroup = dataRecord.getDataRecordGroup();

		writeDefinition(dataRecordGroup, Optional.empty(), 0);

		return definition;
	}

	private DataClient createDataClientUsingAuthToken(String authToken) {
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenUrl, authToken);
		return JavaClientProvider
				.createDataClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
	}

	private void writeDefinition(ClientDataRecordGroup clientDataRecordGroup,
			Optional<ChildReferenceDetails> childReferenceDetails, int currentIndent) {
		addIndentation(currentIndent);
		writeElement(clientDataRecordGroup, childReferenceDetails);
		possiblyTraverseGroup(clientDataRecordGroup, currentIndent);
	}

	private void addIndentation(int level) {
		clearBuilder();
		for (int i = 0; i < level; i++) {
			stringBuilder.append(TAB);
		}
		definition += stringBuilder.toString();
	}

	private void clearBuilder() {
		stringBuilder.setLength(0);
	}

	private void writeElement(ClientDataRecordGroup clientDataRecordGroup,
			Optional<ChildReferenceDetails> details) {
		writeNameInData(clientDataRecordGroup);
		possiblyWriteFinalValue(clientDataRecordGroup);
		possiblyWriteAttributeReferences(clientDataRecordGroup);

		definition += "(";
		possiblyWriteMetadataType(clientDataRecordGroup);
		possiblyWriteChildReferenceDetails(details);
		definition += ")";
	}

	private void writeNameInData(ClientDataRecordGroup clientDataRecordGroup) {
		String metadataNameInData = clientDataRecordGroup
				.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		definition += metadataNameInData + SPACE;
	}

	private void possiblyWriteFinalValue(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.containsChildWithNameInData(FINAL_VALUE)) {
			String finalValue = clientDataRecordGroup
					.getFirstAtomicValueWithNameInData(FINAL_VALUE);
			if (!finalValue.isBlank())
				definition += "{" + finalValue + "}" + SPACE;
		}
	}

	private void possiblyWriteAttributeReferences(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.containsChildWithNameInData(ATTRIBUTE_REFERENCES)) {
			clearBuilder();
			List<Attribute> attributes = readAttributeReferences(clientDataRecordGroup);
			for (Attribute attribute : attributes) {
				stringBuilder.append(MessageFormat.format("{0}:'{'{1}'}' ", attribute.nameInData(),
						String.join(COMMA_SPACE, attribute.values())));
			}
			definition += stringBuilder.toString();
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

	private List<ClientDataRecordLink> getAttributeReferencesRefLinks(
			ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup attributeReferences = clientDataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataGroup.class, ATTRIBUTE_REFERENCES);
		return attributeReferences.getChildrenOfTypeAndName(ClientDataRecordLink.class, REF);
	}

	private void collectAttributes(List<Attribute> attributes, ClientDataRecordLink ref) {
		ClientDataRecordGroup collectionVar = readLink(ref.getLinkedRecordId());
		String attributeNameInData = collectionVar.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		if (collectionVar.containsChildWithNameInData(FINAL_VALUE)) {
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
		return dataClient.read(METADATA, linkedRecordId).getDataRecordGroup();
	}

	private List<String> getCollectionItemValues(ClientDataRecordGroup metadataCollectionVariable) {
		List<ClientDataRecordLink> collectionItemLinks = getCollectionItemLinks(
				metadataCollectionVariable);
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

	private void possiblyWriteMetadataType(ClientDataRecordGroup clientDataRecordGroup) {
		Optional<String> attributeValue = clientDataRecordGroup.getAttributeValue(TYPE);
		definition += attributeValue.isPresent() ? attributeValue.get() : "";
	}

	private void writeChildReferenceDetails(ChildReferenceDetails details) {
		definition += MessageFormat.format(COMMA_SPACE + "{0}-{1}" + COMMA_SPACE + "{2}",
				details.repeatMin(), details.repeatMax(), details.constraints());
		possiblyWriteCollectTerms(details);
	}

	private void possiblyWriteCollectTerms(ChildReferenceDetails details) {
		if (details.storageTerm()) {
			definition += COMMA_SPACE + "S";
		}
		if (details.permissionTerm()) {
			definition += COMMA_SPACE + "P";
		}
		if (details.indexTerm()) {
			definition += COMMA_SPACE + "I";
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
		definition += NEW_LINE;
	}

	private void possiblyWriteChildReferenceDetails(Optional<ChildReferenceDetails> details) {
		if (details.isPresent()) {
			writeChildReferenceDetails(details.get());
		}
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

	private ClientDataRecordGroup readRefLink(ClientDataGroup child) {
		ClientDataRecordLink refLink = child.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
				REF);
		return readLink(refLink.getLinkedRecordId());
	}

	private boolean hasCollectTermType(String type, ClientDataGroup dataGroup) {
		ClientDataChildFilter filter = ClientDataProvider
				.createDataChildFilterUsingChildNameInData("childRefCollectTerm");
		filter.addAttributeUsingNameInDataAndPossibleValues("type", Set.of(type));
		return !dataGroup.getAllChildrenMatchingFilter(filter).isEmpty();
	}

	private String getConstraint(ClientDataGroup dataGroup) {
		return dataGroup.containsChildWithNameInData("recordPartConstraint")
				? dataGroup.getFirstAtomicValueWithNameInData("recordPartConstraint")
				: "noConstraint";
	}

	public String onlyForTestGetBaseUrl() {
		return baseUrl;
	}

	public String onlyForTestGetAppTokenUrl() {
		return appTokenUrl;
	}

	public DataClient onlyForTestGetDataClient() {
		return dataClient;
	}

	private record ChildReferenceDetails(String repeatMin, String repeatMax, String constraints,
			boolean storageTerm, boolean permissionTerm, boolean indexTerm) {
	};

	private record Attribute(String nameInData, List<String> values) {
	}
}
