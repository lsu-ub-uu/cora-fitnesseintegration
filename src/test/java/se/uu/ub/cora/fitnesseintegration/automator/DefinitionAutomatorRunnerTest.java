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
package se.uu.ub.cora.fitnesseintegration.automator;

import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.logger.spies.LoggerSpy;

public class DefinitionAutomatorRunnerTest {
	private LoggerFactorySpy loggerFactorySpy;

	private String[] args;

	@BeforeMethod
	public void setUp() {
		args = new String[] { "recordType", "collectTerm" };

		loggerFactorySpy = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
	}

	@AfterMethod
	public void afterMethod() {
		DefinitionAutomatorRunner.onlyForTestSetDefinitonAutomator(null);

	}

	@Test
	public void testLoggerInit() {
		DefinitionAutomator definitionAutomator = new DefinitionAutomatorSpy();
		DefinitionAutomatorRunner.onlyForTestSetDefinitonAutomator(definitionAutomator);

		DefinitionAutomatorRunner.main(args);

		loggerFactorySpy.MCR.assertParameters("factorForClass", 0, DefinitionAutomatorRunner.class);
		loggerFactorySpy.MCR.assertNumberOfCallsToMethod("factorForClass", 1);
	}

	@Test
	public void testMain() {
		DefinitionAutomator definitionAutomator = new DefinitionAutomatorSpy();
		DefinitionAutomatorRunner.onlyForTestSetDefinitonAutomator(definitionAutomator);

		DefinitionAutomatorRunner.main(args);

		LoggerSpy loggerSpy = (LoggerSpy) loggerFactorySpy.MCR.getReturnValue("factorForClass", 0);

		loggerSpy.MCR.assertNumberOfCallsToMethod("logInfoUsingMessage", 4);

		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 0, "message",
				"Generating definition for: recordType");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 1, "message",
				definitionAutomator.createTestForRecordAndValidationType(args[0]));

		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 2, "message",
				"Generating definition for: collectTerm");
		loggerSpy.MCR.assertParameter("logInfoUsingMessage", 3, "message",
				definitionAutomator.createTestForRecordAndValidationType(args[1]));

	}

	@Test
	public void testOnlyForTestGetDefinitonAutomator() {
		DefinitionAutomatorRunner runner = new DefinitionAutomatorRunner();
		DefinitionAutomator definitionAutomator = runner.onlyForTestGetDefinitonAutomator();
		assertTrue(definitionAutomator instanceof DefinitionAutomatorImp);
	}

	@Test
	public void testOnlyForTestSetDefinitonAutomator() {
		DefinitionAutomatorRunner runner = new DefinitionAutomatorRunner();

		DefinitionAutomator definitionAutomator = new DefinitionAutomatorSpy();
		DefinitionAutomatorRunner.onlyForTestSetDefinitonAutomator(definitionAutomator);
		assertSame(runner.onlyForTestGetDefinitonAutomator(), definitionAutomator);
	}

}
