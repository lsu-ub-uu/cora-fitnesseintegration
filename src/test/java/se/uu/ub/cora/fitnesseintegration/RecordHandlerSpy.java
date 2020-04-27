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
	public boolean readRecordWasCalled = false;
	public boolean createRecordWasCalled = false;
	public boolean updateRecordWasCalled = false;
	public boolean validateWasCalled = false;
	public boolean deleteRecordWasCalled = false;
	public String url;
	public String filter;
	public String authToken;
	public String jsonToReturn = "some json returned from spy";
	public String json;
	public StatusTypeSpy statusTypeReturned;
	public String createdId;
	public String token;
	public String contentType;
	public String contentLength;
	public String contentDisposition;

	@Override
	public BasicHttpResponse readRecordList(String url, String authToken, String filter) {
		readRecordListWasCalled = true;
		this.url = url;
		this.filter = filter;
		this.authToken = authToken;

		statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse readRecord(String url, String authToken) {
		this.url = url;
		this.authToken = authToken;
		readRecordWasCalled = true;
		statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse searchRecord(String url, String authToken, String json) {
		searchRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public ExtendedHttpResponse createRecord(String url, String authToken, String json) {
		createRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		if (statusTypeReturned == null) {
			statusTypeReturned = new StatusTypeSpy();
			statusTypeReturned.statusCodeToReturn = 201;
		}
		BasicHttpResponse readResponse = new BasicHttpResponse(statusTypeReturned, jsonToReturn);

		createdId = "someCreatedId";
		token = "someToken";
		return new ExtendedHttpResponse(readResponse, createdId, token);
	}

	@Override
	public BasicHttpResponse updateRecord(String url, String authToken, String json) {
		updateRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;
		this.json = json;
		statusTypeReturned = new StatusTypeSpy();
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
		if (statusTypeReturned == null) {
			statusTypeReturned = new StatusTypeSpy();
			statusTypeReturned.statusCodeToReturn = 200;
		}
		createdId = "someCreatedId";
		token = "someToken";
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public BasicHttpResponse deleteRecord(String url, String authToken) {
		deleteRecordWasCalled = true;
		this.url = url;
		this.authToken = authToken;

		statusTypeReturned = new StatusTypeSpy();
		return new BasicHttpResponse(statusTypeReturned, jsonToReturn);
	}

	@Override
	public MultipartHttpResponse downloadRecord(String url, String authToken) {
		this.url = url;
		this.authToken = authToken;
		statusTypeReturned = new StatusTypeSpy();
		BasicHttpResponse basicHttpResponse = new BasicHttpResponse(statusTypeReturned,
				jsonToReturn);
		contentLength = "8987988";
		contentDisposition = "form-data; name=\"file\"; filename=\"adelee.png\"\n";
		return new MultipartHttpResponse(basicHttpResponse, contentLength, contentDisposition);
	}

}
