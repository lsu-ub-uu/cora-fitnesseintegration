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

import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataMissingException;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ChildComparerImp implements ChildComparer {

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
		return jsonObject.getValueAsJsonArray("children");
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
		List<ClientDataAttribute> dataAttributes = new ArrayList<>();
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
		for (JsonValue childValue : extractChildren((JsonObject) jsonValue)) {
			checkDataGroupContainsChildWithCorrectValue(dataGroup, errorMessages,
					(JsonObject) childValue);
		}
		return errorMessages;
	}

	private void checkDataGroupContainsChildWithCorrectValue(ClientDataGroup dataGroup,
			List<String> errorMessages, JsonObject childObject) {
		String nameInData = getNameInData(childObject);
		String type = getType(childObject);

		if (noChildWithCorrectTypeExist(dataGroup, nameInData, type)) {
			errorMessages.add(constructMissingMessageWithType(nameInData, type));
		} else {
			checkChildValues(dataGroup, errorMessages, childObject, nameInData);
		}
	}

	private boolean noChildWithCorrectTypeExist(ClientDataGroup dataGroup, String nameInData,
			String type) {
		return childIsMissing(dataGroup, nameInData)
				|| childHasIncorrectType(dataGroup, nameInData, type);
	}

	private boolean childHasIncorrectType(ClientDataGroup dataGroup, String nameInData,
			String type) {
		try {
			if ("atomic".equals(type)) {
				dataGroup.getFirstAtomicValueWithNameInData(nameInData);
			} else {
				dataGroup.getFirstGroupWithNameInData(nameInData);
			}

		} catch (DataMissingException exception) {
			return true;
		}
		return false;
	}

	private boolean childIsMissing(ClientDataGroup dataGroup, String nameInData) {
		return !dataGroup.containsChildWithNameInData(nameInData);
	}

	private void checkChildValues(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject, String nameInData) {
		if (isAtomicType(childObject)) {
			checkAtomicChild(dataGroup, errorMessages, childObject, nameInData);
		} else {
			checkDataGroup(errorMessages, dataGroup, childObject, nameInData);
		}
	}

	private String getNameInData(JsonObject childObject) {
		JsonString name = getName(childObject);
		return name.getStringValue();
	}

	private void checkDataGroup(List<String> errorMessages, ClientDataGroup dataGroup,
			JsonObject groupObject, String nameInData) {
		ClientDataGroup childGroup = dataGroup.getFirstGroupWithNameInData(nameInData);

		JsonArray children = groupObject.getValueAsJsonArray("children");
		for (JsonValue childValue : children) {
			JsonObject childObject = (JsonObject) childValue;
			checkDataGroupContainsChildWithCorrectValue(childGroup, errorMessages, childObject);
		}
	}

	private boolean isAtomicType(JsonObject childObject) {
		String stringType = getType(childObject);
		return "atomic".equals(stringType);
	}

	private String getType(JsonObject childObject) {
		JsonString type = (JsonString) childObject.getValue("type");
		return type.getStringValue();
	}

	private void checkAtomicChild(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject, String nameInData) {
		JsonString value = (JsonString) childObject.getValue("value");
		String atomicValue = dataGroup.getFirstAtomicValueWithNameInData(nameInData);

		if (valueInDataNotSameAsInJson(value, atomicValue)) {
			String messagePrefix = getMessagePrefix(nameInData);
			errorMessages.add(messagePrefix + " does not have the correct value.");
		}
	}

	private boolean valueInDataNotSameAsInJson(JsonString value, String atomicValue) {
		return !atomicValue.equals(value.getStringValue());
	}

	private String getMessagePrefix(String nameInData) {
		return "Child with nameInData " + nameInData;
	}

}
