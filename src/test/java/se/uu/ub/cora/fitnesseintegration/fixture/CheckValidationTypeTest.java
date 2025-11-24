/*
 * Copyright 2025 Uppsala University Library
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.fitnesseintegration.fixture;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.fitnesseintegration.cache.ValidationTypeProvider;
import se.uu.ub.cora.fitnesseintegration.metadata.ValidationType;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DefinitionWriterSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class CheckValidationTypeTest {
	private CheckValidationType fixture;
	private DependencyFactorySpy dependencyFactory;
	private ValidationType validationType;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		ClientDataRecordGroupSpy recordGroup = new ClientDataRecordGroupSpy();
		recordGroup.MRV.setDefaultReturnValuesSupplier("getId", () -> "someId");

		createValidationType();
		ValidationTypeProvider.onlyForTestAddValidationTypeToInternalMap(validationType);

		fixture = new CheckValidationType();
		fixture.setId("id");
	}

	private void createValidationType() {
		String id = "id";
		String validatesRecordTypeId = "recordTypeId";
		String createDefinitionId = "createDefinitionId";
		String updateDefinitionId = "updateDefinitionId";

		validationType = new ValidationType(id, validatesRecordTypeId, createDefinitionId,
				updateDefinitionId);
	}

	@AfterMethod
	public void afterMethod() {
		ValidationTypeProvider.resetInternalHolder();
	}

	@Test
	public void testValidatesRecordType() {
		String value = fixture.validatesRecordType();

		assertEquals(value, "recordTypeId");
	}

	@Test
	public void testCreateDefinitionIs() {
		String definition = fixture.createDefinitionIs();

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId",
				"createDefinitionId");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definition);
	}

	@Test
	public void testUpdateDefinitionIs() {
		String definition = fixture.updateDefinitionIs();

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId",
				"updateDefinitionId");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definition);
	}

}
