package se.uu.ub.cora.fitnesseintegration.spy;

import se.uu.ub.cora.fitnesseintegration.internal.StandardFitnesseMethod;
import se.uu.ub.cora.testutils.mcr.MethodCallRecorder;
import se.uu.ub.cora.testutils.mrv.MethodReturnValues;

public class StandardFitnesseMethodSpy implements StandardFitnesseMethod {

	public MethodCallRecorder MCR = new MethodCallRecorder();
	public MethodReturnValues MRV = new MethodReturnValues();

	public StandardFitnesseMethodSpy() {
		MCR.useMRV(MRV);
	}

	@Override
	public void run() {
		MCR.addCall();
	}
}
