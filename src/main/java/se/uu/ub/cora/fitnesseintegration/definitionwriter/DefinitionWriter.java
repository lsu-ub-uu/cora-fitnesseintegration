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

	private static final String SPACE = " ";
	private static final String ATTRIBUTE_REFERENCES = "attributeReferences";
	private static final String REF = "ref";
	private static final String FINAL_VALUE = "finalValue";
	private static final String COMMA_SPACE = ", ";
	private static final String NAME_IN_DATA = "nameInData";
	private static final String TYPE = "type";
	private static final String METADATA = "metadata";
	private static final String NEW_LINE = "\n";
	private static final String TAB = "\t";

	private String definition = "";
	private String baseUrl;
	private String appTokenUrl;
	private DataClient dataClient;

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
		addTab(currentIndent);
		writeElement(clientDataRecordGroup, childReferenceDetails);
		possiblyTraverseGroupAndChildren(clientDataRecordGroup, currentIndent);
	}

	private void addTab(int level) {
		for (int i = 0; i < level; i++) {
			definition += TAB;
		}
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

	private void possiblyWriteAttributeReferences(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.containsChildWithNameInData(ATTRIBUTE_REFERENCES)) {
			ClientDataGroup attributeReferences = clientDataRecordGroup
					.getFirstChildOfTypeAndName(ClientDataGroup.class, ATTRIBUTE_REFERENCES);

			List<String> attributes = new ArrayList<>();
			readAttributeReferences(attributeReferences, attributes);
		}
	}

	private void readAttributeReferences(ClientDataGroup attributeReferences,
			List<String> attributes) {
		List<ClientDataRecordLink> refs = attributeReferences
				.getChildrenOfTypeAndName(ClientDataRecordLink.class, REF);

		for (ClientDataRecordLink ref : refs) {
			ClientDataRecordGroup metadataCollectionVariable = getFromMetadataRecordGroupFromServer(
					ref.getLinkedRecordId());
			if (metadataCollectionVariable.containsChildWithNameInData(FINAL_VALUE)) {
				String finalValue = metadataCollectionVariable
						.getFirstAtomicValueWithNameInData(FINAL_VALUE);
				attributes.add(finalValue);
			} else {
				List<String> possibleValues = getPossibleValuesFromCollectionVariable(
						metadataCollectionVariable);
				attributes.addAll(possibleValues);
			}
		}
	}

	private ClientDataRecordGroup getFromMetadataRecordGroupFromServer(String linkedRecordId) {
		return dataClient.read(METADATA, linkedRecordId).getDataRecordGroup();
	}

	private List<String> getPossibleValuesFromCollectionVariable(
			ClientDataRecordGroup metadataCollectionVariable) {
		ClientDataRecordLink linkToCollection = metadataCollectionVariable
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "refCollection");
		ClientDataRecordGroup collection = getFromMetadataRecordGroupFromServer(
				linkToCollection.getLinkedRecordId());
		ClientDataGroup itemRefrences = collection
				.getFirstGroupWithNameInData("collectionItemReferences");
		List<ClientDataRecordLink> linkToCollectionItems = itemRefrences
				.getChildrenOfTypeAndName(ClientDataRecordLink.class, REF);
		List<String> attributeValues = new ArrayList<>();
		for (ClientDataRecordLink clientDataRecordLink : linkToCollectionItems) {
			ClientDataRecordGroup collectionItem = getFromMetadataRecordGroupFromServer(
					clientDataRecordLink.getLinkedRecordId());
			String attributeValue = collectionItem.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
			attributeValues.add(attributeValue);
		}

		return attributeValues;
	}

	private void possiblyWriteChildReferenceDetails(Optional<ChildReferenceDetails> details) {
		if (details.isPresent()) {
			writeChildReferenceDetails(details.get());
		}
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

	private void possiblyWriteMetadataType(ClientDataRecordGroup clientDataRecordGroup) {
		Optional<String> attributeValue = clientDataRecordGroup.getAttributeValue(TYPE);
		definition += attributeValue.isPresent() ? attributeValue.get() : "";
	}

	private void writeChildReferenceDetails(ChildReferenceDetails details) {
		definition += COMMA_SPACE;
		definition += details.repeatMin() + "-" + details.repeatMax();
		definition += COMMA_SPACE;
		definition += details.constraints();
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

	private void possiblyTraverseGroupAndChildren(ClientDataRecordGroup clientDataRecordGroup,
			int currentIndent) {
		if (isGroup(clientDataRecordGroup)) {
			possiblyTraverseChildren(clientDataRecordGroup, currentIndent);
		}
	}

	private void addNewLine() {
		definition += NEW_LINE;
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
				Optional<ChildReferenceDetails> childReferenceDetails = collectChildReferenceDetailsAsRecord(
						child);
				ClientDataRecord ref = readChildReferenceLink(child);
				writeDefinition(ref.getDataRecordGroup(), childReferenceDetails, currentIndent + 1);

			}
		}
	}

	private List<ClientDataGroup> getChildReferences(ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataGroup childReferencesGroup = clientDataRecordGroup
				.getFirstGroupWithNameInData("childReferences");
		return childReferencesGroup.getChildrenOfTypeAndName(ClientDataGroup.class,
				"childReference");
	}

	private ClientDataRecord readChildReferenceLink(ClientDataGroup child) {
		ClientDataRecordLink refLink = child.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
				REF);
		return dataClient.read(METADATA, refLink.getLinkedRecordId());
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
}
