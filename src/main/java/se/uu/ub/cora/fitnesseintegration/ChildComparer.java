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

import java.util.List;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataParent;
import se.uu.ub.cora.json.parser.JsonValue;

public interface ChildComparer {

	/**
	 * Checks whether the provided ClientDataGroup contains the children specified in the provided
	 * JsonValue.
	 * 
	 * @param dataGroup
	 *            The ClientDataGroup that is checked
	 * @param jsonValue
	 *            The JsonValue that contains the children to look for
	 * @return A boolean value, true if all values are found, false otherwise
	 * 
	 */
	boolean dataGroupContainsChildren(ClientDataGroup dataGroup, JsonValue jsonValue);

	/**
	 * Checks whether the provided ClientDataGroup contains the children specified in the provided
	 * JsonValue.
	 * 
	 * @param dataGroup
	 *            The ClientDataParent that is checked
	 * @param jsonValue
	 *            The JsonValue that contains the children to look for
	 * @return A List<String> containing messages for potential missing children. Empty list is
	 *         returned if all children are found.
	 * 
	 */
	List<String> checkDataGroupContainsChildren(ClientDataParent dataGroup, JsonValue jsonValue);

	/**
	 * Checks whether the provided ClientDataGroup contains the children, with correct values,
	 * specified in the provided JsonValue.
	 * 
	 * @param dataGroup
	 *            The ClientDataParent that is checked
	 * @param jsonValue
	 *            The JsonValue that contains the children to look for
	 * @return A List<String> containing messages for potential missing children or incorrect
	 *         values. Empty list is returned if all children with correct values are found.
	 * 
	 */
	List<String> checkDataGroupContainsChildrenWithCorrectValues(ClientDataParent dataGroup,
			JsonValue jsonValue);
}
