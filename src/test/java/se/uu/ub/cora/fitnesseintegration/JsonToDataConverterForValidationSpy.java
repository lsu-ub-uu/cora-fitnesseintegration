package se.uu.ub.cora.fitnesseintegration;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.Convertible;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterForValidationSpy implements JsonToDataConverter {

	public boolean toInstanceWasCalled = false;
	public String json;
	public JsonValue jsonValue;
	public String isValid = "true";

	public JsonToDataConverterForValidationSpy(String json) {
		this.json = json;
	}

	public JsonToDataConverterForValidationSpy(JsonValue jsonValue) {
		this.jsonValue = jsonValue;
	}

	@Override
	public Convertible toInstance() {
		toInstanceWasCalled = true;
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("someTopLevelDataGroup");
		clientDataGroup
				.addChild(ClientDataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
		clientDataGroup.addChild(ClientDataAtomic.withNameInDataAndValue("valid", isValid));
		return clientDataGroup;
	}

}
