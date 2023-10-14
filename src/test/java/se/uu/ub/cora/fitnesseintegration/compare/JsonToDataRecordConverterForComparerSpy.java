/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToClientDataRecordConverter;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordOLDSpy;
import se.uu.ub.cora.json.parser.JsonObject;

public class JsonToClientDataRecordConverterForComparerSpy implements JsonToClientDataRecordConverter {

	public JsonObject jsonObject;
	public List<JsonObject> jsonObjects = new ArrayList<>();
	public ClientDataRecordOLDSpy clientClientDataRecordSpy;
	public List<ClientDataRecordOLDSpy> returnedSpies = new ArrayList<>();

	@Override
	public ClientDataRecord toInstance(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		jsonObjects.add(jsonObject);
		clientClientDataRecordSpy = new ClientDataRecordOLDSpy();
		returnedSpies.add(clientClientDataRecordSpy);
		return clientClientDataRecordSpy;
	}

}
