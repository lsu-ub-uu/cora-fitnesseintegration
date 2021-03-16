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

import java.io.UnsupportedEncodingException;

import se.uu.ub.cora.javaclient.rest.ExtendedRestResponse;
import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class RestClientInvalidSpy implements RestClient {

	public String returnedErrorMessage;

	@Override
	public RestResponse readRecordAsJson(String recordType, String recordId) {
		returnedErrorMessage = "Error from spy: " + recordType + " and id: " + recordId;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public ExtendedRestResponse createRecordFromJson(String recordType, String json) {
		returnedErrorMessage = "Error from spy: " + recordType;
		RestResponse restResponse = new RestResponse(500, returnedErrorMessage);
		return new ExtendedRestResponse(restResponse);
	}

	@Override
	public RestResponse updateRecordFromJson(String recordType, String recordId, String json) {
		returnedErrorMessage = "Error from spy: " + recordType + " and id: " + recordId;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public RestResponse deleteRecord(String recordType, String recordId) {
		returnedErrorMessage = "Error from spy: " + recordType + " and id: " + recordId;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public RestResponse readRecordListAsJson(String recordType) {
		returnedErrorMessage = "Error from spy: " + recordType;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public RestResponse readIncomingLinksAsJson(String recordType, String recordId) {
		returnedErrorMessage = "Error from spy: " + recordType + " and id: " + recordId;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public RestResponse readRecordListWithFilterAsJson(String recordType, String filter)
			throws UnsupportedEncodingException {
		returnedErrorMessage = "Error from spy: " + recordType;
		return new RestResponse(500, returnedErrorMessage);
	}

	@Override
	public String getBaseUrl() {
		// TODO Auto-generated method stub
		return null;
	}

}
