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

import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class DefinitionWriterTest {

	private static final String RECORD_ID = "someRecordId";
	private DefinitionWriter writer;
	private JavaClientFactorySpy javaClientFactory;
	private String baseUrl = "someBaseUrl";
	private String appTokenUrl = "someAppTokenUrl";
	private String authToken = "someAuthToken";
	private DataClientSpy dataClient;
	private ClientDataRecordSpy dataRecord;

	@BeforeMethod
	private void beforeMethod() {
		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);
		dataClient = new DataClientSpy();
		javaClientFactory.MRV.setDefaultReturnValuesSupplier(
				"factorDataClientUsingJavaClientAuthTokenCredentials", () -> dataClient);

		dataRecord = new ClientDataRecordSpy();
		dataClient.MRV.setSpecificReturnValuesSupplier("read", () -> dataRecord, "metadata",
				RECORD_ID);

		writer = new DefinitionWriter(baseUrl, appTokenUrl);
	}

	@Test
	public void testOnlyForTestGetBaseUrl() throws Exception {
		assertEquals(writer.onlyForTestGetBaseUrl(), baseUrl);
	}

	@Test
	public void testOnlyForTestGetAppTokenUrl() throws Exception {
		assertEquals(writer.onlyForTestGetAppTokenUrl(), appTokenUrl);
	}

	@Test
	public void testDataClientIsFactored() throws Exception {
		writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);
		javaClientFactory.MCR
				.assertMethodWasCalled("factorDataClientUsingJavaClientAuthTokenCredentials");
		assertEquals(dataClient, writer.onlyForTestGetDataClient());
	}

	@Test
	public void writeOneGroupOnlyNameInData() throws Exception {
		ClientDataRecordGroupSpy dataRecordGroup = createMetadataGroupSpy("someRootGroup", "group");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);
		dataClient.MCR.assertParameters("read", 0, "metadata", RECORD_ID);

		String expectedDefinition = """
				someRootGroup(group)""";

		assertEquals(definition, expectedDefinition);
	}

	private ClientDataRecordGroupSpy createMetadataGroupSpy(String name, String type) {
		ClientDataRecordGroupSpy dataRecordGroupSpy = new ClientDataRecordGroupSpy();

		dataRecordGroupSpy.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(type), "type");
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> name, "nameInData");

		return dataRecordGroupSpy;
	}

	@Test
	public void writeOneGroupOnlyNameInData2() throws Exception {
		ClientDataRecordGroupSpy dataRecordGroup = createMetadataGroupSpy("someRootGroup", "group");
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		createMetadataGroupSpy("childReferences", "group");
		createMetadataGroupSpy("childReference", "group");

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);
		dataClient.MCR.assertParameters("read", 0, "metadata", RECORD_ID);

		String expectedDefinition = """
				someRootGroup(group)""";

		assertEquals(definition, expectedDefinition);
	}

	// @Test
	// public void writeOneGroupOnlyNameInData2() throws Exception {
	// DataGroupSpy dataGroup = createMetadataGroupSpy("someRootGroup", "group");
	// DataGroupSpy attributeRefs = createMetadataGroupSpy("attributeReferences", "group");
	//
	// DataRecordGroupSpy someCollectionVar = new DataRecordGroupSpy();
	// someCollectionVar.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
	// () -> "someCollectionVar", "nameInData");
	//
	// DataRecordLinkSpy someAttributeRef = new DataRecordLinkSpy();
	// someAttributeRef.MRV.setSpecificReturnValuesSupplier("getLinkedRecordId",
	// () -> "someCollectionVar", "someCollectionVar");
	// someAttributeRef.MRV.setSpecificReturnValuesSupplier("getLinkedRecordType",
	// () -> "metadata", "getLinkedRecordType");
	//
	// DataGroupSpy someItemCollection = createMetadataGroupSpy("someCollection",
	// "itemCollection");
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(authToken, dataGroup);
	//
	// String expectedDefinition = """
	// someRootGroup(group)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }

	// @Test
	// public void testOneGroupOneTextVariable() throws Exception {
	// DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
	// DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
	// "textVariable");
	// addChildrenToGroup(dataGroup, textVariable);
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
	//
	// String expectedDefinition = """
	// someGroup(group)
	// someTextVariable(textVariable, 1-1, noConstraint)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }
	//
	// private void addChildrenToGroup(DataGroupSpy group, DataChild... textVariable) {
	// group.MRV.setDefaultReturnValuesSupplier("hasChildren", () -> true);
	// group.MRV.setDefaultReturnValuesSupplier("getChildren", () -> Arrays.asList(textVariable));
	// }
	//
	// private DataAtomicSpy createAtomicUsingNameInDataAndType(String nameInData, String type) {
	// DataAtomicSpy atomic = new DataAtomicSpy();
	// addAttributeType(atomic, type);
	// atomic.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
	// return atomic;
	// }
	//
	// private void addAttributeType(DataAtomicSpy dataChild, String typeValue) {
	// dataChild.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
	// dataChild.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
	// () -> Optional.of(typeValue), "type");
	// }
	//
	// private DataRecordLinkSpy createRecordLinkUsingNameInData(String nameInData) {
	// DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
	// recordLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> nameInData);
	// return recordLink;
	// }
	//
	// private DataResourceLinkSpy createResourceLinkUsingNameInData() {
	// DataResourceLinkSpy resourceLink = new DataResourceLinkSpy();
	// resourceLink.MRV.setDefaultReturnValuesSupplier("getNameInData", () -> "someResourceLink");
	// return resourceLink;
	// }
	//
	// @Test
	// public void testOneGroupOneRecordLink() throws Exception {
	// DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
	// DataRecordLinkSpy recordLink = createRecordLinkUsingNameInData("someRecordLink");
	// addChildrenToGroup(dataGroup, recordLink);
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
	//
	// String expectedDefinition = """
	// someGroup(group)
	// someRecordLink(recordLink, 1-1, noConstraint)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }
	//
	// @Test
	// public void testOneGroupOneResourceLink() throws Exception {
	// DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
	// DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
	// addChildrenToGroup(dataGroup, resourceLink);
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
	// String expectedDefinition = """
	// someGroup(group)
	// someResourceLink(resourceLink, 1-1, noConstraint)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }
	//
	// @Test
	// public void testNestedGroupsWithOneChildEach() throws Exception {
	// DataGroupSpy childGroup2 = createDataGroupSpy("childGroup2");
	// DataAtomicSpy textVariable2 = createAtomicUsingNameInDataAndType("someTextVariable2",
	// "textVariable");
	// addChildrenToGroup(childGroup2, textVariable2);
	// DataGroupSpy childGroup = createDataGroupSpy("childGroup");
	// DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
	// "textVariable");
	// addChildrenToGroup(childGroup, textVariable, childGroup2);
	// DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
	// DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
	// addChildrenToGroup(dataGroup, resourceLink, childGroup);
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
	//
	// String expectedDefinition = """
	// someGroup(group)
	// someResourceLink(resourceLink, 1-1, noConstraint)
	// childGroup(group)
	// someTextVariable(textVariable, 1-1, noConstraint)
	// childGroup2(group)
	// someTextVariable2(textVariable, 1-1, noConstraint)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }
	//
	// @Test
	// public void testNestedGroupsWithChildrenOnVariousLevels() throws Exception {
	// DataGroupSpy childGroup5 = createDataGroupSpy("childGroup5");
	// DataAtomicSpy textVariable5 = createAtomicUsingNameInDataAndType("someTextVariable5",
	// "textVariable");
	// addChildrenToGroup(childGroup5, textVariable5);
	// DataGroupSpy childGroup4 = createDataGroupSpy("childGroup4");
	// DataAtomicSpy textVariable4 = createAtomicUsingNameInDataAndType("someTextVariable4",
	// "textVariable");
	// addChildrenToGroup(childGroup4, textVariable4);
	// DataGroupSpy childGroup3 = createDataGroupSpy("childGroup3");
	// DataAtomicSpy textVariable3 = createAtomicUsingNameInDataAndType("someTextVariable3",
	// "textVariable");
	// addChildrenToGroup(childGroup3, textVariable3);
	// DataGroupSpy childGroup2 = createDataGroupSpy("childGroup2");
	// DataAtomicSpy textVariable2 = createAtomicUsingNameInDataAndType("someTextVariable2",
	// "textVariable");
	// addChildrenToGroup(childGroup2, textVariable2);
	// DataGroupSpy childGroup = createDataGroupSpy("childGroup");
	// DataAtomicSpy textVariable = createAtomicUsingNameInDataAndType("someTextVariable",
	// "textVariable");
	// DataRecordLinkSpy recordLink = createRecordLinkUsingNameInData("someRecordLink");
	// DataAtomicSpy textVariable7 = createAtomicUsingNameInDataAndType("someTextVariable7",
	// "textVariable");
	// addChildrenToGroup(childGroup, textVariable, recordLink, childGroup2, childGroup3,
	// textVariable7);
	// DataResourceLinkSpy resourceLink = createResourceLinkUsingNameInData();
	// DataGroupSpy dataGroup = createDataGroupSpy("someGroup");
	// addChildrenToGroup(dataGroup, resourceLink, childGroup, childGroup4, childGroup5);
	//
	// String definition = writer.writeDefinitionFromUsingDataChild(dataGroup);
	//
	// String expectedDefinition = """
	// someGroup(group)
	// someResourceLink(resourceLink, 1-1, noConstraint)
	// childGroup(group)
	// someTextVariable(textVariable, 1-1, noConstraint)
	// someRecordLink(recordLink, 1-1, noConstraint)
	// childGroup2(group)
	// someTextVariable2(textVariable, 1-1, noConstraint)
	// childGroup3(group)
	// someTextVariable3(textVariable, 1-1, noConstraint)
	// someTextVariable7(textVariable, 1-1, noConstraint)
	// childGroup4(group)
	// someTextVariable4(textVariable, 1-1, noConstraint)
	// childGroup5(group)
	// someTextVariable5(textVariable, 1-1, noConstraint)""";
	//
	// assertEquals(definition, expectedDefinition);
	// }

}
