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
package se.uu.ub.cora.fitnesseintegration;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientConvertible;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.json.parser.JsonObject;

public class JsonToClientDataRecordConverterForIndexBatchJobSpy
		implements JsonToClientDataConverter {

	public JsonObject jsonObject;
	public List<JsonObject> jsonObjects = new ArrayList<>();
	public ClientDataRecordForIndexBatchJobSpy clientClientDataRecordSpy;
	public List<ClientDataRecordForIndexBatchJobSpy> returnedSpies = new ArrayList<>();

	@Override
	public ClientDataRecord toInstance(JsonObject jsonObject) {
		this.jsonObject = jsonObject;
		jsonObjects.add(jsonObject);
		if (clientClientDataRecordSpy == null) {
			clientClientDataRecordSpy = new ClientDataRecordForIndexBatchJobSpy();
		}
		returnedSpies.add(clientClientDataRecordSpy);
		return clientClientDataRecordSpy;
	}

	@Override
	public ClientConvertible toInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
