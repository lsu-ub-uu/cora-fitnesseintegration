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
package se.uu.ub.cora.fitnesseintegration.script;

import java.util.Iterator;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class DataHandlerScript {

	private static final String CHILDREN = "children";

	public String extractDataElement(String fullJson) {
		OrgJsonParser jsonParser = new OrgJsonParser();

		JsonObject fullJsonObject = jsonParser.parseStringAsObject(fullJson);
		JsonObject processedJsonObject = removeTopLevels(fullJsonObject);
		removeAllActionLinksFromData(processedJsonObject);

		return processedJsonObject.toJsonFormattedString();
	}

	private JsonObject removeTopLevels(JsonObject fullJsonObject) {
		return fullJsonObject.getValueAsJsonObject("record").getValueAsJsonObject("data");
	}

	private void removeAllActionLinksFromData(JsonObject data) {
		data.removeKey("actionLinks");
		if (data.containsKey(CHILDREN)) {
			traverseAndProcessChildren(data);
		}
	}

	private void traverseAndProcessChildren(JsonObject data) {
		JsonArray valueAsJsonArray = data.getValueAsJsonArray(CHILDREN);
		Iterator<JsonValue> jsonArrayIterator = valueAsJsonArray.iterator();
		while (jsonArrayIterator.hasNext()) {
			traverseAnyObjects(jsonArrayIterator);
		}
	}

	private void traverseAnyObjects(Iterator<JsonValue> jsonArrayIterator) {
		JsonValue nextJsonValue = jsonArrayIterator.next();
		removeAllActionLinksFromData((JsonObject) nextJsonValue);
	}
}
