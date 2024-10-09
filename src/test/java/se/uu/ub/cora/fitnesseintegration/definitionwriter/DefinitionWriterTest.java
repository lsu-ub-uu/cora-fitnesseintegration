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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataAtomicSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class DefinitionWriterTest {

	private static final String LINKED_RECORD_LINK_CHILD_NiD = "someLinkChild";
	private static final String GROUP_TYPE = "group";
	private static final String TEXT_VARIABLE_TYPE = "textVariable";
	private static final String RECORD_LINK_TYPE = "recordLink";
	private static final String LINKED_CHILD_GROUP_ID = "someLinkedRecordId";
	private static final String CHILD_GROUP_NiD = "someChildGroup";
	private static final String LINKED_TEXT_CHILD_ID = "someLinkedTextChildId";
	private static final String LINKED_TEXT_CHILD_RECORD_NiD = "someTextChild";
	private static final String LINKED_RECORD_LINK_ID = "someLinkedRecordLinkId";
	private static final String RECORD_ID = "someRecordId";

	private DefinitionWriter writer;
	private JavaClientFactorySpy javaClientFactory;
	private String baseUrl = "someBaseUrl";
	private String appTokenUrl = "someAppTokenUrl";
	private String authToken = "someAuthToken";
	private DataClientSpy dataClient;
	private ClientDataRecordSpy dataRecord;
	private ClientDataRecordGroupSpy dataRecordGroup;
	private ClientDataGroupSpy childReferencesGroup;
	private List<ClientDataGroupSpy> childRefs;

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

		setUpFirstGroupAndChildReferences();
	}

	private void setUpFirstGroupAndChildReferences() {
		dataRecordGroup = createDataRecordGroupWithAttributesSpy("someRootGroup", GROUP_TYPE);
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		childReferencesGroup = createChildReferencesGroupForRecordGroup(dataRecordGroup);
		childRefs = new ArrayList<>();
		addChildReferenceListToChildReferencesGroup(childReferencesGroup, childRefs);
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
		ClientDataRecordGroupSpy dataRecordGroup = createDataRecordGroupWithAttributesSpy(
				"someRootGroup", GROUP_TYPE);
		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> dataRecordGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		dataClient.MCR.assertParameters("read", 0, "metadata", RECORD_ID);
		String expectedDefinition = """
				someRootGroup (group)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void writeTwoGroupsWithMinMax() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		dataClient.MCR.assertNumberOfCallsToMethod("read", 2);
		dataClient.MCR.assertParameters("read", 0, "metadata", RECORD_ID);
		dataClient.MCR.assertParameters("read", 1, "metadata", LINKED_CHILD_GROUP_ID);
		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void writeTwoGroupsWithMinMaxAndNoConstraints() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testTwoLevelsWithConstraint() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		addConstraintToChildReference(childReference1, "write");
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, write)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testThreeLevels() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		List<ClientDataGroupSpy> childRefs2 = new ArrayList<>();

		ClientDataGroupSpy textChildReference = createChildReferenceElement("0", "1",
				LINKED_TEXT_CHILD_ID, LINKED_TEXT_CHILD_RECORD_NiD);
		ClientDataRecordGroupSpy someTextChildGroup = createDataRecordGroupWithAttributesSpy(
				LINKED_TEXT_CHILD_RECORD_NiD, TEXT_VARIABLE_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_TEXT_CHILD_ID, someTextChildGroup);

		ClientDataGroupSpy linkChildReference = createChildReferenceElement("1", "1",
				LINKED_RECORD_LINK_ID, LINKED_RECORD_LINK_CHILD_NiD);
		ClientDataRecordGroupSpy someLinkChildGroup = createDataRecordGroupWithAttributesSpy(
				LINKED_RECORD_LINK_CHILD_NiD, RECORD_LINK_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_RECORD_LINK_ID, someLinkChildGroup);

		childRefs2.add(textChildReference);
		childRefs2.add(linkChildReference);

		ClientDataGroupSpy childReferencesGroup2 = createChildReferencesGroupForRecordGroup(
				someChildGroup);
		addChildReferenceListToChildReferencesGroup(childReferencesGroup2, childRefs2);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, noConstraint)
						someTextChild (textVariable, 0-1, noConstraint)
						someLinkChild (recordLink, 1-1, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testThreeLevelsWithFinalValue() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		List<ClientDataGroupSpy> childRefs2 = new ArrayList<>();

		ClientDataGroupSpy textChildReference = createChildReferenceElement("0", "1",
				LINKED_TEXT_CHILD_ID, LINKED_TEXT_CHILD_RECORD_NiD);
		ClientDataRecordGroupSpy someTextChildGroup = createDataRecordGroupWithAttributesSpy(
				LINKED_TEXT_CHILD_RECORD_NiD, TEXT_VARIABLE_TYPE);
		addFinalValueToGroup("someFinalValue", someTextChildGroup);
		createRecordInStorageAndAddDataRecordGroup(LINKED_TEXT_CHILD_ID, someTextChildGroup);

		childRefs2.add(textChildReference);

		ClientDataGroupSpy childReferencesGroup2 = createChildReferencesGroupForRecordGroup(
				someChildGroup);
		addChildReferenceListToChildReferencesGroup(childReferencesGroup2, childRefs2);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, noConstraint)
						someTextChild {someFinalValue} (textVariable, 0-1, noConstraint)""";
		assertEquals(definition, expectedDefinition);
	}

	@Test
	public void testThreeLevelsWithCollectTerms() throws Exception {
		ClientDataGroupSpy childReference1 = createChildReferenceElement("0", "X",
				LINKED_CHILD_GROUP_ID, CHILD_GROUP_NiD);
		childRefs.add(childReference1);
		ClientDataRecordGroupSpy someChildGroup = createDataRecordGroupWithAttributesSpy(
				CHILD_GROUP_NiD, GROUP_TYPE);
		createRecordInStorageAndAddDataRecordGroup(LINKED_CHILD_GROUP_ID, someChildGroup);

		List<ClientDataGroupSpy> childRefs2 = new ArrayList<>();

		ClientDataGroupSpy textChildReference = createChildReferenceElement("0", "1",
				LINKED_TEXT_CHILD_ID, LINKED_TEXT_CHILD_RECORD_NiD);
		ClientDataRecordGroupSpy someTextChildGroup = createDataRecordGroupWithAttributesSpy(
				LINKED_TEXT_CHILD_RECORD_NiD, TEXT_VARIABLE_TYPE);

		textChildReference.MRV.setDefaultReturnValuesSupplier("getAllChildrenMatchingFilter",
				() -> List.of(new Object()));

		createRecordInStorageAndAddDataRecordGroup(LINKED_TEXT_CHILD_ID, someTextChildGroup);

		childRefs2.add(textChildReference);

		ClientDataGroupSpy childReferencesGroup2 = createChildReferencesGroupForRecordGroup(
				someChildGroup);
		addChildReferenceListToChildReferencesGroup(childReferencesGroup2, childRefs2);

		String definition = writer.writeDefinitionFromUsingDataChild(authToken, RECORD_ID);

		String expectedDefinition = """
				someRootGroup (group)
					someChildGroup (group, 0-X, noConstraint)
						someTextChild (textVariable, 0-1, noConstraint, S, P, I)""";
		assertEquals(definition, expectedDefinition);
	}

	private ClientDataRecordGroupSpy createDataRecordGroupWithAttributesSpy(String name,
			String type) {
		ClientDataRecordGroupSpy dataRecordGroupSpy = new ClientDataRecordGroupSpy();

		dataRecordGroupSpy.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(type), "type");
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> name, "nameInData");

		return dataRecordGroupSpy;
	}

	private void addFinalValueToGroup(String finalValue,
			ClientDataRecordGroupSpy someTextChildGroup) {
		someTextChildGroup.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> finalValue, "finalValue");
		someTextChildGroup.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "finalValue");
	}

	private void addConstraintToChildReference(ClientDataGroupSpy childRefrence1,
			String constraint) {
		ClientDataAtomicSpy recordPartConstraint = createAtomicSpy("recordPartConstraint",
				constraint);
		childRefrence1.MRV.setSpecificReturnValuesSupplier("containsChildWithNameInData",
				() -> true, "recordPartConstraint");
		addAtomicToGroup(childRefrence1, "recordPartConstraint", recordPartConstraint);
	}

	private ClientDataGroupSpy createChildReferenceElement(String repeatMin, String repeatMax,
			String linkedRecordIdId, String linkedRecordNameInData) {
		ClientDataGroupSpy childReference = new ClientDataGroupSpy();
		ClientDataAtomicSpy repeatMinAtomic = createAtomicSpy("repeatMin", repeatMin);
		ClientDataAtomicSpy repeatMaxAtomic = createAtomicSpy("repeatMax", repeatMax);

		addAtomicToGroup(childReference, "repeatMin", repeatMinAtomic);
		addAtomicToGroup(childReference, "repeatMax", repeatMaxAtomic);

		addLinkToGroup(childReference, "ref", linkedRecordIdId);

		return childReference;
	}

	private ClientDataRecordSpy createRecordInStorageAndAddDataRecordGroup(String recordId,
			ClientDataRecordGroupSpy someChildGroup) {
		ClientDataRecordSpy childRecord = new ClientDataRecordSpy();
		dataClient.MRV.setSpecificReturnValuesSupplier("read", () -> childRecord, "metadata",
				recordId);

		childRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> someChildGroup);
		return childRecord;
	}

	private void addChildReferenceListToChildReferencesGroup(ClientDataGroupSpy dataGroup,
			List<ClientDataGroupSpy> childRefs) {
		dataGroup.MRV.setSpecificReturnValuesSupplier("getChildrenOfTypeAndName", () -> childRefs,
				ClientDataGroup.class, "childReference");
	}

	private void addLinkToGroup(ClientDataGroupSpy childReference, String nameInData,
			String linkedRecordId) {
		ClientDataRecordLinkSpy recordLinkRefSpy = createDataRecordLinkRef(linkedRecordId);
		childReference.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> recordLinkRefSpy, ClientDataRecordLink.class, nameInData);
	}

	private ClientDataRecordLinkSpy createDataRecordLinkRef(String linkedRecordId) {
		ClientDataRecordLinkSpy recordLinkSpy = new ClientDataRecordLinkSpy();
		recordLinkSpy.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkedRecordId);
		return recordLinkSpy;
	}

	private void addAtomicToGroup(ClientDataGroupSpy someChildReference, String nameInData,
			ClientDataAtomicSpy atomic) {
		someChildReference.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> atomic.getValue(), nameInData);
	}

	private ClientDataGroupSpy createChildReferencesGroupForRecordGroup(
			ClientDataRecordGroupSpy dataRecordGroup) {
		ClientDataGroupSpy someChildReferences = createDataGroupSpy("childReferences", GROUP_TYPE);
		dataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstGroupWithNameInData",
				() -> someChildReferences, "childReferences");
		return someChildReferences;
	}

	private ClientDataGroupSpy createDataGroupSpy(String name, String type) {
		ClientDataGroupSpy dataRecordGroupSpy = new ClientDataGroupSpy();

		dataRecordGroupSpy.MRV.setDefaultReturnValuesSupplier("hasAttributes", () -> true);
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getAttributeValue",
				() -> Optional.of(type), "type");
		dataRecordGroupSpy.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> name, "nameInData");

		return dataRecordGroupSpy;
	}

	private ClientDataAtomicSpy createAtomicSpy(String nameInData, String value) {
		ClientDataAtomicSpy someAtomic = new ClientDataAtomicSpy();
		someAtomic.MRV.setSpecificReturnValuesSupplier("getFirstAtomicValueWithNameInData",
				() -> nameInData, "nameInData");
		someAtomic.MRV.setDefaultReturnValuesSupplier("getValue", () -> value);
		return someAtomic;
	}
}
