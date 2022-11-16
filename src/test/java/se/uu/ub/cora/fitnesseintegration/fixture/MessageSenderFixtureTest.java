/*
 * Copyright 2021 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.fixture;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

import java.util.Collections;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.logger.spies.LoggerFactorySpy;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessagingProvider;

public class MessageSenderFixtureTest {
	private MessagingFactorySpy messagingFactory;
	private MessageSenderFixture fixture;
	private String message = "some JMS message";
	private MessageRoutingInfo defaultRoutingInfo;

	@BeforeMethod
	public void setUp() {
		setUpMessagingProvider();
		setRoutingInfoInHolder();
		fixture = new MessageSenderFixture();
	}

	private void setUpMessagingProvider() {
		createLoggerSpiesNeededByMessaging();
		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);
	}

	private void createLoggerSpiesNeededByMessaging() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
	}

	private void setRoutingInfoInHolder() {
		defaultRoutingInfo = new MessageRoutingInfo("hostName", "port", "routingkey");
		MessageRoutingInfoHolder.setMessageRoutingInfo(defaultRoutingInfo);
	}

	@Test
	public void testEmptyHeadersAndMessage() {
		String answer = fixture.sendMessage();

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(answer, "OK");
		assertEquals(sender.message, "");
		assertEquals(sender.headers, Collections.emptyMap());
	}

	@Test
	public void testSendMessage() {
		setDefaultHeadersInFixture();
		fixture.setMessage(message);

		String answer = fixture.sendMessage();

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(answer, "OK");
		assertEquals(sender.message, message);
		String pid = (String) sender.headers.get("pid");
		assertEquals(pid, "somePid");
	}

	private void setDefaultHeadersInFixture() {
		String headers = "{pid:somePid}";
		fixture.setHeaders(headers);
	}

	@Test
	public void testSendMessageCheckRoutingInfo() {
		setDefaultHeadersInFixture();

		fixture.sendMessage();

		assertSame(messagingFactory.routingInfos.get(0), defaultRoutingInfo);
		assertRoutingInfoIsLatestFromHolder();
	}

	private void assertRoutingInfoIsLatestFromHolder() {
		MessageRoutingInfo routingInfo2 = new MessageRoutingInfo("hostName2", "port2",
				"routingkey2");
		MessageRoutingInfoHolder.setMessageRoutingInfo(routingInfo2);
		fixture.sendMessage();
		assertSame(messagingFactory.routingInfos.get(1), routingInfo2);
	}

	@Test
	public void testSetMultipleHeaders() {
		String headers = "{pid:somePid, methodName:modifyObject, someOtherHeader:someValue}";
		fixture.setHeaders(headers);

		fixture.sendMessage();

		assertCorrectValuesInHeader();
	}

	private void assertCorrectValuesInHeader() {
		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertHeadersContains(sender, "pid", "somePid");
		assertHeadersContains(sender, "methodName", "modifyObject");
		assertHeadersContains(sender, "someOtherHeader", "someValue");
	}

	private void assertHeadersContains(MessageSenderSpy sender, String key, String value) {
		String pid = (String) sender.headers.get(key);
		assertEquals(pid, value);
	}

	@Test
	public void testBrokenHeadersJsonReturnsErrorDoesNotSendMessage() throws Exception {
		String headers = "{pid:}";
		fixture.setHeaders(headers);

		String answer = fixture.sendMessage();
		assertEquals(answer, "Unable to parse json string");
		assertEquals(messagingFactory.factoredSenders.size(), 0);
	}

}
