/*
 * Copyright 2025 Uppsala University Library
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
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.spies.ClientDataListSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
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
		createAndAddRecordToListToBeReturned("someValidationType");
		createAndAddRecordToListToBeReturned("otherValidationType");
	}

	private void createAndAddRecordToListToBeReturned(String recordTypeId) {
		ClientDataRecordSpy dataRecord = new ClientDataRecordSpy();
		dataRecord.MRV.setDefaultReturnValuesSupplier("getId", () -> recordTypeId);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> recordTypeId);

		dataRecord.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup", () -> recordGroup);
		listOfDataRecords.add(dataRecord);
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
	public void testGetRecordGroup() {
		String id = "someValidationType";

		ClientDataRecordGroup validationType = ValidationTypeProvider.getRecordGroup(id);

		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");
		assertEquals(validationType.getId(), id);
	}

	@Test
	public void testGetRecordGroup_readTwoMakeSureOnlyLoadedOnce() {
		String id = "someValidationType";
		String otherId = "otherValidationType";

		ClientDataRecordGroup validationType = ValidationTypeProvider.getRecordGroup(id);
		ClientDataRecordGroup otherType = ValidationTypeProvider.getRecordGroup(otherId);

		client.MCR.assertNumberOfCallsToMethod("readList", 1);
		client.MCR.assertParameters("readList", 0, "validationType");
		assertEquals(validationType.getId(), id);
		assertEquals(otherType.getId(), otherId);
	}

	@Test
	public void testOnlyForTestAddRecordGroupToInternalMap() {
		String id = "someId";
		ClientDataRecordGroupSpy clientDataRecordGroup = new ClientDataRecordGroupSpy();

		ValidationTypeProvider.onlyForTestAddRecordGroupToInternalMap(id, clientDataRecordGroup);

		ClientDataRecordGroup recordType = ValidationTypeProvider.getRecordGroup(id);
		assertSame(recordType, clientDataRecordGroup);
	}
}
