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

import static org.testng.Assert.assertSame;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.clientdata.ClientDataGroup;
import se.uu.ub.cora.clientdata.ClientDataRecord;

public class ComparerFactoryTest {

	private ComparerFactoryImp factory;
	private ClientDataRecord dataRecord;

	@BeforeMethod
	public void setUp() {
		factory = new ComparerFactoryImp();
		ClientDataGroup dataGroup = ClientDataGroup.withNameInData("someNameInData");
		dataRecord = ClientDataRecord.withClientDataGroup(dataGroup);
	}

	@Test
	public void testFactorPermissionComparer() {
		PermissionComparer permissionComparer = (PermissionComparer) factory.factor("permission",
				dataRecord);
		assertSame(permissionComparer.getClientDataRecord(), dataRecord);
	}

	@Test
	public void testFactorActionComparer() {
		ActionComparer actionComparer = (ActionComparer) factory.factor("action", dataRecord);
		assertSame(actionComparer.getClientDataRecord(), dataRecord);
	}

	@Test(expectedExceptions = NotImplementedException.class, expectedExceptionsMessageRegExp = ""
			+ "" + "No converter implemented for: someUnknownType")
	public void testTypeNotImplemented() {
		factory.factor("someUnknownType", dataRecord);

	}

}
