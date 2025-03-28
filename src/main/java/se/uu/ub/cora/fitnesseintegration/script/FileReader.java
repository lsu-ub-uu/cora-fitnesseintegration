/*
 * Copyright 2024 Uppsala University Library
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
package se.uu.ub.cora.fitnesseintegration.script;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReaderImp;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;

public class FileReader {
	boolean fileExists = true;

	public boolean fileNameExistsInPath(String fileName, String path) {
		Path filePath = Path.of(path, fileName);
		return Files.exists(filePath);
	}

	public boolean fileWithNameRemovedFromPath(String fileName, String path) {

		// Specify the directory path
		Path dirPath = Paths.get(path);

		// Check if the directory exists
		if (Files.exists(dirPath) && Files.isDirectory(dirPath)) {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {

				for (Path entry : stream) {
					System.out.println(entry.getFileName());
				}
			} catch (IOException e) {
				System.err.println("Error reading directory: " + e.getMessage());
			}
		} else {
			System.out.println("The specified path is not a directory or does not exist.");
		}

		fileExists = true;
		Path filePath = Path.of(path, fileName);
		Waiter waiter = DependencyProvider.factorWaiter();
		return waiter.waitUntilConditionFullfilled(() -> {
			fileExists = Files.exists(filePath);
			System.out.println(fileExists);
		}, () -> !fileExists, 500, 10);
	}

	public boolean fileNameExistsInArchiveAsVersionUnderPath(String fileName, String version,
			String basePath) {
		ArchiveFileReaderImp archiveFileReader = new ArchiveFileReaderImp();
		Optional<Path> path = archiveFileReader.findPathWithNameAndVersion(basePath, fileName,
				version);
		return path.isPresent();
	}
}
