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

import static org.testng.Assert.assertSame;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.StatusType;

import org.testng.annotations.Test;

public class MultipartHttpResponseTest {

	@Test
	public void testReadResponse() {
		StatusType statusType = Response.Status.fromStatusCode(200);
		String responseText = "some response text";
		BasicHttpResponse basicResponse = new BasicHttpResponse(statusType, responseText);
		String contentLength = "9999";
		String contentDisposition = "form-data; name=\"file\"; filename=\"adele.png\"\n";
		String streamId = "soundBinary:23310456970967";
		MultipartHttpResponse response = new MultipartHttpResponse(basicResponse, contentLength,
				contentDisposition, streamId);

		assertSame(response.statusType, statusType);
		assertSame(response.responseText, responseText);
		assertSame(response.contentLength, contentLength);
		assertSame(response.contentDisposition, contentDisposition);
		assertSame(response.streamId, streamId);
	}

	@Test
	public void testReadResponseNoStreamId() {
		StatusType statusType = Response.Status.fromStatusCode(200);
		String responseText = "some response text";
		BasicHttpResponse basicResponse = new BasicHttpResponse(statusType, responseText);
		String contentLength = "9999";
		String contentDisposition = "form-data; name=\"file\"; filename=\"adele.png\"\n";
		MultipartHttpResponse response = new MultipartHttpResponse(basicResponse, contentLength,
				contentDisposition);

		assertSame(response.statusType, statusType);
		assertSame(response.responseText, responseText);
		assertSame(response.contentLength, contentLength);
		assertSame(response.contentDisposition, contentDisposition);
		assertSame(response.streamId, "");
	}

	@Test
	public void testReadResponseOnlyReadResponse() {
		StatusType statusType = Response.Status.fromStatusCode(200);
		String responseText = "some response text";
		BasicHttpResponse basicResponse = new BasicHttpResponse(statusType, responseText);
		MultipartHttpResponse response = new MultipartHttpResponse(basicResponse);

		assertSame(response.statusType, statusType);
		assertSame(response.responseText, responseText);
		assertSame(response.contentLength, "");
		assertSame(response.contentDisposition, "");
		assertSame(response.streamId, "");
	}
}
