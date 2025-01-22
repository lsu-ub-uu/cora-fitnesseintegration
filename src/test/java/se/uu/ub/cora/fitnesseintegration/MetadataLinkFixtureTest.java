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
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.script.AuthTokenHolder;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class MetadataLinkFixtureTest {
	MetadataLinkFixture fixture;
	private HttpHandlerFactoryOldSpy httpHandlerFactorySpy;
	JsonToClientDataConverterFactorySpy converterFactorySpy = new JsonToClientDataConverterFactorySpy();

	@BeforeMethod
	public void setUp() {
		JsonToClientDataConverterProvider.setJsonToDataConverterFactory(converterFactorySpy);

		SystemUrl.setUrl("http://localhost:8080/therest/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactoryOldSpy");
		httpHandlerFactorySpy = (HttpHandlerFactoryOldSpy) DependencyProvider.getHttpHandlerFactory();

		setupTestRecordInDataHolder();

		fixture = new MetadataLinkFixture();
	}

	private void setupTestRecordInDataHolder() {
		ClientDataGroup topLevelDataGroup = createTopLevelDataGroup();

		ClientDataRecordGroup recordGroup = ClientDataProvider
				.createRecordGroupFromDataGroup(topLevelDataGroup);
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(recordGroup);

		DataHolder.setRecord(record);
	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup topLevelDataGroup = ClientDataProvider
				.createGroupUsingNameInData("metadata");
		ClientDataGroup childReferences = ClientDataProvider
				.createGroupUsingNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("0",
				"metadataGroup", "someRecordId", "0", "X");
		childReferences.addChild(childReference);
		topLevelDataGroup.addChild(childReferences);
		return topLevelDataGroup;
	}

	private ClientDataGroup createChildReferenceWithRepeatIdRecordTypeAndRecordId(String repeatId,
			String linkedRecordType, String linkedRecordId, String repeatMin, String repeatMax) {
		ClientDataGroup childReference = ClientDataProvider
				.createGroupUsingNameInData("childReference");
		childReference.setRepeatId(repeatId);
		ClientDataRecordLink refLink = ClientDataProvider
				.createRecordLinkUsingNameInDataAndTypeAndId("ref", linkedRecordType,
						linkedRecordId);
		childReference.addChild(refLink);
		childReference.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("repeatMin", repeatMin));
		childReference.addChild(
				ClientDataProvider.createAtomicUsingNameInDataAndValue("repeatMax", repeatMax));
		return childReference;
	}

	@Test
	public void testNameInData() {
		setUpConverterFromJsonToReturnNameInDataForClientDataRecordGroup("someNameInData");

		fixture.setAuthToken("someToken");
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");

		assertCorrectHttpHandler();

		String nameInData = fixture.getNameInData();
		assertEquals(nameInData, "someNameInData");

		String responseText = httpHandlerFactorySpy.httpHandlerSpy.getResponseText();
		converterFactorySpy.MCR.assertParameters("factorUsingString", 0, responseText);
	}

	private void setUpConverterFromJsonToReturnNameInDataForClientDataRecordGroup(
			String nameInData) {
		ClientDataRecordGroupSpy clientDataRecordGroup = new ClientDataRecordGroupSpy();
		// clientDataRecordGroup.MRV.setDefaultReturnValuesSupplier("getNameInData", () ->
		// nameInData);
		clientDataRecordGroup.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", () -> nameInData, "nameInData");

		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		clientDataRecordSpy.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> clientDataRecordGroup);

		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance", () -> clientDataRecordSpy);
		converterFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	private void assertCorrectHttpHandler() {
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/metadataGroup/someRecordId");
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestProperties.get("authToken"),
				"someToken");
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestProperties.get("Accept"),
				"application/vnd.uub.record+json");
	}

	@Test
	public void testNoMatchingChild() {
		ClientDataRecordGroup topLevelDataGroup = ClientDataProvider
				.createRecordGroupUsingNameInData("metadata");
		ClientDataRecord record = ClientDataProvider
				.createRecordWithDataRecordGroup(topLevelDataGroup);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
		assertEquals(fixture.getNameInData(), "not found");
	}

	@Test
	public void testNoTopLevelDatagroupInRecord() {
		ClientDataRecord record = ClientDataProvider.createRecordWithDataRecordGroup(null);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordId("someRecordId");
		fixture.setLinkedRecordType("metadataGroup");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	private void createAndAddSecondChild() {
		ClientDataRecord record = DataHolder.getRecord();
		ClientDataRecordGroup clientDataGroup = record.getDataRecordGroup();
		ClientDataGroup childReferences = clientDataGroup
				.getFirstGroupWithNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("1",
				"metadataGroup", "someOtherRecordId", "1", "3");
		childReferences.addChild(childReference);
	}

	@Test
	public void testRepeatMinWithoutRecord() throws Exception {
		DataHolder.setRecord(null);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMinRecordType() {
		fixture.setLinkedRecordType("NOTmetadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMin() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	@Test
	public void testRepeatMinIsCorrect() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
	}

	@Test
	public void testRepeatMinIsCorrectSecondChild() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "1");
	}

	@Test
	public void testRepeatMaxWithoutRecord() throws Exception {
		DataHolder.setRecord(null);
		assertEquals(fixture.getRepeatMax(), "not found");
	}

	@Test
	public void testNoMatchingChildForRepeatMax() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeRecordId");
		assertEquals(fixture.getRepeatMax(), "not found");
	}

	@Test
	public void testRepeatMaxIsCorrect() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMax(), "X");
	}

	@Test
	public void testRepeatMaxIsCorrectSecondChild() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMax(), "3");
	}

	@Test
	public void testMoreThanOneTestOnSameRecord() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
		assertEquals(fixture.getRepeatMax(), "X");

		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "1");
		assertEquals(fixture.getRepeatMax(), "3");
	}

	@Test
	public void testMoreThanOneTestOnSameRecordNoMatchSecondLink() {
		createAndAddSecondChild();
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "0");
		assertEquals(fixture.getRepeatMax(), "X");

		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeOtherRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
		assertEquals(fixture.getRepeatMax(), "not found");
	}

	@Test
	public void testRecordPartConstraintWithoutRecord() throws Exception {
		DataHolder.setRecord(null);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "not found");
	}

	@Test
	public void testNoMatchingChildForRecordTypeRecordPartConstraint() {
		fixture.setLinkedRecordType("NOTmetadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "not found");
	}

	@Test
	public void testNoMatchingChildForRecordIdForRecordPartConstraint() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("NOTsomeRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "not found");
	}

	@Test
	public void testRecordPartConstraintIsCorrectNoConstraint() {
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "noRestrictions");
	}

	@Test
	public void testRecordPartConstraintIsCorrectWriteConstraint() {
		createAndAddChildWithConstraint("write");
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("childWithConstraintRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "write");
	}

	@Test
	public void testRecordPartConstraintIsCorrectReadWriteConstraint() {
		createAndAddChildWithConstraint("readWrite");
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("childWithConstraintRecordId");
		assertEquals(fixture.getRecordPartConstraint(), "readWrite");
	}

	private void createAndAddChildWithConstraint(String constraint) {
		ClientDataRecord record = DataHolder.getRecord();
		ClientDataRecordGroup clientDataGroup = record.getDataRecordGroup();
		ClientDataGroup childReferences = clientDataGroup
				.getFirstGroupWithNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("1",
				"metadataGroup", "childWithConstraintRecordId", "1", "3");
		childReference.addChild(ClientDataProvider
				.createAtomicUsingNameInDataAndValue("recordPartConstraint", constraint));
		childReferences.addChild(childReference);
	}
}
