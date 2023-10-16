/*
 * Copyright 2018, 2023 Uppsala University Library
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

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;

public class PresentationGroupFixtureTest {

	PresentationGroupFixture fixture;
	private ClientDataGroup topLevelDataGroup;

	@BeforeMethod
	public void setUp() {
		fixture = new PresentationGroupFixture();

		topLevelDataGroup = createTopLevelDataGroup();

		ClientDataRecordGroup recordGroup = ClientDataProvider
				.createRecordGroupFromDataGroup(topLevelDataGroup);
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(recordGroup);
		DataHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup dataGroup = ClientDataProvider.createGroupUsingNameInData("presentation");
		ClientDataGroup childReferences = ClientDataProvider
				.createGroupUsingNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
				"0", "presentationGroup", "somePresentationPGroup", "presentation");
		childReferences.addChild(childReference);
		dataGroup.addChild(childReferences);
		return dataGroup;
	}

	private ClientDataGroup createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
			String repeatId, String linkedRecordType, String linkedRecordId, String typeAttribute) {
		ClientDataGroup childReference = ClientDataProvider
				.createGroupUsingNameInData("childReference");
		childReference.setRepeatId(repeatId);

		ClientDataGroup refGroup = ClientDataProvider.createGroupUsingNameInData("refGroup");
		ClientDataGroup ref = ClientDataProvider.createGroupUsingNameInData("ref");
		ref.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("linkedRecordType",
				linkedRecordType));
		ref.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("linkedRecordId",
				linkedRecordId));
		ref.addAttributeByIdWithValue("type", typeAttribute);
		refGroup.addChild(ref);
		childReference.addChild(refGroup);

		return childReference;
	}

	@Test
	public void testNumOfChildrenWithoutRecord() throws Exception {
		DataHolder.setRecord(null);
		fixture.setLinkedRecordId("somePresentationPGroup");
		fixture.setLinkedRecordType("presentationGroup");
		assertEquals(fixture.numberOfRefs(), 0);
	}

	@Test
	public void testNoTopLevelDataGroup() {
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(null);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordId("somePresentationPGroup");
		fixture.setLinkedRecordType("presentationGroup");
		assertEquals(fixture.numberOfRefs(), 0);
	}

	@Test
	public void testNumOfChildrenWithNoChildren() {

		ClientDataRecordGroup dataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("presentation");
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(dataGroup);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordId("somePresentationPGroup");
		fixture.setLinkedRecordType("presentationGroup");
		assertEquals(fixture.numberOfRefs(), 0);
	}

	@Test
	public void testLinkIsNotPresent() {
		fixture.setLinkedRecordType("presentationGroup");
		fixture.setLinkedRecordId("NOTsomePresentationPGroup");
		assertEquals(fixture.numberOfRefs(), 0);
	}

	@Test
	public void testOneLinkIsPresent() {
		fixture.setLinkedRecordType("presentationGroup");
		fixture.setLinkedRecordId("somePresentationPGroup");
		assertEquals(fixture.numberOfRefs(), 1);
	}

	@Test
	public void testOneLinkIsPresentTwice() {
		ClientDataGroup childReferences = topLevelDataGroup
				.getFirstGroupWithNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
				"1", "presentationGroup", "somePresentationPGroup", "presentation");
		childReferences.addChild(childReference);
		fixture.setLinkedRecordType("presentationGroup");
		fixture.setLinkedRecordId("somePresentationPGroup");
		assertEquals(fixture.numberOfRefs(), 2);
	}
}
