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
import se.uu.ub.cora.data.spies.DataAtomicSpy;
import se.uu.ub.cora.data.spies.DataAttributeSpy;
import se.uu.ub.cora.data.spies.DataChildSpy;
import se.uu.ub.cora.data.spies.DataGroupSpy;
import se.uu.ub.cora.data.spies.DataRecordLinkSpy;
import se.uu.ub.cora.data.spies.DataResourceLinkSpy;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

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
		compareWith = createGroupSpy("nameInData1");
		compareAgainst = createGroupSpy("nameInData2");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Group with name: nameInData1 not found.");
	}

	private void assertCompareFoundOnMissmatch(List<String> report) {
		assertFalse(report.isEmpty());
		assertTrue(report.size() == 1);
	}

	private DataGroupSpy createGroupSpy(String nameInData, String... attributesPair) {
		DataGroupSpy group = new DataGroupSpy();
		MethodReturnValues MRV = group.MRV;
		setNameAndAttributes(MRV, nameInData, attributesPair);
		return group;
	}

	private void setNameAndAttributes(MethodReturnValues MRV, String nameInData,
			String... attributesPair) {
		MRV.setDefaultReturnValuesSupplier("getNameInData", (Supplier<String>) () -> nameInData);

		if (attributesPair.length > 0) {
			MRV.setDefaultReturnValuesSupplier("hasAttributes", (Supplier<Boolean>) () -> true);
			List<DataAttribute> attributes = createListOfAttributes(attributesPair);
			MRV.setDefaultReturnValuesSupplier("getAttributes",
					(Supplier<List<DataAttribute>>) () -> attributes);
		}
	}

	private List<DataAttribute> createListOfAttributes(String... attributesPair) {
		int position = 0;
		List<DataAttribute> attributes = new ArrayList<>();
		while (position < attributesPair.length) {
			DataAttributeSpy attribute = createAttribute(attributesPair[position],
					attributesPair[position + 1]);
			attributes.add(attribute);
			position = position + 2;
		}
		return attributes;
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
	public void testCompareDifferentAttributes() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A");
		compareAgainst = createGroupSpy("nameInData");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A] "
						+ "not found.");
	}

	@Test
	public void testCompareNoAttributesOnWithButAttributesOnAgainst() throws Exception {
		compareWith = createGroupSpy("nameInData");
		compareAgainst = createGroupSpy("nameInData", "attributeA", "A");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Group with name: nameInData not found.");
	}

	@Test
	public void testCompareBothHaveAttributesButDifferent() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A");
		compareAgainst = createGroupSpy("nameInData", "attributeB", "B");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A] "
						+ "not found.");
	}

	@Test
	public void testCompareBothHaveAttributesSameAttributesAndDifferentValues() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A");
		compareAgainst = createGroupSpy("nameInData", "attributeA", "B");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A] "
						+ "not found.");
	}

	@Test
	public void testCompareBothHaveAttributesSameAttributesAndValues() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A");
		compareAgainst = createGroupSpy("nameInData", "attributeA", "A");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareBothHaveAttributesOneEqualAnotherDifferent() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A", "attributeC", "C",
				"attributeD", "D");
		compareAgainst = createGroupSpy("nameInData", "attributeB", "B", "attributeA", "A",
				"attributeD", "D");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A, "
						+ "name: attributeC and value: C, name: attributeD and value: D] not found.");
	}

	@Test
	public void testCompareFewerAttributesOnCompareWith() throws Exception {
		compareWith = createGroupSpy("nameInData", "attributeA", "A", "attributeB", "B");
		compareAgainst = createGroupSpy("nameInData", "attributeB", "B", "attributeA", "A",
				"attributeD", "D");

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A, "
						+ "name: attributeB and value: B] not found.");
	}

	@Test
	public void testCompareWithHasAChild() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValue");
		setReturnChildren(compareWith.MRV, atomicWith1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and value: someValue not found.");
	}

	private void setReturnChildren(MethodReturnValues MRV, DataChild... dataChild) {
		List<DataChild> childrenWith = List.of(dataChild);

		MRV.setDefaultReturnValuesSupplier("getChildren",
				(Supplier<List<DataChild>>) () -> childrenWith);

	}

	private DataAtomicSpy createAtomicSpy(String nameInData, String value,
			String... attributesPair) {
		DataAtomicSpy atomic = new DataAtomicSpy();
		MethodReturnValues MRV = atomic.MRV;
		setNameAndAttributes(MRV, nameInData, attributesPair);
		MRV.setDefaultReturnValuesSupplier("getValue", (Supplier<String>) () -> value);
		return atomic;
	}

	@Test
	public void testCompareBothHasOneEqualChild() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValue");
		setReturnChildren(compareWith.MRV, atomicWith1);

		DataAtomicSpy atomicAgainst1 = createAtomicSpy("nameInData", "someValue");
		setReturnChildren(compareAgainst.MRV, atomicAgainst1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareBothHasOneEqualChildAndAttributes() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValue", "attributeA", "A");
		setReturnChildren(compareWith.MRV, atomicWith1);

		DataAtomicSpy atomicAgainst1 = createAtomicSpy("nameInData", "someValue", "attributeA",
				"A");
		setReturnChildren(compareAgainst.MRV, atomicAgainst1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareBothHasAChildWithDifferentValue() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValueA");
		setReturnChildren(compareWith.MRV, atomicWith1);

		DataAtomicSpy atomicAgainst1 = createAtomicSpy("nameInData", "someValueB");
		setReturnChildren(compareAgainst.MRV, atomicAgainst1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Atomic with name: nameInData and value: someValueA not found.");
	}

	@Test
	public void testCompareBothHasAChildWithDifferentAttributes() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValue", "attributeA", "A");
		setReturnChildren(compareWith.MRV, atomicWith1);

		DataAtomicSpy atomicAgainst1 = createAtomicSpy("nameInData", "someValue", "attributeA",
				"B");
		setReturnChildren(compareAgainst.MRV, atomicAgainst1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and attribute/s ["
				+ "name: attributeA and value: A] and value: someValue not found.");
	}

	@Test
	public void testCompareChildWithSameAttributeButWrongValue() throws Exception {
		DataAtomicSpy atomicWith1 = createAtomicSpy("nameInData", "someValue", "attributeA", "A");
		setReturnChildren(compareWith.MRV, atomicWith1);

		DataAtomicSpy atomicAgainst1 = createAtomicSpy("nameInData", "someValueB", "attributeA",
				"A");
		setReturnChildren(compareAgainst.MRV, atomicAgainst1);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and attribute/s ["
				+ "name: attributeA and value: A] and value: someValue not found.");
	}

	@Test
	public void testCompareTwoEqualRecordLink() throws Exception {
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId");
		setReturnChildren(compareWith.MRV, recordLinkWith);
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData", "linkType",
				"linkId");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	private DataRecordLinkSpy createRecordLinkSpy(String nameInData, String linkType, String linkId,
			String... attributesPair) {
		DataRecordLinkSpy recordLink = new DataRecordLinkSpy();
		MethodReturnValues MRV = recordLink.MRV;
		setNameAndAttributes(MRV, nameInData, attributesPair);
		MRV.setDefaultReturnValuesSupplier("getLinkedRecordType",
				(Supplier<String>) () -> linkType);
		MRV.setDefaultReturnValuesSupplier("getLinkedRecordId", (Supplier<String>) () -> linkId);
		return recordLink;
	}

	@Test
	public void testCompareTwoRecordLinkDifferentNameInData() throws Exception {
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId");
		setReturnChildren(compareWith.MRV, recordLinkWith);
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData2", "linkType",
				"linkId");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "RecordLink with name: nameInData, linkType: linkType and "
				+ "linkId: linkId not found.");
	}

	@Test
	public void testCompareTwoRecordLinkDifferentLinkType() throws Exception {
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId");
		setReturnChildren(compareWith.MRV, recordLinkWith);
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData", "linkTypeA",
				"linkId");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "RecordLink with name: nameInData, linkType: linkType and "
				+ "linkId: linkId not found.");
	}

	@Test
	public void testCompareTwoRecordLinkDifferentLinkId() throws Exception {
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId");
		setReturnChildren(compareWith.MRV, recordLinkWith);
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData", "linkType",
				"linkIdA");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "RecordLink with name: nameInData, linkType: linkType and "
				+ "linkId: linkId not found.");
	}

	@Test
	public void testCompareTwoRecordLinkDifferentAttributes() throws Exception {
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId",
				"attributeA", "A");
		setReturnChildren(compareWith.MRV, recordLinkWith);
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData", "linkType",
				"linkId", "attributeA", "B");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "RecordLink with name: nameInData and attribute/s ["
				+ "name: attributeA and value: A], linkType: linkType and linkId: linkId not found.");
	}

	@Test
	public void testDataChildCompareNoAttributesOnWithButAttributesOnAgainst() throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeA", "A");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Group with name: nameInData not found.");
	}

	private DataChildSpy createChildSpy(String nameInData, String... attributesPair) {
		DataChildSpy child = new DataChildSpy();
		MethodReturnValues MRV = child.MRV;
		setNameAndAttributes(MRV, nameInData, attributesPair);
		return child;
	}

	@Test
	public void testDataChildCompareBothHaveAttributesButDifferent() throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData", "attributeA", "A");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeB", "A");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A] "
						+ "not found.");
	}

	@Test
	public void testDataChildCompareBothHaveAttributesSameAttributesAndDifferentValues()
			throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData", "attributeA", "A");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeA", "B");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A] not found.");
	}

	@Test
	public void testDataChildCompareBothHaveAttributesSameAttributesAndValues() throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData", "attributeA", "A");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeA", "A");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testDataChildCompareBothHaveAttributesOneEqualAnotherDifferent() throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData", "attributeA", "A", "attributeC",
				"C", "attributeD", "D");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeB", "B",
				"attributeA", "A", "attributeD", "D");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A, "
						+ "name: attributeC and value: C, name: attributeD and value: D] not found.");
	}

	@Test
	public void testDataChildCompareFewerAttributesOnCompareWith() throws Exception {
		DataChildSpy dataChildWith = createChildSpy("nameInData", "attributeA", "A", "attributeB",
				"B");
		setReturnChildren(compareWith.MRV, dataChildWith);

		DataChildSpy dataChildAgainst = createChildSpy("nameInData", "attributeB", "B",
				"attributeA", "A", "attributeD", "D");
		setReturnChildren(compareAgainst.MRV, dataChildAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"Group with name: nameInData and attribute/s [name: attributeA and value: A, "
						+ "name: attributeB and value: B] not found.");
	}

	@Test
	public void testCompareTwoEqualResourceLink() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	private DataResourceLinkSpy createResourceLinkSpy(String nameInData, String streamId,
			String fileName, String fileSize, String mimeType, String... attributesPair) {
		DataResourceLinkSpy resourceLink = new DataResourceLinkSpy();
		MethodReturnValues MRV = resourceLink.MRV;
		setNameAndAttributes(MRV, nameInData, attributesPair);

		MRV.setDefaultReturnValuesSupplier("getStreamId", (Supplier<String>) () -> streamId);
		MRV.setDefaultReturnValuesSupplier("getFileName", (Supplier<String>) () -> fileName);
		MRV.setDefaultReturnValuesSupplier("getFileSize", (Supplier<String>) () -> fileSize);
		MRV.setDefaultReturnValuesSupplier("getMimeType", (Supplier<String>) () -> mimeType);
		return resourceLink;
	}

	@Test
	public void testCompareTwoResourceLinkDifferentStreamId() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamIdA",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareTwoResourceLinkDifferentFileName() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamId",
				"fileNameA", "fileSize", "mimeType");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareTwoResourceLinkDifferentFileSize() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSizeA", "mimeType");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareTwoResourceLinkDifferentMimeType() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeTypeA");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareTwoResourceLinkWithSameAttributesDifferentValues() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType", "attributeA", "A", "attributeB", "B");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType", "attributeA", "A", "attributeB", "C");
		setReturnChildren(compareAgainst.MRV, resourceLinkAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0),
				"ResourceLink with name: nameInData and attribute/s "
						+ "[name: attributeA and value: A, name: attributeB and value: B], "
						+ "streamId: streamId, fileName: fileName, fileSize: fileSize and "
						+ "mimeType: mimeType not found.");
	}

	@Test
	public void testCompareAResourceLinkAtomicSameNameInData() throws Exception {
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		setReturnChildren(compareWith.MRV, resourceLinkWith);
		DataAtomicSpy atomicAgainst = createAtomicSpy("nameInData", "value");
		setReturnChildren(compareAgainst.MRV, atomicAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareMoreThanOneChildMissing() throws Exception {
		DataAtomicSpy atomicWith = createAtomicSpy("nameInData", "value");
		DataResourceLinkSpy resourceLinkWith = createResourceLinkSpy("nameInData", "streamId",
				"fileName", "fileSize", "mimeType");
		DataRecordLinkSpy recordLinkWith = createRecordLinkSpy("nameInData", "linkType", "linkId");
		setReturnChildren(compareWith.MRV, atomicWith, resourceLinkWith, recordLinkWith);

		DataAtomicSpy atomicAgainst = createAtomicSpy("nameInDataA", "value");
		DataResourceLinkSpy resourceLinkAgainst = createResourceLinkSpy("nameInData", "streamIdA",
				"fileName", "fileSize", "mimeType");
		DataRecordLinkSpy recordLinkAgainst = createRecordLinkSpy("nameInData", "linkType",
				"linkId");
		setReturnChildren(compareAgainst.MRV, recordLinkAgainst, resourceLinkAgainst,
				atomicAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertFalse(report.isEmpty());
		assertTrue(report.size() == 2);
		assertEquals(report.get(0), "Atomic with name: nameInData and value: value not found.");
		assertEquals(report.get(1), "ResourceLink with name: nameInData, streamId: streamId, "
				+ "fileName: fileName, fileSize: fileSize and mimeType: mimeType not found.");
	}

	@Test
	public void testCompareTwoLevelDownAtomicChildDontExist() throws Exception {
		DataGroupSpy childGroupWith = createGroupSpy("childGroup");
		setReturnChildren(compareWith.MRV, childGroupWith);

		DataAtomicSpy atomicWith = createAtomicSpy("nameInData", "value");
		setReturnChildren(childGroupWith.MRV, atomicWith);

		DataGroupSpy childGroupAgainst = createGroupSpy("childGroup");
		setReturnChildren(compareAgainst.MRV, childGroupAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertCompareFoundOnMissmatch(report);
		assertEquals(report.get(0), "Atomic with name: nameInData and value: value not found.");
	}

	@Test
	public void testCompareTwoLevelDownAtomicEquals() throws Exception {
		DataGroupSpy childGroupWith = createGroupSpy("childGroup");
		setReturnChildren(compareWith.MRV, childGroupWith);

		DataAtomicSpy atomicWith = createAtomicSpy("nameInData", "value");
		setReturnChildren(childGroupWith.MRV, atomicWith);

		DataGroupSpy childGroupAgainst = createGroupSpy("childGroup");
		setReturnChildren(compareAgainst.MRV, childGroupAgainst);

		DataAtomicSpy atomicAgainst = createAtomicSpy("nameInData", "value");
		setReturnChildren(childGroupAgainst.MRV, atomicAgainst);

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	@Test
	public void testCompareSeveralLevelDownEquals() throws Exception {

		createComplexTreeGroupCompareWith();
		createComplexTreeGroupCompareAgainst();

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertTrue(report.isEmpty());
	}

	private void createComplexTreeGroupCompareWith() {
		compareWith = createGroupSpy("G11");
		DataGroupSpy G21 = createGroupSpy("G21");
		DataGroupSpy G22 = createGroupSpy("G22", "attributeA", "A", "attributeB", "B");
		setReturnChildren(compareWith.MRV, G21, G22);

		DataAtomicSpy A31 = createAtomicSpy("A31", "value", "attributeA", "A", "attributeB", "B");
		DataRecordLinkSpy RL31 = createRecordLinkSpy("RL31", "linkType", "linkId");
		setReturnChildren(G21.MRV, A31, RL31);

		DataRecordLinkSpy RL32 = createRecordLinkSpy("RL32", "linkType", "linkId");
		DataGroupSpy G31 = createGroupSpy("G31");
		DataAtomicSpy A32 = createAtomicSpy("A32", "value", "attributeA", "A", "attributeB", "B");
		setReturnChildren(G22.MRV, RL32, G31, A32);

		DataResourceLinkSpy RS41 = createResourceLinkSpy("RS41", "streamIdA", "fileName",
				"fileSize", "mimeType", "attributeA", "A", "attributeB", "B");
		setReturnChildren(G31.MRV, RS41);
	}

	private void createComplexTreeGroupCompareAgainst() {
		compareAgainst = createGroupSpy("G11");
		DataGroupSpy G21 = createGroupSpy("G21");
		DataGroupSpy G22 = createGroupSpy("G22", "attributeA", "A", "attributeB", "B");
		DataGroupSpy G23 = createGroupSpy("G23");
		DataAtomicSpy A21 = createAtomicSpy("A21", "value");
		setReturnChildren(compareAgainst.MRV, G21, G22, G23, A21);

		DataAtomicSpy A31 = createAtomicSpy("A31", "value", "attributeA", "A", "attributeB", "B");
		DataRecordLinkSpy RL31 = createRecordLinkSpy("RL31", "linkType", "linkId");
		setReturnChildren(G21.MRV, RL31, A31);

		DataGroupSpy G31 = createGroupSpy("G31");
		DataRecordLinkSpy RL32 = createRecordLinkSpy("RL32", "linkType", "linkId");
		DataAtomicSpy A32 = createAtomicSpy("A32", "value", "attributeA", "A", "attributeB", "B");
		setReturnChildren(G22.MRV, G31, RL32, A32);

		DataResourceLinkSpy RS41 = createResourceLinkSpy("RS41", "streamIdA", "fileName",
				"fileSize", "mimeType", "attributeA", "A", "attributeB", "B");
		setReturnChildren(G31.MRV, RS41);

		DataRecordLinkSpy RL33 = createRecordLinkSpy("RL33", "linkType", "linkId", "attributeA",
				"A", "attributeB", "B");
		setReturnChildren(G23.MRV, RL33);
	}

	@Test
	public void testCompareSeveralLevelDownNotEquals() throws Exception {

		createComplexTreeGroupCompareWith();
		createComplexTreeGroupCompareAgainstWrongOnEachLeaf();

		List<String> report = comparer.compareDataGroupToDataGroup(compareWith, compareAgainst);

		assertFalse(report.isEmpty());
		assertEquals(report.size(), 5);
		assertEquals(report.get(0),
				"Atomic with name: A31 and attribute/s [name: attributeA and value: A, name: attributeB and value: B] and value: value not found.");
		assertEquals(report.get(1),
				"RecordLink with name: RL31, linkType: linkType and linkId: linkId not found.");
		assertEquals(report.get(2),
				"RecordLink with name: RL32, linkType: linkType and linkId: linkId not found.");
		assertEquals(report.get(3),
				"ResourceLink with name: RS41 and attribute/s [name: attributeA and value: A, "
						+ "name: attributeB and value: B], streamId: streamIdA, fileName: fileName, "
						+ "fileSize: fileSize and mimeType: mimeType not found.");
		assertEquals(report.get(4),
				"Atomic with name: A32 and attribute/s [name: attributeA and value: A, "
						+ "name: attributeB and value: B] and value: value not found.");
	}

	private void createComplexTreeGroupCompareAgainstWrongOnEachLeaf() {
		compareAgainst = createGroupSpy("G11");
		DataGroupSpy G21 = createGroupSpy("G21");
		DataGroupSpy G22 = createGroupSpy("G22", "attributeA", "A", "attributeB", "B");
		DataGroupSpy G23 = createGroupSpy("G23");
		DataAtomicSpy A21 = createAtomicSpy("A21", "value");
		setReturnChildren(compareAgainst.MRV, G21, G22, G23, A21);

		DataAtomicSpy A31 = createAtomicSpy("A31", "value", "attributeA", "A");
		DataRecordLinkSpy RL31 = createRecordLinkSpy("RL31", "anotherLink", "linkId");
		setReturnChildren(G21.MRV, RL31, A31);

		DataGroupSpy G31 = createGroupSpy("G31");
		DataRecordLinkSpy RL32 = createRecordLinkSpy("RL32", "anotherLink", "linkId");
		DataAtomicSpy A32 = createAtomicSpy("A32", "anotherValue", "attributeA", "A", "attributeB",
				"B");
		setReturnChildren(G22.MRV, G31, RL32, A32);

		DataResourceLinkSpy RS41 = createResourceLinkSpy("RS41", "streamId", "fileName", "fileSize",
				"mimeType", "attributeA", "A", "attributeB", "C");
		setReturnChildren(G31.MRV, RS41);

		DataRecordLinkSpy RL33 = createRecordLinkSpy("RL33", "linkType", "linkId");
		setReturnChildren(G23.MRV, RL33);
	}

}
