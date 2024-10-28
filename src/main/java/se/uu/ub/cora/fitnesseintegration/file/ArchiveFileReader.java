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

import java.nio.file.Path;
import java.util.Optional;

/**
 * ArchiveFileReader reads a file from an archive.
 */
public interface ArchiveFileReader {
	/**
	 * readFileWithNameAndVersion reads a file from the (Fedora) archive on disk using the specified
	 * basePath, fileName and version.
	 * <p>
	 * The reader searches the file tree starting at the specified base path until it finds a file
	 * with the specified name. From there it finds the folder with the specified version and
	 * returns the contens of the file with the specified filename from the correct version folder.
	 * 
	 * @param basePath
	 *            A String with the basePath of the (Fedora) archive on disk
	 * @param fileName
	 *            A String with the fileName to find in the archive
	 * @param version
	 *            A String with the version of the file to read.
	 * @return A String with the contens of the read file.
	 * @throws {@link
	 *             RuntimeException} if any error occurs while trying to read the file.
	 */
	String readFileWithNameAndVersion(String basePath, String fileName, String version);

	/**
	 * findPathWithNameAndVersion reads a file from the (Fedora) archive on disk using the specified
	 * basePath, fileName and version.
	 * <p>
	 * The reader searches the file tree starting at the specified base path until it finds a file
	 * with the specified name. From there it finds the folder with the specified version and
	 * returns the contens of the file with the specified filename from the correct version folder.
	 * 
	 * @param basePath
	 *            A String with the basePath of the (Fedora) archive on disk
	 * @param fileName
	 *            A String with the fileName to find in the archive
	 * @param version
	 *            A String with the version of the file to find.
	 * @return A Optional<Path> with the path to the file.
	 */
	Optional<Path> findPathWithNameAndVersion(String basePath, String string, String string2);

}
