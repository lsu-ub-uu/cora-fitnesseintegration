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
import java.util.Map.Entry;
import java.util.Set;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.JsonValueType;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class JsonObjectSpy implements JsonObject {

	public JsonObjectSpy jsonObjectSpy;
	public List<String> getValueKeys = new ArrayList<>();
	public List<JsonObjectSpy> getValueObjectsReturned = new ArrayList<>();
	public List<JsonArraySpy> getValueArraysReturned = new ArrayList<>();

	public MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public JsonValueType getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonValue getValue(String key) {
		MCR.addCall("key", key);
		getValueKeys.add(key);

		JsonValue returnJson = null;

		if ("dataList".equals(key)) {
			JsonObjectSpy valueObjectSpy = new JsonObjectSpy();
			getValueObjectsReturned.add(valueObjectSpy);
			returnJson = valueObjectSpy;
		}
		if ("data".equals(key)) {
			JsonArraySpy jsonArraySpy = new JsonArraySpy();
			getValueArraysReturned.add(jsonArraySpy);
			returnJson = jsonArraySpy;
		}
		MCR.addReturned(returnJson);
		return returnJson;
	}

	@Override
	public JsonString getValueAsJsonString(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject getValueAsJsonObject(String key) {
		jsonObjectSpy = new JsonObjectSpy();
		return jsonObjectSpy;
	}

	@Override
	public JsonArray getValueAsJsonArray(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean containsKey(String key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Set<String> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Entry<String, JsonValue>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}

}
