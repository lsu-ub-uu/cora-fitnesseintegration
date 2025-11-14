/*
 * Copyright 2025 Olov McKie
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
package se.uu.ub.cora.fitnesseintegration.definitionwriter;

public interface DefinitionWriter {
	/**
	 * writeDefinitionUsingRecordId creates a definition for the specified metadataId, in tabbed
	 * format similar to the one that jsClient makes, to be able to easily compare one from
	 * jsClient.
	 * @param metadataId
	 *            a String with the metadata id to create a definition for
	 * 
	 * @return A String with a tab indented definition for the specified metadataId
	 */
	String writeDefinitionUsingRecordId(String metadataId);

}
