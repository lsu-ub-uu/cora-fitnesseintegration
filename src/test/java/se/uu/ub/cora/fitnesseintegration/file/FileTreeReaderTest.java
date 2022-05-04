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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FileTreeReaderTest {

	private String basePath = "/tmp/recordStorageOnDiskTemp/";

	@BeforeMethod
	public void makeSureBasePathExistsAndIsEmpty() throws IOException {
		File dir = new File(basePath);
		dir.mkdir();
		deleteFiles(basePath);
	}

	private void deleteFiles(String path) throws IOException {
		Stream<Path> list;
		list = Files.list(Paths.get(path));

		list.forEach(p -> deleteFile(p));
		list.close();
	}

	private void deleteFile(Path path) {
		try {
			if (path.toFile().isDirectory()) {
				deleteFiles(path.toString());
			}
			Files.delete(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@AfterMethod
	public void removeTempFiles() throws IOException {
		if (Files.exists(Paths.get(basePath))) {
			deleteFiles(basePath);
			File dir = new File(basePath);
			dir.delete();
		}
	}

	@Test
	public void testCreateFileTreeFromPathWithEmptyDirectory() throws Exception {

		FileTreeReader treeReader = new FileTreeReaderImp();

		String fileTree = treeReader.createFileTreeFromPath(basePath);

		assertEquals(fileTree, "");
	}

	@Test
	public void testCreateFileTreeFromPathDoesNotExist() throws Exception {

		FileTreeReader treeReader = new FileTreeReaderImp();
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

		FileTreeReader treeReader = new FileTreeReaderImp();
		writeFileToDisk("someData", "", "fileName.xml");

		String fileTree = treeReader.createFileTreeFromPath(basePath);

		assertEquals(fileTree, "fileName.xml");
	}

	private void writeFileToDisk(String content, String folderName, String fileName)
			throws IOException {
		possiblyCreateFolderForDataDivider(folderName);
		Path path = FileSystems.getDefault().getPath(basePath, folderName, fileName);
		BufferedWriter writer;
		writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		writer.write(content, 0, content.length());
		writer.flush();
		writer.close();
	}

	private void possiblyCreateFolderForDataDivider(String dataDivider) {
		Path pathIncludingDataDivider = Paths.get(basePath, dataDivider);
		File newPath = pathIncludingDataDivider.toFile();
		if (!newPath.exists()) {
			newPath.mkdir();
		}
	}

	@Test
	public void testCreateFileTreeFromPathTwoFiles() throws Exception {
		FileTreeReader treeReader = new FileTreeReaderImp();
		writeFileToDisk("someData", "folder", "fileName.xml");
		writeFileToDisk("someData2", "folder", "fileName2.xml");

		String fileTree = treeReader.createFileTreeFromPath(basePath);
		assertEquals(fileTree, """
				folder
				    fileName.xml
				    fileName2.xml""");
	}

	@Test
	public void testCreateFileTreeFromPathTwoFilesIndent() throws Exception {
		FileTreeReader treeReader = new FileTreeReaderImp();
		writeFileToDisk("someData", "", "fileName.xml");
		writeFileToDisk("someData", "folder1", "fileName1.xml");
		writeFileToDisk("someData2", "folder1", "fileName2.xml");
		writeFileToDisk("someData2", "folder1/folder2", "fileName3.xml");
		writeFileToDisk("someData2", "folder3", "fileName4.xml");

		String fileTree = treeReader.createFileTreeFromPath(basePath);
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

		String fileTree = treeReader.createFileTreeFromPath(basePath);

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
