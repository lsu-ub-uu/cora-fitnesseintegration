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

	// testRepeatableAtomicTwoPresentMatchOnOne
	// testValuesRepeatableAtomicMatch
	// testValuesNonRepeatableAtomicInsideGroupNoMatchWrongName
	@Test
	public void testValuesNonRepeatableAtomicChild() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());

	}

	// @Test
	// public void testCheckCorrectValuesOKWhenOneAtomicChild2() {
	// JsonValue jsonValue = jsonParser
	// .parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
	// List<String> errorMessages = childComparer
	// .checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
	// assertTrue(errorMessages.isEmpty());
	// }

	@Test
	public void testValuesNonRepeatableAtomicChildWrongName() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"NOT_workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData NOT_workoutName and value cirkelfys.");
	}

	@Test
	public void testValuesNonRepeatableAtomicChildWrongValue() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\",\"value\":\"NOTcirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData workoutName and value NOTcirkelfys.");
	}

	@Test
	public void testValuesNonRepeatableAtomicChildExtraRepeatId() {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"repeatId\":\"0\",\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		// assertEquals(errorMessages.get(0),
		// "Did not find a match for child with nameInData workoutName and value NOTcirkelfys.");
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData workoutName and value cirkelfys and repeatId 0.");
	}

	@Test
	public void testValuesNonRepeatableAtomicChildWithRepeatIdExtraRepeatId_WEIRD_CASE() {
		dataGroup = ClientDataGroup.withNameInData("someDataGroup");
		ClientDataAtomic atomic = ClientDataAtomic.withNameInDataAndValue("workoutName",
				"cirkelfys");
		atomic.setRepeatId("0");
		dataGroup.addChild(atomic);
		// adding stuff to exercise weird code...
		ClientDataAtomic atomic2 = ClientDataAtomic.withNameInDataAndValue("workoutName",
				"cirkelfys");
		// atomic2.setRepeatId("1");
		dataGroup.addChild(atomic2);

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		// assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.size(), 0);
		// assertEquals(errorMessages.get(0),
		// "Did not find a match for child with nameInData workoutName and value NOTcirkelfys.");
		// assertEquals(errorMessages.get(0),
		// "Child with nameInData workoutName and type atomic and repeatId 0 is missing.");
		// TODO:better errormessage
	}

	// no repeatid
	@Test
	public void testValuesNonRepeatableAtomicWithRepeatIdChildWrongName() {
		ClientDataAtomic atomic = ClientDataAtomic.withNameInDataAndValue("myName", "myValue");
		atomic.setRepeatId("myRepeatId");
		dataGroup.addChild(atomic);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"NOT_myName\",\"value\":\"myValue\",\"repeatId\":\"myRepeatId\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData NOT_myName and value myValue and repeatId myRepeatId.");
	}

	@Test
	public void ttestValuesNonRepeatableAtomicWithRepeatIdWrongValue() {
		ClientDataAtomic atomic = ClientDataAtomic.withNameInDataAndValue("myName", "myValue");
		atomic.setRepeatId("myRepeatId");
		dataGroup.addChild(atomic);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"myName\",\"value\":\"NOT_myValue\",\"repeatId\":\"myRepeatId\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData myName and value NOT_myValue and repeatId myRepeatId.");
	}

	@Test
	public void testValuesNonRepeatableAtomicWithRepeatIdWrongRepeatId() {
		ClientDataAtomic atomic = ClientDataAtomic.withNameInDataAndValue("myName", "myValue");
		atomic.setRepeatId("myRepeatId");
		dataGroup.addChild(atomic);
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"myName\",\"value\":\"myValue\",\"repeatId\":\"NOT_myRepeatId\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData myName and value myValue and repeatId NOT_myRepeatId.");
	}

	@Test
	public void testValuesNonRepeatableAtomicWithRepeatIdNoRepeatId() {
		ClientDataAtomic atomic = ClientDataAtomic.withNameInDataAndValue("myName", "myValue");
		atomic.setRepeatId("myRepeatId");
		dataGroup.addChild(atomic);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"myName\",\"value\":\"myValue\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData myName and value myValue.");
	}
	// no repeatid

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
	public void testValuesOneAtomicChildAndOneGroupChildWithAtomicChild() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":["
						+ "{\"name\":\"firstName\",\"value\":\"Anna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testValuesNotOKWhenOneGrandChildNameDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":["
						+ "{\"name\":\"NOT_firstName\",\"value\":\"Anna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData NOT_firstName and value Anna.");
	}

	@Test
	public void testValuesNotOKWhenOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":["
						+ "{\"name\":\"firstName\",\"value\":\"NOTAnna\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName and value NOTAnna.");
	}

	@Test
	public void testValuesNotOKWhenOneGrandChildExtraRepeatId() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":["
						+ "{\"name\":\"firstName\",\"value\":\"Anna\", \"repeatId\":\"0\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName and value Anna and repeatId 0.");
	}

	@Test
	public void testValuesNotOKWhenOneGrandChildIsMissingOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addChild(ClientDataAtomic.withNameInDataAndValue("lastName", "Ledare"));
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":["
						+ "{\"name\":\"firstName\",\"value\":\"NOTAnna\"},"
						+ "{\"name\":\"NOTlastName\",\"value\":\"Ledare\"}]}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName and value NOTAnna.");
		assertEquals(errorMessages.get(1),
				"Did not find a match for child with nameInData NOTlastName and value Ledare.");
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

	@Test
	public void testCheckCorrectValuesOKWhenOneGroupChildWithRepeatIdAndRepeatIdInDataMultiple() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.setRepeatId("1");
		dataGroup.addChild(instructorName);

		ClientDataGroup instructorName2 = createChildDataGroupInstructorName();
		instructorName2.setRepeatId("0");
		dataGroup.addChild(instructorName2);

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
		assertEquals(errorMessages.get(0),
				"Child with nameInData instructorName and type group and repeatId 0 is missing.");
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
				"Child with nameInData instructorName and type group is missing.");
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
				"Child with nameInData instructorName and type group and repeatId 0 is missing.");
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
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName and value Anna and repeatId 0.");
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
				"Did not find a match for child with nameInData firstName and value Anna.");
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
				"Did not find a match for child with nameInData firstName and value Anna and repeatId 0.");
	}

	// from here
	@Test
	public void testValuesRepeatableAtomicMatch() {
		createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		String changingTestDomainPart = "{\"repeatId\":\"0\",\"name\":\"domain\",\"value\":\"kth\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testValuesRepeatableAtomicTwoPresentMatchOnOne() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"repeatId\":\"1\",\"name\":\"domain\",\"value\":\"test\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testValuesRepeatableAtomicTwoPresentNoMatchEmptyRepeatId() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"repeatId\":\"\",\"name\":\"domain\",\"value\":\"test\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData domain and value test and repeatId .");
	}

	@Test
	public void testValuesRepeatableAtomicTwoPresentNoMatchNoRepeatId() {
		// ClientDataGroup recordInfo =
		// createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		dataGroup.addChild(recordInfo);
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild.setRepeatId("1");
		recordInfo.addChild(atomicChild);

		String changingTestDomainPart = "{\"name\":\"domain\",\"value\":\"test\"}";

		JsonValue jsonValue1 = jsonParser.parseString("{\"children\":[{\"children\":["
				+ changingTestDomainPart + "],\"name\":\"recordInfo\"}],\"name\":\"person\"}");
		JsonValue jsonValue = jsonValue1;
		List<String> errorMessages1 = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		List<String> errorMessages = errorMessages1;
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData domain and value test.");
	}

	@Test
	public void testValuesRepeatableAtomicThreePresentMatchOnOne() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("domain", "kth");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		ClientDataAtomic atomicChild3 = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild3.setRepeatId("2");
		recordInfo.addChild(atomicChild3);

		String changingTestDomainPart = "{\"repeatId\":\"1\",\"name\":\"domain\",\"value\":\"kth\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testValuesRepeatableAtomicThreePresentMatchOnTwo() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("domain", "kth");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		ClientDataAtomic atomicChild3 = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild3.setRepeatId("2");
		recordInfo.addChild(atomicChild3);

		String changingTestDomainPart = "{\"repeatId\":\"1\",\"name\":\"domain\",\"value\":\"kth\"}"
				+ ",{\"repeatId\":\"0\",\"name\":\"domain\",\"value\":\"kth\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testValuesRepeatableAtomicThreePresentNoMatchOnTwo() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("domain", "kth");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		ClientDataAtomic atomicChild3 = ClientDataAtomic.withNameInDataAndValue("domain", "test");
		atomicChild3.setRepeatId("2");
		recordInfo.addChild(atomicChild3);

		String changingTestDomainPart = "{\"repeatId\":\"NOT_0\",\"name\":\"domain\",\"value\":\"kth\"}"
				+ ",{\"repeatId\":\"0\",\"name\":\"domain\",\"value\":\"NOT_kth\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData domain and value kth and repeatId NOT_0.");
		assertEquals(errorMessages.get(1),
				"Did not find a match for child with nameInData domain and value NOT_kth and repeatId 0.");
	}

	private ClientDataGroup createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0() {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		dataGroup.addChild(recordInfo);
		addKthDomainPartWithRepeatId0(recordInfo);
		return recordInfo;
	}

	private void addKthDomainPartWithRepeatId0(ClientDataGroup recordInfo) {
		ClientDataAtomic atomicChild = ClientDataAtomic.withNameInDataAndValue("domain", "kth");
		atomicChild.setRepeatId("0");
		recordInfo.addChild(atomicChild);
	}

	private List<String> compareChangingPartToExistingDataGroups(String changingTestDomainPart) {
		JsonValue jsonValue = createJsonValueForRecordInfoUsingChangingPart(changingTestDomainPart);
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		return errorMessages;
	}

	private JsonValue createJsonValueForRecordInfoUsingChangingPart(String changingTestDomainPart) {
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"children\":[{\"repeatId\":\"0\",\"name\":\"domain\",\"value\":\"kth\"},"
						+ changingTestDomainPart
						+ "],\"name\":\"recordInfo\"}],\"name\":\"person\"}");
		return jsonValue;
	}

	@Test
	public void testValuesNonRepeatableAtomicInsideGroupMatch() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("myName",
				"myValue");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"name\":\"myName\",\"value\":\"myValue\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testValuesNonRepeatableAtomicInsideGroupNoMatchWrongName() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("myName",
				"myValue");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"name\":\"NOT_myName\",\"value\":\"myValue\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData NOT_myName and value myValue.");
	}

	@Test
	public void testValuesNonRepeatableAtomicInsideGroupNoMatchWrongValue() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("myName",
				"myValue");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"name\":\"myName\",\"value\":\"NOT_myValue\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData myName and value NOT_myValue.");
	}

	@Test
	public void testValuesNonRepeatableAtomicInsideGroupNoMatchExtraRepeatId() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataAtomic.withNameInDataAndValue("myName",
				"myValue");
		recordInfo.addChild(atomicChild2);

		String changingTestDomainPart = "{\"repeatId\":\"0\",\"name\":\"myName\",\"value\":\"myValue\"}";

		List<String> errorMessages = compareChangingPartToExistingDataGroups(
				changingTestDomainPart);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData myName and value myValue and repeatId 0.");
	}

	// to here
	@Test
	public void testLinksRepeatId() {
		createAndAddDomainPart("authority-person:106:kth", "0");
		createAndAddDomainPart("authority-person:106:test", "1");
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"repeatId\":\"0\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:kth\"}],\"name\":\"personDomainPart\"},{\"repeatId\":\"1\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:test\"}],\"name\":\"personDomainPart\"}],\"name\":\"person\"}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 0);
	}

	private void createAndAddDomainPart(String linkedRecordId, String repeatId) {
		ClientDataGroup domainPart = ClientDataGroup.asLinkWithNameInDataAndTypeAndId(
				"personDomainPart", "personDomainPart", linkedRecordId);
		domainPart.setRepeatId(repeatId);
		dataGroup.addChild(domainPart);
	}

	@Test
	public void testLinksRepeatIdNoMatch() {
		createAndAddDomainPart("authority-person:106:kth", "0");
		createAndAddDomainPart("authority-person:106:test", "1");
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"repeatId\":\"0\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:kth\"}],\"name\":\"personDomainPart\"},{\"repeatId\":\"3\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:test\"}],\"name\":\"personDomainPart\"}],\"name\":\"person\"}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
	}
}
