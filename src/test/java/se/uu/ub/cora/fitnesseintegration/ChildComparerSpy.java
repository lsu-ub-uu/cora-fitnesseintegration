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

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonValue;

public class ChildComparerSpy implements ChildComparer {

	public ClientDataGroup dataGroup;
	public JsonValue jsonValue;
	public int numberOfErrorsToReturn = 0;
	public List<String> listToReturn;
	public boolean spyShouldThrowError = false;
	public String errorMessage;

	@Override
	public boolean dataGroupContainsChildren(ClientDataGroup dataGroup, JsonValue jsonValue) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<String> checkDataGroupContainsChildren(ClientDataGroup dataGroup,
			JsonValue jsonValue) {
		this.dataGroup = dataGroup;
		this.jsonValue = jsonValue;
		possiblyThrowError();
		listToReturn = new ArrayList<>();
		possiblyAddErrorMessages("is missing.");
		return listToReturn;
	}

	private void possiblyAddErrorMessages(String extraMessage) {
		for (int i = 0; i < numberOfErrorsToReturn; i++) {
			String errorMessage = "From spy: Child with number " + i + " " + extraMessage;
			listToReturn.add(errorMessage);
		}
	}

	private void possiblyThrowError() {
		if (spyShouldThrowError) {
			errorMessage = "error from spy";
			throw new JsonParseException(errorMessage);
		}
	}

	@Override
	public List<String> checkDataGroupContainsChildrenWithCorrectValues(ClientDataGroup dataGroup,
			JsonValue jsonValue) {
		this.dataGroup = dataGroup;
		this.jsonValue = jsonValue;
		possiblyThrowError();
		listToReturn = new ArrayList<>();
		possiblyAddErrorMessages("has incorrect value.");
		return listToReturn;
	}

}
