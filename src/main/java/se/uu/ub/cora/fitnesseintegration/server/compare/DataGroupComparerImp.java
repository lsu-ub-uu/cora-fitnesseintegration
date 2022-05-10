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
import java.util.StringJoiner;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataGroup;

public class DataGroupComparerImp implements DataGroupComparer {

	@Override
	public List<String> compareDataGroupToDataGroup(DataGroup compareWith,
			DataGroup compareAgainst) {
		boolean isSame = isSameChild(compareWith, compareAgainst);

		for (DataChild dataChild : compareWith.getChildren()) {
			// find matching child
			DataChild sameChildFromAgainst = null;
			for (DataChild against : compareAgainst.getChildren()) {
				if (isSameChild(dataChild, against)) {
					sameChildFromAgainst = against;
				}
			}
			if (sameChildFromAgainst == null) {
				// write error

				String nameInData = dataChild.getNameInData();
				String value = ((DataAtomic) dataChild).getValue();

				if (!dataChild.hasAttributes()) {
					String out = "Atomic with name: " + nameInData + " and value: " + value
							+ " not found.";
					return List.of(out);
				}

				StringJoiner sj = getAttributeString(dataChild);
				return List.of(MessageFormat.format(
						"Atomic with name: {0} and attribute/s with {1} and value: {2} not found.",
						nameInData, sj.toString(), value));

			} else {
				// check value
				DataAtomic atomicChild = (DataAtomic) dataChild;
				DataAtomic atomicAgainst = (DataAtomic) sameChildFromAgainst;
				if (!atomicChild.getValue().equals(atomicAgainst.getValue())) {
					String nameInData = dataChild.getNameInData();
					String value = atomicChild.getValue();
					String out = "Atomic with name: " + nameInData + " and value: " + value
							+ " not found.";
					return List.of(out);

				}

			}

		}

		// end checks
		if (isSame) {
			return Collections.emptyList();
		}

		//////////////////////

		if (!compareWith.hasAttributes()) {
			return List.of(MessageFormat.format("Group with name: {0} not found.",
					compareWith.getNameInData()));
		}

		StringJoiner sj = getAttributeString(compareWith);
		return List
				.of(MessageFormat.format("Group with name: {0} and attribute/s with {1} not found.",
						compareWith.getNameInData(), sj.toString()));
	}

	private StringJoiner getAttributeString(DataChild compareWith) {
		String pattern = "name: {0} and value: {1}";
		StringJoiner sj = new StringJoiner(", ");
		for (DataAttribute attribute : compareWith.getAttributes()) {
			sj.add(MessageFormat.format(pattern, attribute.getNameInData(), attribute.getValue()));
		}
		return sj;
	}

	private boolean isSameChild(DataChild compareWith, DataChild compareAgainst) {
		if (!compareAgainst.getNameInData().equals(compareWith.getNameInData())) {
			return false;
		}

		if (compareWith.getAttributes().size() != compareAgainst.getAttributes().size()) {
			return false;
		}
		boolean attributeFound = false;
		for (DataAttribute attributeWith : compareWith.getAttributes()) {
			attributeFound = false;
			for (DataAttribute attributeAgainst : compareAgainst.getAttributes()) {
				if (attributeWith.getNameInData().equals(attributeAgainst.getNameInData())
						&& attributeWith.getValue().equals(attributeAgainst.getValue())) {
					attributeFound = true;
				}
			}
			if (!attributeFound) {
				return false;
			}
		}
		return true;
	}

}
