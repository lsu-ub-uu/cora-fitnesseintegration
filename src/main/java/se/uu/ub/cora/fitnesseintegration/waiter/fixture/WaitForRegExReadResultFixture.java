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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.internal.StandardFitnesseMethod;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class WaitForRegExReadResultFixture {

	private int millis;
	private int maxNoOfCalls;
	private String recordId;
	private String recordType;
	private String authToken;
	private String regEx;

	public WaitForRegExReadResultFixture() {
		// nedded for fitnesse
	}

	public String waitUntilUntilRegExpFoundInReadRecord() {
		Waiter waiter = DependencyProvider.factorWaiter();
		StandardFitnesseMethod methodToRun = DependencyProvider.factorReadAndStoreRecord(authToken,
				recordType, recordId);
		boolean found = waiter.waitUntilConditionFullfilled(methodToRun, this::condition, millis,
				maxNoOfCalls);
		if (found) {
			return "Found";
		}
		return "Not found";
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public void setSleepTime(int millis) {
		this.millis = millis;
	}

	public void setMaxNumberOfCalls(int maxNoOfCalls) {
		this.maxNoOfCalls = maxNoOfCalls;
	}

	public void setRegEx(String regEx) {
		this.regEx = regEx;

	}

	public boolean condition() {
		String recordAsJson = DataHolder.getRecordAsJson();
		return matchRegExp(recordAsJson, regEx);
	}

	private boolean matchRegExp(String recordAsJson, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(recordAsJson);
		return matcher.find();
	}
}
