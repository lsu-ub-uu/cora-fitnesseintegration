package se.uu.ub.cora.fitnesseintegration.spy;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import se.uu.ub.cora.data.DataAtomic;
import se.uu.ub.cora.data.DataAttribute;
import se.uu.ub.cora.data.DataChild;
import se.uu.ub.cora.data.DataChildFilter;
import se.uu.ub.cora.data.DataGroup;
import se.uu.ub.cora.data.ExternallyConvertible;

public class ExternallyConvertibleSpy implements ExternallyConvertible, DataGroup {

	@Override
	public boolean hasChildren() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsChildWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addChild(DataChild dataChild) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addChildren(Collection<DataChild> dataChildren) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<DataChild> getChildren() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataChild> getAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataChild getFirstChildWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFirstAtomicValueWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataAtomic> getAllDataAtomicsWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataAtomic> getAllDataAtomicsWithNameInDataAndAttributes(
			String childNameInData, DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DataGroup getFirstGroupWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataGroup> getAllGroupsWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataGroup> getAllGroupsWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeFirstChildWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllChildrenWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAllChildrenWithNameInDataAndAttributes(String nameInData,
			DataAttribute... childAttributes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataAtomic getFirstDataAtomicWithNameInData(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setRepeatId(String repeatId) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRepeatId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNameInData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addAttributeByIdWithValue(String nameInData, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasAttributes() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public DataAttribute getAttribute(String nameInData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<DataAttribute> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DataChild> getAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean removeAllChildrenMatchingFilter(DataChildFilter childFilter) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T> boolean containsChildOfTypeAndName(Class<T> arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> List<T> getChildrenOfTypeAndName(Class<T> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> T getFirstChildOfTypeAndName(Class<T> arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T extends DataChild> boolean removeChildrenWithTypeAndName(Class<T> arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <T extends DataChild> boolean removeFirstChildWithTypeAndName(Class<T> arg0,
			String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasRepeatId() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Optional<String> getAttributeValue(String arg0) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
