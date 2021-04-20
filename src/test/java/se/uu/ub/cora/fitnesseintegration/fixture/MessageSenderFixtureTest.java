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
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.fitnesseintegration.spy.LoggerFactorySpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessagingProvider;

public class MessageSenderFixtureTest {
	private MessagingFactorySpy messagingFactory;
	private MessageSenderFixture fixture;
	private String message = "some JMS message";
	private MessageRoutingInfo defaultRoutingInfo;

	@BeforeMethod
	public void setUp() {
		setUpProviders();

		setRoutingInfoInHolder();
		fixture = new MessageSenderFixture();

	}

	private void setUpProviders() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);
	}

	private void setRoutingInfoInHolder() {
		defaultRoutingInfo = new MessageRoutingInfo("hostName", "port", "routingkey");
		MessageRoutingInfoHolder.setMessageRoutingInfo(defaultRoutingInfo);
	}

	@Test
	public void testEmptyHeaders() {
		fixture.sendMessage(message);

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(sender.message, message);
		assertEquals(sender.headers, Collections.emptyMap());
	}

	@Test
	public void testSendMessage() {
		Map<String, Object> headers = createDefaultHeaders();
		fixture.setHeaders(headers);

		fixture.sendMessage(message);

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(sender.message, message);
		assertSame(sender.headers, headers);

	}

	private Map<String, Object> createDefaultHeaders() {
		Map<String, Object> headers = new HashMap<>();
		headers.put("pid", "somePid");
		return headers;
	}

	@Test
	public void testSendMessageCheckRoutingInfo() {
		Map<String, Object> headers = createDefaultHeaders();
		fixture.setHeaders(headers);

		fixture.sendMessage(message);

		assertSame(messagingFactory.routingInfos.get(0), defaultRoutingInfo);

		assertRoutingInfoIsLatestFromHolder();

	}

	private void assertRoutingInfoIsLatestFromHolder() {
		MessageRoutingInfo routingInfo2 = new MessageRoutingInfo("hostName2", "port2",
				"routingkey2");
		MessageRoutingInfoHolder.setMessageRoutingInfo(routingInfo2);
		fixture.sendMessage(message);
		assertSame(messagingFactory.routingInfos.get(1), routingInfo2);
	}

}
