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

import static org.testng.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.DataHolder;

public class HasUserRecordPartPermissionsTest {
	private DataRecordSpy clientDataRecord;
	String allPossibleReadPermissions = """
			permissionX
			permissionY
			permissionZ""";
	private RecordPartPermissionsInLastHandledRecord pre;

	@BeforeMethod
	public void beforeMethod() {
		clientDataRecord = new DataRecordSpy();
		DataHolder.setRecord(clientDataRecord);
		pre = new RecordPartPermissionsInLastHandledRecord();
	}

	@Test
	public void testNoPermissionsInRecordOrTested() throws Exception {
		String readPermissions = "";
		pre.setReadPartPermissionsSpecifiedInMetadata(readPermissions);

		assertEquals(pre.expectedMissingReadPartPermissions(), "none");
	}

	@Test
	public void testNoPermissionsInRecordButInTested() throws Exception {
		pre.setReadPartPermissionsSpecifiedInMetadata(allPossibleReadPermissions);
		assertEquals(pre.expectedMissingReadPartPermissions(), allPossibleReadPermissions);
	}

	@Test
	public void testPermissionsInRecordOrButNonInTested() throws Exception {
		Set<String> setPermissions = new HashSet<>();
		setPermissions.add("somePermission");
		setPermissions.add("somePermission2");
		clientDataRecord.readPermissions = setPermissions;

		pre.setReadPartPermissionsSpecifiedInMetadata("");
		assertEquals(pre.expectedMissingReadPartPermissions(), "none");
	}

	@Test
	public void testPermissionsInRecordOtherInTested() throws Exception {
		Set<String> setPermissions = new HashSet<>();
		setPermissions.add("somePermission");
		setPermissions.add("somePermission2");
		clientDataRecord.readPermissions = setPermissions;

		pre.setReadPartPermissionsSpecifiedInMetadata(allPossibleReadPermissions);
		assertEquals(pre.expectedMissingReadPartPermissions(), allPossibleReadPermissions);
	}

	@Test
	public void testPermissionsInRecordSomeInTested() throws Exception {
		Set<String> setPermissions = new HashSet<>();
		setPermissions.add("permissionX");
		setPermissions.add("permissionY");
		setPermissions.add("somePermission2");
		clientDataRecord.readPermissions = setPermissions;

		pre.setReadPartPermissionsSpecifiedInMetadata(allPossibleReadPermissions);
		assertEquals(pre.expectedMissingReadPartPermissions(), """
				permissionZ""");
	}

	@Test
	public void testPermissionsInRecordAllInTested() throws Exception {
		Set<String> setPermissions = new HashSet<>();
		setPermissions.add("permissionX");
		setPermissions.add("permissionY");
		setPermissions.add("permissionZ");
		clientDataRecord.readPermissions = setPermissions;

		pre.setReadPartPermissionsSpecifiedInMetadata(allPossibleReadPermissions);
		assertEquals(pre.expectedMissingReadPartPermissions(), "none");
	}

}
