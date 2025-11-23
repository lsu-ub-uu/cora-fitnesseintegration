/*
 * Copyright 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
 * 
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
package se.uu.ub.cora.fitnesseintegration.cache;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordLinkSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class ValidationTypeProviderTest {
	private DataClientSpy client;
	private ClientDataListSpy clientDataList;
	private List<ClientData> listOfDataRecords;

	@BeforeMethod
	public void beforeMethod() {
		setUpUserProviderToReturnClientSpy();
		createListOfValidationTypes();
		client.MRV.setSpecificReturnValuesSupplier("readList", () -> clientDataList,
				"validationType");
	}

	@AfterMethod
	public void afterMethod() {
		ValidationTypeProvider.resetInternalHolder();
		FitnesseJavaClientProvider.removeAllCreateClients();
	}

	private void createListOfValidationTypes() {
		clientDataList = new ClientDataListSpy();
		listOfDataRecords = new ArrayList<>();
		clientDataList.MRV.setDefaultReturnValuesSupplier("getDataList", () -> listOfDataRecords);
		createAndAddRecordToListToBeReturned("someValidationType", "recordTypeId");
		createAndAddRecordToListToBeReturned("otherValidationType", "otherRecordTypeId");
		createAndAddRecordToListToBeReturned("aValidationType", "recordTypeId");
	}

	private void createAndAddRecordToListToBeReturned(String id, String recordTypeId) {
		ClientDataRecordSpy dataRecord = new ClientDataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> id);

		ClientDataRecordGroupSpy recordGroup = createClientDataValidationGroup(id, recordTypeId);
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> id);

		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> recordGroup);
		listOfDataRecords.add(dataRecord);
	}

	private ClientDataRecordGroupSpy createClientDataValidationGroup(String id, String recordType) {
		ClientDataRecordGroupSpy validation = new ClientDataRecordGroupSpy();
		validation.MRV.setDefaultReturnValuesSupplier("getId", () -> id);
		setReturnValueForLinkWithNameAndValue(validation, "validatesRecordType", recordType);
		setReturnValueForLinkWithNameAndValue(validation, "newMetadataId", "createDefinitionGroup");
		setReturnValueForLinkWithNameAndValue(validation, "metadataId", "updateDefinitionGroup");
		return validation;
	}

	private void setReturnValueForLinkWithNameAndValue(
			ClientDataRecordGroupSpy clientDataRecordGroup, String nameInData,
			String linkPointsTo) {
		ClientDataRecordLinkSpy metadataLink = new ClientDataRecordLinkSpy();
		metadataLink.MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", () -> linkPointsTo);
		clientDataRecordGroup.MRV.setSpecificReturnValuesSupplier("getFirstChildOfTypeAndName",
				() -> metadataLink, ClientDataRecordLink.class, nameInData);
	}

	private void setUpUserProviderToReturnClientSpy() {
		client = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, client);
	}

	@Test
	public void testPrivateConstructor() throws Exception {
		Constructor<ValidationTypeProvider> constructor = ValidationTypeProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	}

	@Test(expectedExceptions = InvocationTargetException.class)
	public void testPrivateConstructorInvoke() throws Exception {
		Constructor<ValidationTypeProvider> constructor = ValidationTypeProvider.class
				.getDeclaredConstructor();
		assertTrue(Modifier.isPrivate(constructor.getModifiers()));
		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void testGetValidationType() {
		String id = "someValidationType";

		ValidationType validationType = ValidationTypeProvider.getValidationType(id);

		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");
		assertEquals(validationType.id(), id);
		assertEquals(validationType.validatesRecordTypeId(), "recordTypeId");
		assertEquals(validationType.createDefinitionId(), "createDefinitionGroup");
		assertEquals(validationType.updateDefinitionId(), "updateDefinitionGroup");
	}

	@Test
	public void testGetValidationTypeThatValidatesRecordType() {
		String id = "recordTypeId";
		Collection<ValidationType> validationTypes = ValidationTypeProvider
				.getValidationTypesThatValidatesRecordType(id);

		Iterator<ValidationType> iterator = validationTypes.iterator();
		ValidationType validationType = iterator.next();
		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");

		assertEquals(validationType.id(), "aValidationType");
		assertEquals(validationType.validatesRecordTypeId(), "recordTypeId");
		assertEquals(validationType.createDefinitionId(), "createDefinitionGroup");
		assertEquals(validationType.updateDefinitionId(), "updateDefinitionGroup");

		ValidationType validationType2 = iterator.next();
		assertEquals(validationType2.id(), "someValidationType");
		assertEquals(validationType2.validatesRecordTypeId(), "recordTypeId");
		assertEquals(validationType2.createDefinitionId(), "createDefinitionGroup");
		assertEquals(validationType2.updateDefinitionId(), "updateDefinitionGroup");
	}

	@Test
	public void testGetValidationTypeThatValidatesRecordType2() {
		String id = "otherRecordTypeId";
		Collection<ValidationType> validationTypes = ValidationTypeProvider
				.getValidationTypesThatValidatesRecordType(id);
		ValidationType validationType = validationTypes.iterator().next();

		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");

		assertEquals(validationType.id(), "otherValidationType");
		assertEquals(validationType.validatesRecordTypeId(), "otherRecordTypeId");
		assertEquals(validationType.createDefinitionId(), "createDefinitionGroup");
		assertEquals(validationType.updateDefinitionId(), "updateDefinitionGroup");
	}

	@Test
	public void testGetValidationType_readTwoMakeSureOnlyLoadedOnce() {
		String id = "someValidationType";
		String otherId = "otherValidationType";

		ValidationType validationType = ValidationTypeProvider.getValidationType(id);
		ValidationType otherType = ValidationTypeProvider.getValidationType(otherId);

		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");
		assertEquals(validationType.id(), id);
		assertEquals(otherType.id(), otherId);
	}

	@Test
	public void testOnlyForTestAddValidationTypeToInternalMap() {
		String id = "id";
		String validatesRecordTypeId = "recordTypeId";
		String createDefinitionId = "createDefinitionId";
		String updateDefinitionId = "updateDefinitionId";

		ValidationType validationType = new ValidationType(id, validatesRecordTypeId,
				createDefinitionId, updateDefinitionId);

		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationType);

		ValidationType validationType2 = ValidationTypeProvider.getValidationType(id);
		assertSame(validationType2, validationType);
	}

	@Test
	public void testOnlyForTestAddValidationTypeToInternalMap2() {
		String id = "id";
		String validatesRecordTypeId = "recordTypeId";
		String createDefinitionId = "createDefinitionId";
		String updateDefinitionId = "updateDefinitionId";

		ValidationType validationType = new ValidationType(id, validatesRecordTypeId,
				createDefinitionId, updateDefinitionId);

		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationType);

		Collection<ValidationType> validationTypes = ValidationTypeProvider
				.getValidationTypesThatValidatesRecordType("recordTypeId");
		assertSame(validationTypes.iterator().next(), validationType);

	}
}
