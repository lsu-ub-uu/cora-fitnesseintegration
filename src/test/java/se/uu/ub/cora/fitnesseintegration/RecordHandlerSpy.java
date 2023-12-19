/*
 * Copyright 2023 Uppsala University Library
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
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class RecordHandlerSpy implements RecordHandler {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	RestResponse restResponse = new RestResponse(200, "someResponseText", Optional.empty(),
			Optional.empty());

	public RecordHandlerSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readRecordList", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("readRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("searchRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("createRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("updateRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("validateRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("deleteRecord", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("readIncomingLinks", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("batchIndex", () -> restResponse);
		MRV.setDefaultReturnValuesSupplier("download", () -> restResponse);
	}

	@Override
	public RestResponse readRecordList(String authToken, String recordType, String filter) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "filter", filter);
	}

	@Override
	public RestResponse readRecord(String authToken, String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "recordId", recordId);
	}

	@Override
	public RestResponse searchRecord(String authToken, String searchId, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "searchId",
				searchId, "json", json);
	}

	@Override
	public RestResponse createRecord(String authToken, String recordType, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "json", json);
	}

	@Override
	public RestResponse updateRecord(String authToken, String recordType, String recordId,
			String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "recordId", recordId, "json", json);
	}

	@Override
	public RestResponse validateRecord(String authToken, String json) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "json", json);
	}

	@Override
	public RestResponse deleteRecord(String authToken, String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "recordId", recordId);
	}

	@Override
	public RestResponse readIncomingLinks(String authToken, String recordType, String recordId) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "recordId", recordId);
	}

	@Override
	public RestResponse batchIndex(String authToken, String recordType, String filterAsJson) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "filterAsJson", filterAsJson);
	}

	@Override
	public RestResponse download(String authToken, String recordType, String recordId,
			String representation) {
		return (RestResponse) MCR.addCallAndReturnFromMRV("authToken", authToken, "recordType",
				recordType, "recordId", recordId, "representation", representation);
	}

}
