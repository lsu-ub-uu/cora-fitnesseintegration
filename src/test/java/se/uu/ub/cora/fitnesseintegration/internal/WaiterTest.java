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
package se.uu.ub.cora.fitnesseintegration.internal;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.spy.StandardFitnesseMethodSpy;

public class WaiterTest {
	private WaiterImp waiter;
	private StandardFitnesseMethodSpy methodSpy;
	private int sleepTime = 10;
	private int maxNumberOfCalls = 3;

	@BeforeMethod
	private void beforeMethod() {
		sleepTime = 10;
		maxNumberOfCalls = 3;
		waiter = new WaiterImp();
		methodSpy = new StandardFitnesseMethodSpy();
	}

	@Test
	public void testFirstCallReturnsTrue() throws Exception {
		boolean result = waiter.waitUntilConditionFullfilled(methodSpy, () -> true, sleepTime,
				maxNumberOfCalls);

		methodSpy.MCR.assertNumberOfCallsToMethod("run", 1);

		assertTrue(result);
	}

	@Test
	public void testCallNeverReturnsTrue_maxCallsMade() throws Exception {
		boolean result = waiter.waitUntilConditionFullfilled(methodSpy, () -> false, sleepTime,
				maxNumberOfCalls);

		methodSpy.MCR.assertNumberOfCallsToMethod("run", maxNumberOfCalls);

		assertFalse(result);
	}

	@Test
	public void testCallReturnsTrue_onSecondCall() throws Exception {
		boolean result = waiter.waitUntilConditionFullfilled(methodSpy, this::foundOnSecondCheck,
				sleepTime, maxNumberOfCalls);

		methodSpy.MCR.assertNumberOfCallsToMethod("run", 2);
		assertTrue(result);
	}

	private boolean foundOnSecondCheck() {
		if (methodSpy.MCR.getNumberOfCallsToMethod("run") == 2) {
			return true;
		}
		return false;
	}

	@Test
	public void testCallNeverReturnsTrue_checkingSleepTime() throws Exception {
		sleepTime = 100;

		Instant start = Instant.now();
		waiter.waitUntilConditionFullfilled(methodSpy, () -> false, sleepTime, 5);
		Instant end = Instant.now();

		Duration timeElapsed = Duration.between(start, end);
		assertTrue(timeElapsed.toMillis() < 600);
		assertTrue(timeElapsed.toMillis() > 499);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Fitnesse Waiter failed unexpectedly: spy exception")
	public void testCallThrowsException() throws Exception {
		methodSpy.MRV.setAlwaysThrowException("run", new RuntimeException("spy exception"));

		waiter.waitUntilConditionFullfilled(methodSpy, () -> false, sleepTime, maxNumberOfCalls);

		methodSpy.MCR.assertNumberOfCallsToMethod("run", maxNumberOfCalls);
	}

}
