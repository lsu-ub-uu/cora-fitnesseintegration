package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class ClientDataRecordOLD2Spy implements ClientDataRecord {

	public MethodCallRecorder MCR = new MethodCallRecorder();

	public ClientDataGroup clientDataGroup;

	@Override
	public ClientDataGroup getDataRecordGroup() {
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
	public Map<String, ClientActionLink> getClientActionLinks() {
		MCR.addCall();

		Map<String, ClientActionLink> emptyMap = Collections.emptyMap();
		MCR.addReturned(emptyMap);
		return emptyMap;
	}

}
