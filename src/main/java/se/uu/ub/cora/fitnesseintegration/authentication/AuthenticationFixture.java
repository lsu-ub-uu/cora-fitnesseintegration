package se.uu.ub.cora.fitnesseintegration.authentication;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se.uu.ub.cora.clientdata.ClientAction;
import se.uu.ub.cora.clientdata.ClientActionLink;
import se.uu.ub.cora.clientdata.ClientDataAuthentication;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverter;
import se.uu.ub.cora.clientdata.converter.JsonToClientDataConverterProvider;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;
import se.uu.ub.cora.fitnesseintegration.script.SystemUrl;
import se.uu.ub.cora.httphandler.HttpHandler;
import se.uu.ub.cora.httphandler.HttpHandlerFactory;

public class AuthenticationFixture {

	private static final String POST = "POST";
	private static final String NEW_LINE = "\n";
	private Status statusType;
	private HttpHandlerFactory factory;
	private ClientDataAuthentication authentication;
	private ClientActionLink renewActionLink;
	private ClientActionLink deleteActionLink;

	private String loginId;

	private String appTokenEndpoint = SystemUrl.getAppTokenVerifierUrl() + "rest/apptoken";
	private String appToken;

	private String passwordEndpoint = SystemUrl.getAppTokenVerifierUrl() + "rest/password";
	private String password;

	public AuthenticationFixture() {
		factory = DependencyProvider.getHttpHandlerFactory();
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void setAppToken(String token) {
		this.appToken = token;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void appTokenLogin() {
		HttpHandler httpHandler = factorHttpHandler(appTokenEndpoint);
		httpHandler.setRequestMethod(POST);
		getFitnesseUserApptoken();
		httpHandler.setOutput(loginId + NEW_LINE + appToken);

		tryToAuthenticate(httpHandler);
	}

	private HttpHandler factorHttpHandler(String loginEndpoint) {
		return factory.factor(loginEndpoint);
	}

	private void getFitnesseUserApptoken() {
		if (appToken == null || "".equals(appToken)) {
			if ("fitnesseAdmin@system.cora.uu.se".equals(loginId)) {
				appToken = "29c30232-d514-4559-b60b-6de47175c1df";
			} else if ("fitnesseUser@system.cora.uu.se".equals(loginId)) {
				appToken = "bd699488-f9d1-419d-a79d-9fa8a0f3bb9d";
			}
		}
	}

	private void tryToAuthenticate(HttpHandler httpHandler) {
		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType == Response.Status.CREATED) {
			String responseText = httpHandler.getResponseText();
			parseAuthTokenJsonToClientDataAuthentication(responseText);
			getActionLinks();
		}
	}

	private void parseAuthTokenJsonToClientDataAuthentication(String authTokenJson) {
		// authTokenJson = possiblyDecodeJavascriptEncoded(authTokenJson);
		JsonToClientDataConverter jsonToClientDataConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(authTokenJson);
		authentication = (ClientDataAuthentication) jsonToClientDataConverter.toInstance();
	}

	// private String possiblyDecodeJavascriptEncoded(String stringToDecode) {
	// Map<String, String> decodePairs = new HashMap<>();
	// decodePairs.put("\\x27", "'");
	// decodePairs.put("\\x26", "&");
	// decodePairs.put("\\x22", "\"");
	// decodePairs.put("\\-", "-");
	// decodePairs.put("\\/", "/");
	// decodePairs.put("\\\\", "\\");
	//
	// for (Entry<String, String> entry : decodePairs.entrySet()) {
	// stringToDecode = stringToDecode.replace(entry.getKey(), entry.getValue());
	// }
	// return stringToDecode;
	// }

	public void passwordLogin() {
		HttpHandler httpHandler = factorHttpHandler(passwordEndpoint);
		httpHandler.setRequestMethod(POST);
		httpHandler.setOutput(loginId + NEW_LINE + password);

		tryToAuthenticate(httpHandler);
	}

	public String getResponseStatus() {
		return statusType.toString().toUpperCase();
	}

	public String getToken() {
		return authentication.getToken();
	}

	public String getLoginId() {
		return authentication.getLoginId();
	}

	public String getUserId() {
		return authentication.getUserId();
	}

	public String getValidUntil() {
		return authentication.getValidUntil();
	}

	public String getRenewUntil() {
		return authentication.getRenewUntil();
	}

	public String getFirstName() {
		return authentication.getFirstName();
	}

	public String getLastName() {
		return authentication.getLastName();
	}

	public void getRenewLink() {
	}

	public void getDeleteLink() {
	}

	private void getActionLinks() {
		renewActionLink = authentication.getActionLink(ClientAction.RENEW).get();
		deleteActionLink = authentication.getActionLink(ClientAction.DELETE).get();
	}
}
