/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;

public class DefinitionWriterTest {

	private DefinitionWriter writer;

	@BeforeMethod
	private void beforeMethod() {
		writer = new DefinitionWriter();
	}

	private DataGroupSpy createDataGroupSpy(String name) {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		return dataGroupSpy;
	}

	@Test
	public void writeOneGroupOnlyNameInData() throws Exception {
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);

		String expectedDefinition = """
				someGroup(group)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testOneGroupOneTextVariable() throws Exception {
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
		DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
				"textVariable");
		addChildrenToGroup(dataGroup, textVariable);

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);

		String expectedDefinition = """
				someGroup(group)
					someTextVariable(textVariable, 1-1, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	private void addChildrenToGroup(DataGroupSpy group, DataChild... textVariable) {
		group.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
		group.MRV.setDefaultReturnValuesSupplier("getChildren", () -> Arrays.asList(textVariable));
	}

	private DataAtomicSpy createAtomicUsingNameInDataAndType(String nameInData, String type) {
		DataAtomicSpy atomic = new DataAtomicSpy();
		addAttributeType(atomic, type);
		atomic.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		return atomic;
	}

	private void addAttributeType(DataAtomicSpy dataChild, String typeValue) {
		dataChild.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataChild.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(typeValue), "type");
	}

	private DataRecordLinkSpy createRecordLinkUsingNameInData(String nameInData) {
		DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
		recordLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
		return recordLink;
	}

	private DataResourceLinkSpy createResourceLinkUsingNameInData() {
		DataResourceLinkSpy resourceLink = new DataResourceLinkSpy();
		resourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someResourceLink");
		return resourceLink;
	}

	@Test
	public void testOneGroupOneRecordLink() throws Exception {
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
		DataRecordLinkSpy recordLink = createRecordLinkUsingNameInData("someRecordLink");
		addChildrenToGroup(dataGroup, recordLink);

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);

		String expectedDefinition = """
				someGroup(group)
					someRecordLink(recordLink, 1-1, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testOneGroupOneResourceLink() throws Exception {
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
		DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
		addChildrenToGroup(dataGroup, resourceLink);

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
		String expectedDefinition = """
				someGroup(group)
					someResourceLink(resourceLink, 1-1, noConstraint)""";

		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testNestedGroupsWithOneChildEach() throws Exception {
		DataGroupSpy childGroup2 = createDataGroupSpy("childGroup2");
		DataAtomicSpy textVariable2 = createAtomicUsingNameInDataAndType("someTextVariable2",
				"textVariable");
		addChildrenToGroup(childGroup2, textVariable2);
		DataGroupSpy childGroup = createDataGroupSpy("childGroup");
		DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
				"textVariable");
		addChildrenToGroup(childGroup, textVariable, childGroup2);
		DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
		addChildrenToGroup(dataGroup, resourceLink, childGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);

		String expectedDefinition = """
				someGroup(group)
					someResourceLink(resourceLink, 1-1, noConstraint)
					childGroup(group)
						someTextVariable(textVariable, 1-1, noConstraint)
						childGroup2(group)
							someTextVariable2(textVariable, 1-1, noConstraint)""";

		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testNestedGroupsOnSameLevelsWithOneChildEach() throws Exception {
		DataGroupSpy childGroup5 = createDataGroupSpy("childGroup5");
		DataAtomicSpy textVariable5 = createAtomicUsingNameInDataAndType("someTextVariable5",
				"textVariable");
		addChildrenToGroup(childGroup5, textVariable5);
		DataGroupSpy childGroup4 = createDataGroupSpy("childGroup4");
		DataAtomicSpy textVariable4 = createAtomicUsingNameInDataAndType("someTextVariable4",
				"textVariable");
		addChildrenToGroup(childGroup4, textVariable4);
		DataGroupSpy childGroup3 = createDataGroupSpy("childGroup3");
		DataAtomicSpy textVariable3 = createAtomicUsingNameInDataAndType("someTextVariable3",
				"textVariable");
		addChildrenToGroup(childGroup3, textVariable3);
		DataGroupSpy childGroup2 = createDataGroupSpy("childGroup2");
		DataAtomicSpy textVariable2 = createAtomicUsingNameInDataAndType("someTextVariable2",
				"textVariable");
		addChildrenToGroup(childGroup2, textVariable2);
		DataGroupSpy childGroup = createDataGroupSpy("childGroup");
		DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
				"textVariable");
		DataAtomicSpy textVariable6 = createAtomicUsingNameInDataAndType("someTextVariable6",
				"textVariable");
		DataAtomicSpy textVariable7 = createAtomicUsingNameInDataAndType("someTextVariable7",
				"textVariable");
		addChildrenToGroup(childGroup, textVariable, textVariable6, childGroup2, childGroup3,
				textVariable7);
		DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
		DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
		addChildrenToGroup(dataGroup, resourceLink, childGroup, childGroup4, childGroup5);

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);

		String expectedDefinition = """
				someGroup(group)
					someResourceLink(resourceLink, 1-1, noConstraint)
					childGroup(group)
						someTextVariable(textVariable, 1-1, noConstraint)
						someTextVariable6(textVariable, 1-1, noConstraint)
						childGroup2(group)
							someTextVariable2(textVariable, 1-1, noConstraint)
						childGroup3(group)
							someTextVariable3(textVariable, 1-1, noConstraint)
						someTextVariable7(textVariable, 1-1, noConstraint)
					childGroup4(group)
						someTextVariable4(textVariable, 1-1, noConstraint)
					childGroup5(group)
						someTextVariable5(textVariable, 1-1, noConstraint)""";

		assertEquals(definition, expectedDefinition);
	}

}
