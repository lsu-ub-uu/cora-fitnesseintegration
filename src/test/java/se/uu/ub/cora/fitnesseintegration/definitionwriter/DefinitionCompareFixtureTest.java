/*
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.fitnesseintegration.definitionwriter;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.spy.DefinitionWriterSpy;
import se.uu.ub.cora.fitnesseintegration.spy.DependencyFactorySpy;

public class DefinitionCompareFixtureTest {

	private DefinitionCompareFixture comparer;
	private DependencyFactorySpy dependencyFactory;

	@BeforeMethod
	public void beforeMethod() {
		dependencyFactory = new DependencyFactorySpy();
		DependencyProvider.onlyForTestSetDependencyFactory(dependencyFactory);

		comparer = new DefinitionCompareFixture();
	}

	@Test
	public void testInit() {
		comparer.setAuthToken("authToken");
		comparer.setRecordId("recordId");
		String definitionView = comparer.getDefinitionView();

		DefinitionWriterSpy writer = (DefinitionWriterSpy) dependencyFactory.MCR
				.getReturnValue("factorDefinitionWriter", 0);

		writer.MCR.assertCalledParametersReturn("writeDefinitionUsingRecordId", "authToken",
				"recordId");
		writer.MCR.assertReturn("writeDefinitionUsingRecordId", 0, definitionView);
	}

}
