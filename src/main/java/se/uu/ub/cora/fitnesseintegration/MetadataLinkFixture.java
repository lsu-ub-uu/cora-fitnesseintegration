/*
 * Copyright 2018, 2023 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientConvertible;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class MetadataLinkFixture {
	private static final String APPLICATION_UUB_RECORD_JSON = "application/vnd.cora.record+json";
	private static final String ACCEPT = "Accept";

	private static final String NOT_FOUND = "not found";
	protected String linkedRecordType;
	protected String linkedRecordId;
	private List<ClientDataGroup> childReferenceList = new ArrayList<>();
	private ClientDataGroup matchingChildReference;
	private HttpHandlerFactory httpHandlerFactory;
	private String baseUrl = SystemUrl.getUrl() + "rest/record/";
	private String authToken;

	public MetadataLinkFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setLinkedRecordType(String linkedRecordType) {
		this.linkedRecordType = linkedRecordType;
		tryToSetMatchingChildReference();
	}

	public void setLinkedRecordId(String linkedRecordId) {
		this.linkedRecordId = linkedRecordId;
		tryToSetMatchingChildReference();
	}

	private void tryToSetMatchingChildReference() {
		if (linkedRecordTypeAndRecordIdExist()) {
			resetData();
			possiblySetChildReferenceList();
			setMatchingChildReference();
		}
	}

	private boolean linkedRecordTypeAndRecordIdExist() {
		return linkedRecordType != null && linkedRecordId != null;
	}

	private void resetData() {
		matchingChildReference = null;
	}

	private void possiblySetChildReferenceList() {
		ClientDataRecord dataRecord = DataHolder.getRecord();
		if (recordContainsDataGroup(dataRecord)) {
			ClientDataRecordGroup topLevelDataGroup = dataRecord.getDataRecordGroup();
			setChildReferenceList(topLevelDataGroup);
		}
	}

	private boolean recordContainsDataGroup(ClientDataRecord dataRecord) {
		return null != dataRecord && dataRecord.getDataRecordGroup() != null;
	}

	private void setChildReferenceList(ClientDataParent topLevelDataGroup) {
		if (childReferencesExists(topLevelDataGroup)) {
			ClientDataGroup childReferences = topLevelDataGroup
					.getFirstGroupWithNameInData("childReferences");
			childReferenceList = childReferences.getAllGroupsWithNameInData("childReference");
		}
	}

	private boolean childReferencesExists(ClientDataParent topLevelDataGroup) {
		return topLevelDataGroup.containsChildWithNameInData("childReferences");
	}

	private void setMatchingChildReference() {
		for (ClientDataGroup childReference : childReferenceList) {
			setChildReferenceIfMatchingTypeAndId(childReference);
		}
	}

	private void setChildReferenceIfMatchingTypeAndId(ClientDataGroup childReference) {
		String childLinkedRecordType = extractValueFromReferenceUsingNameInData(childReference,
				"linkedRecordType");
		String childLinkedRecordId = extractValueFromReferenceUsingNameInData(childReference,
				"linkedRecordId");

		if (childReferenceMatchesTypeAndId(childLinkedRecordType, childLinkedRecordId)) {
			matchingChildReference = childReference;
			setUpHttpHandlerForReadingChildReference(childLinkedRecordType, childLinkedRecordId);
		}
	}

	private HttpHandler setUpHttpHandlerForReadingChildReference(String childLinkedRecordType,
			String childLinkedRecordId) {
		String url = baseUrl + childLinkedRecordType + "/" + childLinkedRecordId;
		HttpHandler httpHandler = httpHandlerFactory.factor(url);
		httpHandler.setRequestMethod("GET");
		httpHandler.setRequestProperty("authToken", authToken);
		httpHandler.setRequestProperty(ACCEPT, APPLICATION_UUB_RECORD_JSON);
		return httpHandler;
	}

	protected boolean childReferenceMatchesTypeAndId(String childLinkedRecordType,
			String childLinkedRecordId) {
		return childLinkedRecordId.equals(linkedRecordId)
				&& childLinkedRecordType.equals(linkedRecordType);
	}

	protected String extractValueFromReferenceUsingNameInData(ClientDataGroup childReference,
			String childNameInData) {
		ClientDataGroup ref = childReference.getFirstGroupWithNameInData("ref");
		return ref.getFirstAtomicValueWithNameInData(childNameInData);
	}

	public String getRepeatMin() {
		return getAtomicValueByNameInDataFromMatchingChild("repeatMin");
	}

	private String getAtomicValueByNameInDataFromMatchingChild(String childNameInData) {
		if (null == matchingChildReference) {
			return NOT_FOUND;
		}
		return matchingChildReference.getFirstAtomicValueWithNameInData(childNameInData);
	}

	public String getRepeatMax() {
		return getAtomicValueByNameInDataFromMatchingChild("repeatMax");
	}

	public String getNameInData() {
		if (null == matchingChildReference) {
			return NOT_FOUND;
		}
		return getNameInDataFromMatchingChildReference();
	}

	private String getNameInDataFromMatchingChildReference() {
		RecordIdentifier identfier = getChildReferenceAsRecordIdentifier();
		String responseText = readRecordAsJson(identfier);
		return getNameInDataFromConvertedJson(responseText);
	}

	private RecordIdentifier getChildReferenceAsRecordIdentifier() {
		String childLinkedRecordType = extractValueFromReferenceUsingNameInData(
				matchingChildReference, "linkedRecordType");
		String childLinkedRecordId = extractValueFromReferenceUsingNameInData(
				matchingChildReference, "linkedRecordId");

		return new RecordIdentifier(childLinkedRecordType, childLinkedRecordId);
	}

	private String readRecordAsJson(RecordIdentifier identfier) {
		// TODO: this should be a recordLink and not an RecordIdentifier... :(
		HttpHandler httpHandler = setUpHttpHandlerForReadingChildReference(identfier.type(),
				identfier.id());
		return httpHandler.getResponseText();
	}

	private String getNameInDataFromConvertedJson(String responseText) {
		JsonToClientDataConverter jsonToClientDataConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(responseText);

		ClientConvertible instance = jsonToClientDataConverter.toInstance();
		ClientDataRecord clientDataRecord = (ClientDataRecord) instance;
		return getNameInDataFromDataGroupInRecord(clientDataRecord);
	}

	private String getNameInDataFromDataGroupInRecord(ClientDataRecord clientClientDataRecord) {
		ClientDataRecordGroup recordGroup = clientClientDataRecord.getDataRecordGroup();
		return recordGroup.getFirstAtomicValueWithNameInData("nameInData");
	}

	public String getRecordPartConstraint() {
		if (null == matchingChildReference) {
			return NOT_FOUND;
		}
		if (!matchingChildReference.containsChildWithNameInData("recordPartConstraint")) {
			return "noRestrictions";
		}
		return matchingChildReference.getFirstAtomicValueWithNameInData("recordPartConstraint");
	}

	private record RecordIdentifier(String type, String id) {
	}
}
