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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileTreeReaderImp implements FileTreeReader {
	private static final String INDENT = "    ";
	private static final String NEW_LINE = "\n";
	private static final String ERROR_READING_FILE_TREE = "Error while reading file tree from directory: {0}";
	private List<String> allFileNames = new ArrayList<>();

	@Override
	public String createFileTreeFromPath(String basePath) {
		tryToReadFileNamesFromDirectory(basePath);
		String[] fileNames = allFileNames.toArray(String[]::new);
		return buildTreeFromFileNames(fileNames);
	}

	private String buildTreeFromFileNames(String[] pathListAsArray) {
		return Arrays.stream(pathListAsArray).collect(Collectors.joining(NEW_LINE));
	}

	private final void tryToReadFileNamesFromDirectory(String basePath) {
		try (Stream<Path> pathStream = Files.list(Paths.get(basePath))) {
			listExtensionPossibility(pathStream);
			recursivlyCollectFileNames(pathStream, "");
		} catch (IOException e) {
			String errorMessage = MessageFormat.format(ERROR_READING_FILE_TREE, basePath);
			throw new RuntimeException(errorMessage, e);
		}
	}

	void listExtensionPossibility(Stream<Path> list) {
		// do nothing, used in test to make sure list is closed..
	}

	private final void recursivlyCollectFileNames(Stream<Path> pathStream, String prefix)
			throws IOException {
		Iterator<Path> iterator = pathStream.sorted().iterator();
		while (iterator.hasNext()) {
			collectFileNameAndRecurseForDirectories(iterator.next(), prefix);
		}
	}

	private final void collectFileNameAndRecurseForDirectories(Path path, String prefix)
			throws IOException {
		File file = path.toFile();
		String fileNameWithPrefix = prefix + file.getName();
		allFileNames.add(fileNameWithPrefix);
		if (file.isDirectory()) {
			Stream<Path> list = Files.list(path);
			recursivlyCollectFileNames(list, prefix + INDENT);
		}
	}
}
