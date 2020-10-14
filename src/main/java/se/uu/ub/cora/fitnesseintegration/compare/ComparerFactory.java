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

import se.uu.ub.cora.clientdata.DataRecord;

/**
 * PermissionComparerFactory creates and returns a PermissionComparer
 */

public interface ComparerFactory {

	/**
	 * Creates and returns an instance of {@link DataComparer}. The provided {@link DataRecord} MUST
	 * be set in the instantiated object, to later be used when comparing permissions.
	 * 
	 * @param type
	 *            the type to use to decide what Comparer to factor
	 * 
	 * @param dataRecord
	 *            The DataRecord to be set in the DataComparer
	 * 
	 * @return A DataComparer
	 */
	DataComparer factor(String type, DataRecord dataRecord);

}
