/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.apptoken.script;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.DataHolder;

public class ReadAppTokenFromRecordTest {

	private ReadAppTokenFromRecord readAppTokenFromRecord;

	@BeforeMethod
	private void beforeMethod() {
		String createdAppTokenRecordAsJson = """
				{"record":{"data":{"children":[{"name":"note","value":"my apptoken"},
				{"name":"token","value":"86e40d5b-f7b1-45ca-b3ab-62812362885a"}],"name":"appToken"}}}
								""";
		DataHolder.setRecordAsJson(createdAppTokenRecordAsJson);
		readAppTokenFromRecord = new ReadAppTokenFromRecord();
	}

	@Test
	public void testReadAppTokenFromRecord() throws Exception {
		String appToken = readAppTokenFromRecord.getAppTokenStringFromCreatedAppTokenRecord();

		assertEquals(appToken, "86e40d5b-f7b1-45ca-b3ab-62812362885a");
	}
}
