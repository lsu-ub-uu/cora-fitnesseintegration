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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

public class ExtendedHttpResponseTest {

	@Test
	public void testCreateResponse() {
		int statusCode = 200;
		String responseText = "some response text";
		BasicHttpResponse basicResponse = new BasicHttpResponse(statusCode, responseText);
		String createdId = "someCreatedId";
		String token = "someToken";
		ExtendedHttpResponse reseponse = new ExtendedHttpResponse(basicResponse, createdId, token);

		assertEquals(reseponse.statusCode, statusCode);
		assertSame(reseponse.responseText, responseText);
		assertSame(reseponse.createdId, createdId);
		assertSame(reseponse.token, token);

	}

	@Test
	public void testCreateResponseCreatedWithOnlyReadResponse() {
		int statusType = 200;
		String responseText = "some response text";
		BasicHttpResponse basicResponse = new BasicHttpResponse(statusType, responseText);
		ExtendedHttpResponse reseponse = new ExtendedHttpResponse(basicResponse);

		assertEquals(reseponse.statusCode, statusType);
		assertSame(reseponse.responseText, responseText);
		assertSame(reseponse.createdId, "");
		assertSame(reseponse.token, "");

	}
}
