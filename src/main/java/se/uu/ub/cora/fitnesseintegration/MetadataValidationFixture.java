package se.uu.ub.cora.fitnesseintegration;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.json.builder.JsonBuilderFactory;
import se.uu.ub.cora.json.builder.org.OrgJsonBuilderFactoryAdapter;

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

		JsonBuilderFactory factory = new OrgJsonBuilderFactoryAdapter();
		DataToJsonConverterFactoryImp dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		DataToJsonConverter converter = dataToJsonConverterFactory
				.createForClientDataElement(factory, validationOrder);
		String validationOrderAsJson = converter.toJson();

		return "{\"order\":" + validationOrderAsJson + ",	\"record\":" + jsonRecordToValidate
				+ "}";

	}

	private ClientDataGroup createValidationOrder() {
		ClientDataGroup validationOrder = ClientDataGroup.withNameInData("validationOrder");
		createAndAddRecordInfo(validationOrder);
		createAndAddRecordTypeGroup(validationOrder);
		createAndAddAtomicValues(validationOrder);
		return validationOrder;
	}

	private void createAndAddRecordInfo(ClientDataGroup validationOrder) {
		ClientDataGroup recordInfo = ClientDataGroup.withNameInData("recordInfo");
		ClientDataGroup dataDividerGroup = createLinkUsingNameInDataRecordTypeAndRecordId(
				"dataDivider", "system", dataDivider);
		recordInfo.addChild(dataDividerGroup);
		validationOrder.addChild(recordInfo);
	}

	private ClientDataGroup createLinkUsingNameInDataRecordTypeAndRecordId(String nameInData,
			String linkedRecordType, String linkedRecordId) {
		ClientDataGroup linkGroup = ClientDataGroup.withNameInData(nameInData);
		linkGroup.addChild(
				ClientDataAtomic.withNameInDataAndValue("linkedRecordType", linkedRecordType));
		linkGroup.addChild(
				ClientDataAtomic.withNameInDataAndValue("linkedRecordId", linkedRecordId));
		return linkGroup;
	}

	private void createAndAddRecordTypeGroup(ClientDataGroup validationOrder) {
		ClientDataGroup recordTypeGroup = createLinkUsingNameInDataRecordTypeAndRecordId(
				"recordType", "recordType", validationOrderType);
		validationOrder.addChild(recordTypeGroup);
	}

	private void createAndAddAtomicValues(ClientDataGroup validationOrder) {
		validationOrder
				.addChild(ClientDataAtomic.withNameInDataAndValue("validateLinks", validateLinks));
		validationOrder.addChild(
				ClientDataAtomic.withNameInDataAndValue("metadataToValidate", "existing"));
	}

	public String testValidateRecord() {
		RecordHandler recordHandler = getRecordHandler();
		BasicHttpResponse response = recordHandler.validateRecord(baseRecordUrl + "workOrder",
				getSetAuthTokenOrAdminAuthToken(), json, "application/vnd.uub.workorder+json");
		statusType = response.statusType;
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
		DataRecord validationResultRecord = convertJsonToClientDataRecord(responseText);

		ClientDataGroup dataGroup = validationResultRecord.getClientDataGroup();
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
