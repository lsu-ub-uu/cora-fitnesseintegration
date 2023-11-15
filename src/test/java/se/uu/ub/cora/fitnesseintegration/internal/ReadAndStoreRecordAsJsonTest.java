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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.fitnesseintegration.spy.RestClientSpy;
import se.uu.ub.cora.javaclient.rest.RestClient;
import se.uu.ub.cora.javaclient.rest.RestResponse;

public class ReadAndStoreRecordAsJsonTest {

	private RestClientSpy restClient;
	private static final String SOME_TYPE = "someType";
	private static final String SOME_ID = "someId";
	private ReadAndStoreRecordAsJson readAndStore;

	@BeforeMethod
	private void beforeMethod() {
		restClient = new RestClientSpy();
		readAndStore = ReadAndStoreRecordAsJson.usingRestClientAndTypeAndId(restClient, SOME_TYPE,
				SOME_ID);
	}

	@Test
	public void testInit() throws Exception {
		assertTrue(readAndStore instanceof StandardFitnesseMethod);
	}

	@Test
	public void testRun() throws Exception {
		readAndStore.run();

		restClient.MCR.assertParameters("readRecordAsJson", 0, SOME_TYPE, SOME_ID);
		RestResponse response = (RestResponse) restClient.MCR.getReturnValue("readRecordAsJson", 0);
		assertEquals(DataHolder.getRecordAsJson(), response.responseText());
	}

	@Test
	public void testOnlyForTestGetDataClient() throws Exception {
		RestClient restClientReturned = readAndStore.onlyForTestGetRestClient();
		assertSame(restClientReturned, restClient);
	}

	@Test
	public void testOnlyForTestGetType() throws Exception {
		String typeReturned = readAndStore.onlyForTestGetType();
		assertSame(typeReturned, SOME_TYPE);
	}

	@Test
	public void testOnlyForTestGetId() throws Exception {
		String idReturned = readAndStore.onlyForTestGetId();
		assertSame(idReturned, SOME_ID);
	}
}
