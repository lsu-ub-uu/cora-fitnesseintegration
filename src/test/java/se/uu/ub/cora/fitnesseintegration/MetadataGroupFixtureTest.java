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

public class MetadataGroupFixtureTest {

	MetadataGroupFixture fixture;

	@BeforeMethod
	public void setUp() {
		fixture = new MetadataGroupFixture();

		ClientDataGroup topLevelDataGroup = createTopLevelDataGroup();

		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		topLevelDataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("testTitle", "a title"));
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
		DataRecord record = ClientDataRecord.withClientDataGroup(null);
		DataHolder.setRecord(record);
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentFirstLevelNoChildDataGroupSet() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
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
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoChildPresentSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testNoMatchingChildSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("NOTtestTitle", "Not a title"));
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 0);
	}

	@Test
	public void testOneChildWithMatchingNameInDataSecondLevel() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("testStudentThesis");
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		topLevelDataGroup.addChild(recordInfo);
		recordInfo.addChild(ClientDataAtomic.withNameInDataAndValue("testTitle", "A title"));
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

		fixture.setChildDataGroup("recordInfo");
		fixture.setChildNameInData("testTitle");
		assertEquals(fixture.numberOfChildrenWithNameInData(), 1);
	}
}
