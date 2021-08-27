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

import java.util.Iterator;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.JsonValueType;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class JsonArraySpy implements JsonArray {

	public MethodCallRecorder MCR = new MethodCallRecorder();

	public IteratorSpy returnedIterator = new IteratorSpy();

	@Override
	public JsonValueType getValueType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterator<JsonValue> iterator() {
		MCR.addCall();
		MCR.addReturned(returnedIterator);
		return returnedIterator;
	}

	@Override
	public JsonValue getValue(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonString getValueAsJsonString(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonObject getValueAsJsonObject(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonArray getValueAsJsonArray(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toJsonFormattedString() {
		// TODO Auto-generated method stub
		return null;
	}

}
