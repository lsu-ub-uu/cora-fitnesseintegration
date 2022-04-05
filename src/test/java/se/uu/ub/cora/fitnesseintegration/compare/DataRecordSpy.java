package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class DataRecordSpy implements DataRecord {

	public MethodCallRecorder MCR = new MethodCallRecorder();

	public ClientDataGroup clientDataGroup;

	@Override
	public ClientDataGroup getClientDataGroup() {
		MCR.addCall();
		if (clientDataGroup == null) {
			clientDataGroup = ClientDataGroup.withNameInData("clientDataGroupSpy");
		}
		MCR.addReturned(clientDataGroup);
		return clientDataGroup;
	}

	@Override
	public Set<String> getReadPermissions() {
		MCR.addCall();

		Set<String> emptySet = Collections.emptySet();
		MCR.addReturned(emptySet);
		return emptySet;
	}

	@Override
	public Set<String> getWritePermissions() {
		MCR.addCall();

		Set<String> emptySet = Collections.emptySet();
		MCR.addReturned(emptySet);
		return emptySet;
	}

	@Override
	public Map<String, ActionLink> getActionLinks() {
		MCR.addCall();

		Map<String, ActionLink> emptyMap = Collections.emptyMap();
		MCR.addReturned(emptyMap);
		return emptyMap;
	}

}
