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

import java.util.Optional;

import se.uu.ub.cora.javaclient.rest.RestResponse;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;

public class RecordHandlerOLDSpy implements RecordHandler {

	public boolean readRecordListWasCalled = false;
	public boolean searchRecordWasCalled = false;
	public String filter;
	public String authToken;
	public String jsonToReturnDefault = "some json returned from spy";
	public boolean readRecordWasCalled = false;
	public String json;
	/**
	 * statusTypeReturned set 200 as default. This flag can be used to set the status code for the
	 * response.
	 */
	public int statusTypeReturned = 200;
	public String createdId = "someCreatedId";
	public String token;
	public boolean createRecordWasCalled = false;
	public boolean updateRecordWasCalled = false;
	public boolean validateWasCalled = false;
	public boolean deleteRecordWasCalled = false;
	public String contentType;
	public String recordType;
	public String recordId;

	public MethodCallRecorder MCR = new MethodCallRecorder();
	private String jsonToReturn = null;

	@Override
	public RestResponse readRecordList(String authToken, String recordType, String filter) {
		MCR.addCall("authToken", authToken, "recordType", recordType, "filter", filter);
		this.recordType = recordType;
		readRecordListWasCalled = true;
		this.filter = filter;
		this.authToken = authToken;

		String jsonReturnForReadRecordList = jsonToReturnDefault;
		if (jsonToReturn != null)
			jsonReturnForReadRecordList = jsonToReturn;

		RestResponse restResponse = new RestResponse(statusTypeReturned,
				jsonReturnForReadRecordList, Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse readRecord(String authToken, String recordType, String recordId) {
		MCR.addCall("authToken", authToken, "recordType", recordType, "recordId", recordId);
		this.authToken = authToken;
		this.recordType = recordType;
		this.recordId = recordId;
		readRecordWasCalled = true;
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse searchRecord(String authToken, String searchId, String json) {
		MCR.addCall("authToken", authToken, "searchId", searchId, "json", json);
		searchRecordWasCalled = true;
		this.authToken = authToken;
		this.json = json;
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse createRecord(String authToken, String recordType, String json) {
		createRecordWasCalled = true;
		this.authToken = authToken;
		this.json = json;
		this.recordType = recordType;
		if (defaultStatusCodeUnchanged()) {
			statusTypeReturned = 201;
		}
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	private boolean defaultStatusCodeUnchanged() {
		return statusTypeReturned == 200;
	}

	@Override
	public RestResponse updateRecord(String authToken, String recordType, String recordId,
			String json) {
		MCR.addCall("recordType", recordType, "authToken", authToken, "recordId", recordId);
		updateRecordWasCalled = true;
		this.recordType = recordType;
		this.recordId = recordId;
		this.authToken = authToken;
		this.json = json;
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse validateRecord(String authToken, String json) {
		validateWasCalled = true;
		this.authToken = authToken;
		this.json = json;
		// if (statusTypeReturned == null) {
		// statusTypeReturned = new StatusTypeSpy();
		// statusTypeReturned.statusCodeToReturn = 200;
		// }
		// createdId = "someCreatedId";
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse deleteRecord(String authToken, String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
		deleteRecordWasCalled = true;
		this.authToken = authToken;

		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	@Override
	public RestResponse readIncomingLinks(String authToken, String recordType, String recordId) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.recordId = recordId;
		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}

	public void setJsonToreturn(String jsonToReturn) {
		this.jsonToReturn = jsonToReturn;
	}

	@Override
	public RestResponse batchIndex(String authToken, String recordType, String filterAsJson) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.filter = filterAsJson;
		if (defaultStatusCodeUnchanged()) {
			statusTypeReturned = 201;
		}

		RestResponse restResponse = new RestResponse(statusTypeReturned, jsonToReturnDefault,
				Optional.empty());
		MCR.addReturned(restResponse);
		return restResponse;
	}
}
