/*
 * Copyright 2024 Uppsala University Library
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
import se.uu.ub.cora.clientdata.spies.ClientDataRecordSpy;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.MetadataHolder;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class MetadataHolderSpy implements MetadataHolder {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public MetadataHolderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("getDataRecordById", ClientDataRecordSpy::new);
	}

	@Override
	public void addDataRecord(ClientDataRecord dataRecord) {
		MCR.addCall("dataRecord", dataRecord);
	}

	@Override
	public ClientDataRecord getDataRecordById(String recordId) {
		return (ClientDataRecord) MCR.addCallAndReturnFromMRV("recordId", recordId);
	}
}
