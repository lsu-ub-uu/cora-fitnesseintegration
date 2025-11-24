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

import se.uu.ub.cora.fitnesseintegration.script.LoginToken;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public class DefinitionAutomatorRunner {
	private Logger logger;
	private static DefinitionAutomator definitionAutomator;

	public static void main(String[] args) {
		DefinitionAutomatorRunner runner = new DefinitionAutomatorRunner();
		runner.startDefinitionAutomatorRunner(args);
	}

	public DefinitionAutomatorRunner() {
		logger = LoggerProvider.getLoggerForClass(DefinitionAutomatorRunner.class);
		ensureStartedDefinitionAutomator();
	}

	static DefinitionAutomator ensureStartedDefinitionAutomator() {
		if (null == definitionAutomator) {
			definitionAutomator = new DefinitionAutomatorImp();
		}
		return definitionAutomator;
	}

	private void startDefinitionAutomatorRunner(String[] args) {
		SystemUrl.setUrl(System.getProperty("systemUnderTestUrl"));
		SystemUrl.setAppTokenVerifierUrl(System.getProperty("appTokenVerifierUrl"));

		LoginToken.setFitnesseAdminLoginId(System.getProperty("fitnesseAdminLoginId"));
		LoginToken.setFitnesseAdminAppToken(System.getProperty("fitnesseAdminAppToken"));

		for (String arg : args) {
			logger.logInfoUsingMessage("Generating definition for: " + arg);
			String newTest = definitionAutomator.createTestForRecordAndValidationType(arg);
			logger.logInfoUsingMessage(newTest);
		}
	}

	public DefinitionAutomator onlyForTestGetDefinitonAutomator() {
		return definitionAutomator;
	}

	public static void onlyForTestSetDefinitonAutomator(DefinitionAutomator definitionAutomatorIn) {
		definitionAutomator = definitionAutomatorIn;
	}

}
