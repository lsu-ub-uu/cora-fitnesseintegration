/*
 * Copyright 2020, 2023 Uppsala University Library
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

import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RestClientSpy implements RestClient {

	public boolean readWasCalled = false;
	public String recordType;
	public String recordId;
	public String returnedJson;
	public String filter;
	public String createdId;

	@Override
	public RestResponse readRecordAsJson(String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
		readWasCalled = true;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public RestResponse createRecordFromJson(String recordType, String json) {
		this.recordType = recordType;
		createdId = "someCreatedId";
		return new RestResponse(201, returnedJson, Optional.of(createdId));
	}

	@Override
	public RestResponse updateRecordFromJson(String recordType, String recordId, String json) {
		this.recordType = recordType;
		this.recordId = recordId;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public RestResponse deleteRecord(String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public RestResponse readRecordListAsJson(String recordType) {
		this.recordType = recordType;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public RestResponse readIncomingLinksAsJson(String recordType, String recordId) {
		this.recordType = recordType;
		this.recordId = recordId;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public RestResponse readRecordListWithFilterAsJson(String recordType, String filter) {
		this.recordType = recordType;
		this.filter = filter;
		returnedJson = "some json returned from RestClientSpy";
		return new RestResponse(200, returnedJson, Optional.empty());
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RestResponse batchIndexWithFilterAsJson(String recordType, String filterAsJson) {
		this.recordType = recordType;
		this.filter = filterAsJson;

		createdId = "someCreatedId";
		return new RestResponse(201, returnedJson, Optional.of(createdId));
	}

}
