package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;

public class DataRecordSpy implements DataRecord {

	public ClientDataGroup clientDataGroup;

	@Override
	public ClientDataGroup getClientDataGroup() {
		if (clientDataGroup == null) {
			clientDataGroup = ClientDataGroup.withNameInData("clientDataGroupSpy");
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
	public Map<String, ActionLink> getActionLinks() {
		// TODO Auto-generated method stub
		return null;
	}

}
