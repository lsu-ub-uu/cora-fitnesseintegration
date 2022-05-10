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
package se.uu.ub.cora.fitnesseintegration.server.compare;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.testspies.data.DataAttributeSpy;
import se.uu.ub.cora.testspies.data.DataGroupSpy;

public class DataGroupComparerTest {

	DataGroupComparer comparer;
	private DataGroupSpy compareWith;
	private DataGroupSpy compareAgainst;

	@BeforeMethod
	public void beforeMethod() {
		comparer = new DataGroupComparerImp();
		compareWith = new DataGroupSpy();
		compareAgainst = new DataGroupSpy();
	}

	@Test
	public void testCompareDataGroupsEmptyDataGroups() throws Exception {
		List<String> result = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);
		assertTrue(result.isEmpty());
	}

	@Test
	public void testCompareDifferentNameInDatas() throws Exception {
		compareWith.MRV.setReturnValues("getNameInData", List.of("nameInData1", "nameInData1"));
		compareAgainst.MRV.setReturnValues("getNameInData", List.of("nameInData2"));

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);
		assertFalse(report.isEmpty());
		assertTrue(report.size() == 1);
		assertEquals(report.get(0), "Group with name: nameInData1 not found.");
	}

	@Test
	public void testCompareDifferentAttributes() throws Exception {
		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A");
		// compareWith.MRV.setDefaultReturnValuesSupplier("getNameInData",
		// (Supplier<String>) () -> "nameInData");
		// compareWith.MRV.setDefaultReturnValuesSupplier("hasAttributes",
		// (Supplier<Boolean>) () -> true);
		// DataAttributeSpy attribute = createAttribute("attributeA", "A");
		// compareWith.MRV.setReturnValues("getAttributes", List.of(List.of(attribute)));

		compareAgainst.MRV.setReturnValues("getNameInData", List.of("nameInData"));
		// compareAgainst.MRV.setDefaultReturnValuesSupplier("hasAttributes",
		// (Supplier<Boolean>) () -> false);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertFalse(report.isEmpty());
		assertTrue(report.size() == 1);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s with name: attributeA and value: A "
						+ "not found.");
	}

	private void setMRVWithNameInDataAndAttributes(DataGroupSpy group, String nameInData,
			String... attributeStuff) {
		group.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);
		if (attributeStuff.length > 0) {
			group.MRV.setDefaultReturnValuesSupplier("hasAttributes",
					(Supplier<Boolean>) () -> true);
			int position = 0;
			List<DataAttribute> attributes = new ArrayList<>();
			while (position < attributeStuff.length) {
				DataAttributeSpy attribute = createAttribute(attributeStuff[position],
						attributeStuff[position + 1]);
				attributes.add(attribute);
				position = position + 2;
			}
			group.MRV.setDefaultReturnValuesSupplier("getAttributes",
					(Supplier<List<DataAttribute>>) () -> attributes);
		}
	}

	private DataAttributeSpy createAttribute(String attributeName, String attributeValue) {
		DataAttributeSpy attribute = new DataAttributeSpy();
		attribute.MRV.setReturnValues("getNameInData", List.of(attributeName));
		attribute.MRV.setReturnValues("getValue", List.of(attributeValue));
		return attribute;
	}

	@Test
	public void testCompareNoAttributesOnWithButAttributesOnAgainst() throws Exception {
		compareWith.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> "nameInData");
		compareWith.MRV.setReturnValues("hasAttributes", List.of(false));

		compareAgainst.MRV.setReturnValues("getNameInData", List.of("nameInData"));
		compareAgainst.MRV.setReturnValues("hasAttributes", List.of(true));
		DataAttributeSpy attribute = createAttribute("attributeA", "A");
		compareAgainst.MRV.setReturnValues("getAttributes", List.of(List.of(attribute)));

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertFalse(report.isEmpty());
		assertTrue(report.size() == 1);
		assertEquals(report.get(0), "Group with name: nameInData not found.");
	}

}
