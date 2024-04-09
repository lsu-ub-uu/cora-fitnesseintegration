/*
 * Copyright 2024 Uppsala University Library
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