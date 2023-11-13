/*
 * Copyright 2019, 2023 Uppsala University Library
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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordGroupSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterFactoryCreatorSpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.ClientDataToJsonConverterSpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterFactorySpy;
import se.uu.ub.cora.clientdata.spies.JsonToClientDataConverterSpy;
import se.uu.ub.cora.fitnesseintegration.script.AuthTokenHolder;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class MetadataValidationFixtureTest {
	JsonToClientDataConverterFactorySpy converterToClientFactorySpy;
	ClientDataToJsonConverterFactoryCreatorSpy toJsonFactoryCreator;

	private MetadataValidationFixture fixture;
	private RecordHandlerOLDSpy recordHandler;

	@BeforeMethod
	public void setUp() {
		converterToClientFactorySpy = new JsonToClientDataConverterFactorySpy();
		JsonToClientDataConverterProvider
				.setJsonToDataConverterFactory(converterToClientFactorySpy);

		toJsonFactoryCreator = new ClientDataToJsonConverterFactoryCreatorSpy();
		ClientDataToJsonConverterProvider
				.setDataToJsonConverterFactoryCreator(toJsonFactoryCreator);

		SystemUrl.setUrl("http://localhost:8080/therest/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		fixture = new MetadataValidationFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		fixture.setType("someType");
		fixture.setAuthToken("someToken");
		recordHandler = new RecordHandlerOLDSpy();
		fixture.onlyForTestSetRecordHandler(recordHandler);
	}

	@Test
	public void init() {
		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactorySpy);
	}

	@Test
	public void testGetValidationOrderForRecord() {
		String jsonRecordToValidate = "{\"name\":\"value\"}";
		fixture.setJsonRecordToValidate(jsonRecordToValidate);
		fixture.setValidateLinks("true");
		fixture.setDataDivider("someDataDivider");
		fixture.setValidationOrderRecordType("someRecordType");

		String validationOrderJson = fixture.testGetValidationOrder();

		String convertedJson = getFirstConvertedJsonFromSpies();
		String expectedJson = "{\"order\":" + convertedJson + ",	\"record\":"
				+ jsonRecordToValidate + "}";
		assertEquals(validationOrderJson, expectedJson);
	}

	private String getFirstConvertedJsonFromSpies() {
		ClientDataToJsonConverterFactorySpy toJsonConverterFacotory = (ClientDataToJsonConverterFactorySpy) toJsonFactoryCreator.MCR
				.getReturnValue("createFactory", 0);
		ClientDataToJsonConverterSpy toJsonConverter = (ClientDataToJsonConverterSpy) toJsonConverterFacotory.MCR
				.getReturnValue("factorUsingConvertible", 0);

		String convertedJson = (String) toJsonConverter.MCR.getReturnValue("toJson", 0);
		return convertedJson;
	}

	@Test
	public void testValidateRecordDataForFactoryIsOk() {
		fixture.setId("someId");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		setupConverterToClientFactorySpyToReturnClientDataRecordSpy();

		String responseText = fixture.testValidateRecord();

		assertTrue(recordHandler.validateWasCalled);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	private void setupConverterToClientFactorySpyToReturnClientDataRecordSpy() {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance", () -> clientDataRecordSpy);
		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	@Test
	public void testValidateRecordStatusTypeSetFromResponse() {
		setupConverterToClientFactorySpyToReturnClientDataRecordSpy();

		fixture.testValidateRecord();

		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testValidateRecordOk() {
		setupConverterFromJsonToReturnValidForClientDataRecordGroup("true");

		String responseText = fixture.testValidateRecord();

		assertEquals(fixture.getValid(), "true");
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	private void setupConverterFromJsonToReturnValidForClientDataRecordGroup(String valid) {
		ClientDataRecordGroupSpy clientDataRecordGroupFinished = new ClientDataRecordGroupSpy();
		clientDataRecordGroupFinished.MRV.setSpecificReturnValuesSupplier(
				"getFirstAtomicValueWithNameInData", () -> valid, "valid");
		ClientDataRecordSpy clientDataRecordValidSpy = new ClientDataRecordSpy();
		clientDataRecordValidSpy.MRV.setDefaultReturnValuesSupplier("getDataRecordGroup",
				() -> clientDataRecordGroupFinished);

		JsonToClientDataConverterSpy converterSpy = new JsonToClientDataConverterSpy();
		converterSpy.MRV.setDefaultReturnValuesSupplier("toInstance",
				() -> clientDataRecordValidSpy);
		converterToClientFactorySpy.MRV.setDefaultReturnValuesSupplier("factorUsingString",
				() -> converterSpy);
	}

	@Test
	public void testValidateRecordNotOk() {
		setupConverterFromJsonToReturnValidForClientDataRecordGroup("false");

		String responseText = fixture.testValidateRecord();

		assertEquals(fixture.getValid(), "false");
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

	@Test
	public void testValidateRecordIncorrectValidationOrder() {
		recordHandler.statusTypeReturned = 401;
		String responseText = fixture.testValidateRecord();
		assertEquals(responseText, recordHandler.jsonToReturnDefault);
	}

}
