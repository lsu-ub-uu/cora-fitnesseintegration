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
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import se.uu.ub.cora.fitnesseintegration.file.ArchiveFileReaderImp;
import se.uu.ub.cora.fitnesseintegration.internal.Waiter;

public class FileReader {

	boolean fileExists = true;
	private static Map<String, Instant> filesLastModifiedInstant = new HashMap<>();

	public boolean fileNameExistsInPath(String fileName, String path) {
		Path filePath = Path.of(path, fileName);
		return Files.exists(filePath);
	}

	public boolean fileExistsInPath(String path) {
		Path filePath = Path.of(path);
		return Files.exists(filePath);
	}

	public boolean fileWithNameRemovedFromPath(String fileName, String path) {
		fileExists = true;
		Path filePath = Path.of(path, fileName);
		Waiter waiter = DependencyProvider.factorWaiter();

		return waiter.waitUntilConditionFullfilled(() -> fileExists = Files.exists(filePath),
				() -> !fileExists, 500, 10);
	}

	public boolean fileNameExistsInArchiveAsVersionUnderPath(String fileName, String version,
			String basePath) {
		ArchiveFileReaderImp archiveFileReader = new ArchiveFileReaderImp();
		Optional<Path> path = archiveFileReader.findPathWithNameAndVersion(basePath, fileName,
				version);
		return path.isPresent();
	}

	public Instant getLastModifiedTime(String path) {
		try {
			Path filePath = Path.of(path);
			return Files.getLastModifiedTime(filePath).toInstant();
		} catch (IOException _) {
			throw new RuntimeException("Path not found!");
		}
	}

	public void storeLastModifiedTime(String path) {
		try {
			Path filePath = Path.of(path);
			Instant instant = Files.getLastModifiedTime(filePath).toInstant();
			filesLastModifiedInstant.put(path, instant);

		} catch (IOException _) {
			throw new RuntimeException("Path not found!");
		}
	}

	public boolean checkFileHasBeenModified(String path) {
		if (!filesLastModifiedInstant.containsKey(path)) {
			throw new RuntimeException("Last modified instant using path has not been stored.");
		}
		Instant instantBefore = filesLastModifiedInstant.get(path);
		Instant instantNow = getLastModifiedTime(path);
		return instantBefore.compareTo(instantNow) < 0;
	}
}
