/*
 * Copyright 2022 Uppsala University Library
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

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverter;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterFactory;
import se.uu.ub.cora.clientdata.converter.ClientDataToJsonConverterProvider;

public class StoredData {
	private ClientDataToJsonConverterFactory dataToJsonConverterFactory;

	public String getStoredRecordDataGroupAsJsonWithoutLinks() {
		ClientDataRecordGroup dataGroup = DataHolder.getRecord().getDataRecordGroup();
		return convertToJsonStringFromClientDataGroup(dataGroup);
	}

	private String convertToJsonStringFromClientDataGroup(
			ClientDataRecordGroup clientDataRecordGroup) {
		ensureDataToJsonConverterFactoryIsFetchedFromProvider();
		return convertToJson(clientDataRecordGroup);
	}

	private void ensureDataToJsonConverterFactoryIsFetchedFromProvider() {
		if (null == dataToJsonConverterFactory) {
			dataToJsonConverterFactory = ClientDataToJsonConverterProvider
					.createImplementingFactory();
		}
	}

	private String convertToJson(ClientDataRecordGroup clientDataRecordGroup) {
		ClientDataToJsonConverter toJsonConverter = dataToJsonConverterFactory
				.factorUsingConvertible(clientDataRecordGroup);
		return toJsonConverter.toJson();
	}
}
