/*
 * Copyright 2020, 2022, 2023 Uppsala University Library
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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataAttribute;
import se.uu.ub.cora.clientdata.ClientDataChild;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;

public class ChildComparerImp implements ChildComparer {

	private static final String GROUP = "group";
	private static final String ATOMIC = "atomic";
	private static final String REPEAT_ID = "repeatId";
	private static final String ATTRIBUTES = "attributes";
	private static final String CHILDREN = "children";
	private String errorMessageGroup = "Child with nameInData {0} and type {1}{2} is missing.";
	private String errorMessageAtomic = "Did not find a match for child with nameInData {0}{2}.";

	@Override
	public boolean dataGroupContainsChildren(ClientDataGroup dataGroup, JsonValue jsonValue) {
		return checkDataGroupContainsChildren(dataGroup, jsonValue).isEmpty();
	}

	@Override
	public List<String> checkDataGroupContainsChildren(ClientDataParent dataGroup,
			JsonValue jsonValue) {
		try {
			List<String> errorMessages = new ArrayList<>();
			return tryToCheckDataGroupContainsChildren(dataGroup, jsonValue, errorMessages);
		} catch (Exception exception) {
			throw new JsonParseException(exception.getMessage(), exception);
		}

	}

	private List<String> tryToCheckDataGroupContainsChildren(ClientDataParent dataGroup,
			JsonValue jsonValue, List<String> errorMessages) {
		JsonArray children = extractChildren((JsonObject) jsonValue);
		checkContainsChildren(dataGroup, errorMessages, children);
		return errorMessages;
	}

	private JsonArray extractChildren(JsonObject jsonObject) {
		return jsonObject.getValueAsJsonArray(CHILDREN);
	}

	private void checkContainsChildren(ClientDataParent dataGroup, List<String> errorMessages,
			JsonArray children) {
		for (JsonValue childValue : children) {
			checkContainsChild(dataGroup, errorMessages, (JsonObject) childValue);
		}
	}

	private void checkContainsChild(ClientDataParent dataGroup, List<String> errorMessages,
			JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		Optional<ClientDataChild> matchingChild = findMatchingChild(dataGroup, childObject,
				nameInData);

		if (matchingChild.isPresent()) {
			checkChildrenIfGroup(errorMessages, childObject, matchingChild.get());
		} else {
			addErrorMessage("", errorMessages, childObject);
		}
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

	private Optional<ClientDataChild> findMatchingChild(ClientDataParent dataGroup,
			JsonObject childObject, String nameInData) {
		List<ClientDataAttribute> dataAttributes = getAttributesFromJsonOrEmpty(childObject);

		return getChildFromDataGroupUsingNameInDataAndRepeatIdAndAttributes(dataGroup, nameInData,
				dataAttributes, childObject);
	}

	private List<ClientDataAttribute> getAttributesFromJsonOrEmpty(JsonObject childObject) {
		if (childObject.containsKey(ATTRIBUTES)) {
			JsonObject jsonAttributes = childObject.getValueAsJsonObject(ATTRIBUTES);
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
		return ClientDataProvider.createAttributeUsingNameInDataAndValue(entry.getKey(),
				attributeValue);
	}

	private Optional<ClientDataChild> getChildFromDataGroupUsingNameInDataAndRepeatIdAndAttributes(
			ClientDataParent dataGroup, String nameInData, List<ClientDataAttribute> dataAttributes,
			JsonObject childJsonObject) {
		Collection<ClientDataChild> possibleMatches = possiblyGetMatchingChildren(dataGroup,
				nameInData, dataAttributes);
		for (ClientDataChild childDataElement : possibleMatches) {
			if (childMatches(childDataElement, childJsonObject)) {
				return Optional.of(childDataElement);
			}
		}
		return Optional.empty();
	}

	private Collection<ClientDataChild> possiblyGetMatchingChildren(ClientDataParent dataGroup,
			String nameInData, List<ClientDataAttribute> dataAttributes) {
		Collection<ClientDataChild> possibleMatches = new ArrayList<>();
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

	private Collection<ClientDataGroup> getMatchingGroups(ClientDataParent dataGroup,
			String nameInData, List<ClientDataAttribute> dataAttributes) {
		return dataGroup.getAllGroupsWithNameInDataAndAttributes(nameInData,
				dataAttributes.toArray(ClientDataAttribute[]::new));
	}

	private void checkChildrenIfGroup(List<String> errorMessages, JsonObject childObject,
			ClientDataChild matchingChild) {
		if (matchingChild instanceof ClientDataGroup) {
			tryToCheckDataGroupContainsChildren((ClientDataGroup) matchingChild, childObject,
					errorMessages);
		}
	}

	private boolean childMatches(ClientDataChild childDataElement, JsonObject childJsonObject) {
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

	private String getRepeatIdFromChildElement(ClientDataChild childElement) {
		if (childElement instanceof ClientDataGroup) {
			return ((ClientDataGroup) childElement).getRepeatId();
		}
		return ((ClientDataAtomic) childElement).getRepeatId();
	}

	private boolean typesAreEqual(String type, ClientDataChild childElement) {
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

	@Override
	public List<String> checkDataGroupContainsChildrenWithCorrectValues(ClientDataParent dataGroup,
			JsonValue jsonValue) {
		try {
			return tryToCheckDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		} catch (Exception exception) {
			throw new JsonParseException(exception.getMessage(), exception);
		}
	}

	private List<String> tryToCheckDataGroupContainsChildrenWithCorrectValues(
			ClientDataParent dataGroup, JsonValue jsonValue) {
		List<String> errorMessages = new ArrayList<>();
		JsonArray childValues = extractChildren((JsonObject) jsonValue);
		checkChildrenHasCorrectValues(dataGroup, errorMessages, childValues);
		return errorMessages;
	}

	private void checkChildrenHasCorrectValues(ClientDataParent dataGroup,
			List<String> errorMessages, JsonArray childValues) {
		for (JsonValue childValue : childValues) {
			checkDataGroupContainsChildWithCorrectValue(dataGroup, (JsonObject) childValue,
					errorMessages);
		}
	}

	private void checkDataGroupContainsChildWithCorrectValue(ClientDataParent dataGroup,
			JsonObject childObject, List<String> errorMessages) {
		String type = getType(childObject);

		if (ATOMIC.equals(type)) {
			checkDataGroupContainsFullyCorrectAtomicChild(dataGroup, childObject, errorMessages);
		} else {
			checkDataGroupContainsCorrectChildren(dataGroup, childObject, errorMessages);
		}
	}

	private void checkDataGroupContainsFullyCorrectAtomicChild(ClientDataParent dataGroup,
			JsonObject atomicChild, List<String> errorMessages) {
		Collection<ClientDataAtomic> allChildAtomicsMatchingNameAndAttributes = getAllMatchingAtomics(
				dataGroup, atomicChild);
		boolean fullyCorrectAtomicChildFound = listContainsAtomicChildsRepeatIdAndValue(
				allChildAtomicsMatchingNameAndAttributes, atomicChild);
		if (!fullyCorrectAtomicChildFound) {
			addErrorMessage("", errorMessages, atomicChild);
		}
	}

	private Collection<ClientDataAtomic> getAllMatchingAtomics(ClientDataParent dataGroup,
			JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		List<ClientDataAttribute> attributes = extractAttributesFromJsonObject(childObject);

		return dataGroup.getAllDataAtomicsWithNameInDataAndAttributes(nameInData,
				(attributes.toArray(new ClientDataAttribute[0])));
	}

	private List<ClientDataAttribute> extractAttributesFromJsonObject(JsonObject childObject) {
		Optional<JsonObject> attributesFromJson = getOptionalAttributesFromJsonObject(childObject);
		List<ClientDataAttribute> attributeListFromJson = new ArrayList<>();
		if (attributesFromJson.isPresent()) {
			Set<Entry<String, JsonValue>> entrySet = attributesFromJson.get().entrySet();
			for (Entry<String, JsonValue> attributeFromJson : entrySet) {
				String key = attributeFromJson.getKey();
				JsonValue value = attributeFromJson.getValue();
				ClientDataAttribute dataAttributeFromJson = ClientDataProvider
						.createAttributeUsingNameInDataAndValue(key,
								((JsonString) value).getStringValue());
				attributeListFromJson.add(dataAttributeFromJson);
			}
		}
		return attributeListFromJson;
	}

	private Optional<JsonObject> getOptionalAttributesFromJsonObject(JsonObject childObject) {
		if (childObject.containsKey(ATTRIBUTES)) {
			return Optional.of(childObject.getValueAsJsonObject(ATTRIBUTES));
		}
		return Optional.empty();
	}

	private boolean listContainsAtomicChildsRepeatIdAndValue(
			Collection<ClientDataAtomic> allDataAtomicsWithNameInData, JsonObject childObject) {
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
		if (repeatIdsAreEqualOrEmpty(repeatIdFromJson, repeatIdFromData)) {
			return dataAtomicHasValueFromJson(json, dataAtomic);
		}
		return false;
	}

	private boolean dataAtomicHasValueFromJson(JsonObject childObject,
			ClientDataAtomic dataAtomic) {
		JsonString valueInJson = (JsonString) childObject.getValue("value");
		String stringValueInJson = valueInJson.getStringValue();
		return stringValueInJson.equals(dataAtomic.getValue());
	}

	private void checkDataGroupContainsCorrectChildren(ClientDataParent dataGroup,
			JsonObject childObject, List<String> errorMessages) {

		Optional<ClientDataGroup> matchingGroup = getChildFromDataGroupUsingNameInDataAndRepeatId(
				dataGroup, childObject);
		if (matchingGroup.isPresent()) {
			JsonArray children = childObject.getValueAsJsonArray(CHILDREN);
			checkChildrenHasCorrectValues(matchingGroup.get(), errorMessages, children);
		} else {
			addErrorMessage(GROUP, errorMessages, childObject);
		}
	}

	private Optional<ClientDataGroup> getChildFromDataGroupUsingNameInDataAndRepeatId(
			ClientDataParent dataGroup, JsonObject childObject) {
		Optional<String> repeatIdFromJson = getOptionalRepeatIdFromJsonObject(childObject);
		Collection<ClientDataGroup> allGroupsWithNameInData = getAllGroups(dataGroup, childObject);
		for (ClientDataGroup cDataGroup : allGroupsWithNameInData) {
			String repeatIdFromData = cDataGroup.getRepeatId();
			if (repeatIdsAreEqualOrEmpty(repeatIdFromJson, repeatIdFromData)) {
				return Optional.of(cDataGroup);
			}
		}
		return Optional.empty();
	}

	private Collection<ClientDataGroup> getAllGroups(ClientDataParent dataGroup,
			JsonObject childObject) {
		String nameInData = extractNameInDataFromJsonObject(childObject);
		List<ClientDataAttribute> attributes = extractAttributesFromJsonObject(childObject);

		return dataGroup.getAllGroupsWithNameInDataAndAttributes(nameInData,
				(attributes.toArray(new ClientDataAttribute[0])));
	}

	private Optional<String> getOptionalValueFromJsonObject(JsonObject childObject) {
		if (childObject.containsKey("value")) {
			return Optional.of(((JsonString) childObject.getValue("value")).getStringValue());
		}
		return Optional.empty();

	}

	private void addErrorMessage(String type, List<String> errorMessages, JsonObject childObject) {

		String nameInData = extractNameInDataFromJsonObject(childObject);
		String additionalMessage = "";
		additionalMessage += addValueIfPresent(childObject);
		additionalMessage += addRepeatIdIfPresent(childObject);
		additionalMessage += addAttributesIfPresent(childObject);
		String messageToFormat = decideMessageToFormatByType(type);
		errorMessages
				.add(MessageFormat.format(messageToFormat, nameInData, type, additionalMessage));

	}

	private String addValueIfPresent(JsonObject childObject) {
		Optional<String> value = getOptionalValueFromJsonObject(childObject);
		if (!value.isEmpty()) {
			return " and value " + value.get();
		}
		return "";
	}

	private String addRepeatIdIfPresent(JsonObject childObject) {
		Optional<String> repeatIdFromJson = getOptionalRepeatIdFromJsonObject(childObject);
		if (repeatIdFromJson.isPresent()) {
			return " and repeatId " + repeatIdFromJson.get();
		}
		return "";
	}

	private String addAttributesIfPresent(JsonObject childObject) {
		List<ClientDataAttribute> attributes = extractAttributesFromJsonObject(childObject);
		if (!attributes.isEmpty()) {
			List<String> asstributeAsString = new ArrayList<>();
			for (ClientDataAttribute attribute : attributes) {
				asstributeAsString.add(MessageFormat.format("{0}:{1}", attribute.getNameInData(),
						attribute.getValue()));
			}

			return " and attributes [" + String.join(", ", asstributeAsString) + "]";
		}
		return "";
	}

	private String decideMessageToFormatByType(String type) {
		if (type.equals(GROUP)) {
			return errorMessageGroup;
		}
		return errorMessageAtomic;
	}

}
