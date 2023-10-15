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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import se.uu.ub.cora.fitnesseintegration.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.JsonHandler;
import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.json.parser.JsonObject;
import se.uu.ub.cora.json.parser.JsonString;
import se.uu.ub.cora.json.parser.JsonValue;
import se.uu.ub.cora.messaging.MessageRoutingInfo;
import se.uu.ub.cora.messaging.MessageSender;
import se.uu.ub.cora.messaging.MessagingProvider;

/**
 * MessageSenderFixture is used to send messages to Coras messaging system.
 */
public class MessageSenderFixture {

	private Map<String, Object> headers = new HashMap<>();
	protected JsonHandler jsonHandler = DependencyProvider.getJsonHandler();
	private String jsonHeaders = "";
	private String message = "";

	public String sendMessage() {
		try {
			return tryToSendMessage(message);
		} catch (Exception exception) {
			return exception.getMessage();
		}
	}

	private String tryToSendMessage(String message) {
		handleHeaders();
		MessageSender messageSender = createMessageSender();
		messageSender.sendMessage(headers, message);
		return "OK";
	}

	private void handleHeaders() {
		if (!jsonHeaders.isEmpty()) {
			parseHeaders();
		}
	}

	private void parseHeaders() {
		JsonObject headersObject = jsonHandler.parseStringAsObject(jsonHeaders);
		addHeadersFromJsonObject(headersObject);
	}

	private void addHeadersFromJsonObject(JsonObject headersObject) {
		headers = new HashMap<>();
		for (Entry<String, JsonValue> entry : headersObject.entrySet()) {
			addHeader(entry);
		}
	}

	private void addHeader(Entry<String, JsonValue> entry) {
		String key = entry.getKey();
		JsonString value = (JsonString) entry.getValue();
		headers.put(key, value.getStringValue());
	}

	private MessageSender createMessageSender() {
		MessageRoutingInfo messageRoutingInfo = MessageRoutingInfoHolder.getMessageRoutingInfo();
		return MessagingProvider.getTopicMessageSender(messageRoutingInfo);
	}

	public void setHeaders(String headers) {
		this.jsonHeaders = headers;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
