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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import se.uu.ub.cora.logger.Logger;
import se.uu.ub.cora.logger.LoggerProvider;

public class HttpListenerInt {
	private static final String END_OF_HEADER = "\r\n\r\n";
	private static final String CONTENT_LENGTH = "Content-Length:";
	private static final String HTTP_OK = "HTTP/1.1 200 OK\r\n";
	private static final String CONTENT_TYPE = "Content-Type: text/plain;charset=utf-8\r\n\r\n";
	private static final String GET_CALL_NO = "GET /getCallNo/";
	private Logger logger = LoggerProvider.getLoggerForClass(HttpListenerInt.class);
	private List<String> calls = new ArrayList<>();
	private boolean waitForCalls = true;
	private int port;

	public HttpListenerInt(int port) {
		this.port = port;
	}

	public void startServer() throws IOException {

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
		logger.logInfoUsingMessage("...stopping HttpListener");
	}

	private void reply(Socket socket, String answer) throws IOException {
		String httpResponse = HTTP_OK + CONTENT_TYPE + answer;
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
		DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
		StringBuilder readHeader = readHeader(in);
		if (!readHeader.toString().contains(CONTENT_LENGTH)) {
			return readHeader.toString();
		}
		int contentLength = calculateContentLength(readHeader.toString());
		int readingOnPosition = readHeader.length();
		int totalBytesToRead = readingOnPosition + contentLength;

		StringBuilder readBody = readBody(in, totalBytesToRead, readingOnPosition);
		return readHeader.append(readBody).toString();
	}

	private StringBuilder readHeader(DataInputStream in) throws IOException {
		StringBuilder readHeader = new StringBuilder();
		byte[] byteArray = new byte[1];
		while (stillReadingHeader(readHeader)) {
			byteArray[0] = in.readByte();
			readHeader.append(new String(byteArray, StandardCharsets.UTF_8));
		}
		return readHeader;
	}

	private boolean stillReadingHeader(StringBuilder readHeader) {
		if (readHeader.length() < END_OF_HEADER.length()) {
			return true;
		}
		String lastFour = readHeader.substring(readHeader.length() - END_OF_HEADER.length());
		return !END_OF_HEADER.equals(lastFour);
	}

	private int calculateContentLength(String call) {
		int indexOf = call.indexOf(CONTENT_LENGTH);
		int endContentLength = call.indexOf("\r\n", indexOf);
		return Integer
				.parseInt(call.substring(indexOf + CONTENT_LENGTH.length() + 1, endContentLength));
	}

	private StringBuilder readBody(DataInputStream in, int totalBytesToRead, int readingOnPosition)
			throws IOException {
		StringBuilder readBody = new StringBuilder();

		byte[] messageByte = new byte[totalBytesToRead];

		while (readingOnPosition < totalBytesToRead) {
			int currentBytesRead = in.read(messageByte);
			readingOnPosition += currentBytesRead;
		}
		readBody.append(new String(messageByte, 0, totalBytesToRead, StandardCharsets.UTF_8));
		return readBody;
	}

	private boolean callToGetPreviousCall(String call) {
		return call.startsWith(GET_CALL_NO);
	}

	private String getPreviousCall(String call) {
		logger.logInfoUsingMessage("Call recieved: Get previous call");
		int callNo = calculateRequestedCallNumber(call);

		if (requestedPreviousCallExists(callNo)) {
			return calls.get(callNo);
		}

		return handleNoMatchingCalls(callNo);
	}

	private int calculateRequestedCallNumber(String call) {
		int spaceAfterGetCallNo = call.indexOf(' ', GET_CALL_NO.length());
		return Integer.parseInt(call.substring(GET_CALL_NO.length(), spaceAfterGetCallNo));
	}

	private boolean requestedPreviousCallExists(int callNo) {
		return callNo < calls.size();
	}

	private String handleNoMatchingCalls(int callNo) {
		if (calls.isEmpty()) {
			return "No calls registered.";
		}
		String message = "No call made with no:{0}, the highest registered call number is:{1} ";
		return MessageFormat.format(message, String.valueOf(callNo),
				String.valueOf(lastStoredCallNo()));
	}

	private int lastStoredCallNo() {
		return calls.size() - 1;
	}

	private boolean callToClearMemory(String call) {
		return call.startsWith("GET /empty_memory");
	}

	private String emptyMemory() {
		logger.logInfoUsingMessage("Call recieved: Empty memory");
		calls = new ArrayList<>();

		return "forgot all remembered calls";
	}

	private String storeCall(String call) {
		calls.add(call);
		logger.logInfoUsingMessage("Call recieved: Remember call");
		return "remembering call as no: " + lastStoredCallNo();
	}

	public void stopAfterNextCall() {
		waitForCalls = false;
	}

}
