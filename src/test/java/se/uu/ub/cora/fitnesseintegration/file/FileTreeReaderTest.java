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
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FileTreeReaderTest {

	private FileTestHelper fileHelper;
	FileTreeReader treeReader;

	@BeforeMethod
	public void beforeMethod() throws IOException {
		fileHelper = FileTestHelper.forDirectory("/tmp/recordStorageOnDiskTemp/");
		treeReader = new FileTreeReaderImp();
	}

	@AfterMethod
	public void afterMethod() throws IOException {
		fileHelper.removeFiles();
	}

	@Test
	public void testCreateFileTreeFromPathWithEmptyDirectory() throws Exception {
		String fileTree = treeReader.createFileTreeFromPath(fileHelper.basePath);

		assertEquals(fileTree, "");
	}

	@Test
	public void testCreateFileTreeFromPathDoesNotExist() throws Exception {
		try {
			treeReader.createFileTreeFromPath("somePath");
			assertTrue(false);
		} catch (Exception e) {
			assertTrue(e instanceof RuntimeException);
			assertEquals(e.getMessage(), "Error while reading file tree from directory: somePath");
			assertNotNull(e.getCause());
		}
	}

	@Test
	public void testCreateFileTreeFromPathOneFiles() throws Exception {
		// <<<<<<< HEAD
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName.xml");
		// =======
		// fileHelper.writeFileToDisk("someData", "", "fileName.xml");
		// >>>>>>> branch 'issues/ALVIN-2668' of
		// https://github.com/lsu-ub-uu/cora-fitnesseintegration.git

		String fileTree = treeReader.createFileTreeFromPath(fileHelper.basePath);

		assertEquals(fileTree, "fileName.xml");
	}

	@Test
	public void testCreateFileTreeFromPathTwoFiles() throws Exception {
		// <<<<<<< HEAD
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "folder", "fileName.xml");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData2", "folder",
				"fileName2.xml");
		// =======
		// fileHelper.writeFileToDisk("someData", "folder", "fileName.xml");
		// fileHelper.writeFileToDisk("someData2", "folder", "fileName2.xml");
		// >>>>>>> branch 'issues/ALVIN-2668' of
		// https://github.com/lsu-ub-uu/cora-fitnesseintegration.git

		String fileTree = treeReader.createFileTreeFromPath(fileHelper.basePath);
		assertEquals(fileTree, """
				folder
				    fileName.xml
				    fileName2.xml""");
	}

	@Test
	public void testCreateFileTreeFromPathTwoFilesIndent() throws Exception {
		// <<<<<<< HEAD
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName.xml");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "folder1",
				"fileName1.xml");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData2", "folder1",
				"fileName2.xml");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData2", "folder1/folder2",
				"fileName3.xml");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData2", "folder3",
				"fileName4.xml");
		// =======
		// fileHelper.writeFileToDisk("someData", "", "fileName.xml");
		// fileHelper.writeFileToDisk("someData", "folder1", "fileName1.xml");
		// fileHelper.writeFileToDisk("someData2", "folder1", "fileName2.xml");
		// fileHelper.writeFileToDisk("someData2", "folder1/folder2", "fileName3.xml");
		// fileHelper.writeFileToDisk("someData2", "folder3", "fileName4.xml");
		// >>>>>>> branch 'issues/ALVIN-2668' of
		// https://github.com/lsu-ub-uu/cora-fitnesseintegration.git

		String fileTree = treeReader.createFileTreeFromPath(fileHelper.basePath);
		assertEquals(fileTree, """
				fileName.xml
				folder1
				    fileName1.xml
				    fileName2.xml
				    folder2
				        fileName3.xml
				folder3
				    fileName4.xml""");
	}

	@Test
	public void testMakeSureFileListIsClosed() throws Exception {
		OnlyForTestFileTreeReader treeReader = new OnlyForTestFileTreeReader();

		String fileTree = treeReader.createFileTreeFromPath(fileHelper.basePath);

		assertEquals(fileTree, "");
		assertTrue(treeReader.closeCheck.runCalled);
	}

	class OnlyForTestFileTreeReader extends FileTreeReaderImp {
		public CloseCheck closeCheck = new CloseCheck();

		@Override
		void listExtensionPossibility(Stream<Path> list) {
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
