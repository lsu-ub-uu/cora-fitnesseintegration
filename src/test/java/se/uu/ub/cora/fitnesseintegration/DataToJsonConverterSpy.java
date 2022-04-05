package se.uu.ub.cora.fitnesseintegration;

import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.json.builder.JsonObjectBuilder;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataToJsonConverterSpy extends DataToJsonConverter {

	MethodCallRecorder MCR = new MethodCallRecorder();

	@Override
	public String toJson() {
		MCR.addCall();
		// TODO Auto-generated method stub
		String aJson = "";
		MCR.addReturned(aJson);
		return aJson;
	}

	@Override
	protected JsonObjectBuilder toJsonObjectBuilder() {
		MCR.addCall();

		return null;
	}

}
