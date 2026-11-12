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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.fitnesseintegration.file.FileTestHelper;
import se.uu.ub.cora.fitnesseintegration.script.internal.DependencyFactoryImp;

public class FileReaderTest {
	private FileTestHelper fileHelper;
	private String basePath = "/tmp/fitnesseOnDiskTemp";
	private FileReader fileReader;

	@BeforeMethod
	public void beforeMethod() throws IOException {
		DependencyProvider.onlyForTestSetDependencyFactory(new DependencyFactoryImp());
		fileHelper = FileTestHelper.forDirectory(basePath);
		fileReader = new FileReader();

	}

	@AfterMethod
	public void afterMethod() throws IOException {
		fileHelper.removeFiles();
	}

	@Test
	public void testFileNotFound() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		assertFalse(fileReader.fileNameExistsInPath("otherFileName", basePath));
	}

	@Test
	public void testFileNotFoundIfInSubFolder() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "subFolder", "fileName");

		assertFalse(fileReader.fileNameExistsInPath("fileName", basePath));
	}

	@Test
	public void testFileFound() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		assertTrue(fileReader.fileNameExistsInPath("fileName", basePath));
	}

	@Test
	public void testFileExistsInPath_Found() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		assertTrue(fileReader.fileExistsInPath(basePath + "/fileName"));
	}

	@Test
	public void testFileExistsInPath_NotFound() {
		assertFalse(fileReader.fileExistsInPath(basePath + "/anotherfileName"));
	}

	@Test
	public void testFileRemovedFromPath() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		assertTrue(fileReader.fileWithNameRemovedFromPath("fileNameOther", basePath));
	}

	@Test
	public void testFileRemovedFromPath_stillThere() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		assertFalse(fileReader.fileWithNameRemovedFromPath("fileName", basePath));
	}

	String x = """
			309
				327
				    c7a
				        309327c7a7e47b7a9a7cf5086a225163aa4a34653ae3edc05233faaa2bc369a8
				            0=ocfl_object_1.0
				            inventory.json
				            inventory.json.sha512
				            v1
				                content
				                    .fcrepo
				                        fcr-root.json
				                        fcr-root~fcr-desc.json
				                    demo:demo:520004
				                    demo:demo:520004~fcr-desc.nt
				                inventory.json
				                inventory.json.sha512
				            v2
				                content
				                    .fcrepo
				                        fcr-root.json
				                        fcr-root~fcr-desc.json
				                    demo:demo:520004
				                inventory.json
				                inventory.json.sha512
			""";

	@Test
	public void testArchiveNotFound() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "v1", "fileName");

		assertFalse(fileReader.fileNameExistsInArchiveAsVersionUnderPath("otherFileName", "v1",
				basePath));
	}

	@Test
	public void testArchiveNotFoundIfWrongVersionSubFolder() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData",
				"309/327/c7a/xxx/yy/v1/content", "fileName");

		assertFalse(
				fileReader.fileNameExistsInArchiveAsVersionUnderPath("fileName", "v2", basePath));
	}

	@Test
	public void testArchiveNotFoundIfRightVersionSubFolder() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData",
				"309/327/c7a/xxx/yy/v1/content", "fileName");

		assertTrue(
				fileReader.fileNameExistsInArchiveAsVersionUnderPath("fileName", "v1", basePath));
	}

	@Test
	public void testGetLastModifiedTimeForPath() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		Instant fileInstant = fileReader.getLastModifiedTime(basePath + "/fileName");

		assertTrue(Duration.between(fileInstant, Instant.now()).abs()
				.compareTo(Duration.ofSeconds(2)) < 0);

		Instant fileInstant2 = fileReader.getLastModifiedTime(basePath + "/fileName");

		assertTrue(fileInstant.compareTo(fileInstant2) == 0);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Path not found!")
	public void testGetLastModifiedTimeForNonExistingPath() {
		fileReader.getLastModifiedTime(basePath + "/fileName");
	}

	@Test
	public void testStoreAndCheckLastModifiedTimeForPath_NotModified() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		fileReader.storeLastModifiedTime(basePath + "/fileName");
		boolean checkFileHasBeenModified = fileReader
				.checkFileHasBeenModified(basePath + "/fileName");

		assertFalse(checkFileHasBeenModified);
	}

	@Test
	public void testStoreAndCheckLastModifiedTimeForPath_Modified() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");
		fileReader.storeLastModifiedTime(basePath + "/fileName");
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "", "fileName");

		boolean checkFileHasBeenModified = fileReader
				.checkFileHasBeenModified(basePath + "/fileName");

		assertTrue(checkFileHasBeenModified);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Path not found!")
	public void testStoreAndCheckLastModifiedTimeForPath_PathNotFound() {
		fileReader.storeLastModifiedTime(basePath + "/fileName");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Last modified instant using path has not been stored.")
	public void testStoreAndCheckLastModifiedTimeForPath_NotStored() {
		fileReader.checkFileHasBeenModified(basePath + "/fileName2");
	}

	@Test
	public void testFindFileExists() throws Exception {
		fileHelper.writeFileToDiskWithContentInFolderWithName("someData", "someFolder", "fileName");
		String fileToPath = fileReader.findFileInBasepath("fileName", basePath);

		assertEquals(fileToPath, basePath + "/someFolder/fileName");
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Could not find any file with the given path and filename.")
	public void testFindFileDoNotExists() {
		fileReader.findFileInBasepath("fileName", basePath);
	}

	@Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = ""
			+ "Something went wrong when trying to find the file in the given path. Maybe basePath do not exists.")
	public void testFindFileSomethingWrong() {
		fileReader.findFileInBasepath("fileName", "doNotExistFolder");
	}
}
