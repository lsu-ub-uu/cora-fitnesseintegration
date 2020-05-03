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

public class RecordHandlerSpy implements RecordHandler {

	public boolean readRecordListWasCalled = false;
	public boolean searchRecordWasCalled = false;
	public String url;
	public String filter;
	public String authToken;
	public String jsonToReturn = "some json returned from spy";
	public boolean readRecordWasCalled = false;
	public String json;
	public int statusTypeReturned = 200;
	public String createdId;
	public String token;
	public boolean createRecordWasCalled = false;
	public boolean updateRecordWasCalled = false;
	public boolean validateWasCalled = false;
	public boolean deleteRecordWasCalled = false;
	public String contentType;
	public String recordType;
	public String recordId;

	@Override
	public BasicHttpResponse readRecordList(String authToken, String recordType, String filter) {
		this.recordType = recordType;
		readRecordListWasCalled = true;
		this.filter = filter;
		this.authToken = authToken;

		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse readRecord(String authToken, String recordType, String recordId) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.recordId = recordId;
		readRecordWasCalled = true;
		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse searchRecord(String url, String authToken, String json) {
		searchRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public ExtendedHttpResponse createRecord(String authToken, String recordType, String json) {
		createRecordWasCalled = true;
		this.authToken = authToken;
		this.json = json;
		this.recordType = recordType;
		if (defaultStatusCodeUnchanged()) {
			statusTypeReturned = 201;
		}
		// if (statusTypeReturned == null) {
		// statusTypeReturned = new StatusTypeSpy();
		// statusTypeReturned.statusCodeToReturn = 201;
		// }
		BasicHttpResponse readResponse = new BasicHttpResponse(statusTypeReturned, jsonToReturn);

		createdId = "someCreatedId";
		token = "someToken";
		return new ExtendedHttpResponse(readResponse, createdId, token);
	}

	private boolean defaultStatusCodeUnchanged() {
		return statusTypeReturned == 200;
	}

	@Override
	public BasicHttpResponse updateRecord(String authToken, String recordType, String recordId,
			String json) {
		updateRecordWasCalled = true;
		this.recordType = recordType;
		this.recordId = recordId;
		this.authToken = authToken;
		this.json = json;
		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse validateRecord(String url, String authToken, String json,
			String contentType) {
		validateWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		this.contentType = contentType;
		// if (statusTypeReturned == null) {
		// statusTypeReturned = new StatusTypeSpy();
		// statusTypeReturned.statusCodeToReturn = 200;
		// }
		createdId = "someCreatedId";
		token = "someToken";
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse deleteRecord(String authToken, String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
		deleteRecordWasCalled = true;
		this.authToken = authToken;

		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse readIncomingLinks(String authToken, String recordType,
			String recordId) {
		this.authToken = authToken;
		this.recordType = recordType;
		this.recordId = recordId;
		// statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

}
