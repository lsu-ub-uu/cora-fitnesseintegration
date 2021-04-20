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

import java.util.Collections;
import java.util.Map;

import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessageSender;
import se.uu.ub.cora.messaging.MessagingProvider;

/**
 * MessageSenderFixture is used to send messages to Coras messaging system.
 */
public class MessageSenderFixture {
	private Map<String, Object> headers = Collections.emptyMap();

	public void sendMessage(String message) {
		MessageSender messageSender = createMessageSender();
		messageSender.sendMessage(headers, message);
	}

	private MessageSender createMessageSender() {
		MessageRoutingInfo messageRoutingInfo = MessageRoutingInfoHolder.getMessageRoutingInfo();
		return MessagingProvider.getTopicMessageSender(messageRoutingInfo);
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}
}
