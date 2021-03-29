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
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ChildComparerImp implements ChildComparer {

	private static final String GROUP = "group";
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

	private boolean childIsMissing(ClientDataGroup dataGroup, String nameInData) {
		return !dataGroup.containsChildWithNameInData(nameInData);
	}

	private String constructMissingMessage(String nameInData) {
		return getMessagePrefix(nameInData) + " is missing.";
	}

	private String getMessagePrefix(String nameInData) {
		return "Child with nameInData " + nameInData;
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
		// TODO: should be jsonObject not value... ??
		// TODO: attributes???
		// TODO: check nameInData top group
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
		checkChildrenHasCorrectValues(dataGroup, errorMessages, childValues);
		return errorMessages;
	}

	private void checkChildrenHasCorrectValues(ClientDataGroup dataGroup,
			List<String> errorMessages, JsonArray childValues) {
		for (JsonValue childValue : childValues) {
			checkDataGroupContainsChildWithCorrectValue(dataGroup, (JsonObject) childValue,
					errorMessages);
		}
	}

	private void checkDataGroupContainsChildWithCorrectValue(ClientDataGroup dataGroup,
			JsonObject childObject, List<String> errorMessages) {
		String type = getType(childObject);

		if (ATOMIC.equals(type)) {
			checkAtomicHasCorrectValues(dataGroup, childObject, errorMessages);
		} else {
			checkGroupContainsCorrectChildren(dataGroup, childObject, errorMessages);
		}
	}

	private String getType(JsonObject childObject) {
		if (childObject.containsKey(CHILDREN)) {
			return GROUP;
		}
		return ATOMIC;
	}

	private String getRepeatIdOrNullFromJsonObject(JsonObject childObject) {
		if (childObject.containsKey(REPEAT_ID)) {
			return ((JsonString) childObject.getValue(REPEAT_ID)).getStringValue();
		}
		return null;
	}

	private boolean repeatIdsAreEqual(String repeatId, String repeatIdFromJson) {
		if (repeatId == null) {
			return null == repeatIdFromJson;
		}
		return repeatId.equals(repeatIdFromJson);
	}

	private void checkAtomicHasCorrectValues(ClientDataGroup dataGroup, JsonObject childObject,
			List<String> errorMessages) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		List<ClientDataAtomic> allDataAtomicsWithNameInData = dataGroup
				.getAllDataAtomicsWithNameInData(nameInData);
		boolean atomicExists = checkIfMatchingAtomicExistsInList(childObject,
				allDataAtomicsWithNameInData);
		if (!atomicExists) {
			createAndAddErrorMessage(errorMessages, childObject, nameInData);
		}
	}

	private boolean checkIfMatchingAtomicExistsInList(JsonObject childObject,
			List<ClientDataAtomic> allDataAtomicsWithNameInData) {
		String repeatIdFromJson = getRepeatIdOrNullFromJsonObject(childObject);
		for (ClientDataAtomic dataAtomic : allDataAtomicsWithNameInData) {
			if (dataAtomicMatchesJson(childObject, repeatIdFromJson, dataAtomic)) {
				return true;
			}
		}
		return false;
	}

	private boolean dataAtomicMatchesJson(JsonObject childObject, String repeatIdFromJson,
			ClientDataAtomic dataAtomic) {
		String repeatIdFromData = dataAtomic.getRepeatId();
		if (repeatIdsAreEqual(repeatIdFromJson, repeatIdFromData)) {
			return dataAtomicHasValueFromJson(childObject, dataAtomic);
		}
		return false;
	}

	private boolean dataAtomicHasValueFromJson(JsonObject childObject,
			ClientDataAtomic dataAtomic) {
		JsonString valueInJson = (JsonString) childObject.getValue("value");
		String stringValueInJson = valueInJson.getStringValue();
		return stringValueInJson.equals(dataAtomic.getValue());
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

	private void checkGroupContainsCorrectChildren(ClientDataGroup dataGroup,
			JsonObject childObject, List<String> errorMessages) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		String repeatIdFromJson = getRepeatIdOrNullFromJsonObject(childObject);
		List<ClientDataGroup> allGroupsWithNameInData = dataGroup
				.getAllGroupsWithNameInData(nameInData);
		boolean foundMatchingGroup = false;
		for (ClientDataGroup cDataGroup : allGroupsWithNameInData) {
			String repeatIdFromData = cDataGroup.getRepeatId();
			if (repeatIdsAreEqual(repeatIdFromJson, repeatIdFromData)) {
				foundMatchingGroup = true;

				JsonArray children = childObject.getValueAsJsonArray(CHILDREN);
				for (JsonValue childValue : children) {
					JsonObject grandChildObject = (JsonObject) childValue;
					checkDataGroupContainsChildWithCorrectValue(cDataGroup, grandChildObject,
							errorMessages);

				}
			}
		}
		if (!foundMatchingGroup) {
			if (null != repeatIdFromJson) {
				errorMessages.add(constructMissingMessageWithTypeAndRepeatId(nameInData, GROUP,
						repeatIdFromJson));
			} else {
				errorMessages.add(constructMissingMessageWithType(nameInData, GROUP));
			}
		}
	}

}
