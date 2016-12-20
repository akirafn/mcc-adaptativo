package br.ufscar.mcc.offload;

import android.content.Context;
import android.util.Log;
import br.ufscar.mcc.history.model.MethodExecutionProfile;

public class Layer02Interaction {
	private String serverUrl;
	private double gain;
	private double downRate;
	private double upRate;
	private String clsname = Layer02Interaction.class.getName();

	public Layer02Interaction(Context context, String serverUrl, double gain) {
		this.serverUrl = serverUrl;
		this.gain = gain;
		this.downRate = 0;
		this.upRate = 0;
	}

	// Getters and Setters
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setThroughputRate(String upRate, String downRate) {
		this.upRate = Double.parseDouble(upRate) * 1000000;
		this.downRate = Double.parseDouble(downRate) * 1000000;
	}

	//
	public DecisionFlag makeDecision(MethodExecutionProfile methodProfile) {
		DecisionFlag decision = DecisionFlag.NoDecision;
		double transferTime = 0;
		double differ = 0.0;

		if (methodProfile.getLocalTime() == 0) {
			decision = DecisionFlag.ForcedLocal;
		} else if (methodProfile.getRemoteTime() == 0) {
			decision = DecisionFlag.ForcedOffload;
		} else if (downRate != 0 && upRate != 0) {
			transferTime = ((double) methodProfile.getInputSize() / downRate)
					+ ((double) methodProfile.getOutputSize() / upRate);
			differ = 1.0 - (((double) transferTime + (double) methodProfile.getRemoteTime())
					/ (double) methodProfile.getLocalTime());

			Log.i(clsname, "Diferença de tempo: local - " + String.valueOf(methodProfile.getLocalTime()) + ", remoto - "
					+ String.valueOf(methodProfile.getRemoteTime()) + " + " + String.valueOf(transferTime));

			if (differ > gain) {
				decision = DecisionFlag.GoOffload;
			} else {
				decision = DecisionFlag.GoLocal;
			}
		}

		return decision;
	}

}
