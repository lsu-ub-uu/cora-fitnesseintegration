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

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataRecordConverter;
import se.uu.ub.cora.fitnesseintegration.ClientDataRecordSpy;
import se.uu.ub.cora.json.parser.JsonObject;

public class JsonToDataRecordConverterForComparerSpy implements JsonToDataRecordConverter {

	public JsonObject jsonObject;
	public List<JsonObject> jsonObjects = new ArrayList<>();
	public ClientDataRecordSpy clientDataRecordSpy;
	public List<ClientDataRecordSpy> returnedSpies = new ArrayList<>();

	@Override
	public DataRecord toInstance(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		jsonObjects.add(jsonObject);
		clientDataRecordSpy = new ClientDataRecordSpy();
		returnedSpies.add(clientDataRecordSpy);
		return clientDataRecordSpy;
	}

}
