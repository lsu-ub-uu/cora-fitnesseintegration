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
	private String baseUrl = "http://localhost:8080/systemone/rest/";

	private String authToken = "325e24c8-123f-42c5-a362-71f791891d72";

	@Test(enabled = false)
	public void testName() throws Exception {
		SystemUrl.setUrl(baseUrl);
		SystemUrl.setAppTokenVerifierUrl(apptokenUrl);
		writer = new DefinitionWriter();
		String definition = writer.writeDefinitionFromUsingDataChild(authToken, "binaryGroup");

		System.out.println(definition);
	}
}
