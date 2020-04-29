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

import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestClientFactory;

public class RestClientFactorySpy implements RestClientFactory {

	public String authToken;
	public RestClient returnedRestClient;
	public boolean factorInvalidRestClient = false;
	public String jsonToReturn = "";

	@Override
	public RestClient factorUsingAuthToken(String authToken) {
		this.authToken = authToken;
		if (factorInvalidRestClient) {
			returnedRestClient = new RestClientInvalidSpy();
			return returnedRestClient;
		}
		returnedRestClient = new RestClientSpy();
		if (!"".equals(jsonToReturn)) {
			((RestClientSpy) returnedRestClient).returnedJson = jsonToReturn;
		}
		return returnedRestClient;
	}

}
