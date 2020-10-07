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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class PermissionComparerSpy implements DataComparer {

	public JsonValue jsonValue;
	public int numberOfErrorsToReturn = 0;
	public List<String> listToReturn;
	public boolean spyShouldThrowError = false;
	public String errorMessage;

	@Override
	public List<String> checkDataRecordContainsPermissions(JsonValue jsonValue) {
		this.jsonValue = jsonValue;
		possiblyThrowError();
		listToReturn = new ArrayList<>();
		possiblyAddErrorMessages("is missing.");
		return listToReturn;
	}

	private void possiblyAddErrorMessages(String extraMessage) {
		for (int i = 0; i < numberOfErrorsToReturn; i++) {
			String errorMessage = "From spy: Permission with number " + i + " " + extraMessage;
			listToReturn.add(errorMessage);
		}
	}

	private void possiblyThrowError() {
		if (spyShouldThrowError) {
			errorMessage = "error from spy";
			throw new JsonParseException(errorMessage);
		}
	}

}
