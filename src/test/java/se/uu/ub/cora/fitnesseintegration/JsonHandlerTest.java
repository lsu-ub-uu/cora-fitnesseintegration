/*
 * Copyright 2020 Uppsala University Library
 * Copyright 2023 Olov McKie
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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.json.parser.JsonArray;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonValue;

public class JsonHandlerTest {

	private JsonHandler jsonHandler;
	private JsonParserSpy jsonParser;

	@BeforeMethod
	public void setUp() {
		jsonParser = new JsonParserSpy();
		jsonHandler = JsonHandlerImp.usingJsonParser(jsonParser);
	}

	@Test
	public void testGetJsonParser() {
		assertSame(((JsonHandlerImp) jsonHandler).onlyForTestGetJsonParser(), jsonParser);
	}

	@Test
	public void testGetAsValue() {
		String jsonString = "{\"example\":[{\"textVariable\":\"someTextId\"}]}";
		JsonValue jsonValue = jsonHandler.parseStringAsValue(jsonString);
		assertEquals(jsonString, jsonParser.jsonStringSentToParser);
		assertSame(jsonParser.jsonValueSpy, jsonValue);
	}

	@Test
	public void testGetAsObject() {
		String jsonString = "{\"example\":[{\"textVariable\":\"someTextId\"}]}";
		JsonObject jsonObject = jsonHandler.parseStringAsObject(jsonString);
		assertEquals(jsonString, jsonParser.jsonStringsSentToParser.get(0));
		assertSame(jsonParser.jsonObjectSpy, jsonObject);
	}

	@Test
	public void testGetAsArray() {
		String jsonString = "{\"example\":[{\"textVariable\":\"someTextId\"}]}";
		JsonArray jsonArray = jsonHandler.parseStringAsArray(jsonString);
		assertEquals(jsonString, jsonParser.jsonStringSentToParser);
		assertSame(jsonParser.jsonArraySpy, jsonArray);
	}
}
