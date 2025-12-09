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
package se.uu.ub.cora.fitnesseintegration.fixture;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.cache.FitnesseJavaClientProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DataClientSpy;

public class UserFixtureTest {

	private UserFixture userFixture;
	private DataClientSpy client;

	@BeforeMethod
	private void beforeMethod() {
		client = new DataClientSpy();
		FitnesseJavaClientProvider.onlyForTestSetClient(
				FitnesseJavaClientProvider.FITNESSE_ADMIN_JAVA_CLIENT, client);

		userFixture = new UserFixture();
	}

	@Test
	public void testSetLoginId_ReadsUser() {
		userFixture.setUserId("someUserId");

		client.MCR.assertParameters("read", 0, "user", "someUserId");
	}

	@Test
	public void testCreateAppToken() {
		userFixture.setUserId("someUserId");

		String apptoken = userFixture.createApptoken();
		ClientDataRecordSpy userRecord = (ClientDataRecordSpy) client.MCR
				.assertCalledParametersReturn("read", "user", "someUserId");
	}

}
