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

import se.uu.ub.cora.javaclient.rest.RestResponse;

public interface RecordHandler {

	/**
	 * Reads a list of records using authToken, recordType and possibly a filter. The result is
	 * returned as a responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record that is to be read
	 * @param filter
	 *            A String used to filter the result
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse readRecordList(String authToken, String recordType, String filter);

	/**
	 * Reads a record using authToken, recordType and recordId. The result is returned as a
	 * responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record that is to be read
	 * @param recordId
	 *            A String recordId, the id of the record that is to be read
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse readRecord(String authToken, String recordType, String recordId);

	/**
	 * Searches for records using url, authToken and a string to define the search. The result is
	 * returned as a responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * 
	 * @param searchId
	 *            A String with the searchId name.
	 * 
	 * @param json
	 *            A String used to define the search
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse searchRecord(String authToken, String searchId, String json);

	/**
	 * Creates a record using authToken, recordType and a string to set as output in the
	 * httpRequest. The result is returned as a responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record that is to be created
	 * @param json
	 *            A String used to use as output in the http request
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse createRecord(String authToken, String recordType, String json);

	/**
	 * Updates a record using url and authToken and a string to set as output in the httpRequest.
	 * The result is returned as a responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record that is to be updated
	 * @param recordId
	 *            A String recordId, the id of the record that is to be updated
	 * @param json
	 *            A String used to use as output in the http request
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse updateRecord(String authToken, String recordType, String recordId, String json);

	/**
	 * Validates a record. The result is returned as a responseText in the {@link RestResponse}
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param json
	 *            A String used to use as output in the http request
	 * 
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse validateRecord(String authToken, String json);

	/**
	 * Deletes a record using authToken, recordType and recordId.
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record that is to be deleted
	 * @param recordId
	 *            A String recordId, the id of the record that is to be deleted
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse deleteRecord(String authToken, String recordType, String recordId);

	/**
	 * Reads incoming links for a record using authToken, recordType and recordId.
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record to read incoming links for
	 * @param recordId
	 *            A String recordId, the id of the record to read incoming links for
	 * 
	 * @return A {@link RestResponse} containing the response
	 */
	RestResponse readIncomingLinks(String authToken, String recordType, String recordId);

	/**
	 * Creates an IndexBatchJob for the provided recordType.
	 * 
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param recordType
	 *            A String recordType representing the type of record to batch index
	 * 
	 * @param filter
	 *            A JSON-formatted String used to filter the result
	 * 
	 */
	RestResponse batchIndex(String authToken, String recordType, String filterAsJson);

}
