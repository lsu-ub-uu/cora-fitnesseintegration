/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class DefinitionWriterRealTest {

	private DefinitionWriter writer;

	private String apptokenUrl = "http://localhost:8180/login/rest/";
	private String baseUrl = "http://localhost:8080/systemone/";

	private String authToken = "17f6be0c-61a4-4634-913d-724969553aee";

	@Test(enabled = false)
	public void testWrite() throws Exception {
		SystemUrl.setUrl(baseUrl);
		SystemUrl.setAppTokenVerifierUrl(apptokenUrl);
		writer = new DefinitionWriter();

		String definition = writer.writeDefinitionUsingRecordId(authToken, "binaryGroup");
		System.out.println(definition);
	}
}
