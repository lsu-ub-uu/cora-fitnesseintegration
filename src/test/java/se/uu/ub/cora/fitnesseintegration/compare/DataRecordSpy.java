package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataRecordSpy implements DataRecord {
	MethodCallRecorder MCR = new MethodCallRecorder();

	public ClientDataGroup clientDataGroup;
	public Set<String> readPermissions = Collections.emptySet();;
	public Set<String> writePermissions = Collections.emptySet();;

	@Override
	public ClientDataGroup getClientDataGroup() {
		MCR.addCall();
		if (clientDataGroup == null) {
			clientDataGroup = ClientDataGroup.withNameInData("clientDataGroupSpy");
		}
		return clientDataGroup;
	}

	@Override
	public Set<String> getReadPermissions() {
		MCR.addCall();

		MCR.addReturned(readPermissions);
		return readPermissions;
	}

	@Override
	public Set<String> getWritePermissions() {
		MCR.addCall();
		MCR.addReturned(writePermissions);
		return writePermissions;
	}

	@Override
	public Map<String, ActionLink> getActionLinks() {
		MCR.addCall();
		return null;
	}

}
