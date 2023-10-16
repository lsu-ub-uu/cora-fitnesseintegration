/*
 * Copyright 2020, 2023 Uppsala University Library
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

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.json.parser.JsonObject;

public class ActionComparerFixture extends ComparerFixture {

	private ComparerFactory comparerFactory;
	private String actions;
	private JsonHandler jsonHandler;

	public ActionComparerFixture() {
		super();
		comparerFactory = DependencyProvider.getComparerFactory();
		jsonHandler = DependencyProvider.getJsonHandler();
	}

	public String testCheckActions() {
		ClientDataRecord dataRecord = DataHolder.getRecord();
		return checkActions(dataRecord);
	}

	public String testCheckActionsFromList() {
		ClientDataRecord dataRecord = getClientDataRecordFromRecordHolderUsingIndex();
		return checkActions(dataRecord);
	}

	private String checkActions(ClientDataRecord dataRecord) {
		DataComparer comparer = comparerFactory.factor("action", dataRecord);
		JsonObject permissionObject = jsonHandler.parseStringAsObject(actions);
		List<String> errorMessages = comparer.checkClientDataRecordContains(permissionObject);
		return errorMessages.isEmpty() ? "OK" : joinErrorMessages(errorMessages);
	}

	public void setActions(String actions) {
		this.actions = actions;
	}

	public ComparerFactory getComparerFactory() {
		return comparerFactory;
	}

	void onlyForTestSetJsonHandler(JsonHandler jsonHandler) {
		this.jsonHandler = jsonHandler;
	}

	public JsonHandler onlyForTestGetJsonHandler() {
		return jsonHandler;
	}

}
