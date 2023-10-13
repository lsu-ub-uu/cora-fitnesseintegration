/*
 * Copyright 2020 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataRecord;

public class ComparerFactorySpy implements ComparerFactory {

	public ComparerSpy factoredComparer;
	public List<ComparerSpy> factoredComparers = new ArrayList<>();
	public ClientDataRecord dataRecord;
	public List<ClientDataRecord> dataRecords = new ArrayList<>();
	public int numberOfErrorsToReturn = 0;
	public boolean spyShouldThrowError = false;
	public String type;

	@Override
	public DataComparer factor(String type, ClientDataRecord dataRecord) {
		this.type = type;
		this.dataRecord = dataRecord;
		dataRecords.add(dataRecord);
		factoredComparer = new ComparerSpy();
		factoredComparer.type = type;
		factoredComparer.spyShouldThrowError = spyShouldThrowError;
		factoredComparers.add(factoredComparer);
		factoredComparer.numberOfErrorsToReturn = numberOfErrorsToReturn;
		return factoredComparer;
	}
}
