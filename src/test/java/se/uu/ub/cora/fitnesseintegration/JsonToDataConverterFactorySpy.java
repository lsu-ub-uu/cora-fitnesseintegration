/*
 * Copyright 2018, 2019 Uppsala University Library
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

import se.uu.ub.cora.clientdata.converter.jsontojava.JsonToDataClientActionLinkConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverter;
import se.uu.ub.cora.data.converter.JsonToDataConverterFactory;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonToDataConverterFactorySpy implements JsonToDataConverterFactory {

	public JsonToDataConverter factored;
	public String typeToFactor = "";
	public String isValid = "true";
	public JsonValue jsonValueSentToConverter;

	@Override
	public JsonToDataConverter createForJsonObject(JsonValue jsonValue) {
		this.jsonValueSentToConverter = jsonValue;
		if ("validatorSpy".equals(typeToFactor)) {
			factored = new JsonToDataConverterForValidationSpy(jsonValue);
			((JsonToDataConverterForValidationSpy) factored).isValid = isValid;
		} else {
			factored = new JsonToDataConverterSpy(jsonValue);
		}
		return factored;
	}

	@Override
	public JsonToDataConverter createForJsonString(String json) {
		if ("validatorSpy".equals(typeToFactor)) {
			factored = new JsonToDataConverterForValidationSpy(json);
			((JsonToDataConverterForValidationSpy) factored).isValid = isValid;
		} else {
			factored = new JsonToDataConverterSpy(json);
		}
		return factored;
	}

	@Override
	public JsonToDataClientActionLinkConverter createClientActionLinksConverterForJsonString(String json) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JsonToDataClientActionLinkConverter createJsonToDataClientActionLinkConverterForJsonObject(
			JsonValue jsonValue) {
		return new JsonToDataClientActionLinkConverterSpy(jsonValue);
	}

}
