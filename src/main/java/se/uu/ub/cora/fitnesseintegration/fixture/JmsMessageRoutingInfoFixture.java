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

import se.uu.ub.cora.fitnesseintegration.message.MessageRoutingInfoHolder;
import se.uu.ub.cora.messaging.JmsMessageRoutingInfo;

public class JmsMessageRoutingInfoFixture {

	private String hostname;
	private String port;
	private String routingKey;
	private String username;
	private String password;

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public void setPort(String port) {
		this.port = port;

	}

	public void setRoutingKey(String routingKey) {
		this.routingKey = routingKey;

	}

	public void setUsername(String username) {
		this.username = username;

	}

	public void setPassword(String password) {
		this.password = password;

	}

	public void createInHolder() {
		MessageRoutingInfoHolder.setMessageRoutingInfo(
				new JmsMessageRoutingInfo(hostname, port, routingKey, username, password));

	}

}
