/*
 * Copyright 2016 Uppsala University Library
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

package se.uu.ub.cora.fitnesseintegration;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

import se.uu.ub.cora.httphandler.HttpHandler;

public class HttpHandlerSpy implements HttpHandler {

	public HttpURLConnection httpUrlConnection;
	public String requestMetod;
	public String outputString;
	public Map<String, String> requestProperties = new HashMap<>();
	public InputStream stream;
	public int responseCode = 200;
	public String responseText = "Everything ok";

	public String idFromLogin = "other@user.domain.org";
	public String authTokenJsEscaped = "a8675062\\-a00d\\-4f6b\\-ada3\\-510934ad779d";
	public String validForNoSeconds = "600";
	public String deleteUrlJsEscaped = "http:\\/\\/localhost:8180\\/apptokenverifier\\/rest\\/apptoken\\/141414";
	public String mainSystemDomain;
	public String returnedHeaderField;
	public String errorText = "";

	private HttpHandlerSpy(HttpURLConnection httpUrlConnection) {
		this.httpUrlConnection = httpUrlConnection;
	}

	public static HttpHandlerSpy usingURLConnection(HttpURLConnection httpUrlConnection) {
		return new HttpHandlerSpy(httpUrlConnection);
	}

	@Override
	public void setRequestMethod(String requestMetod) {
		this.requestMetod = requestMetod;
	}

	@Override
	public String getResponseText() {
		if (httpUrlConnection.getURL().toString().contains("autogeneratedIdType")) {
			return "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"sound:23310139824886\"},{\"name\":\"type\",\"value\":\"sound\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"sound\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/sound/sound:23310139824886\",\"accept\":\"application/vnd.uub.record+json\"},\"upload\":{\"requestMethod\":\"POST\",\"rel\":\"upload\",\"contentType\":\"multipart/form-data\",\"url\":\"http://localhost:8080/therest/rest/record/sound/sound:23310139824886/master\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/sound/sound:23310139824886\",\"accept\":\"application/vnd.uub.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/sound/sound:23310139824886\"}}}}";
		}
		if (httpUrlConnection.getURL().toString().contains("appToken")) {
			return "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"appToken:7053734211763\"},{\"name\":\"type\",\"value\":\"appToken\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"},{\"name\":\"note\",\"value\":\"My  device\"},{\"name\":\"token\",\"value\":\"ba064c86-bd7c-4283-a5f3-86ba1dade3f3\"}],\"name\":\"appToken\"},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"},\"read_incoming_links\":{\"requestMethod\":\"GET\",\"rel\":\"read_incoming_links\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763/incomingLinks\",\"accept\":\"application/vnd.uub.recordList+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/appToken/appToken:7053734211763\",\"accept\":\"application/vnd.uub.record+json\"}}}}";
		}
		if (httpUrlConnection.getURL().toString().contains("rest/apptoken/")) {
			return "{\"children\":[{\"name\":\"id\",\"value\":\"a1acff95-5849-4e10-9ee9-4b192aef17fd\"},{\"name\":\"validForNoSeconds\",\"value\":\"600\"}],\"name\":\"authToken\"}";
		}
		if (httpUrlConnection.getURL().toString().contains("someRecordType")) {
			return "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"someId\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"someRecordType\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/recordType/someRecordType\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"type\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"someRecordTypeAttribute\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\"}}}}";
		}
		if (httpUrlConnection.getURL().toString().contains("someWrongRecordTypeWrongJson")) {
			return "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"id\",\"value\":\"someId\"},{\"name\":\"createdBy\",\"value\":\"131313\"}],\"name\":\"recordInfo\"}],\"name\":\"binary\",\"attributes\":{\"type\":\"someRecordTypeAttribute\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\",\"accept\":\"application/vnd.uub.record+json\"},\"delete\":{\"requestMethod\":\"DELETE\",\"rel\":\"delete\",\"url\":\"http://localhost:8080/therest/rest/record/someRecordType/someId\"}}}}";
		}
		if (httpUrlConnection.getURL().toString().contains("metadataGroup/someMetadataGroupId")) {
			return "{\"record\":{\"data\":{\"children\":[{\"name\":\"nameInData\",\"value\":\"presentation\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"recordLink\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataRecordLink/linkedRecordPresentationPresentationLink\",\"accept\":\"application/vnd.uub.record+json\"}}}}";
		}
		if (httpUrlConnection.getURL().toString().contains("metadataGroup/someRecordId")) {
			responseText = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"recordInfoGroup\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"metadataGroup\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/recordType/metadataGroup\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"type\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"systemOneUser\"},{\"name\":\"linkedRecordId\",\"value\":\"12345\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/systemOneUser/12345\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"createdBy\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"cora\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/system/cora\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"dataDivider\"},{\"name\":\"tsCreated\",\"value\":\"2017-10-01 00:00:00.0\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/user/141414\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"updatedBy\"},{\"name\":\"tsUpdated\",\"value\":\"2018-04-24 10:25:25.659\"}],\"name\":\"updated\"},{\"repeatId\":\"2\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/user/141414\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"updatedBy\"},{\"name\":\"tsUpdated\",\"value\":\"2018-04-24 14:22:10.111\"}],\"name\":\"updated\"},{\"repeatId\":\"3\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/user/141414\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"updatedBy\"},{\"name\":\"tsUpdated\",\"value\":\"2018-09-27 09:24:02.191\"}],\"name\":\"updated\"}],\"name\":\"recordInfo\"},{\"name\":\"nameInData\",\"value\":\"recordInfo\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"recordInfoText\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/coraText/recordInfoText\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"textId\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"coraText\"},{\"name\":\"linkedRecordId\",\"value\":\"recordInfoDefText\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/coraText/recordInfoDefText\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"defTextId\"},{\"children\":[{\"repeatId\":\"1\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"1\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataTextVariable\"},{\"name\":\"linkedRecordId\",\"value\":\"idTextVar\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataTextVariable/idTextVar\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"},{\"repeatId\":\"0\",\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"collectIndexTerm\"},{\"name\":\"linkedRecordId\",\"value\":\"recordIdCollectIndexTerm\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/collectIndexTerm/recordIdCollectIndexTerm\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"childRefCollectTerm\",\"attributes\":{\"type\":\"index\"}}],\"name\":\"childReference\"},{\"repeatId\":\"5\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"1\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataRecordLink\"},{\"name\":\"linkedRecordId\",\"value\":\"recordTypeLink\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataRecordLink/recordTypeLink\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"}],\"name\":\"childReference\"},{\"repeatId\":\"3\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"1\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataRecordLink\"},{\"name\":\"linkedRecordId\",\"value\":\"createdByLink\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataRecordLink/createdByLink\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"}],\"name\":\"childReference\"},{\"repeatId\":\"4\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"1\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataRecordLink\"},{\"name\":\"linkedRecordId\",\"value\":\"dataDividerLink\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataRecordLink/dataDividerLink\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"collectPermissionTerm\"},{\"name\":\"linkedRecordId\",\"value\":\"systemPermissionTerm\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/collectPermissionTerm/systemPermissionTerm\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"childRefCollectTerm\",\"attributes\":{\"type\":\"permission\"}}],\"name\":\"childReference\"},{\"repeatId\":\"6\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"0\"},{\"name\":\"repeatMax\",\"value\":\"X\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataRecordLink\"},{\"name\":\"linkedRecordId\",\"value\":\"permissionUnitLink\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataRecordLink/permissionUnitLink\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"}],\"name\":\"childReference\"},{\"repeatId\":\"8\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"1\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataTextVariable\"},{\"name\":\"linkedRecordId\",\"value\":\"tsCreatedTextVar\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataTextVariable/tsCreatedTextVar\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"}],\"name\":\"childReference\"},{\"repeatId\":\"10\",\"children\":[{\"name\":\"repeatMin\",\"value\":\"1\"},{\"name\":\"repeatMax\",\"value\":\"X\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"metadataGroup\"},{\"name\":\"linkedRecordId\",\"value\":\"updatedGroup\"}],\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataGroup/updatedGroup\",\"accept\":\"application/vnd.uub.record+json\"}},\"name\":\"ref\"}],\"name\":\"childReference\"}],\"name\":\"childReferences\"}],\"name\":\"metadata\",\"attributes\":{\"type\":\"group\"}},\"actionLinks\":{\"read\":{\"requestMethod\":\"GET\",\"rel\":\"read\",\"url\":\"http://localhost:8080/therest/rest/record/metadataGroup/recordInfoGroup\",\"accept\":\"application/vnd.uub.record+json\"},\"read_incoming_links\":{\"requestMethod\":\"GET\",\"rel\":\"read_incoming_links\",\"url\":\"http://localhost:8080/therest/rest/record/metadataGroup/recordInfoGroup/incomingLinks\",\"accept\":\"application/vnd.uub.recordList+json\"},\"update\":{\"requestMethod\":\"POST\",\"rel\":\"update\",\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/metadataGroup/recordInfoGroup\",\"accept\":\"application/vnd.uub.record+json\"},\"index\":{\"requestMethod\":\"POST\",\"rel\":\"index\",\"body\":{\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"metadataGroup\"}],\"name\":\"recordType\"},{\"name\":\"recordId\",\"value\":\"recordInfoGroup\"},{\"name\":\"type\",\"value\":\"index\"}],\"name\":\"workOrder\"},\"contentType\":\"application/vnd.uub.record+json\",\"url\":\"http://localhost:8080/therest/rest/record/workOrder/\",\"accept\":\"application/vnd.uub.record+json\"}}}}";
			return responseText;
		}
		if (httpUrlConnection.getURL().toString().contains("workOrder")) {
			responseText = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"validationOrder:3611202964781\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"validationOrder\"}],\"name\":\"type\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"name\":\"createdBy\"},{\"name\":\"tsCreated\",\"value\":\"2019-02-23 07:23:53.856\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"name\":\"updatedBy\"},{\"name\":\"tsUpdated\",\"value\":\"2019-02-23 07:23:53.856\"}],\"name\":\"updated\"}],\"name\":\"recordInfo\"},{\"name\":\"valid\",\"value\":\"true\"}],\"name\":\"validationResult\"},\"actionLinks\":{}}}";
			return responseText;
		}
		if (httpUrlConnection.getURL().toString().contains("someCheckChildrenOkType")) {
			responseText = "{\"record\":{\"data\":{\"children\":[{\"children\":[{\"name\":\"id\",\"value\":\"cirkelfys\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"name\":\"createdBy\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"system\"},{\"name\":\"linkedRecordId\",\"value\":\"systemOne\"}],\"name\":\"dataDivider\"},{\"name\":\"tsCreated\",\"value\":\"2018-08-29T14:27:20.307000Z\"},{\"repeatId\":\"0\",\"children\":[{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"141414\"}],\"name\":\"updatedBy\"},{\"name\":\"tsUpdated\",\"value\":\"2018-08-29T14:27:20.307000Z\"}],\"name\":\"updated\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"recordType\"},{\"name\":\"linkedRecordId\",\"value\":\"testWorkout\"}],\"name\":\"type\"},{\"children\":[{\"name\":\"linkedRecordType\",\"value\":\"user\"},{\"name\":\"linkedRecordId\",\"value\":\"131313\"}],\"name\":\"createdBy\"},{\"name\":\"tsCreated\",\"value\":\"2020-03-12T13:00:58.190293Z\"}],\"name\":\"recordInfo\"},{\"name\":\"workoutName\",\"value\":\"cirkelfys\"},{\"name\":\"numOfParticipants\",\"value\":\"45\"},{\"name\":\"instructorId\",\"value\":\"3564\"},{\"children\":[{\"name\":\"firstName\",\"value\":\"Anna\"},{\"name\":\"lastName\",\"value\":\"Ledare\"}],\"name\":\"instructorName\"},{\"children\":[{\"name\":\"rating\",\"value\":\"4\"}],\"name\":\"popularity\"}],\"name\":\"workout\"},\"actionLinks\":{}}}";
			return responseText;
		}
		if (httpUrlConnection.getURL().toString().equals("http://localhost:8380/idplogin/login")) {
			return getIdpLoginAnswer();
		}

		// valid result
		return responseText;
	}

	private String getIdpLoginAnswer() {

		StringBuilder answer = new StringBuilder();

		answer.append("<!DOCTYPE html>");
		answer.append("<html><head>");
		answer.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		answer.append("<script type=\"text/javascript\">");
		answer.append("window.onload = start;");
		answer.append("function start() {");
		answer.append("var authInfo = {");
		answer.append("\"userId\" : \"" + idFromLogin + "\",");
		answer.append("\"token\" : \"");
		answer.append(authTokenJsEscaped);
		answer.append("\",");
		answer.append("\"idFromLogin\" : \"");
		answer.append(idFromLogin);
		answer.append("\",");
		answer.append("\"validForNoSeconds\" : \"");
		answer.append(validForNoSeconds);
		answer.append("\",");
		answer.append("\"actionLinks\" : {");
		answer.append("\"delete\" : {");
		answer.append("\"requestMethod\" : \"DELETE\",");
		answer.append("\"rel\" : \"delete\",");
		answer.append("\"url\" : \"" + deleteUrlJsEscaped);
		answer.append("\"");
		answer.append("}");
		answer.append("}");
		answer.append("};");
		answer.append("window.opener.postMessage(authInfo, \"" + mainSystemDomain + "\");");
		answer.append("window.opener.focus();");
		answer.append("window.close();");
		answer.append("}");
		answer.append("</script>");
		answer.append("<body>");
		answer.append("</body></html>");

		return answer.toString();
	}

	@Override
	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public void setOutput(String outputString) {
		this.outputString = outputString;

	}

	@Override
	public void setRequestProperty(String key, String value) {
		requestProperties.put(key, value);
	}

	@Override
	public String getErrorText() {
		errorText = "errorText from spy";
		return errorText;
	}

	@Override
	public void setStreamOutput(InputStream stream) {
		this.stream = stream;
	}

	@Override
	public String getHeaderField(String name) {
		if ("Content-Length".equals(name)) {
			return "9999";
		}
		if ("Content-Disposition".equals(name)) {
			return "form-data; name=\"file\"; filename=\"adele.png\"\n";
		}
		if ("Location".equals(name)) {
			returnedHeaderField = "http://epc.ub.uu.se/therest/rest/record/someRecordType/someRecordType:35824453170224822";
			return returnedHeaderField;
		}
		return null;
	}

	@Override
	public void setBasicAuthorization(String arg0, String arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public InputStream getResponseBinary() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> getResponseHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}
