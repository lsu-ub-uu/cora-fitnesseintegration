package se.uu.ub.cora.fitnesseintegration.definitionwriter;

public class DefinitionCompareFixture {

	private String authToken;
	private String recordId;
	private DefinitionWriter writer;

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public DefinitionCompareFixture() {
		this.writer = new DefinitionWriter();
	}

	public String getDefinitionView() {
		return writer.writeDefinitionUsingRecordId(authToken, recordId);
	}
}
