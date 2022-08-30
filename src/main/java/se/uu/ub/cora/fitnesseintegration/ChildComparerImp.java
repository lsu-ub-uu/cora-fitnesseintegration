/*
 * Copyright 2020, 2022 Uppsala University Library
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataElement;
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
			List<String> errorMessages = new ArrayList<>();
			return tryToCheckDataGroupContainsChildren(dataGroup, jsonValue, errorMessages);
		} catch (Exception exception) {
			throw new JsonParseException(exception.getMessage(), exception);
		}
	}

	private List<String> tryToCheckDataGroupContainsChildren(ClientDataGroup dataGroup,
			JsonValue jsonValue, List<String> errorMessages) {
		JsonArray children = extractChildren((JsonObject) jsonValue);
		checkContainsChildren(dataGroup, errorMessages, children);
		return errorMessages;
	}

	private JsonArray extractChildren(JsonObject jsonObject) {
		return jsonObject.getValueAsJsonArray(CHILDREN);
	}

	private void checkContainsChildren(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonArray children) {
		for (JsonValue childValue : children) {
			checkContainsChild(dataGroup, errorMessages, (JsonObject) childValue);
		}
	}

	private void checkContainsChild(ClientDataGroup dataGroup, List<String> errorMessages,
			JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		Optional<ClientDataElement> matchingChild = findMatchingChild(dataGroup, childObject,
				nameInData);

		matchingChild.ifPresentOrElse(
				foundChild -> checkChildrenIfGroup(errorMessages, childObject, foundChild),
				() -> createAndAddErrorMessage(errorMessages, childObject, nameInData));
	}

	private String extractNameInDataFromJsonObject(JsonObject childObject) {
		JsonString name = getName(childObject);
		return name.getStringValue();
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

	private Optional<ClientDataElement> findMatchingChild(ClientDataGroup dataGroup,
			JsonObject childObject, String nameInData) {
		List<ClientDataAttribute> dataAttributes = getAttributesFromJsonOrEmpty(childObject);

		return getChildFromDataGroupUsingNameInDataAndRepeatIdAndAttributes(dataGroup, nameInData,
				dataAttributes, childObject);
	}

	private List<ClientDataAttribute> getAttributesFromJsonOrEmpty(JsonObject childObject) {
		if (childObject.containsKey("attributes")) {
			JsonObject jsonAttributes = childObject.getValueAsJsonObject("attributes");
			return getAttributesAsClientDataAttributes(jsonAttributes);
		}
		return Collections.emptyList();
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

	private Optional<ClientDataElement> getChildFromDataGroupUsingNameInDataAndRepeatIdAndAttributes(
			ClientDataGroup dataGroup, String nameInData, List<ClientDataAttribute> dataAttributes,
			JsonObject childJsonObject) {
		Collection<ClientDataElement> possibleMatches = possiblyGetMatchingChildren(dataGroup,
				nameInData, dataAttributes);
		for (ClientDataElement childDataElement : possibleMatches) {
			if (childMatches(childDataElement, childJsonObject)) {
				return Optional.of(childDataElement);
			}
		}
		return Optional.empty();
	}

	private Collection<ClientDataElement> possiblyGetMatchingChildren(ClientDataGroup dataGroup,
			String nameInData, List<ClientDataAttribute> dataAttributes) {
		Collection<ClientDataElement> possibleMatches = new ArrayList<>();
		if (dataAttributesExistsWhichMeansChildIsGroup(dataAttributes)) {
			possibleMatches.addAll(getMatchingGroups(dataGroup, nameInData, dataAttributes));
		} else {
			possibleMatches = dataGroup.getAllChildrenWithNameInData(nameInData);
		}
		return possibleMatches;
	}

	private boolean dataAttributesExistsWhichMeansChildIsGroup(
			List<ClientDataAttribute> dataAttributes) {
		return !dataAttributes.isEmpty();
	}

	private Collection<ClientDataGroup> getMatchingGroups(ClientDataGroup dataGroup,
			String nameInData, List<ClientDataAttribute> dataAttributes) {
		return dataGroup.getAllGroupsWithNameInDataAndAttributes(nameInData,
				dataAttributes.toArray(ClientDataAttribute[]::new));
	}

	private void checkChildrenIfGroup(List<String> errorMessages, JsonObject childObject,
			ClientDataElement matchingChild) {
		if (matchingChild instanceof ClientDataGroup) {
			tryToCheckDataGroupContainsChildren((ClientDataGroup) matchingChild, childObject,
					errorMessages);
		}
	}

	private boolean childMatches(ClientDataElement childDataElement, JsonObject childJsonObject) {
		String type = getType(childJsonObject);
		Optional<String> repeatIdFromJson = getOptionalRepeatIdFromJsonObject(childJsonObject);
		String repeatIdFromData = getRepeatIdFromChildElement(childDataElement);
		return typesAreEqual(type, childDataElement)
				&& repeatIdsAreEqualOrEmpty(repeatIdFromJson, repeatIdFromData);
	}

	private String getType(JsonObject childObject) {
		if (childObject.containsKey(CHILDREN)) {
			return GROUP;
		}
		return ATOMIC;
	}

	private Optional<String> getOptionalRepeatIdFromJsonObject(JsonObject childObject) {
		if (childObject.containsKey(REPEAT_ID)) {
			return Optional.of(((JsonString) childObject.getValue(REPEAT_ID)).getStringValue());
		}
		return Optional.empty();
	}

	private String getRepeatIdFromChildElement(ClientDataElement childElement) {
		if (childElement instanceof ClientDataGroup) {
			return ((ClientDataGroup) childElement).getRepeatId();
		}
		return ((ClientDataAtomic) childElement).getRepeatId();
	}

	private boolean typesAreEqual(String type, ClientDataElement childElement) {
		String childElementType = ATOMIC;
		if (childElement instanceof ClientDataGroup) {
			childElementType = GROUP;
		}
		return childElementType.equals(type);
	}

	private boolean repeatIdsAreEqualOrEmpty(Optional<String> repeatIdFromJson,
			String repeatIdFromDataElement) {
		return repeatIdFromJson.isEmpty() || repeatIdFromJson.get().equals(repeatIdFromDataElement);
	}

	private void createAndAddErrorMessage(List<String> errorMessages, JsonObject childObject,
			String nameInData) {
		String message = "Did not find a match for child with nameInData " + nameInData
				+ getPossibleRepeatIdMessage(childObject) + ".";
		errorMessages.add(message);
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

	private void checkAtomicHasCorrectValues(ClientDataGroup dataGroup, JsonObject childObject,
			List<String> errorMessages) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		List<ClientDataAtomic> allDataAtomicsWithNameInData = dataGroup
				.getAllDataAtomicsWithNameInData(nameInData);
		boolean atomicExists = checkIfMatchingAtomicExistsInList(childObject,
				allDataAtomicsWithNameInData);
		if (!atomicExists) {
			createAndAddErrorMessageIncludingValue(errorMessages, childObject, nameInData);
		}
	}

	private boolean checkIfMatchingAtomicExistsInList(JsonObject childObject,
			List<ClientDataAtomic> allDataAtomicsWithNameInData) {
		for (ClientDataAtomic dataAtomic : allDataAtomicsWithNameInData) {
			if (dataAtomicMatchesJson(childObject, dataAtomic)) {
				return true;
			}
		}
		return false;
	}

	private boolean dataAtomicMatchesJson(JsonObject json, ClientDataAtomic dataAtomic) {
		Optional<String> repeatIdFromJson = getOptionalRepeatIdFromJsonObject(json);
		String repeatIdFromData = dataAtomic.getRepeatId();

		if (!sameAttributes(json, dataAtomic)) {
			return false;
		}
		if (repeatIdsAreEqualOrEmpty(repeatIdFromJson, repeatIdFromData)) {
			return dataAtomicHasValueFromJson(json, dataAtomic);
		}
		return false;
	}

	private boolean sameAttributes(JsonObject json, ClientDataElement dataElement) {
		Optional<JsonObject> attributesFromJson = getOptionalAttributesFromJsonObject(json);

		int dataSize = dataElement.getAttributes().size();
		int jsonSize = 0;
		if (attributesFromJson.isPresent()) {
			Set<Entry<String, JsonValue>> entrySet = attributesFromJson.get().entrySet();
			jsonSize = entrySet.size();
		}

		if (jsonSize != dataSize) {
			return false;
		}
		if (jsonSize == 0) {
			return true;
		}

		Set<Entry<String, JsonValue>> entrySet = attributesFromJson.get().entrySet();
		Map<String, String> attributes = dataElement.getAttributes();

		for (Entry<String, JsonValue> attributeFromJson : entrySet) {
			String key = attributeFromJson.getKey();
			if (!attributes.containsKey(key)) {
				return false;
			}
			JsonValue value = attributeFromJson.getValue();
			if (!attributes.get(key).equals(((JsonString) value).getStringValue())) {
				return false;
			}
		}
		return true;
	}

	private Optional<JsonObject> getOptionalAttributesFromJsonObject(JsonObject childObject) {
		if (childObject.containsKey("attributes")) {
			return Optional.of(childObject.getValueAsJsonObject("attributes"));
		}
		return Optional.empty();
	}

	private boolean dataAtomicHasValueFromJson(JsonObject childObject,
			ClientDataAtomic dataAtomic) {
		JsonString valueInJson = (JsonString) childObject.getValue("value");
		String stringValueInJson = valueInJson.getStringValue();
		return stringValueInJson.equals(dataAtomic.getValue());
	}

	private void createAndAddErrorMessageIncludingValue(List<String> errorMessages,
			JsonObject childObject, String nameInData) {
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
		Optional<String> repeatIdFromJson = getOptionalRepeatIdFromJsonObject(childObject);

		Optional<ClientDataGroup> matchingGroup = getChildFromDataGroupUsingNameInDataAndRepeatId(
				dataGroup, nameInData, repeatIdFromJson);
		matchingGroup.ifPresentOrElse(foundGroup -> {
			JsonArray children = childObject.getValueAsJsonArray(CHILDREN);
			checkChildrenHasCorrectValues(foundGroup, errorMessages, children);
		}, () -> createAndAddError(errorMessages, nameInData, repeatIdFromJson));
	}

	private Optional<ClientDataGroup> getChildFromDataGroupUsingNameInDataAndRepeatId(
			ClientDataGroup dataGroup, String nameInData, Optional<String> repeatIdFromJson) {
		List<ClientDataGroup> allGroupsWithNameInData = dataGroup
				.getAllGroupsWithNameInData(nameInData);
		for (ClientDataGroup cDataGroup : allGroupsWithNameInData) {
			String repeatIdFromData = cDataGroup.getRepeatId();
			if (repeatIdsAreEqualOrEmpty(repeatIdFromJson, repeatIdFromData)) {
				return Optional.of(cDataGroup);
			}
		}
		return Optional.empty();
	}

	private void createAndAddError(List<String> errorMessages, String nameInData,
			Optional<String> repeatIdFromJson) {
		repeatIdFromJson.ifPresentOrElse(
				repeatId -> errorMessages.add(
						constructMissingMessageWithTypeAndRepeatId(nameInData, GROUP, repeatId)),
				() -> errorMessages.add(constructMissingMessageWithType(nameInData, GROUP)));
	}
}
