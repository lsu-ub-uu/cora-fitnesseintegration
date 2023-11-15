/*
 * Copyright 2020, 2022, 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.fitnesseintegration.ChildComparer;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.JsonHandlerImp;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParseException;

public class ChildComparerFixture extends ComparerFixture {
	private ChildComparer childComparer;
	private String childrenToCompare;
	private int expectedNumberOfChildren;
	private JsonHandler jsonHandler;

	public ChildComparerFixture() {
		super();
		childComparer = DependencyProvider.getChildComparer();
		jsonHandler = DependencyProvider.getJsonHandler();

	}

	public String testCheckContain() {
		try {
			ClientDataRecordGroup readDataGroup = DataHolder.getRecord().getDataRecordGroup();
			return compareChildrenUsingDataGroup(readDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private String compareChildrenUsingDataGroup(ClientDataParent clientDataGroup) {
		JsonObject childrenObject = jsonHandler.parseStringAsObject(childrenToCompare);
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(clientDataGroup,
				childrenObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public String testCheckContainWithValues() {
		try {
			ClientDataRecordGroup readDataGroup = DataHolder.getRecord().getDataRecordGroup();
			return compareChildrenWithValuesUsingDataGroup(readDataGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	private String compareChildrenWithValuesUsingDataGroup(ClientDataParent clientDataGroup) {
		JsonObject jsonToBeComparedWith = jsonHandler.parseStringAsObject(childrenToCompare);
		List<String> errorMessages = childComparer.checkDataGroupContainsChildrenWithCorrectValues(
				clientDataGroup, jsonToBeComparedWith);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public String testReadFromListCheckContain() {
		try {
			ClientDataRecord clientDataRecord = getClientDataRecordFromRecordHolderUsingIndex();
			ClientDataRecordGroup dataRecordGroup = clientDataRecord.getDataRecordGroup();
			return compareChildrenUsingDataGroup(dataRecordGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	public String testReadFromListCheckContainWithValues() {
		try {
			ClientDataRecord clientDataRecord = getClientDataRecordFromRecordHolderUsingIndex();
			ClientDataRecordGroup dataRecordGroup = clientDataRecord.getDataRecordGroup();
			return compareChildrenWithValuesUsingDataGroup(dataRecordGroup);
		} catch (JsonParseException exception) {
			return exception.getMessage();
		}
	}

	public String testCheckNumberOfChildren() {
		int numberOfChildren = getNumberOfChildReferences();
		if (actualAndExpectedIsSameSize(numberOfChildren)) {
			return "OK";
		}
		return "Expected " + expectedNumberOfChildren + " but found " + numberOfChildren;
	}

	private int getNumberOfChildReferences() {
		ClientDataRecordGroup readDataGroup = DataHolder.getRecord().getDataRecordGroup();
		ClientDataGroup childReferences = readDataGroup
				.getFirstGroupWithNameInData("childReferences");
		return childReferences.getAllGroupsWithNameInData("childReference").size();
	}

	private boolean actualAndExpectedIsSameSize(int numberOfChildren) {
		return numberOfChildren == expectedNumberOfChildren;
	}

	public void setChildren(String children) {
		childrenToCompare = children;

	}

	public void setExpectedNumberOfChildren(int expectedNumberOfChildren) {
		this.expectedNumberOfChildren = expectedNumberOfChildren;

	}

	public ChildComparer onlyForTestGetChildComparer() {
		return childComparer;
	}

	public void onlyForTestSetJsonHandler(JsonHandlerImp jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

	public JsonHandler onlyForTestGetJsonHandler() {
		return jsonHandler;
	}

}
