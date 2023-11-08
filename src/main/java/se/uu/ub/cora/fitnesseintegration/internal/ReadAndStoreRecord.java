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

import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;
import se.uu.ub.cora.javaclient.data.DataClient;

public class ReadAndStoreRecord implements StandardFitnesseMethods {

	public static ReadAndStoreRecord usingDataClientAndTypeAndId(DataClient dataClient, String type,
			String id) {
		return new ReadAndStoreRecord(dataClient, type, id);
	}

	private DataClient dataClient;
	private String type;
	private String id;

	private ReadAndStoreRecord(DataClient dataClient, String type, String id) {
		this.dataClient = dataClient;
		this.type = type;
		this.id = id;
	}

	@Override
	public void run() {
		ClientDataRecord clientDataRecord = dataClient.read(type, id);
		DataHolder.setRecord(clientDataRecord);
	}

	public DataClient onlyForTestGetDataClient() {
		return dataClient;
	}

	public String onlyForTestGetType() {
		return type;
	}

	public String onlyForTestGetId() {
		return id;
	}

}
