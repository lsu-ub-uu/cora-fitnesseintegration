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
package se.uu.ub.cora.fitnesseintegration.message;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertSame;

import org.testng.annotations.Test;

import se.uu.ub.cora.messaging.MessageRoutingInfo;

public class MessageRoutingInfoHolderTest {

	@Test
	public void testInit() {
		MessageRoutingInfoHolder holder = new MessageRoutingInfoHolder();
		assertNotNull(holder);
	}

	@Test
	public void testSetRoutingInfo() {
		MessageRoutingInfo routingInfo = new MessageRoutingInfo("hostName", "port", "routingkey");
		MessageRoutingInfoHolder.setMessageRoutingInfo(routingInfo);
		assertSame(MessageRoutingInfoHolder.getMessageRoutingInfo(), routingInfo);
	}
}
