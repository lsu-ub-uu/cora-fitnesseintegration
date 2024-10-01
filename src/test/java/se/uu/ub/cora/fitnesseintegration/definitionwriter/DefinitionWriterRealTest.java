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

public class DefinitionWriterRealTest {

	private DefinitionWriter writer;

	private String apptokenUrl = "https://cora.epc.ub.uu.se/systemone/login/rest/";
	private String baseUrl = "https://cora.epc.ub.uu.se/systemone/rest/";

	private String authToken = "d1aa70ec-4d87-4e5d-8e23-edc47e208e4d";

	@Test(enabled = true)
	public void testName() throws Exception {
		writer = new DefinitionWriter(baseUrl, apptokenUrl);
		String definition = writer.writeDefinitionFromUsingDataChild(authToken, "adminInfoGroup");

		System.out.println(definition);
	}
}