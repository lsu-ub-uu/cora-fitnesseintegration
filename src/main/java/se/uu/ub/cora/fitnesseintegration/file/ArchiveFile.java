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

public class ArchiveFile {

	public FileTreeReader fileTreeReader;
	public ArchiveFileReader archiveFileReader;
	private String basePath;
	private String filename;
	private String version;

	public ArchiveFile() {
		fileTreeReader = new FileTreeReaderImp();
		archiveFileReader = new ArchiveFileReaderImp();
	}

	public void setPath(String basePath) {
		this.basePath = basePath;
	}

	public String getTree() {
		return "<pre>" + fileTreeReader.createFileTreeFromPath(basePath) + "</pre>";
	}

	public void setFileName(String filename) {
		this.filename = filename;

	}

	public void setVersion(String version) {
		this.version = version;

	}

	public String getReadFile() {
		return archiveFileReader.readFileWithNameAndVersion(basePath, filename, version);
	}
}
