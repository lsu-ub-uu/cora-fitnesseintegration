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
package se.uu.ub.cora.fitnesseintegration.internal;

import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class ReadAndStoreRecordAsJson implements StandardFitnesseMethod {

	public static ReadAndStoreRecordAsJson usingRestClientAndTypeAndId(RestClient restClient,
			String type, String id) {
		return new ReadAndStoreRecordAsJson(restClient, type, id);
	}

	private RestClient restClient;
	private String type;
	private String id;

	private ReadAndStoreRecordAsJson(RestClient restClient, String type, String id) {
		this.restClient = restClient;
		this.type = type;
		this.id = id;
	}

	@Override
	public void run() {
		RestResponse response = restClient.readRecordAsJson(type, id);
		DataHolder.setRecordAsJson(response.responseText());
	}

	public RestClient onlyForTestGetRestClient() {
		return restClient;
	}

	public String onlyForTestGetType() {
		return type;
	}

	public String onlyForTestGetId() {
		return id;
	}

}
