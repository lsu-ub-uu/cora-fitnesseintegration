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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;

public class MetadataLinkFixtureTest {

	MetadataLinkFixture fixture;
	private HttpHandlerFactorySpy httpHandlerFactorySpy;
	private JsonToDataConverterFactorySpy jsonToDataConverterFactory;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		DependencyProvider.setJsonToDataFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.JsonToDataConverterFactorySpy");
		httpHandlerFactorySpy = (HttpHandlerFactorySpy) DependencyProvider.getHttpHandlerFactory();
		jsonToDataConverterFactory = (JsonToDataConverterFactorySpy) DependencyProvider
				.getJsonToDataConverterFactory();
		fixture = new MetadataLinkFixture();

		ClientDataGroup topLevelDataGroup = createTopLevelDataGroup();

		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);

	}

	private ClientDataGroup createTopLevelDataGroup() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("metadata");
		ClientDataGroup childReferences = ClientDataGroup.withNameInData("childReferences");
		ClientDataGroup childReference = createChildReferenceWithRepeatIdRecordTypeAndRecordId("0",
				"metadataGroup", "someRecordId", "0", "X");
		childReferences.addChild(childReference);
		topLevelDataGroup.addChild(childReferences);
		return topLevelDataGroup;
	}

	private ClientDataGroup createChildReferenceWithRepeatIdRecordTypeAndRecordId(String repeatId,
			String linkedRecordType, String linkedRecordId, String repeatMin, String repeatMax) {
		ClientDataGroup childReference = ClientDataGroup.withNameInData("childReference");
		childReference.setRepeatId(repeatId);
		ClientDataGroup ref = ClientDataGroup.withNameInData("ref");
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		ref.addChild(ClientDataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		childReference.addChild(ref);
		childReference.addChild(ClientDataAtomic.withNameInDataAndValue("repeatMin", repeatMin));
		childReference.addChild(ClientDataAtomic.withNameInDataAndValue("repeatMax", repeatMax));
		return childReference;
	}

	@Test
	public void testNameInData() {
		fixture.setAuthToken("someToken");
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");

		assertCorrectHttpHandler();

		String nameInData = fixture.getNameInData();
		assertEquals(nameInData, "someNameInData");

		assertTrue(fixture.getJsonToRecordDataConverter() instanceof JsonToDataRecordConverter);
		JsonToDataConverterSpy converterSpy = (JsonToDataConverterSpy) jsonToDataConverterFactory.factored;

		assertTrue(converterSpy.toInstanceWasCalled);

		JsonObject jsonObject = (JsonObject) converterSpy.jsonValue;
		assertJsonObjectFromReadRecordIsSentToConverter(jsonObject);

	}

	private void assertCorrectHttpHandler() {
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestMetod, "GET");
		assertEquals(httpHandlerFactorySpy.urlString,
				"http://localhost:8080/therest/rest/record/metadataGroup/someRecordId");
		assertEquals(httpHandlerFactorySpy.httpHandlerSpy.requestProperties.get("authToken"),
				"someToken");
	}

	private void assertJsonObjectFromReadRecordIsSentToConverter(JsonObject jsonObject) {
		JsonString name = jsonObject.getValueAsJsonString("name");
		assertEquals(name.getStringValue(), "metadata");

		assertNotNull(jsonObject.getValue("children"));
		assertNotNull(jsonObject.getValue("attributes"));
		assertEquals(jsonObject.entrySet().size(), 3);
	}

	@Test
	public void testNoMatchingChild() {
		ClientDataGroup topLevelDataGroup = ClientDataGroup.withNameInData("metadata");
		DataRecord record = ClientDataRecord.withClientDataGroup(topLevelDataGroup);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordType("metadataGroup");
		fixture.setLinkedRecordId("someRecordId");
		assertEquals(fixture.getRepeatMin(), "not found");
		assertEquals(fixture.getNameInData(), "not found");
	}

	@Test
	public void testNoTopLevelDatagroupInRecord() {
		DataRecord record = ClientDataRecord.withClientDataGroup(null);
		DataHolder.setRecord(record);
		fixture.setLinkedRecordId("someRecordId");
		fixture.setLinkedRecordType("metadataGroup");
		assertEquals(fixture.getRepeatMin(), "not found");
	}

	private void createAndAddSecondChild() {
		DataRecord record = (DataRecord) DataHolder.getRecord();
		ClientDataGroup clientDataGroup = record.getClientDataGroup();
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
}
