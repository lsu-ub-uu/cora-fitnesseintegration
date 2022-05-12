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

package se.uu.ub.cora.fitnesseintegration.file;

import static org.testng.Assert.assertEquals;

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArchiveFileTest {

	ArchiveFile archiveFile;
	FileTreeReaderSpy fileTreeReaderSpy;
	ArchiveFileReaderSpy archiveFileReaderSpy;

	String basePath = "someFolder/someOtherFolder/";

	@BeforeMethod
	public void beforeMethod() {
		fileTreeReaderSpy = new FileTreeReaderSpy();
		archiveFileReaderSpy = new ArchiveFileReaderSpy();

		archiveFile = new ArchiveFile();
		archiveFile.fileTreeReader = fileTreeReaderSpy;
		archiveFile.archiveFileReader = archiveFileReaderSpy;
	}

	@Test
	public void tesGetTree() throws Exception {
		String expectedOutputFromSpy = "someFiles";
		fileTreeReaderSpy.MRV.setReturnValues("createFileTreeFromPath",
				List.of(expectedOutputFromSpy), basePath);

		archiveFile.setPath(basePath);
		String tree = archiveFile.getTree();

		fileTreeReaderSpy.MCR.assertParameters("createFileTreeFromPath", 0, basePath);
		String returnValue = (String) fileTreeReaderSpy.MCR.getReturnValue("createFileTreeFromPath",
				0);
		assertEquals(tree, "<pre>" + returnValue + "</pre>");

	}

	@Test
	public void testReadFile() throws Exception {
		String expectedOutputFromSpy = "someContent";
		String fileName = "someFilename";
		String version = "someVersion";
		archiveFileReaderSpy.MRV.setReturnValues("readFileWithNameAndVersion",
				List.of(expectedOutputFromSpy), basePath, fileName, version);

		archiveFile.setPath(basePath);
		archiveFile.setFileName(fileName);
		archiveFile.setVersion(version);

		String fileContent = archiveFile.getReadFile();

		archiveFileReaderSpy.MCR.assertParameters("readFileWithNameAndVersion", 0, basePath,
				fileName, version);
		String returnValue = (String) archiveFileReaderSpy.MCR
				.getReturnValue("readFileWithNameAndVersion", 0);

		assertEquals(fileContent, returnValue);

	}
}
