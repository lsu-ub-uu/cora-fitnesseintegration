/*
 * Copyright 2020 Uppsala University Library
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
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataElement;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataMissingException;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ChildComparerImp implements ChildComparer {

	private static final String REPEAT_ID = "repeatId";
	private static final String CHILDREN = "children";
	private static final String ATOMIC = "atomic";

	@Override
	public boolean dataGroupContainsChildren(ClientDataGroup dataGroup, JsonValue jsonValue) {
		return checkDataGroupContainsChildren(dataGroup, jsonValue).isEmpty();
	}

	@Override
	public List<String> checkDataGroupContainsChildren(ClientDataGroup dataGroup,
			JsonValue jsonValue) {
		try {
			return tryToCheckDataGroupContainsChildren(dataGroup, jsonValue);
		} catch (Exception exception) {
			throw new JsonParseException(exception.getMessage(), exception);
		}
	}

	private List<String> tryToCheckDataGroupContainsChildren(ClientDataGroup dataGroup,
			JsonValue jsonValue) {
		List<String> errorMessages = new ArrayList<>();
		for (JsonValue childValue : extractChildren((JsonObject) jsonValue)) {
			checkDataGroupContainsChild(dataGroup, errorMessages, (JsonObject) childValue);
		}
		return errorMessages;
	}

	private JsonArray extractChildren(JsonObject jsonObject) {
		return jsonObject.getValueAsJsonArray(CHILDREN);
	}

	private void checkDataGroupContainsChild(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject) {
		if (childObject.containsKey("attributes")) {
			addErrorMessageIfChildWithAttributeIsMissing(dataGroup, errorMessages, childObject);
		} else {
			String nameInData = extractNameInDataFromJsonObject(childObject);
			addErrorMessageIfChildIsMissing(dataGroup, nameInData, errorMessages);
		}
	}

	private void addErrorMessageIfChildWithAttributeIsMissing(ClientDataGroup dataGroup,
			List<String> errorMessages, JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		JsonObject jsonAttributes = childObject.getValueAsJsonObject("attributes");

		List<ClientDataAttribute> dataAttributes = getAttributesAsClientDataAttributes(
				jsonAttributes);
		Collection<ClientDataGroup> children = dataGroup.getAllGroupsWithNameInDataAndAttributes(
				nameInData, dataAttributes.toArray(ClientDataAttribute[]::new));
		if (children.isEmpty()) {
			errorMessages.add(constructMissingMessage(nameInData));
		}
	}

	private String extractNameInDataFromJsonObject(JsonObject childObject) {
		JsonString name = getName(childObject);
		return name.getStringValue();
	}

	private List<ClientDataAttribute> getAttributesAsClientDataAttributes(
			JsonObject jsonAttributes) {
		List<ClientDataAttribute> dataAttributes = new ArrayList<>(jsonAttributes.size());
		for (Entry<String, JsonValue> entry : jsonAttributes.entrySet()) {
			ClientDataAttribute attribute = getAttributeAsClientDataAttribute(entry);
			dataAttributes.add(attribute);
		}
		return dataAttributes;
	}

	private ClientDataAttribute getAttributeAsClientDataAttribute(Entry<String, JsonValue> entry) {
		String attributeValue = ((JsonString) entry.getValue()).getStringValue();
		return ClientDataAttribute.withNameInDataAndValue(entry.getKey(), attributeValue);
	}

	private void addErrorMessageIfChildIsMissing(ClientDataGroup dataGroup, String nameInData,
			List<String> errorMessages) {
		if (childIsMissing(dataGroup, nameInData)) {
			errorMessages.add(constructMissingMessage(nameInData));
		}
	}

	private String constructMissingMessage(String nameInData) {
		return getMessagePrefix(nameInData) + " is missing.";
	}

	private String constructMissingMessageWithType(String nameInData, String type) {
		return getMessagePrefix(nameInData) + " and type " + type + " is missing.";
	}

	private String constructMissingMessageWithTypeAndRepeatId(String nameInData, String type,
			String repeatId) {
		return getMessagePrefix(nameInData) + " and type " + type + " and repeatId " + repeatId
				+ " is missing.";
	}

	private JsonString getName(JsonObject child) {
		throwErrorIfMissingKey(child);
		return (JsonString) child.getValue("name");
	}

	private void throwErrorIfMissingKey(JsonObject child) {
		if (!child.containsKey("name")) {
			throw new JsonParseException("child must contain key: name");
		}
	}

	@Override
	public List<String> checkDataGroupContainsChildrenWithCorrectValues(ClientDataGroup dataGroup,
			JsonValue jsonValue) {
		try {
			return tryToCheckDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		} catch (Exception exception) {
			throw new JsonParseException(exception.getMessage(), exception);
		}
	}

	private List<String> tryToCheckDataGroupContainsChildrenWithCorrectValues(
			ClientDataGroup dataGroup, JsonValue jsonValue) {
		List<String> errorMessages = new ArrayList<>();
		JsonArray childValues = extractChildren((JsonObject) jsonValue);
		for (JsonValue childValue : childValues) {
			checkDataGroupContainsChildWithCorrectValue(dataGroup, errorMessages,
					(JsonObject) childValue);
		}
		return errorMessages;
	}

	private void checkDataGroupContainsChildWithCorrectValue(ClientDataGroup dataGroup,
			List<String> errorMessages, JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		String type = getType(childObject);
		String repeatId = getRepeatId(childObject);

		if (noChildWithCorrectTypeExist(dataGroup, nameInData, type, repeatId)) {
			if (!"".equals(repeatId)) {
				errorMessages.add(
						constructMissingMessageWithTypeAndRepeatId(nameInData, type, repeatId));
			} else {
				errorMessages.add(constructMissingMessageWithType(nameInData, type));
			}
		} else {
			checkChildValues(dataGroup, errorMessages, childObject, nameInData);
		}
	}

	private String getRepeatId(JsonObject childObject) {
		if (childObject.containsKey(REPEAT_ID)) {
			return ((JsonString) childObject.getValue(REPEAT_ID)).getStringValue();
		}
		return "";
	}

	private boolean noChildWithCorrectTypeExist(ClientDataGroup dataGroup, String nameInData,
			String type, String repeatId) {
		return childIsMissing(dataGroup, nameInData)
				|| childHasIncorrectRepeatId(dataGroup, nameInData, repeatId)
				|| childHasIncorrectType(dataGroup, nameInData, type);
	}

	private boolean childIsMissing(ClientDataGroup dataGroup, String nameInData) {
		return !dataGroup.containsChildWithNameInData(nameInData);
	}

	private boolean childHasIncorrectRepeatId(ClientDataGroup dataGroup, String nameInData,
			String repeatId) {
		return !oneRepeatIdMatches(dataGroup, nameInData, repeatId);
	}

	private boolean oneRepeatIdMatches(ClientDataGroup dataGroup, String nameInData,
			String repeatId) {
		List<ClientDataElement> matchingChildren = dataGroup
				.getAllChildrenWithNameInData(nameInData);
		boolean matchingRepeatId = false;
		for (ClientDataElement childGroup : matchingChildren) {
			String repeatIdInData = getRepeatIdFromAtomicOrGroup(childGroup);
			if (bothRepeatIdsAreEmpty(repeatId, repeatIdInData)
					|| repeatId.equals(repeatIdInData)) {
				matchingRepeatId = true;
			}
		}
		return matchingRepeatId;
	}

	private String getRepeatIdFromAtomicOrGroup(ClientDataElement childElement) {
		if (childElement instanceof ClientDataGroup) {
			return ((ClientDataGroup) childElement).getRepeatId();
		}
		return ((ClientDataAtomic) childElement).getRepeatId();
	}

	private boolean bothRepeatIdsAreEmpty(String repeatIdInJson, String repeatIdInData) {
		return "".equals(repeatIdInJson) && (repeatIdInData == null || "".equals(repeatIdInData));
	}

	private boolean childHasIncorrectType(ClientDataGroup dataGroup, String nameInData,
			String type) {
		try {
			if (ATOMIC.equals(type)) {
				dataGroup.getFirstAtomicValueWithNameInData(nameInData);
			} else {
				dataGroup.getFirstGroupWithNameInData(nameInData);
			}

		} catch (DataMissingException exception) {
			return true;
		}
		return false;
	}

	private void checkChildValues(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject, String nameInData) {
		if (isAtomicType(childObject)) {
			checkValueAtomicChild(dataGroup, errorMessages, childObject, nameInData);

		} else {
			checkDataGroup(errorMessages, dataGroup, childObject, nameInData);
		}
	}

	private boolean isAtomicType(JsonObject childObject) {
		String stringType = getType(childObject);
		return ATOMIC.equals(stringType);
	}

	private String getType(JsonObject childObject) {
		if (childObject.containsKey(CHILDREN)) {
			return "group";
		}
		return ATOMIC;
	}

	private void checkValueAtomicChild(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject, String nameInData) {
		boolean foundMatching = jsonChildFoundInDataChildren(dataGroup, childObject, nameInData);
		if (!foundMatching) {
			createAndAddErrorMessage(errorMessages, childObject, nameInData);
		}
	}

	private boolean jsonChildFoundInDataChildren(ClientDataGroup dataGroup, JsonObject childObject,
			String nameInData) {
		JsonString valueInJson = (JsonString) childObject.getValue("value");

		List<ClientDataAtomic> atomicsWithNameInData = dataGroup
				.getAllDataAtomicsWithNameInData(nameInData);

		for (ClientDataAtomic dataAtomic : atomicsWithNameInData) {
			if (jsonChildMatchesDataChild(childObject, valueInJson, dataAtomic)) {
				return true;
			}
		}
		return false;
	}

	private boolean jsonChildMatchesDataChild(JsonObject childObject, JsonString valueInJson,
			ClientDataAtomic dataAtomic) {
		if (valueInDataIsSameAsJson(dataAtomic.getValue(), valueInJson)) {
			return repeatIdMatches(dataAtomic, childObject);
		}
		return false;
	}

	private boolean valueInDataIsSameAsJson(String atomicValue, JsonString valueInJson) {
		return atomicValue.equals(valueInJson.getStringValue());
	}

	private boolean repeatIdMatches(ClientDataAtomic dataAtomic, JsonObject childObject) {
		if (childObject.containsKey(REPEAT_ID)) {
			JsonString repeatIdInJson = (JsonString) childObject.getValue(REPEAT_ID);
			return sameRepeatIdValue(dataAtomic, repeatIdInJson);
		}
		return dataAtomic.getRepeatId() == null;
	}

	private boolean sameRepeatIdValue(ClientDataAtomic dataAtomic, JsonString repeatIdInJson) {
		return repeatIdInJson.getStringValue().equals(dataAtomic.getRepeatId());
	}

	private void createAndAddErrorMessage(List<String> errorMessages, JsonObject childObject,
			String nameInData) {
		JsonString valueInJson = (JsonString) childObject.getValue("value");
		String message = "Did not find a match for child with nameInData " + nameInData
				+ " and value " + valueInJson.getStringValue()
				+ getPossibleRepeatIdMessage(childObject) + ".";
		errorMessages.add(message);
	}

	private String getPossibleRepeatIdMessage(JsonObject childObject) {
		if (childObject.containsKey(REPEAT_ID)) {
			JsonString repeatIdInJson = (JsonString) childObject.getValue(REPEAT_ID);
			return " and repeatId " + repeatIdInJson.getStringValue();
		}
		return "";
	}

	private void checkDataGroup(List<String> errorMessages, ClientDataGroup dataGroup,
			JsonObject groupObject, String nameInData) {
		List<ClientDataGroup> matchingChildGroups = dataGroup
				.getAllGroupsWithNameInData(nameInData);

		JsonArray children = groupObject.getValueAsJsonArray(CHILDREN);
		for (JsonValue childValue : children) {
			JsonObject childObject = (JsonObject) childValue;
			for (ClientDataGroup childGroup : matchingChildGroups) {
				String childNameInData = extractNameInDataFromJsonObject(childObject);
				String type = getType(childObject);
				String repeatId = getRepeatId(childObject);

				if (noChildWithCorrectTypeExist(childGroup, childNameInData, type, repeatId)) {
					if (!"".equals(repeatId)) {
						errorMessages.add(constructMissingMessageWithTypeAndRepeatId(
								childNameInData, type, repeatId));
					} else {
						errorMessages.add(constructMissingMessageWithType(childNameInData, type));
					}
				} else {
					checkChildValues(childGroup, errorMessages, childObject, childNameInData);
				}
				// checkDataGroupContainsChildWithCorrectValue(childGroup, errorMessages,
				// childObject);

			}
		}
	}

	private String getMessagePrefix(String nameInData) {
		return "Child with nameInData " + nameInData;
	}
}
