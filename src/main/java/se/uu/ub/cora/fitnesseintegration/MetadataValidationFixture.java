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

import javax.ws.rs.core.Response;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class MetadataValidationFixture extends RecordEndpointFixture {

	private HttpHandlerFactory httpHandlerFactory;
	private String validateLinks;
	private String dataDivider;
	private String validationOrderType;
	private String jsonRecordToValidate;
	private String valid;

	public MetadataValidationFixture() {
		httpHandlerFactory = DependencyProvider.getHttpHandlerFactory();
	}

	public String testGetValidationOrder() {
		ClientDataGroup validationOrder = createValidationOrder();
		String validationOrderAsJson = convertToJsonStringFromClientDataGroup(validationOrder);
		return "{\"order\":" + validationOrderAsJson + ",	\"record\":" + jsonRecordToValidate
				+ "}";
	}

	private ClientDataGroup createValidationOrder() {
		ClientDataGroup validationOrder = ClientDataProvider
				.createGroupUsingNameInData("validationOrder");
		createAndAddRecordInfo(validationOrder);
		createAndAddRecordTypeGroup(validationOrder);
		createAndAddAtomicValues(validationOrder);
		return validationOrder;
	}

	private void createAndAddRecordInfo(ClientDataGroup validationOrder) {
		ClientDataGroup recordInfo = ClientDataProvider.createGroupUsingNameInData("recordInfo");
		ClientDataGroup dataDividerGroup = createLinkUsingNameInClientDataRecordTypeAndRecordId(
				"dataDivider", "system", dataDivider);
		recordInfo.addChild(dataDividerGroup);
		validationOrder.addChild(recordInfo);
	}

	private ClientDataGroup createLinkUsingNameInClientDataRecordTypeAndRecordId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		ClientDataGroup linkGroup = ClientDataProvider.createGroupUsingNameInData(nameInData);
		linkGroup.addChild(ClientDataProvider
				.createAtomicUsingNameInDataAndValue("linkedRecordType", linkedRecordType));
		linkGroup.addChild(ClientDataProvider.createAtomicUsingNameInDataAndValue("linkedRecordId",
				linkedRecordId));
		return linkGroup;
	}

	private void createAndAddRecordTypeGroup(ClientDataGroup validationOrder) {
		ClientDataGroup recordTypeGroup = createLinkUsingNameInClientDataRecordTypeAndRecordId(
				"recordType", "recordType", validationOrderType);
		validationOrder.addChild(recordTypeGroup);
	}

	private void createAndAddAtomicValues(ClientDataGroup validationOrder) {
		validationOrder.addChild(ClientDataProvider
				.createAtomicUsingNameInDataAndValue("validateLinks", validateLinks));
		validationOrder.addChild(ClientDataProvider
				.createAtomicUsingNameInDataAndValue("metadataToValidate", "existing"));
	}

	private String convertToJsonStringFromClientDataGroup(ClientDataGroup clientDataGroup) {
		ClientDataToJsonConverterFactory dataToJsonConverterFactory = ClientDataToJsonConverterProvider
				.createImplementingFactory();
		ClientDataToJsonConverter toJsonConverter = dataToJsonConverterFactory
				.factorUsingConvertible(clientDataGroup);
		return toJsonConverter.toJson();
	}

	public String testValidateRecord() {
		RecordHandler recordHandler = getRecordHandler();
		// BasicHttpResponse response = recordHandler.validateRecord(baseRecordUrl + "workOrder",
		// getSetAuthTokenOrAdminAuthToken(), json, "application/vnd.uub.workorder+json");
		RestResponse response = recordHandler.validateRecord(getSetAuthTokenOrAdminAuthToken(),
				json);
		statusType = Response.Status.fromStatusCode(response.responseCode());
		return getResponseTextFromHttpHandler(response);
	}

	private String getResponseTextFromHttpHandler(RestResponse response) {
		if (responseIsOk()) {
			return getValidationResponseText(response);
		}
		return response.responseText();
	}

	private String getValidationResponseText(RestResponse response) {
		String responseText = response.responseText();
		extractAndSetValidValue(responseText);
		return responseText;
	}

	private void extractAndSetValidValue(String responseText) {
		ClientDataRecord validationResultRecord = convertJsonToClientDataRecord(responseText);

		ClientDataRecordGroup dataGroup = validationResultRecord.getDataRecordGroup();
		valid = dataGroup.getFirstAtomicValueWithNameInData("valid");
	}

	@Override
	public HttpHandlerFactory getHttpHandlerFactory() {
		return httpHandlerFactory;
	}

	public void setValidateLinks(String validateLinks) {
		this.validateLinks = validateLinks;

	}

	public void setDataDivider(String dataDivider) {
		this.dataDivider = dataDivider;

	}

	public void setValidationOrderRecordType(String validationOrderRecordType) {
		this.validationOrderType = validationOrderRecordType;

	}

	public void setJsonRecordToValidate(String jsonRecordToValidate) {
		this.jsonRecordToValidate = jsonRecordToValidate;
	}

	public String getValid() {
		return valid;
	}
}
