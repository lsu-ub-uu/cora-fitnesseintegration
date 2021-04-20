package se.uu.ub.cora.fitnesseintegration.fixture;

import java.util.Map;

import se.uu.ub.cora.messaging.MessageSender;

public class MessageSenderSpy implements MessageSender {

	public Map<String, Object> headers;
	public String message;

	@Override
	public void sendMessage(Map<String, Object> headers, String message) {
		this.headers = headers;
		this.message = message;
	}

}
