/*
 * Copyright 2018 Uppsala University Library
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

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.DataRecord;

public class PresentationGroupFixtureTest {

	PresentationGroupFixture fixture;
	private ClientDataGroup topLevelDataGroup;

	@BeforeMethod
	public void setUp() {
		fixture = new PresentationGroupFixture();

		topLevelDataGroup = createTopLevelDataGroup();

		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("presentation");
		ClientDataGroup childReferences = ClientDataGroup.withNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
				"0", "presentationGroup", "somePresentationPGroup", "presentation");
		childReferences.addChild(childReference);
		dataGroup.addChild(childReferences);
		return dataGroup;
	}

	private ClientDataGroup createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
			String repeatId, String linkedRecordType, String linkedRecordId, String typeAttribute) {
		ClientDataGroup childReference = ClientDataGroup.withNameInData("childReference");
		childReference.setRepeatId(repeatId);

		ClientDataGroup refGroup = ClientDataGroup.withNameInData("refGroup");
		ClientDataGroup ref = ClientDataGroup.withNameInData("ref");
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
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
		DataRecord record = ClientDataRecord.withClientDataGroup(null);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordId("somePresentationPGroup");
		fixture.setLinkedRecordType("presentationGroup");
		assertEquals(fixture.numberOfRefs(), 0);
	}

	@Test
	public void testNumOfChildrenWithNoChildren() {

		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("presentation");
		DataRecord record = ClientDataRecord.withClientDataGroup(dataGroup);
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
				.getFirstGroupWithNameInDataAndAttributes("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeRecordIdAndType(
				"1", "presentationGroup", "somePresentationPGroup", "presentation");
		childReferences.addChild(childReference);
		fixture.setLinkedRecordType("presentationGroup");
		fixture.setLinkedRecordId("somePresentationPGroup");
		assertEquals(fixture.numberOfRefs(), 2);
	}
}
