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

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.testng.annotations.Test;

public class FileTreeReaderTest {

	private String basePath = "/tmp/recordStorageOnDiskTemp/";

	@Test
	public void testCreateFileTreeFromPathNoFiles() throws Exception {

		FileTreeReader treeReader = new FileTreeReaderImp();

		String fileTree = treeReader.createFileTreeFromPath("somePath");

		assertEquals(fileTree, "");
	}

	@Test
	public void testCreateFileTreeFromPathOneFiles() throws Exception {

		FileTreeReader treeReader = new FileTreeReaderImp();
		writeFileToDisk("someData", "folder", "fileName.xml");

		String fileTree = treeReader.createFileTreeFromPath("basePath");

		assertEquals(fileTree, "/folder/fileName.xml");
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

}
