package se.uu.ub.cora.fitnesseintegration;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.javatojson.DataToJsonConverterFactoryImp;

public class StoredData {
	// TODO:spike...

	String getStoredRecordDataGroupAsJsonWithoutLinks() {
		ClientDataGroup dataGroup = DataHolder.getRecord().getClientDataGroup();
		DataToJsonConverter converter = createConverterWithoutLinks(dataGroup);
		return converter.toJson();
	}

	private DataToJsonConverter createConverterWithoutLinks(ClientDataGroup dataGroup) {
		DataToJsonConverterFactory dataToJsonConverterFactory = new DataToJsonConverterFactoryImp();
		return dataToJsonConverterFactory.createForClientDataElementIncludingActionLinks(dataGroup,
				false);
	}
}
