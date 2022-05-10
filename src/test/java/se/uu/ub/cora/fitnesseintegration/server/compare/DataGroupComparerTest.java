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
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.testspies.data.DataAtomicSpy;
import se.uu.ub.cora.testspies.data.DataAttributeSpy;
import se.uu.ub.cora.testspies.data.DataGroupSpy;

public class DataGroupComparerTest {

	DataGroupComparer comparer;
	private DataGroupSpy compareWith;
	private DataGroupSpy compareAgainst;
	private DataAtomicSpy atomicWith1;
	private DataAtomicSpy atomicAgainst1;

	@BeforeMethod
	public void beforeMethod() {
		comparer = new DataGroupComparerImp();
		compareWith = new DataGroupSpy();
		compareAgainst = new DataGroupSpy();

		atomicWith1 = new DataAtomicSpy();
		atomicAgainst1 = new DataAtomicSpy();
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

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Group with name: nameInData1 not found.");
	}

	private void assertCompareFoundOnMissmatch(List<String> report) {
		assertFalse(report.isEmpty());
		assertTrue(report.size() == 1);
	}

	@Test
	public void testCompareDifferentAttributes() throws Exception {
		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A");
		compareAgainst.MRV.setReturnValues("getNameInData", List.of("nameInData"));

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
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
		attribute.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> attributeName);
		attribute.MRV.setDefaultReturnValuesSupplier("getValue",
				(Supplier<String>) () -> attributeValue);
		return attribute;
	}

	@Test
	public void testCompareNoAttributesOnWithButAttributesOnAgainst() throws Exception {
		compareWith.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> "nameInData");

		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeA", "A");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Group with name: nameInData not found.");
	}

	@Test
	public void testCompareBothHaveAttributesButDifferent() throws Exception {

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeB", "B");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s with name: attributeA and value: A "
						+ "not found.");
	}

	@Test
	public void testCompareBothHaveAttributesSameAttributesAndDifferentValues() throws Exception {

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeA", "B");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s with name: attributeA and value: A "
						+ "not found.");
	}

	@Test
	public void testCompareBothHaveAttributesSameAttributesAndValues() throws Exception {

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeA", "A");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareBothHaveAttributesOneEqualAnotherDifferent() throws Exception {

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A",
				"attributeC", "C", "attributeD", "D");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeB", "B",
				"attributeA", "A", "attributeD", "D");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s with name: attributeA and value: A, "
						+ "name: attributeC and value: C, name: attributeD and value: D not found.");
	}

	@Test
	public void testCompareFewerAttributesOnCompareWith() throws Exception {

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData", "attributeA", "A",
				"attributeB", "B");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData", "attributeB", "B",
				"attributeA", "A", "attributeD", "D");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s with name: attributeA and value: A, "
						+ "name: attributeB and value: B not found.");
	}

	@Test
	public void testCompareWithHasAChild() throws Exception {
		setMRVAtomicWithNameInDataAndAttributes(atomicWith1, "nameInData", "someValue");
		List<DataChild> childrenWith = List.of(atomicWith1);

		compareWith.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenWith);

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and value: someValue not found.");
	}

	@Test
	public void testCompareBothHasOneEqualChild() throws Exception {
		setMRVAtomicWithNameInDataAndAttributes(atomicWith1, "nameInData", "someValue");
		List<DataChild> childrenWith = List.of(atomicWith1);

		setMRVAtomicWithNameInDataAndAttributes(atomicAgainst1, "nameInData", "someValue");
		List<DataChild> childrenAgainst = List.of(atomicAgainst1);

		compareWith.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenWith);
		compareAgainst.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenAgainst);

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareBothHasAChildWithDifferentValue() throws Exception {
		setMRVAtomicWithNameInDataAndAttributes(atomicWith1, "nameInData", "someValueA");
		List<DataChild> childrenWith = List.of(atomicWith1);

		setMRVAtomicWithNameInDataAndAttributes(atomicAgainst1, "nameInData", "someValueB");
		List<DataChild> childrenAgainst = List.of(atomicAgainst1);

		compareWith.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenWith);
		compareAgainst.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenAgainst);

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Atomic with name: nameInData and value: someValueA not found.");
	}

	@Test
	public void testCompareBothHasAChildWithDifferentAttributes() throws Exception {
		setMRVAtomicWithNameInDataAndAttributes(atomicWith1, "nameInData", "someValue",
				"attributeA", "A");
		List<DataChild> childrenWith = List.of(atomicWith1);

		setMRVAtomicWithNameInDataAndAttributes(atomicAgainst1, "nameInData", "someValue",
				"attributeA", "B");
		List<DataChild> childrenAgainst = List.of(atomicAgainst1);

		compareWith.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenWith);
		compareAgainst.MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenAgainst);

		setMRVWithNameInDataAndAttributes(compareWith, "nameInData");
		setMRVWithNameInDataAndAttributes(compareAgainst, "nameInData");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and attribute/s with "
				+ "name: attributeA and value: A and value: someValue not found.");
	}

	private void setMRVAtomicWithNameInDataAndAttributes(DataAtomicSpy atomic, String nameInData,
			String value, String... attributeStuff) {
		atomic.MRV.setDefaultReturnValuesSupplier("getNameInData",
				(Supplier<String>) () -> nameInData);
		atomic.MRV.setDefaultReturnValuesSupplier("getValue", (Supplier<String>) () -> value);

		if (attributeStuff.length > 0) {
			atomic.MRV.setDefaultReturnValuesSupplier("hasAttributes",
					(Supplier<Boolean>) () -> true);
			int position = 0;
			List<DataAttribute> attributes = new ArrayList<>();
			while (position < attributeStuff.length) {
				DataAttributeSpy attribute = createAttribute(attributeStuff[position],
						attributeStuff[position + 1]);
				attributes.add(attribute);
				position = position + 2;
			}
			atomic.MRV.setDefaultReturnValuesSupplier("getAttributes",
					(Supplier<List<DataAttribute>>) () -> attributes);
		}
	}

}
