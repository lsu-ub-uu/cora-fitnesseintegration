/*
 * Copyright 2023 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.spy;

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.WaiterSpy;
import se.uu.ub.cora.fitnesseintegration.compare.ComparerOldSpy;
import se.uu.ub.cora.fitnesseintegration.compare.DataComparer;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.internal.StandardFitnesseMethod;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactory;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class DependencyFactorySpy implements DependencyFactory {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public DependencyFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorReadAndStoreRecord",
				StandardFitnesseMethodSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorReadAndStoreRecordAsJson",
				StandardFitnesseMethodSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorWaiter", WaiterSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorDefinitionWriter", DefinitionWriterSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorPermissionComparer", ComparerOldSpy::new);
		MRV.setDefaultReturnValuesSupplier("factorActionComparer", ComparerOldSpy::new);
	}

	@Override
	public StandardFitnesseMethod factorReadAndStoreRecord(String authToken, String type,
			String id) {
		return (StandardFitnesseMethod) MCR.addCallAndReturnFromMRV("authToken", authToken, "type",
				type, "id", id);
	}

	@Override
	public StandardFitnesseMethod factorReadAndStoreRecordAsJson(String authToken, String type,
			String id) {
		return (StandardFitnesseMethod) MCR.addCallAndReturnFromMRV("authToken", authToken, "type",
				type, "id", id);
	}

	@Override
	public Waiter factorWaiter() {
		return (Waiter) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public DefinitionWriter factorDefinitionWriter() {
		return (DefinitionWriter) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public DataComparer factorPermissionComparer(ClientDataRecord dataRecord) {
		return (DataComparer) MCR.addCallAndReturnFromMRV("dataRecord", dataRecord);
	}

	@Override
	public DataComparer factorActionComparer(ClientDataRecord dataRecord) {
		return (DataComparer) MCR.addCallAndReturnFromMRV("dataRecord", dataRecord);
	}
}
