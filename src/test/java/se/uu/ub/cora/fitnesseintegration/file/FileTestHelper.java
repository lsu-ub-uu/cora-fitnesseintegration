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

public class FileTestHelper {
	public static FileTestHelper forDirectory(String basePath) throws IOException {
		return new FileTestHelper(basePath);
	}

	public String basePath;

	private FileTestHelper(String basePath) throws IOException {
		this.basePath = basePath;
		makeSureBasePathExistsAndIsEmpty();
	}

	private void makeSureBasePathExistsAndIsEmpty() throws IOException {
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

	public void writeFileToDiskWithContentInFolderWithName(String content, String folderName,
			String fileName) throws IOException {
		String currentPath = possiblyCreateFolder(folderName);
		Path path = FileSystems.getDefault().getPath(currentPath, fileName);
		BufferedWriter writer;
		writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
		writer.write(content, 0, content.length());
		writer.flush();
		writer.close();
	}

	private String possiblyCreateFolder(String folderName) {
		String[] folders = folderName.split("/");
		String currentPath = basePath;
		for (String folder : folders) {
			Path pathIncludingFolderName = Paths.get(currentPath, folder);
			File newPath = pathIncludingFolderName.toFile();
			if (!newPath.exists()) {
				newPath.mkdir();
			}
			currentPath += "/" + folder;
		}
		return currentPath;
	}

	public void removeFiles() throws IOException {
		if (Files.exists(Paths.get(basePath))) {
			deleteFiles(basePath);
			File dir = new File(basePath);
			dir.delete();
		}
	}
}