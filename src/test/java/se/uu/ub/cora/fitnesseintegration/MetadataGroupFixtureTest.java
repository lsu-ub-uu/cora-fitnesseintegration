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

public class MetadataGroupFixtureTest {

	MetadataGroupFixture fixture;

	@BeforeMethod
	public void setUp() {
		fixture = new MetadataGroupFixture();

		ClientDataRecordGroup topLevelDataGroup = createTopLevelDataGroup();

		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

	}

	private ClientDataRecordGroup createTopLevelDataGroup() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		topLevelDataGroup.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("testTitle", "a title"));
		return topLevelDataGroup;
	}

	@Test
	public void testNumOfChildrenWithoutRecord() throws Exception {
		DataHolder.setRecord(null);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoTopLevelDatagroup() {
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(null);
		DataHolder.setRecord(record);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentFirstLevelNoChildDataGroupSet() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentFirstLevelEmptyNameForChildDataGroup() {
		fixture.setChildDataGroup("");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}

	@Test
	public void testNumOfChildrenNoMatchingChildFirstLevel() {
		fixture.setChildNameInData("NOTtestTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testOneChildWithMatchingNameInDataFirstLevel() {
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}

	@Test
	public void testChildDataGroupDoesNotExist() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentSecondLevel() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoMatchingChildSecondLevel() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		recordInfo.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("NOTtestTitle",
				"Not a title"));
		topLevelDataGroup.addChild(recordInfo);
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);

		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testOneChildWithMatchingNameInDataSecondLevel() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		recordInfo.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("testTitle", "A title"));
		topLevelDataGroup.addChild(recordInfo);
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}
}
