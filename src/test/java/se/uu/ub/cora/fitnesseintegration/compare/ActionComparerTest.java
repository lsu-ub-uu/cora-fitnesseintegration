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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class ActionComparerTest {
	private ClientDataRecord dataRecord;
	private JsonParser jsonParser;
	private ActionComparer comparer;

	@BeforeMethod
	public void setUp() {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		dataRecord = ClientDataRecord.withClientDataGroup(dataGroup);
		jsonParser = new OrgJsonParser();
		comparer = new ActionComparer(dataRecord);
	}

	@Test
	public void testReadAction() {
		addLinkToRecord("read", Action.READ);

		String actions = "{\"actions\":[ \"read\"]}";
		JsonValue jsonValue = jsonParser.parseString(actions);

		List<String> results = comparer.checkClientDataRecordContains(jsonValue);
		assertTrue(results.isEmpty());
	}

	@Test
	public void testReadActionInJsonTwoActionsInRecordOk() {
		addLinkToRecord("read", Action.READ);
		addLinkToRecord("update", Action.UPDATE);
		String actions = "{\"actions\":[ \"read\"]}";
		JsonValue jsonValue = jsonParser.parseString(actions);

		List<String> results = comparer.checkClientDataRecordContains(jsonValue);
		assertTrue(results.isEmpty());
	}

	private void addLinkToRecord(String key, Action action) {
		ClientActionLink actionLink = ClientActionLink.withAction(Action.READ);

		dataRecord.addClientActionLink(key, actionLink);
	}

	@Test
	public void testNoUpdateInRecordNOTOk() {
		addLinkToRecord("read", Action.READ);
		String actions = "{\"actions\":[ \"read\", \"update\"]}";
		JsonValue jsonValue = jsonParser.parseString(actions);

		List<String> results = comparer.checkClientDataRecordContains(jsonValue);
		assertEquals(results.size(), 1);
		assertEquals(results.get(0), "Action update is missing.");

	}

	@Test
	public void testMultipleActionsOk() {
		addLinkToRecord("read", Action.READ);
		addLinkToRecord("update", Action.UPDATE);
		addLinkToRecord("delete", Action.DELETE);
		String actions = "{\"actions\":[ \"read\", \"delete\",\"update\"]}";
		JsonValue jsonValue = jsonParser.parseString(actions);

		List<String> results = comparer.checkClientDataRecordContains(jsonValue);
		assertEquals(results.size(), 0);

	}
}
