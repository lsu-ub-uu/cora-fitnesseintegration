package se.uu.ub.cora.fitnesseintegration.authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

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

	private static final String FITNESSE_ADMIN = "fitnesseAdmin@system.cora.uu.se";
	private static final String FITNESSE_ADMIN_APPTOKEN = "29c30232-d514-4559-b60b-6de47175c1df";
	private static final String FITNESSE_USER = "fitnesseUser@system.cora.uu.se";
	private static final String FITNESSE_USER_APPTOKEN = "bd699488-f9d1-419d-a79d-9fa8a0f3bb9d";
	private static final String GET = "GET";
	private static final String POST = "POST";
	private static final String NEW_LINE = "\n";

	private Status statusType;
	private HttpHandlerFactory factory;
	private HttpHandler httpHandler;
	private String response;
	private ClientDataAuthentication authentication;
	private ClientActionLink renewActionLink;
	private ClientActionLink deleteActionLink;

	private String loginId;

	private String appTokenEndpoint = SystemUrl.getAppTokenVerifierUrl() + "rest/apptoken";
	private String appToken;

	private String passwordEndpoint = SystemUrl.getAppTokenVerifierUrl() + "rest/password";
	private String password;

	private String idpLoginEndpoint = SystemUrl.getIdpLoginUrl();
	private String eppn;

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

	public void setEPPN(String eppn) {
		this.eppn = eppn;
	}

	public String appTokenLogin() {
		factorHttpHandler(POST, appTokenEndpoint);
		possiblyGetFitnesseUserApptoken();
		httpHandler.setOutput(loginId + NEW_LINE + appToken);

		tryToAuthenticate(httpHandler);
		return response;
	}

	private void factorHttpHandler(String method, String loginEndpoint) {
		httpHandler = factory.factor(loginEndpoint);
		httpHandler.setRequestMethod(method);
	}

	private void possiblyGetFitnesseUserApptoken() {
		if (appToken == null || "".equals(appToken)) {
			if (FITNESSE_ADMIN.equals(loginId)) {
				appToken = FITNESSE_ADMIN_APPTOKEN;
			} else if (FITNESSE_USER.equals(loginId)) {
				appToken = FITNESSE_USER_APPTOKEN;
			}
		}
	}

	private void tryToAuthenticate(HttpHandler httpHandler) {
		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		if (statusType == Response.Status.CREATED) {
			response = httpHandler.getResponseText();
			parseAuthTokenJsonToClientDataAuthentication(response);
		} else {
			response = httpHandler.getErrorText();
		}
	}

	private void parseAuthTokenJsonToClientDataAuthentication(String responseToParse) {
		responseToParse = possiblyDecodeJavascriptEncoded(responseToParse);
		JsonToClientDataConverter jsonToClientDataConverter = JsonToClientDataConverterProvider
				.getConverterUsingJsonString(responseToParse);
		authentication = (ClientDataAuthentication) jsonToClientDataConverter.toInstance();
		getActionLinks();
	}

	private String possiblyDecodeJavascriptEncoded(String responseToParse) {
		Map<String, String> decodePairs = new HashMap<>();
		decodePairs.put("\\x27", "'");
		decodePairs.put("\\x26", "&");
		decodePairs.put("\\x22", "\"");
		decodePairs.put("\\-", "-");
		decodePairs.put("\\/", "/");
		decodePairs.put("\\\\", "\\");

		for (Entry<String, String> entry : decodePairs.entrySet()) {
			responseToParse = responseToParse.replace(entry.getKey(), entry.getValue());
		}
		return responseToParse;
	}

	public String passwordLogin() {
		factorHttpHandler(POST, passwordEndpoint);
		httpHandler.setOutput(loginId + NEW_LINE + password);

		tryToAuthenticate(httpHandler);
		return response;
	}

	public String idpLogin() {
		factorHttpHandler(GET, idpLoginEndpoint);
		httpHandler.setRequestProperty("eppn", eppn);
		httpHandler.setRequestProperty("sn", "someLastName");
		httpHandler.setRequestProperty("givenName", "someFirstName");

		statusType = Response.Status.fromStatusCode(httpHandler.getResponseCode());
		response = httpHandler.getResponseText();
		String authenticationJson = tryToGetFirstMatchFromAnswerUsingRegEx(response);
		parseAuthTokenJsonToClientDataAuthentication(authenticationJson);

		return response;
	}

	private String tryToGetFirstMatchFromAnswerUsingRegEx(String responseToParse) {
		try {
			return getFirstMatchFromAnswerUsingRegEx(responseToParse);
		} catch (Exception e) {
			return "Not parseable";
		}
	}

	private String getFirstMatchFromAnswerUsingRegEx(String responseToParse) {
		Pattern pattern = Pattern.compile("authentication\\s=\\s([\\s\\S]*?});");
		Matcher matcher = pattern.matcher(responseToParse);
		matcher.find();
		return matcher.group(1);
	}

	public StatusType getResponseStatus() {
		return statusType;
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

	public String getRenewUrl() {
		return renewActionLink.getURL();
	}

	public String getRenewRequestMethod() {
		return renewActionLink.getRequestMethod();
	}

	public String getRenewContentType() {
		return renewActionLink.getContentType();
	}

	public String getRenewAccept() {
		return renewActionLink.getAccept();
	}

	public String getDeleteUrl() {
		return deleteActionLink.getURL();
	}

	public String getDeleteRequestMethod() {
		return deleteActionLink.getRequestMethod();
	}

	public String getDeleteContentType() {
		return deleteActionLink.getContentType();
	}

	public String getDeleteAccept() {
		return deleteActionLink.getAccept();
	}

	private void getActionLinks() {
		Optional<ClientActionLink> optRenewLink = authentication.getActionLink(ClientAction.RENEW);
		if (optRenewLink.isPresent()) {
			renewActionLink = optRenewLink.get();
		} else {
			throw new NoSuchElementException("Renew actionLink is missing");
		}

		Optional<ClientActionLink> optDeleteLink = authentication
				.getActionLink(ClientAction.DELETE);
		if (optDeleteLink.isPresent()) {
			deleteActionLink = optDeleteLink.get();
		} else {
			throw new NoSuchElementException("Delete actionLink is missing");
		}
	}
}
