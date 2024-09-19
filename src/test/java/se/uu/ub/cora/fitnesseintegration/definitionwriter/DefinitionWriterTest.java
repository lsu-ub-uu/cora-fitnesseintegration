package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;

public class DefinitionWriterTest {

	private DefinitionWriter writer;
	private DataGroupSpy dataGroup;

	@BeforeMethod
	private void beforeMethod() {
		dataGroup = createDataGroupSpy("someGroup");

		writer = new DefinitionWriter();
	}

	private DataGroupSpy createDataGroupSpy(String name) {
		DataGroupSpy dataGroupSpy = new DataGroupSpy();
		dataGroupSpy.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> name);
		return dataGroupSpy;
	}

	@Test
	public void writeOneGroupOnlyNameInData() throws Exception {
		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
		String expectedDefinition = """
				someGroup(group)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testOneGroupOneTextVariable() throws Exception {
		DataAtomicSpy textVariable = new DataAtomicSpy();
		textVariable.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someTextVariable");
		dataGroup.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren", () -> List.of(textVariable));

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
		String expectedDefinition = """
				someGroup(group)
					someTextVariable(textVariable, 1-1, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testOneGroupOneRecordLink() throws Exception {
		DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
		recordLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someRecordLink");
		dataGroup.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren", () -> List.of(recordLink));

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
		String expectedDefinition = """
				someGroup(group)
					someRecordLink(recordLink, 1-1, noConstraint)""";

		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testOneGroupOneResourceLink() throws Exception {
		DataResourceLinkSpy resourceLink = new DataResourceLinkSpy();
		resourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someResourceLink");
		dataGroup.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren", () -> List.of(resourceLink));

		String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
		String expectedDefinition = """
				someGroup(group)
					someResourceLink(resourceLink, 1-1, noConstraint)""";

		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testNestedGroupsWithOneChildEach() throws Exception {
		DataResourceLinkSpy resourceLink = new DataResourceLinkSpy();
		resourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someResourceLink");
		dataGroup.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);

		DataGroupSpy childGroup = createDataGroupSpy("childGroup");
		DataAtomicSpy textVariable = new DataAtomicSpy();
		textVariable.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someTextVariable");
		childGroup.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);

		DataGroupSpy childGroup2 = createDataGroupSpy("childGroup2");
		DataAtomicSpy textVariable2 = new DataAtomicSpy();
		textVariable2.MRV.setDefaultReturnValuesSupplier("getNameInData",
				() -> "someTextVariable2");
		childGroup2.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
		childGroup2.MRV.setDefaultReturnValuesSupplier("getChildren", () -> List.of(textVariable2));

		childGroup.MRV.setDefaultReturnValuesSupplier("getChildren",
				() -> List.of(textVariable, childGroup2));
		dataGroup.MRV.setDefaultReturnValuesSupplier("getChildren",
				() -> List.of(resourceLink, childGroup));

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

}
