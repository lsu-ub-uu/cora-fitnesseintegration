package se.uu.ub.cora.fitnesseintegration.spy;

import se.uu.ub.cora.converter.ConverterFactory;
import se.uu.ub.cora.converter.ExternallyConvertibleToStringConverter;
import se.uu.ub.cora.converter.StringToExternallyConvertibleConverter;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class ConverterFactorySpy implements ConverterFactory {
	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public ConverterFactorySpy() {
		MCR.useMRV(MRV);
		MRV.setDefaultReturnValuesSupplier("factorStringToExternallyConvertableConverter",
				StringToExternallyConvertibleConverterSpy::new);
	}

	@Override
	public ExternallyConvertibleToStringConverter factorExternallyConvertableToStringConverter() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StringToExternallyConvertibleConverter factorStringToExternallyConvertableConverter() {
		return (StringToExternallyConvertibleConverter) MCR.addCallAndReturnFromMRV();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
