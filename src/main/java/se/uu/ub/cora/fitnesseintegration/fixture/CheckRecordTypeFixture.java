/*
 * Copyright 2025 Uppsala University Library
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

import se.uu.ub.cora.clientdata.ClientDataRecordGroup;
import se.uu.ub.cora.clientdata.ClientDataRecordLink;
import se.uu.ub.cora.fitnesseintegration.cache.RecordTypeProvider;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DefinitionWriter;
import se.uu.ub.cora.fitnesseintegration.script.DependencyProvider;

public class CheckRecordTypeFixture {
	private String id;

	public CheckRecordTypeFixture() {
		// needed by fitnesse
	}

	public void setId(String id) {
		this.id = id;
	}

	public String assertDefinitionIs() {
		// SPIKE
		// JavaClientAppTokenCredentials cred = new JavaClientAppTokenCredentials(
		// SystemUrl.getRestUrl(), SystemUrl.getAppTokenVerifierRestUrl(),
		// LoginToken.getFitnesseAdminLoginId(), LoginToken.getFitnesseAdminAppToken());
		// dataClient = JavaClientProvider.createDataClientUsingJavaClientAppTokenCredentials(cred);
		// TODO: 1. fix systemUrl to return getAppTokenVerifierRestUrl, DONE
		// TOOD: 2. move this to FitnesseJavaClientProvider.getJavaClientForFitnesseAdmin, DONE
		// TOOD: 3. create a recordTypeHolder class, DONE
		// TODO: 4. refactor this class to use 3
		// TODO: 5. refactor definitionWriter to use 2

		// DataClient dataClient = FitnesseJavaClientProvider.getFitnesseAdminDataClient();

		// ClientDataRecord clientDataRecord = dataClient.read("recordType", id);
		// ClientDataRecordGroup dataRecordGroup = clientDataRecord.getDataRecordGroup();
		ClientDataRecordGroup dataRecordGroup = RecordTypeProvider.getRecordGroup(id);
		ClientDataRecordLink metadataLink = dataRecordGroup
				.getFirstChildOfTypeAndName(ClientDataRecordLink.class, "metadataId");
		String linkedRecordId = metadataLink.getLinkedRecordId();
		DefinitionWriter writer = DependencyProvider.factorDefinitionWriter();
		return writer.writeDefinitionUsingRecordId(linkedRecordId);
	}

}
