/*
 * Copyright 2022 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.server.compare.fixtures;

import java.util.List;

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReader;
import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReaderImp;
import se.uu.ub.cora.fitnesseintegration.server.compare.DataGroupComparer;
import se.uu.ub.cora.fitnesseintegration.server.compare.DataGroupComparerImp;

public class ArchiveFileComparer {
	ArchiveFileReader fileReader = new ArchiveFileReaderImp();
	DataGroupComparer comparer = new DataGroupComparerImp();
	private String basePath;
	private String version;
	private String fileName;
	private String xmlCompareWith;
	private StringToExternallyConvertibleConverter xmlConverter = ConverterProvider
			.getStringToExternallyConvertibleConverter("xml");

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setXmlToCompareWith(String xmlCompareWith) {
		this.xmlCompareWith = xmlCompareWith;
	}

	public String compare() {
		String archiveFile = "";
		try {
			archiveFile = fileReader.readFileWithNameAndVersion(basePath, fileName, version);

		} catch (RuntimeException e) {
			return "Failed to read file from archive: " + e.getMessage();
		}
		// TODO: Handle error on conversion for archiveFile
		ExternallyConvertible compareAgainst = xmlConverter.convert(archiveFile);
		// TODO: Handle error on conversion for xmlCompareWith
		ExternallyConvertible compareWith = xmlConverter.convert(xmlCompareWith);

		List<String> comparerMessages = comparer
				.compareDataGroupToDataGroup((DataGroup) compareWith, (DataGroup) compareAgainst);
		if (comparerMessages.isEmpty()) {
			return "OK";
		}
		return String.join("\n", comparerMessages);
	}

	ArchiveFileReader onlyForTestGetArchiveFileReader() {
		return fileReader;
	}

	void onlyForTestSetFileReader(ArchiveFileReader fileReader) {
		this.fileReader = fileReader;
	}

	DataGroupComparer onlyGetDataGroupComparer() {
		return comparer;
	}

	void onlyForTestSetGroupComparer(DataGroupComparer groupComparer) {
		comparer = groupComparer;
	}

}
