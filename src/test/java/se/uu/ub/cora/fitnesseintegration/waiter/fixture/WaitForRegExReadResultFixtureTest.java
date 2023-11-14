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
package se.uu.ub.cora.fitnesseintegration.waiter.fixture;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.WaiterSpy;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter.WhatYouAreWaitingFor;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.StandardFitnesseMethodSpy;

public class WaitForRegExReadResultFixtureTest {

	private static final String SOME_ID = "someId";
	private static final String SOME_TYPE = "someType";
	private static final String SOME_AUTH_TOKEN = "someAuthToken";
	private DependencyFactorySpy dependencyFactory;
	private WaitForRegExReadResultFixture waiterFixture;

	@BeforeMethod
	private void beforeMethod() {

		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		waiterFixture = new WaitForRegExReadResultFixture();

		waiterFixture.setAuthToken(SOME_AUTH_TOKEN);
		waiterFixture.setRecordType(SOME_TYPE);
		waiterFixture.setRecordId(SOME_ID);
		waiterFixture.setSleepTime(1000);
		waiterFixture.setMaxNumberOfCalls(5);
		waiterFixture.setRegEx("master");
	}

	@Test
	public void testCallWaitForResultFixture() throws Exception {
		WaiterSpy waiterSpy = new WaiterSpy();
		waiterSpy.MRV.setDefaultReturnValuesSupplier("waitUntilConditionFullfilled", () -> true);
		dependencyFactory.MRV.setDefaultReturnValuesSupplier("factorWaiter", () -> waiterSpy);

		String result = waiterFixture.waitUntilUntilRegExpFoundInReadRecord();

		dependencyFactory.MCR.assertParameters("factorReadAndStoreRecord", 0, SOME_AUTH_TOKEN,
				SOME_TYPE, SOME_ID);

		StandardFitnesseMethodSpy methodToRun = (StandardFitnesseMethodSpy) dependencyFactory.MCR
				.getReturnValue("factorReadAndStoreRecord", 0);

		dependencyFactory.MCR.methodWasCalled("factorWaiter");

		waiterSpy.MCR.assertMethodWasCalled("waitUntilConditionFullfilled");

		waiterSpy.MCR.assertParameter("waitUntilConditionFullfilled", 0, "methodToRun",
				methodToRun);
		waiterSpy.MCR.assertParameter("waitUntilConditionFullfilled", 0, "sleepTime", 1000);
		waiterSpy.MCR.assertParameter("waitUntilConditionFullfilled", 0, "maxNumberOfCalls", 5);

		assertEquals(result, "Found");
	}

	@Test
	public void testCallWaitForResultFixture_RegExNotFound() throws Exception {
		String result = waiterFixture.waitUntilUntilRegExpFoundInReadRecord();

		assertEquals(result, "Not found");
	}

	@Test
	public void testCallConditionTrue() throws Exception {
		WhatYouAreWaitingFor condition = getCondition();

		waiterFixture.setRegEx("master");
		DataHolder.setRecordAsJson("master");

		assertTrue(condition.completed());
	}

	@Test
	public void testCallConditionFalse() throws Exception {
		WhatYouAreWaitingFor condition = getCondition();

		waiterFixture.setRegEx("master");
		DataHolder.setRecordAsJson("not found");

		assertFalse(condition.completed());
	}

	private WhatYouAreWaitingFor getCondition() {
		waiterFixture.waitUntilUntilRegExpFoundInReadRecord();
		WaiterSpy waiterSpy = (WaiterSpy) dependencyFactory.MCR.getReturnValue("factorWaiter", 0);

		WhatYouAreWaitingFor condition = (WhatYouAreWaitingFor) waiterSpy.MCR
				.getValueForMethodNameAndCallNumberAndParameterName("waitUntilConditionFullfilled",
						0, "whatYouAreWaitingFor");
		return condition;
	}

}
