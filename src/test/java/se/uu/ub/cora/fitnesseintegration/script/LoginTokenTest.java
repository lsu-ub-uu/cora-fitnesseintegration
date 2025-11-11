/*
 * Copyright 2025 Uppsala University Library
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

package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

public class LoginTokenTest {

	@Test
	public void testConstructorNeededByFitnesse() {
		LoginToken loginToken = new LoginToken();
		assertNotNull(loginToken);
	}

	@Test
	public void testFitnesseAdminLoginId() {
		LoginToken.setFitnesseAdminLoginId("someAdminLoginId");
		assertEquals(LoginToken.getFitnesseAdminLoginId(), "someAdminLoginId");
	}

	@Test
	public void testFitnesseAdminAppToken() {
		LoginToken.setFitnesseAdminAppToken("someAdminToken");
		assertEquals(LoginToken.getFitnesseAdminAppToken(), "someAdminToken");
	}

	@Test
	public void testFitnesseUserLoginId() {
		LoginToken.setFitnesseUserLoginId("someUserLoginId");
		assertEquals(LoginToken.getFitnesseUserLoginId(), "someUserLoginId");
	}

	@Test
	public void testSetFitnesseUserAppToken() {
		LoginToken.setFitnesseUserAppToken("someUserToken");
		assertEquals(LoginToken.getFitnesseUserAppToken(), "someUserToken");
	}
}
