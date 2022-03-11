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
package se.uu.ub.cora.fitnesseintegration.httplistener;

import java.io.IOException;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public class HttpListener {
	private static Logger logger = LoggerProvider.getLoggerForClass(HttpListener.class);

	public static void main(String[] args) throws IOException {
		logger.logInfoUsingMessage("HttpListener starting...");

		if (portIsSet(args)) {
			startServerAndlistenForHttpCalls(args);
		} else {
			logger.logFatalUsingMessage("No port specified, stopping");
		}

	}

	private static void startServerAndlistenForHttpCalls(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		new HttpListenerInt(port);
	}

	private static boolean portIsSet(String[] args) {
		return args != null && args.length > 0;
	}

}
