/*
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.fitnesseintegration.automator;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DefinitionAutomatorSpy implements DefinitionAutomator {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DefinitionAutomatorSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("createTestForRecordType",
				() -> "createTestForRecordType");
		MRV.setDefaultReturnValuesSupplier("createTestForValidationType",
				() -> "createTestForValidationType");
		MRV.setDefaultReturnValuesSupplier("createTestForRecordAndValidationType",
				() -> "createTestForRecordAndValidationType");

	}

	@Override
	public String createTestForRecordType(String recordType) {
		return (String) MCR.addCallAndReturnFromMRV("recordType", recordType);
	}

	@Override
	public String createTestForValidationType(String validationTypeId) {
		return (String) MCR.addCallAndReturnFromMRV("validationTypeId", validationTypeId);
	}

	@Override
	public String createTestForRecordAndValidationType(String recordType) {
		return (String) MCR.addCallAndReturnFromMRV("recordType", recordType);
	}

}
