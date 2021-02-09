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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.json.parser.JsonParseException;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.json.parser.org.OrgJsonParser;

public class ChildComparerTest {

	private ChildComparerImp childComparer;
	private JsonParser jsonParser;
	private ClientDataGroup dataGroup;

	@BeforeMethod
	public void setUp() {
		childComparer = new ChildComparerImp();
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("workoutName", "cirkelfys"));
		jsonParser = new OrgJsonParser();

	}

	@Test
	public void testCheckContainOKWhenOneChild() {
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertTrue(containsChildren);

	}

	@Test
	public void testContainOKWhenOneChild() {
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());

	}

	@Test
	public void testCheckContainOKWhenMoreThanOneChild() {
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("instructorId", "45"));

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertTrue(containsChildren);

	}

	@Test
	public void testContainOKWhenMoreThanOneChild() {
		dataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("instructorId", "45"));
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testCheckContainNotOKNoChildExistInDataGroup() {
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");

		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertFalse(containsChildren);
	}

	@Test
	public void testContainNotOKNoChildExistInDataGroup() {
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");

	}

	@Test
	public void testCheckContainOneButNotTheOther() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertFalse(containsChildren);

	}

	@Test
	public void testContainOneButNotTheOther() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData instructorId is missing.");

	}

	@Test
	public void testContainNone() {
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0), "Child with nameInData workoutName is missing.");
		assertEquals(errorMessages.get(1), "Child with nameInData instructorId is missing.");

	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "child must contain key: name")
	public void testJsonValueDoesNotContainName() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"NOTname\":\"workoutName\"}]}");
		childComparer.checkDataGroupContainsChildren(dataGroup, jsonValue);
	}

	@Test
	public void testJsonValueDoesNotContainNameInitalExceptionIsSentAlong() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"NOTname\":\"workoutName\"}]}");
		try {
			childComparer.checkDataGroupContainsChildren(dataGroup, jsonValue);

		} catch (Exception e) {
			assertTrue(e.getCause() instanceof JsonParseException);
		}
	}

	@Test
	public void testCheckCorrectValuesOKWhenOneAtomicChild() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());

	}

	@Test
	public void testCheckCorrectValuesOKWhenOneAtomicChild2() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());

	}

	@Test
	public void testCheckCorrectValuesNotOKWhenChildValueDiffers() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"NOTcirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData workoutName does not have the correct value "
						+ "(cirkelfys expected NOTcirkelfys).");
	}

	@Test
	public void testCheckCorrectValuesNotOKWhenChildTypeDiffers() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"children\":[]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData workoutName and type group is missing.");
	}

	@Test
	public void testCheckCorrectValuesOKWhenOneAtomicChildAndOneGroupChild() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testCorrectValuesNotOKWhenOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"NOTAnna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName does not have the correct value "
						+ "(Anna expected NOTAnna).");
	}

	@Test
	public void testCorrectValuesNotOKWhenOneGrandChildIsMissingOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("lastName", "Ledare"));
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"NOTAnna\"},{\"name\":\"NOTlastName\",\"value\":\"Ledare\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName does not have the correct value "
						+ "(Anna expected NOTAnna).");
		assertEquals(errorMessages.get(1),
				"Child with nameInData NOTlastName and type atomic is missing.");
	}

	@Test(expectedExceptions = JsonParseException.class, expectedExceptionsMessageRegExp = ""
			+ "child must contain key: name")
	public void testCheckCorrectValuesJsonValueDoesNotContainName() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"NOTname\":\"workoutName\"}]}");
		childComparer.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
	}

	@Test
	public void testCheckCorrectValuesJsonValueDoesNotContainNameInitalExceptionIsSentAlong() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"NOTname\":\"workoutName\"}]}");
		try {
			childComparer.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		} catch (Exception e) {
			assertTrue(e.getCause() instanceof JsonParseException);
		}
	}

	@Test
	public void testCheckContainOKWhenOneChildWithOneAttribute() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addAttributeByIdWithValue("type", "default");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"attributes\":{\"type\":\"default\"}}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertTrue(containsChildren);
	}

	@Test
	public void testCheckContainNOTOKWhenOneChildWithDifferentAttribute() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addAttributeByIdWithValue("type", "NOTdefault");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"attributes\":{\"type\":\"default\"}}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);

		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData instructorName is missing.");
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertFalse(containsChildren);
	}

	@Test
	public void testCheckContainOKWhenOneChildWithTwoAttributes() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addAttributeByIdWithValue("type", "default");
		instructorName.addAttributeByIdWithValue("other", "name");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"},{\"name\":\"lastName\",\"value\":\"Ledare\"}],\"attributes\":{\"type\":\"default\",\"other\":\"name\"}}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertTrue(containsChildren);
	}

	@Test
	public void testCheckContainNOTOKWhenOneChildWithOneSameOneDifferentAttribute() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addAttributeByIdWithValue("type", "default");
		instructorName.addAttributeByIdWithValue("other", "NOTname");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"},{\"name\":\"lastName\",\"value\":\"Ledare\"}],\"attributes\":{\"type\":\"default\",\"other\":\"name\"}}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);

		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData instructorName is missing.");
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertFalse(containsChildren);
	}

	@Test
	public void testCheckCorrectValuesOKWhenOneGroupChildWithRepeatIdAndRepeatIdInData() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.setRepeatId("0");
		dataGroup.addChild(instructorName);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		assertTrue(errorMessages.isEmpty());
	}

	private ClientDataGroup createChildDataGroupInstructorName() {
		ClientDataGroup instructorName = ClientDataGroup.withNameInData("instructorName");
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("firstName", "Anna"));
		return instructorName;
	}

	@Test
	public void testCheckNotOkWhenOneGroupChildWithNORepeatIdButRepeatIdInData() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData instructorName has NO repeatId.");
	}

	@Test
	public void testCheckNotOkWhenOneGroupChildWithRepeatIdButNORepeatIdInData() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.setRepeatId("0");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData instructorName should NOT have repeatId.");
	}

	@Test
	public void testCheckCorrectValuesNOtOKWhenOneGroupChildWithDifferentRepeatIds() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.setRepeatId("differentRepeatId");
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData instructorName does not have correct repeatId (differentRepeatId expected 0).");
	}

	@Test
	public void testCheckCorrectValuesOKWhenOneAtomicChildWithRepeatIdAndRepeatIdInData() {
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("firstName", "Anna");
		atomicChild.setRepeatId("0");
		dataGroup.addChild(atomicChild);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\",\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testCheckNotOkWhenOneAtomicChildWithNORepeatIdButRepeatIdInData() {
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("firstName", "Anna");
		dataGroup.addChild(atomicChild);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\",\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData firstName has NO repeatId.");
	}

	@Test
	public void testCheckNotOkWhenOneAtomicChildWithRepeatIdButNORepeatIdInData() {
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("firstName", "Anna");
		atomicChild.setRepeatId("0");
		dataGroup.addChild(atomicChild);

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName should NOT have repeatId.");
	}

	@Test
	public void testCheckCorrectValuesNOtOKWhenOneAtomicChildWithDifferentRepeatIds() {
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("firstName", "Anna");
		atomicChild.setRepeatId("differentRepeatId");
		dataGroup.addChild(atomicChild);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\",\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData firstName does not have correct repeatId (differentRepeatId expected 0).");
	}
}
