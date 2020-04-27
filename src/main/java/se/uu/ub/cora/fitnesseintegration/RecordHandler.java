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

public interface RecordHandler {

	/**
	 * Reads a list of records using url, authToken and possibly a filter. The result is returned as
	 * a responseText in the {@link BasicHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param filter
	 *            A String used to filter the result
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse readRecordList(String url, String authToken, String filter)
			throws UnsupportedEncodingException;

	/**
	 * Reads a record using url and authToken. The result is returned as a responseText in the
	 * {@link BasicHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse readRecord(String url, String authToken);

	/**
	 * Searches for records using url, authToken and a string to define the search. The result is
	 * returned as a responseText in the {@link BasicHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param json
	 *            A String used to define the search
	 * 
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse searchRecord(String url, String authToken, String json)
			throws UnsupportedEncodingException;

	/**
	 * Creates a record using url and authToken and a string to set as output in the httpRequest.
	 * The result is returned as a responseText in the {@link ExtendedHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request * @param authToken A String authToken
	 *            to use in the http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param json
	 *            A String used to use as output in the http request
	 * 
	 * @return A {@link ExtendedHttpResponse} containing the response text and StatusType
	 */
	ExtendedHttpResponse createRecord(String url, String authToken, String json);

	/**
	 * Updates a record using url and authToken and a string to set as output in the httpRequest.
	 * The result is returned as a responseText in the {@link BasicHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request * @param authToken A String authToken
	 *            to use in the http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param json
	 *            A String used to use as output in the http request
	 * 
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse updateRecord(String url, String authToken, String json);

	/**
	 * Validates a record using url and authToken, a string to set as output and contentType to set
	 * in the httpRequest. The result is returned as a responseText in the {@link BasicHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request * @param authToken A String authToken
	 *            to use in the http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @param json
	 *            A String used to use as output in the http request
	 * @param contenType
	 *            A String to set as contentType in the http request
	 * 
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse validateRecord(String url, String authToken, String json, String contentType);

	/**
	 * Delete a record using url and authToken.
	 * 
	 * @param url
	 *            A String used as url to make a http request * @param authToken A String authToken
	 *            to use in the http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * @return A {@link BasicHttpResponse} containing the response text and StatusType
	 */
	BasicHttpResponse deleteRecord(String url, String authToken);

	/**
	 * Downloads data using url and authToken. The result is returned in the
	 * {@link MultipartHttpResponse}
	 * 
	 * @param url
	 *            A String used as url to make a http request * @param authToken A String authToken
	 *            to use in the http request
	 * @param authToken
	 *            A String authToken to use in the http request
	 * 
	 * @return A {@link MultipartHttpResponse} containing the response text, StatusType, content
	 *         length and content disposition
	 */
	MultipartHttpResponse downloadRecord(String url, String authToken);

}
