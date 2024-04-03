package se.uu.ub.cora.fitnesseintegration.binary;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class DownloadFixture {

	private RecordHandler recordHandler;
	private String authToken;
	private String recordType;
	private String recordId;
	private String representation;
	private Status statusType;

	public DownloadFixture() {
		recordHandler = new RecordHandlerImp(SystemUrl.getUrl() + "rest/",
				SystemUrl.getAppTokenVerifierUrl());
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public void setRepresentation(String representation) {
		this.representation = representation;
	}

	public String testDownload() {
		RestResponse response = recordHandler.download(authToken, recordType, recordId,
				representation);
		statusType = Response.Status.fromStatusCode(response.responseCode());

		return response.responseText();
	}

	public RecordHandler onlyForTestgetRecordHandler() {
		return recordHandler;
	}

	public void onlyForTestSetRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}

	public Status getStatusType() {
		return statusType;
	}
}