/*
 * Copyright 2022, 2024 Uppsala University Library
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ArchiveFileReaderImp implements ArchiveFileReader {

	private static final String ERROR_FILE_NOT_FOUND = "File with name: {0} and version: {1},"
			+ " not found in archive.";
	private static final String ERROR_DIRECTORY_NOT_FOUND = "Error while reading file tree from directory: {0}";

	@Override
	public String readFileWithNameAndVersion(String basePathText, String fileName, String version) {
		Path basePath = Paths.get(basePathText);
		try (Stream<Path> allFilePaths = Files.walk(basePath)) {
			pathStreamExtensionPossibility(allFilePaths);

			return readFile(fileName, version, allFilePaths);

		} catch (IOException e) {
			throw new RuntimeException(
					MessageFormat.format(ERROR_DIRECTORY_NOT_FOUND, basePath.toString()), e);
		}
	}

	private String readFile(String fileName, String version, Stream<Path> allFilePaths)
			throws IOException {
		Path pathToFile = findFileWithNameAndVersion(fileName, version, allFilePaths);
		return readFileFromPath(pathToFile);
	}

	private Path findFileWithNameAndVersion(String fileName, String version,
			Stream<Path> pathStream) {
		Optional<Path> path = findFirstPathEndsWithPattern(fileName, version, pathStream);
		if (path.isEmpty()) {
			throw new RuntimeException(
					MessageFormat.format(ERROR_FILE_NOT_FOUND, fileName, version));
		}
		return path.get();
	}

	public Optional<Path> findFirstPathEndsWithPattern(String fileName, String version,
			Stream<Path> pathStream) {
		String pattern = version + "/content/" + fileName;
		return pathStream.filter(endsWithPattern(pattern)).findFirst();
	}

	private Predicate<? super Path> endsWithPattern(String pattern) {
		return x -> x.endsWith(pattern);
	}

	private String readFileFromPath(Path path) throws IOException {
		BufferedReader fileReader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
		return readContentFromFileAsString(fileReader);
	}

	void pathStreamExtensionPossibility(Stream<Path> list) {
		// do nothing, used in test to make sure list is closed..
	}

	private String readContentFromFileAsString(BufferedReader bufferedReader) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
		}
		return stringBuilder.toString();
	}

	@Override
	public Optional<Path> findPathWithNameAndVersion(String basePathText, String fileName,
			String version) {
		Path basePath = Paths.get(basePathText);
		try (Stream<Path> allFilePaths = Files.walk(basePath)) {

			return findPath(fileName, version, allFilePaths);

		} catch (IOException e) {
			return Optional.empty();
		}
	}

	private Optional<Path> findPath(String fileName, String version, Stream<Path> allFilePaths) {
		return findPathWithNameAndVersion(fileName, version, allFilePaths);
	}

	private Optional<Path> findPathWithNameAndVersion(String fileName, String version,
			Stream<Path> pathStream) {
		return findFirstPathEndsWithPattern(fileName, version, pathStream);
	}
}
