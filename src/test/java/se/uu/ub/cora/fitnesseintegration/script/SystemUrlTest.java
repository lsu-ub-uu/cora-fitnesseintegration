/*
 * Copyright 2017 Uppsala University Library
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

public class SystemUrlTest {

	@Test
	public void testConstructorNeededByFitnesse() {
		SystemUrl systemUrl = new SystemUrl();
		assertNotNull(systemUrl);
	}

	@Test
	public void testGetUrl() {
		SystemUrl.setUrl("http://localhost:8080/systemone/");
		assertEquals(SystemUrl.getUrl(), "http://localhost:8080/systemone/");
	}

	@Test
	public void testGetRestUrl() {
		SystemUrl.setUrl("http://localhost:8080/systemone/");
		assertEquals(SystemUrl.getRestUrl(), "http://localhost:8080/systemone/rest/");
	}

	@Test
	public void testGetTokenVerifierUrl() {
		SystemUrl.setTokenVerifierUrl("http://localhost:8180/tokenverifier/");
		assertEquals(SystemUrl.getTokenVerifierUrl(), "http://localhost:8180/tokenverifier/");
	}

	@Test
	public void testGetAppTokenVerifierUrl() {
		SystemUrl.setAppTokenVerifierUrl("http://localhost:8180/apptokenverifier/");
		assertEquals(SystemUrl.getAppTokenVerifierUrl(), "http://localhost:8180/apptokenverifier/");
	}

	@Test
	public void testGetIdpLoginUrl() {
		SystemUrl.setIdpLoginUrl("http://localhost:8380/idplogin/");
		assertEquals(SystemUrl.getIdpLoginUrl(), "http://localhost:8380/idplogin/");
	}

	@Test
	public void testGetGatekeeperServerUrl() {
		SystemUrl.setGatekeeperServerUrl("http://localhost:8281/gatekeeperserver/rest/authToken");
		assertEquals(SystemUrl.getGatekeeperServerUrl(),
				"http://localhost:8281/gatekeeperserver/rest/authToken");
	}
}
