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

import java.text.SimpleDateFormat;
import java.util.Date;

public class StringSupport {

	public String concatenate(String string1, String string2) {
		return string1.concat(string2);
	}

	public String dateFormat(String pattern) {
		Date date = new Date();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(pattern);
		return sDateFormat.format(date);
	}

	public String replaceAll(String text, String find, String replaceWith) {
		return text.replace(find, replaceWith);
	}

}
