package se.uu.ub.cora.fitnesseintegration.compare;

import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.JsonObjectSpy;
import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonParser;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class JsonHandlerSpy implements JsonHandler {

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public JsonParser getJsonParser() {
		MCR.addCall();

		MCR.addReturned(null);
		return null;
	}

	@Override
	public JsonValue parseStringAsValue(String jsonString) {
		MCR.addCall("jsonString", jsonString);

		MCR.addReturned(null);
		return null;
	}

	@Override
	public JsonObject parseStringAsObject(String jsonString) {
		MCR.addCall("jsonString", jsonString);

		JsonObjectSpy returnObject = new JsonObjectSpy();

		MCR.addReturned(returnObject);
		return returnObject;
	}

	@Override
	public JsonArray parseStringAsArray(String jsonString) {
		MCR.addCall("jsonString", jsonString);

		MCR.addReturned(null);
		return null;
	}

}
