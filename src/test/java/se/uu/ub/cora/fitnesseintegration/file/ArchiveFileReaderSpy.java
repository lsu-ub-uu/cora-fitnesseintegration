package se.uu.ub.cora.fitnesseintegration.file;

import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class ArchiveFileReaderSpy implements ArchiveFileReader {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public ArchiveFileReaderSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("readFileWithNameAndVersion", String::new);
	}

	@Override
	public String readFileWithNameAndVersion(String basePath, String fileName, String version) {
		return (String) MCR.addCallAndReturnFromMRV("basePath", basePath, "fileName", fileName,
				"version", version);
	}

}
