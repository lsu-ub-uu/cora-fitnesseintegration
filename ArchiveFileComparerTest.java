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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.converter.ConverterProvider;
import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReaderImp;
import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReaderSpy;
import se.uu.ub.cora.fitnesseintegration.server.compare.DataGroupComparerImp;
import se.uu.ub.cora.fitnesseintegration.spy.ConverterFactorySpy;
import se.uu.ub.cora.fitnesseintegration.spy.StringToExternallyConvertibleConverterSpy;
import se.uu.ub.cora.logger.LoggerProvider;
import se.uu.ub.cora.testspies.logger.LoggerFactorySpy;

public class ArchiveFileComparerTest {

	private static final String NEW_LINE = "\n";
	private static final String COMPAREWITH = "<comparewith></comparewith>";
	private ArchiveFileReaderSpy fileReaderSpy;
	private DataGroupComparerSpy groupComparerSpy;
	private ArchiveFileComparer archiveFileComparer;
	private ConverterFactorySpy converterFactory;

	@BeforeMethod
	public void beforeMethod() {
		LoggerProvider.setLoggerFactory(new LoggerFactorySpy());
		converterFactory = new ConverterFactorySpy();
		ConverterProvider.setConverterFactory("xml", converterFactory);
		archiveFileComparer = new ArchiveFileComparer();
		fileReaderSpy = new ArchiveFileReaderSpy();
		groupComparerSpy = new DataGroupComparerSpy();

		archiveFileComparer.onlyForTestSetFileReader(fileReaderSpy);
		archiveFileComparer.onlyForTestSetGroupComparer(groupComparerSpy);

	}

	@Test
	public void testArchiveFileReaderCreates() throws Exception {
		archiveFileComparer = new ArchiveFileComparer();
		ArchiveFileReaderImp fileReader = (ArchiveFileReaderImp) archiveFileComparer
				.onlyForTestGetArchiveFileReader();
		assertNotNull(fileReader);
		DataGroupComparerImp groupComparer = (DataGroupComparerImp) archiveFileComparer
				.onlyGetDataGroupComparer();
		assertNotNull(groupComparer);
	}

	@Test
	public void testNoFileFoundInArchive() throws Exception {
		RuntimeException runtimeException = new RuntimeException("ExceptionFromSpy");
		fileReaderSpy.MRV.setAlwaysThrowException("readFileWithNameAndVersion", runtimeException);

		String result = archiveFileComparer.compare();

		fileReaderSpy.MCR.assertMethodWasCalled("readFileWithNameAndVersion");

		assertEquals(result, "Failed to read file from archive: ExceptionFromSpy");
	}

	@Test
	public void testCallSentOnToFileReader() throws Exception {
		String basePath = "basePath";
		String version = "version";
		String fileName = "fileName";

		archiveFileComparer.setBasePath(basePath);
		archiveFileComparer.setVersion(version);
		archiveFileComparer.setFileName(fileName);
		archiveFileComparer.setXmlToCompareWith(COMPAREWITH);

		archiveFileComparer.compare();

		fileReaderSpy.MCR.assertParameters("readFileWithNameAndVersion", 0, basePath, fileName,
				version);
	}

	@Test
	public void testCallResultFromFileReaderSentToXMLConverter() throws Exception {
		archiveFileComparer.compare();

		var returnValueFromFileReader = fileReaderSpy.MCR
				.getReturnValue("readFileWithNameAndVersion", 0);
		StringToExternallyConvertibleConverterSpy converterSpy = (StringToExternallyConvertibleConverterSpy) converterFactory.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		converterSpy.MCR.assertParameters("convert", 0, returnValueFromFileReader);
	}

	@Test
	public void testConverterCalledForEnteredXML() throws Exception {
		archiveFileComparer.setXmlToCompareWith(COMPAREWITH);

		archiveFileComparer.compare();

		StringToExternallyConvertibleConverterSpy converterSpy = (StringToExternallyConvertibleConverterSpy) converterFactory.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		converterSpy.MCR.assertParameters("convert", 1, COMPAREWITH);
	}

	@Test
	public void testComparerCalledWithResultsFromConverter() throws Exception {
		archiveFileComparer.compare();

		StringToExternallyConvertibleConverterSpy converterSpy = (StringToExternallyConvertibleConverterSpy) converterFactory.MCR
				.getReturnValue("factorStringToExternallyConvertableConverter", 0);
		var convertedFile = converterSpy.MCR.getReturnValue("convert", 0);
		var convertedXML = converterSpy.MCR.getReturnValue("convert", 1);
		groupComparerSpy.MCR.assertParameters("compareDataGroupToDataGroup", 0, convertedXML,
				convertedFile);
	}

	@Test
	public void testCompareReturnsOkForEmptyCompareMessageList() throws Exception {
		String compareResult = archiveFileComparer.compare();

		assertEquals(compareResult, "OK");
	}

	@Test
	public void testCompareReturnsCompareMessageList() throws Exception {
		ArrayList<String> result = new ArrayList<>();
		result.add("compare1");
		groupComparerSpy.MRV.setDefaultReturnValuesSupplier("compareDataGroupToDataGroup",
				((Supplier<List<String>>) () -> result));
		String compareResult = archiveFileComparer.compare();
		assertEquals(compareResult, "compare1");
	}

	@Test
	public void testCompareReturnsSeveralCompareMessageList() throws Exception {
		ArrayList<String> result = new ArrayList<>();
		result.add("compare1");
		result.add("compare2");
		result.add("compare3");
		groupComparerSpy.MRV.setDefaultReturnValuesSupplier("compareDataGroupToDataGroup",
				((Supplier<List<String>>) () -> result));
		String compareResult = archiveFileComparer.compare();
		assertEquals(compareResult, "compare1" + NEW_LINE + "compare2" + NEW_LINE + "compare3");
	}

}
