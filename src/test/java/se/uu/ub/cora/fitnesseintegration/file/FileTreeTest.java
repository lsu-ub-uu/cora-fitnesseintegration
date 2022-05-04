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

import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class FileTreeTest {

	FileTree fileTree;
	FileTreeReaderSpy fileTreeReaderSpy;

	String basePath = "someFolder/someOtherFolder/";

	@BeforeMethod
	public void beforeMethod() {
		fileTreeReaderSpy = new FileTreeReaderSpy();

		fileTree = new FileTree();
		fileTree.fileTreeReader = fileTreeReaderSpy;
	}

	@Test
	public void testInit() throws Exception {
		String expecedOutputFromSpy = "someFiles";
		fileTreeReaderSpy.MRV.setReturnValues("createFileTreeFromPath",
				List.of(expecedOutputFromSpy), basePath);

		fileTree.setPath(basePath);
		String tree = fileTree.getTree();

		fileTreeReaderSpy.MCR.assertParameters("createFileTreeFromPath", 0, basePath);
		String returnValue = (String) fileTreeReaderSpy.MCR.getReturnValue("createFileTreeFromPath",
				0);
		assertEquals(tree, "<pre>" + returnValue + "</pre>");

	}
}
