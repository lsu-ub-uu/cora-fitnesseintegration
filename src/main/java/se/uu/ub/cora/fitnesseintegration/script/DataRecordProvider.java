package se.uu.ub.cora.fitnesseintegration.script;

import se.uu.ub.cora.fitnesseintegration.definitionwriter.DataRecordHolder;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.DataRecordHolderPopulator;

public final class DataRecordProvider {

	private static DataRecordHolder holder;

	public DataRecordProvider() {
		super();
	}

	public static synchronized DataRecordHolder getHolder(String authToken) {
		if (null == holder) {
			holder = new DataRecordHolderPopulator().createAndPopulateHolder(authToken);
		}
		return holder;
	}

}
