package se.uu.ub.cora.fitnesseintegration.definitionwriter;

public class DefinitionCompareFixture {

	private String authToken;
	private String recordId;
	private String definition;
	private DefinitionWriter writer;

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	public void setDefinition(String definition) {
		this.definition = definition;
	}

	public DefinitionCompareFixture() {
		this.writer = new DefinitionWriter();
	}

	public boolean compareDefinitions() {
		return definition.equals(writer.writeDefinitionFromUsingDataChild(authToken, recordId));
	}
}
