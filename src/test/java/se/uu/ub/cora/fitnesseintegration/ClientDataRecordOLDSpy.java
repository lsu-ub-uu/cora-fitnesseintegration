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
package se.uu.ub.cora.fitnesseintegration;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientAction;
import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataProvider;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.clientdata.ClientDataRecordGroup;

public class ClientDataRecordOLDSpy implements ClientDataRecord, ClientData {

	public ClientDataRecordGroup clientDataGroup;

	@Override
	public ClientDataRecordGroup getDataRecordGroup() {
		if (clientDataGroup == null) {
			clientDataGroup = ClientDataProvider
					.createRecordGroupUsingNameInData("clientDataGroupSpy");
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
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDataRecordGroup(ClientDataRecordGroup dataRecordGroup) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addActionLink(ClientActionLink actionLink) {
		// TODO Auto-generated method stub

	}

	@Override
	public Optional<ClientActionLink> getActionLink(ClientAction action) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

	@Override
	public void addReadPermission(String readPermission) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addReadPermissions(Collection<String> readPermissions) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasReadPermissions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addWritePermission(String writePermission) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addWritePermissions(Collection<String> writePermissions) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasWritePermissions() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getSearchId() {
		// TODO Auto-generated method stub
		return null;
	}
}
