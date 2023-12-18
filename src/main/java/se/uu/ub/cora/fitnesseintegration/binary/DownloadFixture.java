package se.uu.ub.cora.fitnesseintegration.binary;

import se.uu.ub.cora.fitnesseintegration.RecordHandler;
import se.uu.ub.cora.fitnesseintegration.RecordHandlerImp;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;

public class DownloadFixture {

	private RecordHandler recordHandler;

	public DownloadFixture() {
		recordHandler = new RecordHandlerImp(SystemUrl.getUrl() + "rest/",
				SystemUrl.getAppTokenVerifierUrl());
	}

	public void setAuthToken(String someAuthToken) {
		// TODO Auto-generated method stub

	}

	public void setType(String someType) {
		// TODO Auto-generated method stub

	}

	public void setId(String someId) {
		// TODO Auto-generated method stub

	}

	public void setRepresentation(String string) {
		// TODO Auto-generated method stub

	}

	public String testDownload() {
		// TODO Auto-generated method stub
		return "OK";
	}

	public String getMimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentLength() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getContentDisposition() {
		// TODO Auto-generated method stub
		return null;
	}

	public RecordHandler onlyForTestgetRecordHandler() {
		return recordHandler;
	}
}