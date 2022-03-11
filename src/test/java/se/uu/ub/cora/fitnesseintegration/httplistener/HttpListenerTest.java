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
		httpListenerThread = new HttpListenerThread("1111");
		httpListenerThread.start();
		TimeUnit.MILLISECONDS.sleep(100);
		assertEquals(getInfoLogNo(0), "HttpListener starting...");
		assertEquals(loggerFactorySpy.getInfoLogMessageUsingClassNameAndNo("HttpListenerInt", 0),
				"Listening for connection on port 1111");
		httpListenerThread.interrupt();
	}

	@Test
	public void testInitListensToCorrectPort() throws Exception {
		httpListenerThread = new HttpListenerThread("11111");
		httpListenerThread.start();
		HttpHandler httpHandler = httpHandlerFactory.factor("http://localhost:11111");
		int responseCode = httpHandler.getResponseCode();
		assertEquals(responseCode, 200);
		httpListenerThread.interrupt();
	}

	@Test
	public void testListensToAndRemembersCalls() throws Exception {
		httpListenerThread = new HttpListenerThread("111");
		httpListenerThread.start();

		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:111/remember_this_call");
		int responseCode2 = httpHandler.getResponseCode();
		String responseText2 = httpHandler.getResponseText();

		assertEquals(responseCode2, 200);
		assertEquals(responseText2, "remembering call as no: 0");
		httpListenerThread.interrupt();
	}

	@Test
	public void testGetPreviousCall() throws Exception {
		httpListenerThread = new HttpListenerThread("222");
		httpListenerThread.start();
		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:222/remember_this_call");
		httpHandler.getResponseCode();

		HttpHandler httpHandler2 = httpHandlerFactory.factor("http://localhost:222/getCallNo/0");
		int responseCode2 = httpHandler2.getResponseCode();
		String responseText2 = httpHandler2.getResponseText();

		assertEquals(responseCode2, 200);
		assertTrue(responseText2.startsWith("GET /remember_this_call"));
		httpListenerThread.interrupt();
	}

	@Test
	public void testEmptyCallMemory() throws Exception {
		httpListenerThread = new HttpListenerThread("333");
		httpListenerThread.start();

		HttpHandler httpHandler = httpHandlerFactory
				.factor("http://localhost:333/remember_this_call1");
		httpHandler.getResponseCode();

		HttpHandler httpHandlerEmpty = httpHandlerFactory
				.factor("http://localhost:333/empty_memory");
		int responseCode = httpHandlerEmpty.getResponseCode();
		String responseText = httpHandlerEmpty.getResponseText();
		assertEquals(responseCode, 200);
		assertEquals(responseText, "forgot all remembered calls");

		HttpHandler httpHandler3 = httpHandlerFactory
				.factor("http://localhost:333/remember_this_call");
		int responseCode3 = httpHandler3.getResponseCode();
		String responseText3 = httpHandler3.getResponseText();

		assertEquals(responseCode3, 200);
		assertEquals(responseText3, "remembering call as no: 0");
		httpListenerThread.interrupt();
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
