package br.ufscar.mcc.offload;

import java.lang.reflect.Method;
import java.util.HashMap;

import br.ufscar.mcc.model.FunctionProfile;

public class Layer01Stimulus {
	private HashMap<Integer, FunctionProfile> profileMap;
	private double supLimit;
	private double infLimit;

	public Layer01Stimulus(double limit) {
		this.profileMap = new HashMap<Integer, FunctionProfile>();
		this.supLimit = limit;
		this.infLimit = -1.0 * limit;
	}

	public DecisionFlag makeDecision(Method method, int inputSize) {
		DecisionFlag decision = DecisionFlag.NoDecision;
		FunctionProfile profile;
		double value;

		Integer chave = Integer.valueOf(generateProfileHash(method.getName(), method.getClass().getName()));

		if (profileMap.containsKey(chave)) {
			profile = profileMap.get(chave);
			if(profile.getMethodName().equals(method.getName())){
				value = (profile.getFactorA() * (double)inputSize) + profile.getFactorB();
				if (value > supLimit)
					decision = DecisionFlag.GoOffload;
				else if (value < infLimit)
					decision = DecisionFlag.GoLocal;				
			}
		}

		return decision;
	}

	private int generateProfileHash(String methodName, String className) {
		int prime = 59;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());

		return result;
	}

	public void insertIntoTable(Method method, int inputSize, boolean offloadDecision) {
		Integer chave = Integer.valueOf(generateProfileHash(method.getName(), method.getClass().getName()));
		FunctionProfile profile = new FunctionProfile();

		if (profileMap.containsKey(chave)) {
			// TODO
		} else {
			profileMap.put(chave, profile);
		}
	}
}
