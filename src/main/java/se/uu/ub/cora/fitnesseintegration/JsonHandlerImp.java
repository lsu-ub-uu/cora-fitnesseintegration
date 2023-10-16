/*
 * Copyright 2020 Uppsala University Library
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

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonHandlerImp implements JsonHandler {

	public static JsonHandlerImp usingJsonParser(JsonParser jsonParser) {
		return new JsonHandlerImp(jsonParser);
	}

	private JsonParser jsonParser;

	private JsonHandlerImp(JsonParser jsonParser) {
		this.jsonParser = jsonParser;
	}

	@Override
	public JsonValue parseStringAsValue(String jsonString) {
		return jsonParser.parseString(jsonString);
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		return jsonParser.parseStringAsObject(jsonString);
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		return jsonParser.parseStringAsArray(jsonString);
	}

	public JsonParser onlyForTestGetJsonParser() {
		return jsonParser;
	}
}
