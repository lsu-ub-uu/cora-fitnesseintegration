package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;

public class DefinitionWriterTest {

	private DefinitionWriter writer;
	private DataGroupSpy dataGroup;

	@BeforeMethod
	private void beforeMethod() {
		dataGroup = new DataGroupSpy();
		dataGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someGroup");

		writer = new DefinitionWriter();
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

}
