package se.uu.ub.cora.fitnesseintegration;

import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.data.Convertible;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterSpy implements JsonToDataConverter {

	public boolean toInstanceWasCalled = false;
	public String json;
	public JsonValue jsonValue;

	public JsonToDataConverterSpy(String json) {
		this.json = json;
	}

	public JsonToDataConverterSpy(JsonValue jsonValue) {
		this.jsonValue = jsonValue;
	}

	@Override
	public Convertible toInstance() {
		toInstanceWasCalled = true;
		ClientDataGroup clientDataGroup = ClientDataGroup.withNameInData("someTopLevelDataGroup");
		clientDataGroup
				.addChild(ClientDataAtomic.withNameInDataAndValue("nameInData", "someNameInData"));
		return clientDataGroup;
	}

}
