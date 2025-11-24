/*
 * Copyright 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.cache;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.javaclient.data.DataClient;

public class ValidationTypeProvider {
	private static Map<String, ValidationType> validationTypeMap = new HashMap<>();

	private ValidationTypeProvider() {
		throw new UnsupportedOperationException();
	}

	public static ValidationType getValidationType(String id) {
		possiblyLoadValidationTypesFromServer();
		return validationTypeMap.get(id);
	}

	public static Collection<ValidationType> getValidationTypesThatValidatesRecordType(String id) {
		possiblyLoadValidationTypesFromServer();
		Stream<ValidationType> streamOfAllValidationTypes = validationTypeMap.values().stream()
				.sorted(sortOnId());
		return streamOfAllValidationTypes.filter(v -> validatesType(id, v)).toList();
	}

	private static Comparator<ValidationType> sortOnId() {
		return (ValidationType a, ValidationType b) -> a.id().compareTo(b.id());
	}

	private static boolean validatesType(String id, ValidationType v) {
		String validatesRecordTypeId = v.validatesRecordTypeId();
		return validatesRecordTypeId.equals(id);
	}

	private static void possiblyLoadValidationTypesFromServer() {
		if (validationTypesNotLoaded()) {
			loadValidationTypesFromServer();
		}
	}

	private static boolean validationTypesNotLoaded() {
		return validationTypeMap.size() == 0;
	}

	private static void loadValidationTypesFromServer() {
		List<ClientData> listOfRecords = loadListOfValidationTypesFromServer();
		for (ClientData recordItem : listOfRecords) {
			ClientDataRecord clientDataRecord = (ClientDataRecord) recordItem;

			ValidationType validationType = createValidationTypeFromRecordGroup(clientDataRecord);
			validationTypeMap.put(validationType.id(), validationType);
		}
	}

	private static ValidationType createValidationTypeFromRecordGroup(
			ClientDataRecord clientDataRecord) {
		ClientDataRecordGroup recordGroup = clientDataRecord.getDataRecordGroup();
		String id = clientDataRecord.getId();
		String recordTypeId = getLinkValueForNameInData(recordGroup, "validatesRecordType");
		String createDefinitionId = getLinkValueForNameInData(recordGroup, "newMetadataId");
		String updateDefinitionId = getLinkValueForNameInData(recordGroup, "metadataId");
		return new ValidationType(id, recordTypeId, createDefinitionId, updateDefinitionId);
	}

	private static String getLinkValueForNameInData(ClientDataRecordGroup clientDataRecord,
			String nameInData) {
		ClientDataRecordLink metadataLink = clientDataRecord
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, nameInData);
		return metadataLink.getLinkedRecordId();
	}

	private static List<ClientData> loadListOfValidationTypesFromServer() {
		DataClient client = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		ClientDataList dataList = client.readList("validationType");
		return dataList.getDataList();
	}

	public static void resetInternalHolder() {
		validationTypeMap = new HashMap<>();
	}

	public static void onlyForTestAddValidationTypeToInternalMap(ValidationType validationType) {
		validationTypeMap.put(validationType.id(), validationType);
	}

}
