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
	public String url;
	public String filter;
	public String authToken;
	public String jsonToReturn;
	public boolean readRecordWasCalled = false;

	@Override
	public ReadResponse readRecordList(String url, String filter, String authToken) {
		readRecordListWasCalled = true;
		this.url = url;
		this.filter = filter;
		this.authToken = authToken;

		jsonToReturn = "some json returned from spy";
		return new ReadResponse(new StatusTypeSpy(), jsonToReturn);
	}

	@Override
	public ReadResponse readRecord(String url, String authToken) {
		this.url = url;
		this.authToken = authToken;
		readRecordWasCalled = true;
		jsonToReturn = "some json returned from read record in spy";
		return new ReadResponse(new StatusTypeSpy(), jsonToReturn);
	}

}
