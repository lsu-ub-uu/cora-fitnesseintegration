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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.List;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.json.parser.JsonValue;

/**
 * PermissionComparer compares the content in a JsonValue with the permissions in a
 * {@link DataRecord}. The {@link DataRecord} is expected to be provided at instance creation.
 * 
 */
public interface DataComparer {

	/**
	 * Checks whether the DataRecord provided at object instantiation contains the permissions
	 * specified in the provided JsonValue.
	 * 
	 * @param jsonValue
	 *            The JsonValue that contains the permissions to look for
	 * 
	 * @return A List<String> containing messages for potential missing permissions
	 * 
	 */
	List<String> checkDataRecordContainsPermissions(JsonValue jsonValue);

}
