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
package se.uu.ub.cora.fitnesseintegration.compare;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringJoiner;

import se.uu.ub.cora.clientdata.DataRecord;
import se.uu.ub.cora.fitnesseintegration.DataHolder;

public class RecordPartPermissionsInLastHandledRecord {

	private Set<String> allPossibleReadPermissions = Collections.emptySet();

	public void setReadPartPermissionsSpecifiedInMetadata(String readPermissions) {
		if (readPermissions.trim().length() > 0) {
			// readPermissions.trim();
			String[] permissionAsArray = readPermissions.split("\n");
			allPossibleReadPermissions = new LinkedHashSet<>(Arrays.asList(permissionAsArray));
		} else {
			allPossibleReadPermissions = Collections.emptySet();
		}
	}

	public String expectedMissingReadPartPermissions() {

		if (allPossibleReadPermissions.isEmpty()) {
			return "none";
		} else {
			DataRecord dataRecord = DataHolder.getRecord();
			Set<String> readPermissions = dataRecord.getReadPermissions();

			StringJoiner stringJoiner = new StringJoiner("\n");
			stringJoiner.setEmptyValue("none");

			for (String possible : allPossibleReadPermissions) {
				if (!readPermissions.contains(possible)) {
					stringJoiner.add(possible);
				}
			}
			return stringJoiner.toString();
		}
	}
}
