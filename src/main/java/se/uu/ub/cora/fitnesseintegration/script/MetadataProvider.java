/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.script;

import se.uu.ub.cora.fitnesseintegration.definitionwriter.MetadataHolder;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.MetadataHolderPopulator;
import se.uu.ub.cora.fitnesseintegration.definitionwriter.MetadataHolderPopulatorImp;

public final class MetadataProvider {

	private static MetadataHolder holder;

	private MetadataProvider() {
		super();
	}

	public static synchronized MetadataHolder getHolder(String authToken) {
		if (null == holder) {
			MetadataHolderPopulator populator = new MetadataHolderPopulatorImp();
			holder = populator.createAndPopulateHolder(authToken);
		}
		return holder;
	}

	public static void onlyForTestSetHolder(MetadataHolder metadataHolder) {
		holder = metadataHolder;
	}
}
