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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.spy.LoggerFactorySpy;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;
import se.uu.ub.cora.httphandler.HttpHandlerFactoryImp;
import se.uu.ub.cora.logger.LoggerProvider;

public class HttpListenerTest {
	private LoggerFactorySpy loggerFactorySpy = new LoggerFactorySpy();
	private String testedClassName = "HttpListener";
	private HttpHandlerFactory httpHandlerFactory = new HttpHandlerFactoryImp();
	private HttpListenerThread httpListenerThread;

	@BeforeMethod
	public void setUp() {
		loggerFactorySpy.resetLogs(testedClassName);
		LoggerProvider.setLoggerFactory(loggerFactorySpy);
	}

	@Test
	public void testStartupNoArgs_shouldLoggFatalError() throws Exception {
		HttpListener.main(null);
		assertEquals(getInfoLogNo(0), "HttpListener starting...");
		assertEquals(getFatalLogNo(0), "No port specified, stopping");
	}

	@Test
	public void testStartupEmptyArgs_shouldLoggFatalError() throws Exception {
		HttpListener.main(new String[] {});
		assertEquals(getInfoLogNo(0), "HttpListener starting...");
		assertEquals(getFatalLogNo(0), "No port specified, stopping");
	}

	@Test
	public void testInit() throws Exception {
		startServer("1111");
		assertEquals(getInfoLogNo(0), "HttpListener starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 0),
				"Listening for connection on port 1111");
		stopServer("1111");
	}

	private void startServer(String port) throws InterruptedException {
		httpListenerThread = new HttpListenerThread(port);
		httpListenerThread.start();
		TimeUnit.MILLISECONDS.sleep(100);
	}

	@Test
	public void testStopServer() throws Exception {
		startServer("1111");
		assertEquals(getInfoLogNo(0), "HttpListener starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 0),
				"Listening for connection on port 1111");
		stopServer("1111");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 2),
				"...stopping HttpListener");
	}

	private void stopServer(String port) {
		HttpListener.stopAfterNextCall();
		HttpHandler httpHandler = httpHandlerFactory.factor("http://localhost:" + port);
		httpHandler.getResponseCode();
	}

	@Test
	public void testInitListensToCorrectPort() throws Exception {
		startServer("11111");
		HttpHandler httpHandler = httpHandlerFactory.factor("http://localhost:11111");
		assertResponseCodeOkAndTextPlainContentType(httpHandler);
		stopServer("11111");
	}

	private void assertResponseCodeOkAndTextPlainContentType(HttpHandler httpHandler) {
		assertEquals(httpHandler.getResponseCode(), 200);
		assertEquals(httpHandler.getHeaderField("Content-Type"), "text/plain;charset=utf-8");
	}

	@Test
	public void testListensToAndRemembersCalls() throws Exception {
		startServer("11111");

		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:11111/remember_this_call");
		assertResponseCodeOkAndTextPlainContentType(httpHandler);
		String responseText2 = httpHandler.getResponseText();
		assertEquals(responseText2, "remembering call as no: 0");

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 1),
				"Call recieved: Remember call");

		stopServer("11111");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 3),
				"...stopping HttpListener");
	}

	@Test
	public void testGetPreviousCall() throws Exception {
		startServer("11111");
		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:11111/remember_this_call");
		httpHandler.getResponseCode();

		HttpHandler httpHandler2 = httpHandlerFactory.factor("http://localhost:11111/getCallNo/0");
		assertResponseCodeOkAndTextPlainContentType(httpHandler2);
		String responseText2 = httpHandler2.getResponseText();
		assertTrue(responseText2.startsWith("GET /remember_this_call"));

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 2),
				"Call recieved: Get previous call");
		stopServer("11111");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 4),
				"...stopping HttpListener");
	}

	@Test
	public void testGetPreviousCallWithBody() throws Exception {
		startServer("2223");
		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:2223/remember_this_call");
		httpHandler.setRequestMethod("POST");
		httpHandler.setOutput("some body content");
		// httpHandler.
		httpHandler.getResponseCode();

		HttpHandler httpHandler2 = httpHandlerFactory.factor("http://localhost:2223/getCallNo/0");
		assertResponseCodeOkAndTextPlainContentType(httpHandler2);
		String responseText2 = httpHandler2.getResponseText();
		assertTrue(responseText2.startsWith("POST /remember_this_call"));
		assertTrue(responseText2.contains("some body content"));

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 2),
				"Call recieved: Get previous call");
		stopServer("2223");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 4),
				"...stopping HttpListener");
	}

	@Test
	public void testGetPreviousNoCallsMade() throws Exception {
		startServer("11111");
		HttpHandler httpHandler2 = httpHandlerFactory
				.factor("http://localhost:11111/getCallNo/987");
		assertResponseCodeOkAndTextPlainContentType(httpHandler2);
		String responseText2 = httpHandler2.getResponseText();
		assertEquals(responseText2, "No calls registered.");
		stopServer("11111");
	}

	@Test
	public void testGetPreviousCallNotMade() throws Exception {
		startServer("11111");
		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:11111/remember_this_call");
		httpHandler.getResponseCode();

		HttpHandler httpHandler2 = httpHandlerFactory
				.factor("http://localhost:11111/getCallNo/987");
		assertResponseCodeOkAndTextPlainContentType(httpHandler2);
		String responseText2 = httpHandler2.getResponseText();
		assertEquals(responseText2,
				"No call made with no:987, the highest registered call number is:0 ");
		stopServer("11111");

	}

	@Test
	public void testEmptyCallMemory() throws Exception {
		startServer("11111");

		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:11111/remember_this_call1");
		httpHandler.getResponseCode();

		HttpHandler httpHandlerEmpty = httpHandlerFactory
				.factor("http://localhost:11111/empty_memory");
		assertResponseCodeOkAndTextPlainContentType(httpHandler);
		String responseText = httpHandlerEmpty.getResponseText();
		assertEquals(responseText, "forgot all remembered calls");

		HttpHandler httpHandler3 = httpHandlerFactory
				.factor("http://localhost:11111/remember_this_call");

		assertResponseCodeOkAndTextPlainContentType(httpHandler3);
		String responseText3 = httpHandler3.getResponseText();
		assertEquals(responseText3, "remembering call as no: 0");

		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 2),
				"Call recieved: Empty memory");
		stopServer("11111");
	}

	@Test
	public void testCoverageForHttpListener() throws Exception {
		new HttpListener();
	}

	private String getInfoLogNo(int messageNo) {
		return loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo(testedClassName, messageNo);
	}

	private String getFatalLogNo(int messageNo) {
		return loggerFactorySpy.getFatalLogMessageUsingClassNameAndNo(testedClassName, messageNo);
	}

	public class HttpListenerThread extends Thread {

		private String[] argsToHttpListner;

		public HttpListenerThread(String port) {
			argsToHttpListner = new String[] { port };
		}

		@Override
		public void run() {
			try {
				HttpListener.main(argsToHttpListner);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
