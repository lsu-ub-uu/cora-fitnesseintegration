package se.uu.ub.cora.fitnesseintegration;

import javax.ws.rs.core.Response;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

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

	private String convertToJsonStringFromClientDataGroup(ClientDataGroup validationOrder) {
		ClientDataToJsonConverterFactory dataToJsonConverterFactory = ClientDataToJsonConverterProvider
				.createImplementingFactory();
		ClientDataToJsonConverter toJsonConverter = dataToJsonConverterFactory
				.factorUsingConvertible(validationOrder);
		return toJsonConverter.toJson();
	}

	public String testValidateRecord() {
		RecordHandler recordHandler = getRecordHandler();
		BasicHttpResponse response = recordHandler.validateRecord(baseRecordUrl + "workOrder",
				getSetAuthTokenOrAdminAuthToken(), json, "application/vnd.uub.workorder+json");
		statusType = Response.Status.fromStatusCode(response.statusCode);
		return getResponseTextFromHttpHandler(response);
	}

	private String getResponseTextFromHttpHandler(BasicHttpResponse response) {
		if (responseIsOk()) {
			return getValidationResponseText(response);
		}
		return response.responseText;
	}

	private String getValidationResponseText(BasicHttpResponse response) {
		String responseText = response.responseText;
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
