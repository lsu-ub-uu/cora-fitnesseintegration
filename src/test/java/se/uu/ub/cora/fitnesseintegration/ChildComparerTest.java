/*
 * Copyright 2020, 2022 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
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
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		dataGroup.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("workoutName", "cirkelfys"));
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
		dataGroup.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("instructorId", "45"));

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertTrue(containsChildren);

	}

	@Test
	public void testContainOKWhenMoreThanOneChild() {
		dataGroup.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("instructorId", "45"));
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testCheckContainNotOKNoChildExistInDataGroup() {
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[{\"name\":\"workoutName\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData workoutName.");

		boolean containsChildren = childComparer.dataGroupContainsChildren(dataGroup, jsonValue);
		assertFalse(containsChildren);
	}

	@Test
	public void testContainNotOKNoChildWithCorrectTypeExistInDataGroup() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"children\":[]}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData workoutName.");

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
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData instructorId.");

	}

	@Test
	public void testContainNone() {
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorId\"}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 2);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData workoutName.");
		assertEquals(errorMessages.get(1),
				"Did not find a match for child with nameInData instructorId.");
	}

	@Test
	public void testCheckContainOneGrandChildTestCorrect() {
		ClientDataGroup instructorName = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		instructorName.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("firstName",
				"someUnimportantValue"));
		dataGroup.addChild(instructorName);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\"}]}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testCheckContainOneAtomicGrandChildTestMissingGrandChild() {
		ClientDataGroup instructorName = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		instructorName.setRepeatId("0");
		dataGroup.addChild(instructorName);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"workoutName\"},{\"name\":\"instructorName\",\"repeatId\":\"0\",\"children\":[{\"name\":\"firstName\"}]}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName.");
	}

	@Test
	public void testCompareGroupWithAttributes() {
		ClientDataGroup topGroup = ClientDataProvider.createGroupUsingNameInData("topGroup");
		ClientDataGroup childGroupLevel1 = ClientDataProvider
				.createGroupUsingNameInData("childGroupLevel1");
		childGroupLevel1.addAttributeByIdWithValue("exampleAttributeChoice", "one");
		ClientDataAtomic atomicChild1 = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("atomicChild1", "valueOne");
		childGroupLevel1.addChild(atomicChild1);
		topGroup.addChild(childGroupLevel1);

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"childGroupLevel1\", "
						+ "\"attributes\":{\"exampleAttributeChoice\":\"one\"},"
						+ "\"children\":[{\"name\":\"atomicChild1\",\"value\":\"valueOne\"}]}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(topGroup, jsonValue);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testCompareGroupWithAttributesDifferent() {
		ClientDataGroup topGroup = ClientDataProvider.createGroupUsingNameInData("topGroup");
		ClientDataGroup childGroupLevel1 = ClientDataProvider
				.createGroupUsingNameInData("childGroupLevel1");
		childGroupLevel1.addAttributeByIdWithValue("exampleAttributeChoice", "oneNOT");
		ClientDataAtomic atomicChild1 = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("atomicChild1", "valueOne");
		childGroupLevel1.addChild(atomicChild1);
		topGroup.addChild(childGroupLevel1);

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"childGroupLevel1\", \"attributes\":{"
						+ "\"exampleAttributeChoiceOne\":\"one\", \"exampleAttributeChoiceTwo\":\"two\"},"
						+ "\"children\":[{\"name\":\"atomicChild1\",\"value\":\"valueOne\"}]}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(topGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), "Child with nameInData childGroupLevel1 "
				+ "and type group and attributes [exampleAttributeChoiceTwo:two, exampleAttributeChoiceOne:one] is missing.");
	}

	@Test
	public void testCompareAtomicWithAttributes() {
		ClientDataGroup aDataGroup = ClientDataProvider.createGroupUsingNameInData("aDataGroup");
		ClientDataAtomic selectableAttribute = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("selectableAttribute1", "valueOne");
		selectableAttribute.addAttributeByIdWithValue("exampleAttributeChoice", "one");
		aDataGroup.addChild(selectableAttribute);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"selectableAttribute1\",\"value\":\"valueOne\","
						+ "\"attributes\":{\"exampleAttributeChoice\":\"one\"}}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(aDataGroup, jsonValue);
		assertEquals(errorMessages.size(), 0);
	}

	@Test
	public void testCompareAtomicWithAttributesAndAttributesMissing() {
		ClientDataGroup aDataGroup = ClientDataProvider.createGroupUsingNameInData("aDataGroup");
		ClientDataAtomic selectableAttribute = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("selectableAttribute1", "valueOne");
		aDataGroup.addChild(selectableAttribute);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"selectableAttribute1\",\"value\":\"valueOne\","
						+ "\"attributes\":{\"exampleAttributeChoice\":\"one\"}}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(aDataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
	}

	@Test
	public void testCompareAtomicWithAttributesNoMatchingAttributes() {
		ClientDataGroup aDataGroup = ClientDataProvider.createGroupUsingNameInData("aDataGroup");
		ClientDataAtomic selectableAttribute = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("selectableAttribute1", "valueOne");
		selectableAttribute.addAttributeByIdWithValue("exampleAttributeChoice", "one");
		aDataGroup.addChild(selectableAttribute);

		JsonValue jsonValue = jsonParser.parseString(

				"{\"children\":[{\"name\":\"selectableAttribute1\",\"value\":\"valueOne\"}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(aDataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
	}

	@Test
	public void testCompareAtomicWithAttributesNoMatchingAttributeName() {
		ClientDataGroup aDataGroup = ClientDataProvider.createGroupUsingNameInData("aDataGroup");
		ClientDataAtomic selectableAttribute = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("selectableAttribute1", "valueOne");
		selectableAttribute.addAttributeByIdWithValue("exampleAttributeChoiceWrong", "one");
		aDataGroup.addChild(selectableAttribute);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"selectableAttribute1\",\"value\":\"valueOne\","
						+ "\"attributes\":{\"exampleAttributeChoice\":\"one\"}}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(aDataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
	}

	@Test
	public void testCompareAtomicWithAttributesNoMatchingAttributeValue() {
		ClientDataGroup aDataGroup = ClientDataProvider.createGroupUsingNameInData("aDataGroup");
		ClientDataAtomic selectableAttribute = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("selectableAttribute1", "valueOne");
		selectableAttribute.addAttributeByIdWithValue("exampleAttributeChoice", "oneWrong");
		aDataGroup.addChild(selectableAttribute);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"selectableAttribute1\",\"value\":\"valueOne\","
						+ "\"attributes\":{\"exampleAttributeChoice\":\"one\"}}]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(aDataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
	}

	@Test
	public void testCheckContainOneGroupGrandChildTestMissingGrandChild() {
		ClientDataGroup instructorName = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		dataGroup.addChild(instructorName);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"children\":[]}]}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData firstName.");
	}

	@Test
	public void testCheckContainOneGroupGrandChildTestMissingOneChildAndGrandChild() {
		ClientDataGroup instructorName = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		instructorName.setRepeatId("0");
		dataGroup.addChild(instructorName);
		ClientDataGroup instructorName1 = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		instructorName1.setRepeatId("1");
		dataGroup.addChild(instructorName1);

		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"NOTworkoutName\"},{\"name\":\"instructorName\",\"repeatId\":\"0\",\"children\":[{\"name\":\"firstName\",\"children\":[]}]},{\"name\":\"instructorName\",\"repeatId\":\"NOT1\",\"children\":[]}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 3);
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData NOTworkoutName.");
		assertEquals(errorMessages.get(1),
				"Did not find a match for child with nameInData firstName.");
		assertEquals(errorMessages.get(2),
				"Did not find a match for child with nameInData instructorName and repeatId NOT1.");
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
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData instructorName and attributes [type:default].");
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
				"{\"children\":[{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"attributes\":{\"type\":\"default\",\"other\":\"name\"}}]}");
		List<String> errorMessages = childComparer.checkDataGroupContainsChildren(dataGroup,
				jsonValue);
		assertEquals(errorMessages.size(), 0);
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
		assertEquals(errorMessages.get(0),
				"Did not find a match for child with nameInData instructorName and attributes [type:default, other:name].");
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

	@Test
	public void testValuesNonRepeatableAtomicChildTestProblematicValue() {
		String exampleWithProblematicCharacter = "–-";
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		dataGroup.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("workoutName",
				exampleWithProblematicCharacter));

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\""
						+ exampleWithProblematicCharacter + "\"}]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestProblematicValueAsUnicode() {
		String exampleWithProblematicCharacter = "\u2013-";
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		dataGroup.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("workoutName",
				exampleWithProblematicCharacter));

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\""
						+ exampleWithProblematicCharacter + "\"}]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestProblematicValueAsUnicode2() {
		String exampleWithProblematicCharacterUnicode = "–-";
		String exampleWithProblematicCharacter = "\u2013-";
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		dataGroup.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("workoutName",
				exampleWithProblematicCharacterUnicode));

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\""
						+ exampleWithProblematicCharacter + "\"}]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestProblematicValueAsUnicode3() {
		String exampleWithProblematicCharacterUnicode = "–-";
		String exampleWithProblematicCharacter = "\u2013-";
		dataGroup = ClientDataProvider.createGroupUsingNameInData("someDataGroup");
		dataGroup.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("workoutName",
				exampleWithProblematicCharacter));

		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\""
						+ exampleWithProblematicCharacterUnicode + "\"}]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestCorrect() {
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestWrongName() {
		String json = "{\"name\":\"NOT_workoutName\",\"value\":\"cirkelfys\"}";
		String error = "Did not find a match for child with nameInData NOT_workoutName and value cirkelfys.";
		testValuesNonRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestWrongValue() {
		String json = "{\"name\":\"workoutName\",\"value\":\"NOT_cirkelfys\"}";
		String error = "Did not find a match for child with nameInData workoutName and value NOT_cirkelfys.";
		testValuesNonRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestExtraRepeatId() {
		String json = "{\"name\":\"workoutName\",\"value\":\"cirkelfys\""
				+ ",\"repeatId\":\"EXTRA_repeatId\"}";
		String error = "Did not find a match for child with nameInData workoutName and value cirkelfys"
				+ " and repeatId EXTRA_repeatId.";
		testValuesNonRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesNonRepeatableAtomicChildTestChildTypeDiffers() {
		String json = "{\"name\":\"workoutName\",\"children\":[]}";
		String error = "Child with nameInData workoutName and type group is missing.";
		testValuesNonRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	private void testValuesNonRepeatableAtomicWithRepeatIdJsonResultsInOneError(String jsonString,
			String error) {
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + jsonString + "]}");
		assertOneErrorWithMessage(jsonValue, error);
	}

	private void assertNoErrorMessagesWhenContainsWithValues(JsonValue jsonValue) {
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 0);
	}

	private void assertOneErrorWithMessage(JsonValue jsonValue, String expectedError) {
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0), expectedError);
	}

	@Test
	public void testTwoAtomicChildrenTestCorrect() {
		addRepeatableAtomic();
		String firstChild = "{\"name\":\"workoutName\",\"value\":\"cirkelfys\"}";
		String secondChild = "{\"name\":\"myName\",\"value\":\"myValue\",\"repeatId\":\"myRepeatId\"}";
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[" + firstChild + "," + secondChild + "]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesRepeatableAtomicWithRepeatIdTestCorrect() {
		addRepeatableAtomic();
		JsonValue jsonValue = jsonParser.parseString(
				"{\"children\":[{\"name\":\"myName\",\"value\":\"myValue\",\"repeatId\":\"myRepeatId\"}]}");

		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesRepeatableAtomicWithRepeatIdChildTestWrongName() {
		String json = "{\"name\":\"NOT_myName\",\"value\":\"myValue\",\"repeatId\":\"myRepeatId\"}";
		String error = "Did not find a match for child with nameInData NOT_myName and value myValue "
				+ "and repeatId myRepeatId.";
		testValuesRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesRepeatableAtomicWithRepeatIdTestWrongValue() {
		String json = "{\"name\":\"myName\",\"value\":\"NOT_myValue\",\"repeatId\":\"myRepeatId\"}";
		String error = "Did not find a match for child with nameInData myName and value NOT_myValue "
				+ "and repeatId myRepeatId.";
		testValuesRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesRepeatableAtomicWithRepeatIdTestWrongRepeatId() {
		String json = "{\"name\":\"myName\",\"value\":\"myValue\",\"repeatId\":\"NOT_myRepeatId\"}";
		String error = "Did not find a match for child with nameInData myName and value myValue "
				+ "and repeatId NOT_myRepeatId.";
		testValuesRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesRepeatableAtomicWithRepeatIdTestNoRepeatId() {
		addRepeatableAtomic();
		String json = "{\"name\":\"myName\",\"value\":\"myValue\"}";
		// String error = "Did not find a match for child with nameInData myName and value
		// myValue.";
		// testValuesRepeatableAtomicWithRepeatIdJsonResultsInOneError(json, error);
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + json + "]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	private void testValuesRepeatableAtomicWithRepeatIdJsonResultsInOneError(String jsonString,
			String error) {
		addRepeatableAtomic();
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + jsonString + "]}");
		assertOneErrorWithMessage(jsonValue, error);
	}

	private void addRepeatableAtomic() {
		ClientDataAtomic atomic = ClientDataProvider.createAtomicUsingNameInDataAndValue("myName",
				"myValue");
		atomic.setRepeatId("myRepeatId");
		dataGroup.addChild(atomic);
	}

	@Test
	public void testValuesOneAtomicChildAsGrandChildTestCorrect() {
		String json = "{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}]}";

		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);

		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + json + "]}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesOneAtomicChildAsGrandChildTestWrongName() {
		String json = "{\"name\":\"NOT_firstName\",\"value\":\"Anna\"}";
		String error = "Did not find a match for child with nameInData NOT_firstName and value Anna.";
		testValuesOneAtomicChildAsGrandChildJsonResultsInOneError(json, error);
	}

	private void testValuesOneAtomicChildAsGrandChildJsonResultsInOneError(String jsonString,
			String error) {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser.parseString("{\"children\":["
				+ "{\"name\":\"instructorName\",\"children\":[" + jsonString + "]}]}");
		assertOneErrorWithMessage(jsonValue, error);
	}

	@Test
	public void testValuesOneAtomicChildAsGrandChildTestWrongValue() {
		String json = "{\"name\":\"firstName\",\"value\":\"NOT_Anna\"}";
		String error = "Did not find a match for child with nameInData firstName and value NOT_Anna.";
		testValuesOneAtomicChildAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test(enabled = false)
	public void testValuesOneAtomicChildAsGrandChildTestExtraRepeatId() {
		String json = "{\"name\":\"firstName\",\"value\":\"Anna\", \"repeatId\":\"EXTRA_repeatId\"}";
		String error = "Did not find a match for child with nameInData firstName and value Anna and "
				+ "repeatId EXTRA_repeatId.";
		testValuesOneAtomicChildAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestCorrect() {
		createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		String changingTestDomainPart = "{\"repeatId\":\"0\",\"name\":\"domain\",\"value\":\"kth\"}";
		JsonValue jsonValue = createJsonValueForRecordInfoUsingChangingPart(changingTestDomainPart);

		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestWrongName() {
		String json = "{\"name\":\"NOT_firstName\",\"value\":\"Anna\",\"repeatId\":\"0\"}";
		String error = "Did not find a match for child with nameInData NOT_firstName and value Anna"
				+ " and repeatId 0.";
		testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestWrongValue() {
		String json = "{\"name\":\"firstName\",\"value\":\"NOT_Anna\",\"repeatId\":\"0\"}";
		String error = "Did not find a match for child with nameInData firstName and value NOT_Anna"
				+ " and repeatId 0.";
		testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestWrongRepeatId() {
		String json = "{\"name\":\"firstName\",\"value\":\"Anna\", \"repeatId\":\"NOT_repeatId\"}";
		String error = "Did not find a match for child with nameInData firstName and value Anna and "
				+ "repeatId NOT_repeatId.";
		testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestEmptyRepeatId() {
		String json = "{\"name\":\"firstName\",\"value\":\"Anna\", \"repeatId\":\"\"}";
		String error = "Did not find a match for child with nameInData firstName and value Anna and "
				+ "repeatId .";
		testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(json, error);
	}

	@Test
	public void testValuesOneAtomicChildWithRepeatIdAsGrandChildTestMissingRepeatId() {
		String json = "{\"name\":\"firstName\",\"value\":\"Anna\"}";
		String error = "Did not find a match for child with nameInData firstName and value Anna.";
		testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(json, error);
	}

	private void testValuesOneAtomicChildWithRepeatIdAsGrandChildJsonResultsInOneError(
			String jsonString, String error) {
		createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		JsonValue jsonValue = createJsonValueForRecordInfoUsingChangingPart(jsonString);
		assertOneErrorWithMessage(jsonValue, error);
	}

	@Test
	public void testValuesNotOKWhenOneGrandChildIsMissingOneGrandChildValueDiffers() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("lastName", "Ledare"));
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
	public void testValuesTwoGroupChildWithRepeatIdTestCorrect() {
		createAndAddChildDataGroupInstructorNameWithRepeatId("1");
		createAndAddChildDataGroupInstructorNameWithRepeatId("0");

		JsonValue jsonValue = jsonParser.parseString("{\"children\":["
				+ "{\"name\":\"instructorName\",\"children\":[],\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		assertTrue(errorMessages.isEmpty());
	}

	private void createAndAddChildDataGroupInstructorNameWithRepeatId(String repeatId) {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		instructorName.setRepeatId(repeatId);
		dataGroup.addChild(instructorName);
	}

	private ClientDataGroup createChildDataGroupInstructorName() {
		ClientDataGroup instructorName = ClientDataProvider
				.createGroupUsingNameInData("instructorName");
		instructorName.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("firstName", "Anna"));
		return instructorName;
	}

	@Test
	public void testValuesOneGroupChildWithNORepeatIdTestExtraRepeatId() {
		ClientDataGroup instructorName = createChildDataGroupInstructorName();
		dataGroup.addChild(instructorName);
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},"
						+ "{\"name\":\"instructorName\",\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"}],\"repeatId\":\"0\"}]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData instructorName and type group and repeatId 0 is missing.");
	}

	@Test
	public void testValuesTwoGroupChildWithRepeatIdTestNoRepeatIdInData() {
		String json = "{\"name\":\"instructorName\",\"children\":[]}";

		createAndAddChildDataGroupInstructorNameWithRepeatId("1");
		createAndAddChildDataGroupInstructorNameWithRepeatId("0");

		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + json + "]}");

		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);

		assertTrue(errorMessages.isEmpty());
	}

	@Test
	public void testValuesTwoGroupChildWithRepeatIdTestWrongRepeatIds() {
		String json = "{\"name\":\"instructorName\",\"children\":[], \"repeatId\":\"NOT_0\"}";
		String error = "Child with nameInData instructorName and type group and repeatId NOT_0 is missing.";
		testValuesTwoGroupChildWithRepeatIdJsonResultsInOneError(json, error);
	}

	private void testValuesTwoGroupChildWithRepeatIdJsonResultsInOneError(String jsonString,
			String error) {
		createAndAddChildDataGroupInstructorNameWithRepeatId("1");
		createAndAddChildDataGroupInstructorNameWithRepeatId("0");
		JsonValue jsonValue = jsonParser.parseString("{\"children\":[" + jsonString + "]}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertOneErrorWithMessage(jsonValue, error);
	}

	@Test(enabled = false)
	public void testValuesRepeatableAtomicThreePresentNoMatchOnTwo() {
		ClientDataGroup recordInfo = createRecordInfoInStandardDataGroupWithDomainKTHWithRepeatId0();

		ClientDataAtomic atomicChild2 = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("domain", "kth");
		atomicChild2.setRepeatId("1");
		recordInfo.addChild(atomicChild2);

		ClientDataAtomic atomicChild3 = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("domain", "test");
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
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		dataGroup.addChild(recordInfo);
		addKthDomainPartWithRepeatId0(recordInfo);
		return recordInfo;
	}

	private void addKthDomainPartWithRepeatId0(ClientDataGroup recordInfo) {
		ClientDataAtomic atomicChild = ClientDataProvider
				.createAtomicUsingNameInDataAndValue("domain", "kth");
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
	public void testLinksRepeatId() {
		createAndAddDomainPart("authority-person:106:kth", "0");
		createAndAddDomainPart("authority-person:106:test", "1");
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"repeatId\":\"0\",\"children\":["
						+ "{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:kth\"}"
						+ "],\"name\":\"personDomainPart\"}," + "{\"repeatId\":\"1\",\"children\":["
						+ "{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:test\"}"
						+ "],\"name\":\"personDomainPart\"}],\"name\":\"person\"}");
		assertNoErrorMessagesWhenContainsWithValues(jsonValue);
	}

	private void createAndAddDomainPart(String linkedRecordId, String repeatId) {
		ClientDataRecordLink domainPart = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("personDomainPart", "personDomainPart",
						linkedRecordId);
		domainPart.setRepeatId(repeatId);
		dataGroup.addChild(domainPart);
	}

	@Test
	public void testLinksRepeatIdNoMatch() {
		createAndAddDomainPart("authority-person:106:kth", "0");
		createAndAddDomainPart("authority-person:106:test", "1");
		JsonValue jsonValue = jsonParser
				.parseString("{\"children\":[{\"repeatId\":\"0\",\"children\":["
						+ "{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:kth\"}"
						+ "],\"name\":\"personDomainPart\"},{\"repeatId\":\"3\",\"children\":["
						+ "{\"name\":\"linkedRecordType\",\"value\":\"personDomainPart\"},"
						+ "{\"name\":\"linkedRecordId\",\"value\":\"authority-person:106:test\"}"
						+ "],\"name\":\"personDomainPart\"}],\"name\":\"person\"}");
		List<String> errorMessages = childComparer
				.checkDataGroupContainsChildrenWithCorrectValues(dataGroup, jsonValue);
		assertEquals(errorMessages.size(), 1);
		assertEquals(errorMessages.get(0),
				"Child with nameInData personDomainPart and type group and repeatId 3 is missing.");

	}
}
