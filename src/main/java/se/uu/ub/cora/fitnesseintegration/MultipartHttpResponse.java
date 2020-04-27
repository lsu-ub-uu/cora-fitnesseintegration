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

import javax.ws.rs.core.Response.StatusType;

public class MultipartHttpResponse {

	public final StatusType statusType;
	public final String responseText;
	public final String contentLength;
	public final String contentDisposition;
	public final String streamId;

	/**
	 * Stores status type, response text, content length, content disposition and stream id.
	 * 
	 * @param basicResponse
	 *            A {@link BasicHttpResponse} which contains the StatusType and the reseponse text
	 * 
	 * @param contentLength,
	 *            A string representing the contentLength
	 * 
	 * @param contentDisposition
	 *            A String representing the contentDisposition
	 * @param streamId
	 *            A string representing the stream id
	 */
	public MultipartHttpResponse(BasicHttpResponse basicResponse, String contentLength,
			String contentDisposition, String streamId) {
		statusType = basicResponse.statusType;
		responseText = basicResponse.responseText;
		this.contentLength = contentLength;
		this.contentDisposition = contentDisposition;
		this.streamId = streamId;
	}

	/**
	 * Stores status type, response text, content length, content disposition and empty stream id.
	 * 
	 * @param basicResponse
	 *            A {@link BasicHttpResponse} which contains the StatusType and the reseponse text
	 * 
	 * @param contentLength,
	 *            A string representing the contentLength
	 * 
	 * @param contentDisposition
	 *            A String representing the contentDisposition
	 */
	public MultipartHttpResponse(BasicHttpResponse basicResponse, String contentLength,
			String contentDisposition) {
		statusType = basicResponse.statusType;
		responseText = basicResponse.responseText;
		this.contentLength = contentLength;
		this.contentDisposition = contentDisposition;
		this.streamId = "";
	}

	/**
	 * Stores status type and response text, and empty content length, content disposition and
	 * stream id.
	 * 
	 * @param basicResponse
	 *            A {@link BasicHttpResponse} which contains the StatusType and the reseponse text
	 * 
	 */
	public MultipartHttpResponse(BasicHttpResponse basicResponse) {
		statusType = basicResponse.statusType;
		responseText = basicResponse.responseText;
		this.contentLength = "";
		this.contentDisposition = "";
		this.streamId = "";
	}

}
