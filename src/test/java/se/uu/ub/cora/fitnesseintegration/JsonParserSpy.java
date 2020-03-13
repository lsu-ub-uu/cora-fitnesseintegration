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
import java.util.List;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonParserSpy implements JsonParser {

	public String jsonStringSentToParser;
	public JsonValueSpy jsonValueSpy;
	public JsonObjectSpy jsonObjectSpy;
	public List<JsonObjectSpy> jsonObjectSpies = new ArrayList<>();
	public JsonArraySpy jsonArraySpy;
	public List<String> jsonStringsSentToParser = new ArrayList<>();

	@Override
	public JsonValue parseString(String jsonString) {
		this.jsonStringSentToParser = jsonString;
		jsonValueSpy = new JsonValueSpy();
		return jsonValueSpy;
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		jsonStringsSentToParser.add(jsonString);
		// jsonStringSentToParser = jsonString;
		jsonObjectSpy = new JsonObjectSpy();
		jsonObjectSpies.add(jsonObjectSpy);
		return jsonObjectSpy;
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		jsonStringSentToParser = jsonString;
		jsonArraySpy = new JsonArraySpy();
		return jsonArraySpy;
	}

}
