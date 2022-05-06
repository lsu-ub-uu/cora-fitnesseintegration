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

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ArchiveFileReaderTest {
	private FileTestHelper fileHelper;
	private String basePath = "/tmp/recordStorageOnDiskTemp";
	private ArchiveFileReader archiveFileReader;

	@BeforeMethod
	public void beforeMethod() throws IOException {
		fileHelper = FileTestHelper.forDirectory(basePath);
		archiveFileReader = new ArchiveFileReaderImp();
	}

	@AfterMethod
	public void afterMethod() throws IOException {
		fileHelper.removeFiles();
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "File with name: someFileName and version: v1, not found in archive.")
	public void testInit() throws Exception {
		archiveFileReader.readFileWithNameAndVersion(basePath, "someFileName", "v1");
	}

	@Test
	public void testWrongPath() throws Exception {
		fileHelper.removeFiles();
		try {
			archiveFileReader.readFileWithNameAndVersion(basePath, "someFileName", "v1");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals(e.getMessage(),
					"Error while reading file tree from directory: " + basePath);
			assertNotNull(e.getCause());
		}
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "File with name: someFileName and version: v1, not found in archive.")
	public void testOneFileNoVersion() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		archiveFileReader.readFileWithNameAndVersion(basePath, "someFileName", "v1");
	}

	@Test
	public void testOneFileOneVersion() throws Exception {
		String x = """
				309
					327
					    c7a
					        309327c7a7e47b7a9a7cf5086a225163aa4a34653ae3edc05233faaa2bc369a8
					            0=ocfl_object_1.0
					            inventory.json
					            inventory.json.sha512
					            v1
					                content
					                    .fcrepo
					                        fcr-root.json
					                        fcr-root~fcr-desc.json
					                    demo:demo:520004
					                    demo:demo:520004~fcr-desc.nt
					                inventory.json
					                inventory.json.sha512
					            v2
					                content
					                    .fcrepo
					                        fcr-root.json
					                        fcr-root~fcr-desc.json
					                    demo:demo:520004
					                inventory.json
					                inventory.json.sha512
				""";
		String folderName = "309/327/309327c7a7e47b7a9a7cf5086a225163aa4a34653ae3edc05233faaa2bc369a8/v1/content";
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", folderName,
				"someFileName");

		String fileContent = archiveFileReader.readFileWithNameAndVersion(basePath, "someFileName",
				"v1");
		assertEquals(fileContent, "someData");
	}

	@Test
	public void testOneFileTwoVersion() throws Exception {
		String folderName = "309/327/309327c7a7e47b7a9a7cf5086a225163aa4a34653ae3edc05233faaa2bc369a8";
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData",
				folderName + "/v1/content", "someFileName");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData2",
				folderName + "/v2/content", "someFileName");

		String fileContent = archiveFileReader.readFileWithNameAndVersion(basePath, "someFileName",
				"v2");
		assertEquals(fileContent, "someData2");
	}

	@Test
	public void testMakeSureFileListIsClosed() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "v1/content",
				"someFileName");
		OnlyForTestArchiveFileReader treeReader = new OnlyForTestArchiveFileReader();

		String fileContent = treeReader.readFileWithNameAndVersion(basePath, "someFileName", "v1");

		assertEquals(fileContent, "someData");
		assertTrue(treeReader.closeCheck.runCalled);
	}

	class OnlyForTestArchiveFileReader extends ArchiveFileReaderImp {

		public CloseCheck closeCheck = new CloseCheck();

		@Override
		void pathStreamExtensionPossibility(Stream<Path> list) {
			list.onClose(closeCheck);
		}

		class CloseCheck implements Runnable {
			public boolean runCalled = false;

			@Override
			public void run() {
				runCalled = true;
			}

		}
	}

}
