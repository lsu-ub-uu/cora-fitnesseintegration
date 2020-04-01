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
import java.util.Iterator;
import java.util.List;

import se.uu.ub.cora.json.parser.JsonValue;

public class IteratorSpy implements Iterator<JsonValue> {

	public boolean hasNextWasCalled = false;
	public List<JsonObjectSpy> objectsReturnedFromNext = new ArrayList<>();
	private int numNextCalled = 0;

	@Override
	public boolean hasNext() {
		hasNextWasCalled = true;
		if (numNextCalled < 2) {
			numNextCalled++;
			return true;
		}
		return false;
	}

	@Override
	public JsonValue next() {
		JsonObjectSpy next = new JsonObjectSpy();
		objectsReturnedFromNext.add(next);
		return next;
	}

}
