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
package se.uu.ub.cora.fitnesseintegration.server.compare;

import java.util.List;

import se.uu.ub.cora.data.DataGroup;

/**
 * DataGroupComparer compares two {@link DataGroup}s based on content.
 */
public interface DataGroupComparer {
	/**
	 * compareDataGroupToDataGroup compares two {@link DataGroup}s based on content. The comparision
	 * is done so that the compareWith is compared against the compareAgainst. A {@link List} of
	 * messages are returned for all the compareWith group and its children that are different or
	 * missing from the compareAgainst group. An empty list is returned if the entire compare group
	 * is found in compareAgainst.
	 * <p>
	 * compareAgainst may contain extra child elements at any level, these extra elements will NOT
	 * result in a compare message.
	 * 
	 * @param compareWith
	 *            A {@link DataGroup} to compare with the other group
	 * @param compareAgainst
	 *            A {@link DataGroup} to compare the other group against
	 * @return A {@link List} of comparission messages if some element in compareWith is missing
	 *         from compareAgainst.
	 */
	List<String> compareDataGroupToDataGroup(DataGroup compareWith, DataGroup compareAgainst);

}
