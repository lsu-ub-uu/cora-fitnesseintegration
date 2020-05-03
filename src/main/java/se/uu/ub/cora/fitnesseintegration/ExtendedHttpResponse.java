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

/**
 * ExtendedHttpResponse is used to store information from a HttpResponse.
 */
public class ExtendedHttpResponse {

	public final int statusCode;
	public final String responseText;
	public final String createdId;
	public final String token;

	/**
	 * Stores status type, response text, created id and token.
	 * 
	 * @param basicResponse
	 *            A {@link BasicHttpResponse} which contains the StatusType and the reseponse text
	 * 
	 * @param createdId,
	 *            A string representing the id of a created record
	 * 
	 * @param token
	 *            A String representing a created token
	 */
	public ExtendedHttpResponse(BasicHttpResponse basicResponse, String createdId, String token) {
		statusCode = basicResponse.statusCode;
		responseText = basicResponse.responseText;
		this.createdId = createdId;
		this.token = token;
	}

	/**
	 * Stores status type, response text and empty created id and token. This constructor is
	 * supposed to be used if the reseponse was not ok, and there is no created id and token.
	 * 
	 * @param basicResponse
	 *            A {@link BasicHttpResponse} which contains the StatusType and the reseponse text
	 */
	public ExtendedHttpResponse(BasicHttpResponse basicResponse) {
		statusCode = basicResponse.statusCode;
		responseText = basicResponse.responseText;
		this.createdId = "";
		this.token = "";
	}

}
