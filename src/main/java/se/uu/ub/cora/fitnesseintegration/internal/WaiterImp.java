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

public class WaiterImp implements Waiter {
	int numberOfReads = 0;
	boolean continueRunning = true;
	private StandardFitnesseMethod methodToRun;
	private WhatYouAreWaitingFor whatYouAreWaitingFor;
	private int sleepTime;
	private int maxNumberOfCalls;

	@Override
	public boolean waitUntilConditionFullfilled(StandardFitnesseMethod methodToRun,
			WhatYouAreWaitingFor whatYouAreWaitingFor, int sleepTime, int maxNumberOfCalls) {
		this.methodToRun = methodToRun;
		this.whatYouAreWaitingFor = whatYouAreWaitingFor;
		this.sleepTime = sleepTime;
		this.maxNumberOfCalls = maxNumberOfCalls;

		return tryWaiting();
	}

	private boolean tryWaiting() {
		try {
			return waiting();
		} catch (Exception e) {
			throw new RuntimeException("Fitnesse Waiter failed unexpectedly: " + e.getMessage());
		}
	}

	private boolean waiting() throws InterruptedException {
		while (continueRunning) {
			Thread.sleep(sleepTime);

			methodToRun.run();

			numberOfReads++;
			if (whatYouAreWaitingFor.completed()) {
				return true;
			}
			if (numberOfReads >= maxNumberOfCalls) {
				continueRunning = false;
			}

		}
		return false;
	}
}