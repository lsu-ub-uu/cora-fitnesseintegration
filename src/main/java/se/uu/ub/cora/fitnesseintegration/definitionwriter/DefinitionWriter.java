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

import java.util.Optional;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.javaclient.JavaClientAuthTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;
import se.uu.ub.cora.javaclient.data.DataClient;

public class DefinitionWriter {

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
		writeDefinition(dataRecord.getDataRecordGroup(), 0);
		return definition;
	}

	private DataClient createDataClientUsingAuthToken(String authToken) {
		JavaClientAuthTokenCredentials authTokenCredentials = new JavaClientAuthTokenCredentials(
				baseUrl, appTokenUrl, authToken);
		return JavaClientProvider
				.createDataClientUsingJavaClientAuthTokenCredentials(authTokenCredentials);
	}

	private void writeDefinition(ClientDataRecordGroup clientDataRecordGroup, int currentIndent) {
		addTab(currentIndent);
		definition += writeElement(clientDataRecordGroup);
		possiblyTraverseGroup(clientDataRecordGroup, currentIndent);
	}

	private void addTab(int level) {
		for (int i = 0; i < level; i++) {
			definition += TAB;
		}
	}

	private void possiblyTraverseGroup(ClientDataRecordGroup clientDataRecordGroup,
			int currentIndent) {
		if (isGroup(clientDataRecordGroup)) {
			// possiblyTraverseChildren(clientDataRecordGroup, currentIndent);
		}
	}

	private boolean isGroup(ClientDataRecordGroup clientDataRecordGroup) {
		if (clientDataRecordGroup.hasAttributes()) {
			Optional<String> type = clientDataRecordGroup.getAttributeValue(TYPE);
			return type.isPresent() && "group".equals(type.get());
		}
		return false;
	}

	private String writeElement(ClientDataRecordGroup clientDataRecordGroup) {
		String metadataNameInData = clientDataRecordGroup
				.getFirstAtomicValueWithNameInData(NAME_IN_DATA);
		String metadataType = getMetadataType(clientDataRecordGroup);

		return metadataNameInData + "(" + metadataType + ")";
	}

	private String getMetadataType(ClientDataRecordGroup clientDataRecordGroup) {
		Optional<String> attributeValue = clientDataRecordGroup.getAttributeValue(TYPE);
		return attributeValue.isPresent() ? attributeValue.get() : "";
	}

	// private void possiblyTraverseChildren(ClientDataRecordGroup clientDataRecordGroup,
	// int currentIndent) {
	// if (clientDataRecordGroup.hasChildren()) {
	// List<ClientDataGroup> childReferences = getChildReferences(clientDataRecordGroup);
	// for (ClientDataGroup child : childReferences) {
	// addNewLine();
	// ClientDataRecord ref = readChildReferenceLink(child);
	// writeDefinition(ref.getDataRecordGroup(), currentIndent + 1);
	// }
	// }
	// }

	// private List<ClientDataGroup> getChildReferences(ClientDataRecordGroup clientDataRecordGroup)
	// {
	// ClientDataGroup childReferencesGroup = clientDataRecordGroup
	// .getFirstGroupWithNameInData("childReferences");
	// return childReferencesGroup.getChildrenOfTypeAndName(ClientDataGroup.class,
	// "childReference");
	// }
	//
	// private ClientDataRecord readChildReferenceLink(ClientDataGroup child) {
	// ClientDataRecordLink refLink = child.getFirstChildOfTypeAndName(ClientDataRecordLink.class,
	// "ref");
	// return dataClient.read(METADATA, refLink.getLinkedRecordId());
	// }

	// private void addNewLine() {
	// definition += NEW_LINE;
	// }

	public String onlyForTestGetBaseUrl() {
		return baseUrl;
	}

	public String onlyForTestGetAppTokenUrl() {
		return appTokenUrl;
	}

	public DataClient onlyForTestGetDataClient() {
		return dataClient;
	}

}