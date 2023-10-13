/*
 * Copyright 2021 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration;

import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataAtomic;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class ClientDataRecordForIndexBatchJobSpy implements ClientDataRecord, ClientData {

	public ClientDataGroup clientDataGroup;

	public int callsUntilFinished;
	public int callsToGetClientDataGroup;

	@Override
	public ClientDataGroup getDataRecordGroup() {
		callsToGetClientDataGroup++;
		if (clientDataGroup == null) {
			clientDataGroup = createBatchJobDataGroupWithStatus("notFinished");
		}
		if (callsToGetClientDataGroup == callsUntilFinished) {
			clientDataGroup = createBatchJobDataGroupWithStatus("finished");
		}

		return clientDataGroup;
	}

	@Override
	public Set<String> getReadPermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getWritePermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, ClientActionLink> getClientActionLinks() {
		// TODO Auto-generated method stub
		return null;
	}

	private ClientDataGroup createBatchJobDataGroupWithStatus(String status) {
		ClientDataGroup indexBatchJobDataGroup = ClientDataGroup.withNameInData("indexBatchJob");

		ClientDataAtomic statusDataAtomic = ClientDataAtomic.withNameInDataAndValue("status",
				status);
		indexBatchJobDataGroup.addChild(statusDataAtomic);

		return indexBatchJobDataGroup;
	}
}
