package se.uu.ub.cora.fitnesseintegration.binary;

import java.text.MessageFormat;

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
		if (downloadOk(response)) {
			return responseCodeAsString(response);
		}
		return buildErrorResponse(response);
	}

	private boolean downloadOk(RestResponse response) {
		return response.responseCode() == 200;
	}

	private String responseCodeAsString(RestResponse response) {
		return String.valueOf(response.responseCode());
	}

	private String buildErrorResponse(RestResponse response) {
		return MessageFormat.format("Code: {0} Message: {1}", responseCodeAsString(response),
				response.responseText());
	}

	public RecordHandler onlyForTestgetRecordHandler() {
		return recordHandler;
	}

	public void onlyForTestSetRecordHandler(RecordHandler recordHandler) {
		this.recordHandler = recordHandler;
	}
}