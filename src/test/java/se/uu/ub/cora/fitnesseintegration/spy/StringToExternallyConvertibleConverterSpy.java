package se.uu.ub.cora.fitnesseintegration.spy;

import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.data.ExternallyConvertible;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class StringToExternallyConvertibleConverterSpy
		implements StringToExternallyConvertibleConverter {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public StringToExternallyConvertibleConverterSpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("convert", ExternallyConvertibleSpy::new);
	}

	@Override
	public ExternallyConvertible convert(String dataString) {
		return (ExternallyConvertible) MCR.addCallAndReturnFromMRV("dataString", dataString);
	}

}
