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
package se.uu.ub.cora.fitnesseintegration.apptoken.script;

import se.uu.ub.cora.fitnesseintegration.DataHolder;

public class ReadAppTokenFromRecord {

	private static final int DISTANCE_TO_START_OF_TOKEN = 24;

	public String getAppTokenStringFromCreatedAppTokenRecord() {
		String createdAppTokenAsJson = DataHolder.getRecordAsJson();
		int tokenIdIndex = createdAppTokenAsJson.lastIndexOf("\"name\":\"token\"")
				+ DISTANCE_TO_START_OF_TOKEN;
		return createdAppTokenAsJson.substring(tokenIdIndex,
				createdAppTokenAsJson.indexOf('"', tokenIdIndex));
	}
}