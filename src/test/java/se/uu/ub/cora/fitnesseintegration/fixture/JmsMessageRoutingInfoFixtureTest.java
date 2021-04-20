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
import static org.testng.Assert.assertTrue;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.messaging.JmsMessageRoutingInfo;
import se.uu.ub.cora.messaging.MessageRoutingInfo;

public class JmsMessageRoutingInfoFixtureTest {
	JmsMessageRoutingInfoFixture fixture;

	@BeforeMethod
	public void beforeMethod() {
		MessageRoutingInfoHolder.setMessageRoutingInfo(null);
		fixture = new JmsMessageRoutingInfoFixture();
	}

	@Test
	public void testNoParameters() throws Exception {
		fixture.createInHolder();
		MessageRoutingInfo messageRoutingInfo = MessageRoutingInfoHolder.getMessageRoutingInfo();
		assertTrue(messageRoutingInfo instanceof JmsMessageRoutingInfo);
	}

	@Test
	public void testParameters() throws Exception {
		String hostname = "hostname";
		fixture.setHostname(hostname);
		String port = "port";
		fixture.setPort(port);
		String routingKey = "routingKey";
		fixture.setRoutingKey(routingKey);
		String username = "username";
		fixture.setUsername(username);
		String password = "password";
		fixture.setPassword(password);

		fixture.createInHolder();

		JmsMessageRoutingInfo messageRoutingInfo = (JmsMessageRoutingInfo) MessageRoutingInfoHolder
				.getMessageRoutingInfo();
		assertEquals(messageRoutingInfo.hostname, hostname);
		assertEquals(messageRoutingInfo.port, port);
		assertEquals(messageRoutingInfo.routingKey, routingKey);
		assertEquals(messageRoutingInfo.username, username);
		assertEquals(messageRoutingInfo.password, password);
	}
}
