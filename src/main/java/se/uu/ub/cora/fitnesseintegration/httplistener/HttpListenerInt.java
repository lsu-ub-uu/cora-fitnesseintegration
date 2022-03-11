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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public class HttpListenerInt {
	private static final String HTTP_OK = "HTTP/1.1 200 OK\r\n\r\n";
	private static final String GET_CALL_NO = "GET /getCallNo/";
	private Logger logger = LoggerProvider.getLoggerForClass(HttpListenerInt.class);
	private List<String> calls = new ArrayList<>();
	private boolean waitForCalls = true;

	public HttpListenerInt(int port) throws IOException {
		try (ServerSocket server = new ServerSocket(port)) {
			logger.logInfoUsingMessage("Listening for connection on port " + port);
			listenForHttpCalls(server);
		}
	}

	private void listenForHttpCalls(ServerSocket server) throws IOException {
		while (waitForCalls) {
			try (Socket socket = server.accept()) {
				String answer = handleCall(socket);
				reply(socket, answer);
			}
		}
	}

	private void reply(Socket socket, String answer) throws IOException {
		String httpResponse = HTTP_OK + answer;
		socket.getOutputStream().write(httpResponse.getBytes(StandardCharsets.UTF_8));
	}

	private String handleCall(Socket socket) throws IOException {
		String call = readInputToString(socket);
		if (callToGetPreviousCall(call)) {
			return getPreviousCall(call);
		}
		if (callToClearMemory(call)) {
			return emptyMemory();
		}
		return storeCall(call);
	}

	private String readInputToString(Socket socket) throws IOException {
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader reader = new BufferedReader(isr);
		String line = reader.readLine();
		StringBuilder totalRead = new StringBuilder(line);

		while (!line.isEmpty()) {
			line = reader.readLine();
			totalRead.append(line);
		}
		return totalRead.toString();
	}

	private boolean callToGetPreviousCall(String call) {
		return call.startsWith(GET_CALL_NO);
	}

	private String getPreviousCall(String call) {
		int callNo = Integer
				.parseInt(call.substring(GET_CALL_NO.length(), GET_CALL_NO.length() + 1));
		return calls.get(callNo);
	}

	private boolean callToClearMemory(String call) {
		return call.startsWith("GET /empty_memory");
	}

	private String emptyMemory() {
		calls = new ArrayList<>();
		return "forgot all remembered calls";
	}

	private String storeCall(String call) {
		return storeInputInMemory(call);
	}

	private String storeInputInMemory(String line) {
		calls.add(line);
		return "remembering call as no: " + (calls.size() - 1);
	}

}
