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
package se.uu.ub.cora.fitnesseintegration.script;

import static org.testng.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class StringSupportTest {

	private StringSupport stringSupport;

	@BeforeMethod
	private void beforeMethod() {
		stringSupport = new StringSupport();

	}

	@Test
	public void testConcatenate() {
		String result = stringSupport.concatenate("one", "two");

		assertEquals(result, "onetwo");
	}

	@Test
	public void testGenerateStingUsingFormat() {
		String pattern = "YYYY-YYYY-YYYY-YYYY";
		SimpleDateFormat stodaysYear = new SimpleDateFormat(pattern);
		String todaysYear = stodaysYear.format(new Date());

		String result = stringSupport.dateFormat(pattern);

		assertEquals(result, todaysYear);
	}

	@Test
	public void testReplaceAll() {
		String text = "ababa";
		String find = "b";
		String replaceWith = "a";
		String textReplaced = stringSupport.replaceAll(text, find, replaceWith);

		assertEquals(textReplaced, "aaaaa");
	}

	@Test
	public void testGetValueFromVariable() {

		String textReplaced = stringSupport.getValueFromVariable("someValue");

		assertEquals(textReplaced, "someValue");
	}
}
