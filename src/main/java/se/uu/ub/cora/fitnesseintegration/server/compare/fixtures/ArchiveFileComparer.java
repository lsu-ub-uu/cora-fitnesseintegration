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

import se.uu.ub.cora.converter.ConverterException;
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

	public void setXmlCompareWith(String xmlCompareWith) {
		this.xmlCompareWith = xmlCompareWith;
	}

	public String compare() {
		try {
			return tryToConvert();
		} catch (RuntimeException e) {
			return e.getMessage();
		}
	}

	private String tryToConvert() {
		String archiveFile = tryToReadFileWithNameAndVersion();
		ExternallyConvertible compareAgainst = tryToConvertArchiveFileToXml(archiveFile);
		ExternallyConvertible compareWith = tryToConvertCompareWithToXml();
		return compareAndReturnResult(compareAgainst, compareWith);
	}

	private String compareAndReturnResult(ExternallyConvertible compareAgainst,
			ExternallyConvertible compareWith) {
		List<String> comparerMessages = comparer
				.compareDataGroupToDataGroup((DataGroup) compareWith, (DataGroup) compareAgainst);
		return handleResults(comparerMessages);
	}

	private String handleResults(List<String> comparerMessages) {
		if (!comparerMessages.isEmpty()) {
			return String.join("\n", comparerMessages);
		}
		return "OK";
	}

	private String tryToReadFileWithNameAndVersion() {
		try {
			return fileReader.readFileWithNameAndVersion(basePath, fileName, version);
		} catch (RuntimeException e) {
			throw new RuntimeException("Failed to read file from archive: " + e.getMessage());
		}
	}

	private ExternallyConvertible tryToConvertArchiveFileToXml(String archiveFile) {
		try {
			return xmlConverter.convert(archiveFile);
		} catch (ConverterException e) {
			throw new RuntimeException("Conversion of XML from archive failed: " + e.getMessage());
		}
	}

	private ExternallyConvertible tryToConvertCompareWithToXml() {
		try {
			return xmlConverter.convert(xmlCompareWith);
		} catch (ConverterException e) {
			throw new RuntimeException("Conversion of compare with XML failed: " + e.getMessage());
		}
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

	public String getXmlComparerWith() {
		return xmlCompareWith;
	}
}
