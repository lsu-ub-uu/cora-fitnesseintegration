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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.spy.LoggerFactorySpy;
import se.uu.ub.cora.logger.LoggerFactory;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.messaging.MessagingProvider;

public class MessageSenderFixtureTest {
	private MessagingFactorySpy messagingFactory;
	private MessageSenderFixture fixture;

	@BeforeMethod
	public void setUp() {
		LoggerFactory loggerFactory = new LoggerFactorySpy();
		LoggerProvider.setLoggerFactory(loggerFactory);
		messagingFactory = new MessagingFactorySpy();
		MessagingProvider.setMessagingFactory(messagingFactory);

		fixture = new MessageSenderFixture();
	}

	@Test
	public void testEmptyHeaders() {
		String message = "some JMS message";

		fixture.sendMessage(message);

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(sender.message, message);
		assertEquals(sender.headers, Collections.emptyMap());
	}

	@Test
	public void testInit() {
		Map<String, Object> headers = new HashMap<>();
		fixture.setHeaders(headers);
		String message = "some JMS message";

		fixture.sendMessage(message);

		MessageSenderSpy sender = messagingFactory.factoredSenders.get(0);
		assertEquals(sender.message, message);
		assertEquals(sender.headers, headers);

	}

}
