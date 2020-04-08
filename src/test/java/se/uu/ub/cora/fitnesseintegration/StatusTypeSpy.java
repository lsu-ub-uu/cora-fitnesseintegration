package se.uu.ub.cora.fitnesseintegration;

import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;

public class StatusTypeSpy implements StatusType {

	public int statusCodeToReturn = 200;

	@Override
	public int getStatusCode() {
		// TODO Auto-generated method stub
		return statusCodeToReturn;
	}

	@Override
	public Family getFamily() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReasonPhrase() {
		// TODO Auto-generated method stub
		return null;
	}

}
