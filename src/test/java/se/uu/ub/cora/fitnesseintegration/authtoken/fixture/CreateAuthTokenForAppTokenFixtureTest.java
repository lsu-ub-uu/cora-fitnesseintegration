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
package se.uu.ub.cora.fitnesseintegration.authtoken.fixture;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.fitnesseintegration.spy.JavaClientFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.TokenClientSpy;
import se.uu.ub.cora.javaclient.AppTokenCredentials;
import se.uu.ub.cora.javaclient.JavaClientProvider;

public class CreateAuthTokenForAppTokenFixtureTest {
	private CreateAuthTokenForAppTokenFixture fixture;
	private JavaClientFactorySpy javaClientFactory;

	@BeforeMethod
	public void setUp() {
		SystemUrl.setAppTokenVerifierUrl("someAppTokenVerifierUrl/");

		javaClientFactory = new JavaClientFactorySpy();
		JavaClientProvider.onlyForTestSetJavaClientFactory(javaClientFactory);

		fixture = new CreateAuthTokenForAppTokenFixture();
	}

	@Test
	public void testGetAuthTokenForAppToken() throws Exception {
		fixture.setUserId("someUserId");
		fixture.setAppToken("someAppToken");

		String authToken = fixture.getAuthTokenUsingUserIdAndAppToken();

		AppTokenCredentials expectedAppTokenCredentials = new AppTokenCredentials(
				"someAppTokenVerifierUrl/rest/", "someUserId", "someAppToken");

		javaClientFactory.MCR.assertParameterAsEqual("factorTokenClientUsingAppTokenCredentials", 0,
				"appTokenCredentials", expectedAppTokenCredentials);

		TokenClientSpy tokenClient = (TokenClientSpy) javaClientFactory.MCR
				.getReturnValue("factorTokenClientUsingAppTokenCredentials", 0);
		tokenClient.MCR.assertReturn("getAuthToken", 0, authToken);
	}

	@Test
	public void testGetAuthTokenForAppTokenThrowsExcpetion() throws Exception {
		javaClientFactory.MRV.setAlwaysThrowException("factorTokenClientUsingAppTokenCredentials",
				new RuntimeException("Spy error Message."));

		fixture.setUserId("someUserId");
		fixture.setAppToken("someAppToken");

		String authToken = fixture.getAuthTokenUsingUserIdAndAppToken();

		assertEquals(authToken,
				"Failed to get authToken using user id and appToken: Spy error Message.");
	}
}
