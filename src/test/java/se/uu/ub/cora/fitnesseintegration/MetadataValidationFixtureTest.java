package se.uu.ub.cora.fitnesseintegration;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;

public class MetadataValidationFixtureTest {

	private MetadataValidationFixture fixture;
	private RecordHandlerSpy recordHandler;
	private JsonParserSpy jsonParser;
	private JsonHandlerImp jsonHandler;
	private JsonToDataRecordConverterSpy jsonToDataRecordConverter;
	private ClientDataRecordSpy clientDataRecordSpy;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setUrl("http://localhost:8080/therest/");
		AuthTokenHolder.setAdminAuthToken("someAdminToken");
		DependencyProvider.setHttpHandlerFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.HttpHandlerFactorySpy");
		DependencyProvider.setJsonToDataFactoryClassName(
				"se.uu.ub.cora.fitnesseintegration.JsonToDataConverterFactorySpy");
		fixture = new MetadataValidationFixture();
		setUpFixture();
	}

	private void setUpFixture() {
		fixture.setType("someType");
		fixture.setAuthToken("someToken");
		recordHandler = new RecordHandlerSpy();
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
		createToDataRecordConverterWithDefaultReturnRecord();

		fixture.setRecordHandler(recordHandler);
		fixture.setJsonHandler(jsonHandler);
		fixture.setJsonToDataRecordConverter(jsonToDataRecordConverter);
	}

	private void createToDataRecordConverterWithDefaultReturnRecord() {
		jsonToDataRecordConverter = new JsonToDataRecordConverterSpy();
		clientDataRecordSpy = createDataRecordSpyUsingValidValue("true");
		jsonToDataRecordConverter.clientDataRecordSpy = clientDataRecordSpy;
	}

	@Test
	public void init() {
		assertTrue(fixture.getHttpHandlerFactory() instanceof HttpHandlerFactorySpy);
		assertTrue(fixture.getJsonHandler() instanceof JsonHandlerImp);
	}

	@Test
	public void testGetValidationOrderForRecord() {
		fixture.setJsonRecordToValidate("{\"name\":\"value\"}");
		fixture.setValidateLinks("true");
		fixture.setDataDivider("someDataDivider");
		fixture.setValidationOrderRecordType("someRecordType");
		String validationOrderJson = fixture.testGetValidationOrder();
		String expectedJson = "{\"order\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"someDataDivider\"}],\"name\":\"dataDivider\"}],\"name\":\"recordInfo\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}],\"name\":\"recordType\"},{\"name\":\"validateLinks\",\"value\":\"true\"},{\"name\":\"metadataToValidate\",\"value\":\"existing\"}],\"name\":\"validationOrder\"},	\"record\":{\"name\":\"value\"}}";
		assertEquals(validationOrderJson, expectedJson);

	}

	@Test
	public void testValidateRecordDataForFactoryIsOk() {
		fixture.setId("someId");
		String json = "{\"name\":\"value\"}";
		fixture.setJson(json);
		String responseText = fixture.testValidateRecord();

		String expectedUrl = SystemUrl.getUrl() + "rest/record/workOrder";

		assertTrue(recordHandler.validateWasCalled);
		assertEquals(recordHandler.url, expectedUrl);
		assertEquals(recordHandler.authToken, "someToken");
		assertEquals(recordHandler.json, json);
		assertEquals(recordHandler.contentType, "application/vnd.uub.workorder+json");
		assertEquals(responseText, recordHandler.jsonToReturn);

	}

	private ClientDataRecordSpy createDataRecordSpyUsingValidValue(String valid) {
		ClientDataRecordSpy clientDataRecordSpy = new ClientDataRecordSpy();
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("workOrder");
		clientDataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("valid", valid));
		clientDataRecordSpy.clientDataGroup = clientDataGroup;
		return clientDataRecordSpy;
	}

	@Test
	public void testValidateRecordStatusTypeSetFromResponse() {
		fixture.testValidateRecord();
		assertEquals(fixture.getStatusType().getStatusCode(), recordHandler.statusTypeReturned);
	}

	@Test
	public void testValidateRecordOk() {
		String responseText = fixture.testValidateRecord();

		assertEquals(jsonParser.jsonStringsSentToParser.get(0), recordHandler.jsonToReturn);
		assertEquals(jsonToDataRecordConverter.jsonObject, jsonParser.jsonObjectSpies.get(0));
		assertEquals(jsonToDataRecordConverter.returnedSpies.get(0), clientDataRecordSpy);

		assertEquals(fixture.getValid(), "true");
		assertEquals(responseText, recordHandler.jsonToReturn);
	}

	@Test
	public void testValidateRecordNotOk() {
		ClientDataRecordSpy clientDataRecordSpy = createDataRecordSpyUsingValidValue("false");
		jsonToDataRecordConverter.clientDataRecordSpy = clientDataRecordSpy;
		String responseText = fixture.testValidateRecord();
		assertEquals(fixture.getValid(), "false");
		assertEquals(responseText, recordHandler.jsonToReturn);
	}

	@Test
	public void testValidateRecordIncorrectValidationOrder() {
		recordHandler.statusTypeReturned = 401;
		String responseText = fixture.testValidateRecord();
		assertEquals(responseText, recordHandler.jsonToReturn);
	}

}
