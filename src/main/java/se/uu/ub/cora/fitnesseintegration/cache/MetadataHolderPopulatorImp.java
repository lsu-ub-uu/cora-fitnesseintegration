/*
 * Copyright 2024, 2025 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.cache;

import se.uu.ub.cora.clientdata.ClientData;
import se.uu.ub.cora.clientdata.ClientDataList;
import se.uu.ub.cora.clientdata.ClientDataRecord;
import se.uu.ub.cora.javaclient.data.DataClient;

public class MetadataHolderPopulatorImp implements MetadataHolderPopulator {

	private static final String METADATA = "metadata";

	@Override
	public MetadataHolder createAndPopulateHolder() {
		ClientDataList readList = readMetadataFromServer();
		return createAndPopulateHolderFromList(readList);
	}

	private ClientDataList readMetadataFromServer() {
		DataClient dataClient = FitnesseJavaClientProvider.getFitnesseAdminDataClient();
		return dataClient.readList(METADATA);
	}

	private MetadataHolder createAndPopulateHolderFromList(ClientDataList readList) {
		MetadataHolder holder = new MetadataHolderImp();
		for (ClientData dataRecord : readList.getDataList()) {
			holder.addDataRecord((ClientDataRecord) dataRecord);
		}
		return holder;
	}

}
