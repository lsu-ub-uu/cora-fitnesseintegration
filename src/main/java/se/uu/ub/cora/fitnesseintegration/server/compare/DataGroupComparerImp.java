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

import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupComparerImp implements DataGroupComparer {

	@Override
	public List<String> compareDataGroupToDataGroup(DataGroup compareWith,
			DataGroup compareAgainst) {

		if (!compareAgainst.getNameInData().equals(compareWith.getNameInData())) {
			String name2 = compareWith.getNameInData();
			// compareWith.hasAttributes()
			return List.of(MessageFormat.format("Group with name: {0} not found.", name2));
		}
		if (!compareWith.hasAttributes() && compareAgainst.hasAttributes()) {
			String name2 = compareWith.getNameInData();
			// compareWith.hasAttributes()
			return List.of(MessageFormat.format("Group with name: {0} not found.", name2));
		}
		if (compareAgainst.hasAttributes() != compareWith.hasAttributes()) {
			String attributeString = "";
			String pattern = "name: {0} and value: {1}";
			for (DataAttribute attribute : compareWith.getAttributes()) {
				attributeString += MessageFormat.format(pattern, attribute.getNameInData(),
						attribute.getValue());
			}
			return List.of(
					MessageFormat.format("Group with name: {0} and attribute/s with {1} not found.",
							compareWith.getNameInData(), attributeString));
		}

		return Collections.emptyList();
	}

}
