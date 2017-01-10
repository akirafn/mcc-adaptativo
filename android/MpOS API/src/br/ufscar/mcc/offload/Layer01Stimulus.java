package br.ufscar.mcc.offload;

import java.lang.reflect.Method;
import java.util.HashMap;

import android.util.Log;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.FunctionProfile;

public class Layer01Stimulus {
	private HashMap<Integer, FunctionProfile> localMap;
	private HashMap<Integer, FunctionProfile> remoteMap;
	private double supLimit;
	private double infLimit;
	private String clsName = Layer01Stimulus.class.getName();

	public Layer01Stimulus(double limit) {
		this.localMap = new HashMap<Integer, FunctionProfile>();
		this.remoteMap = new HashMap<Integer, FunctionProfile>();
		this.supLimit = limit;
		this.infLimit = -1.0 * limit;
	}

	public HashMap<Integer, FunctionProfile> getLocalMap() {
		return localMap;
	}

	public void setLocalMap(HashMap<Integer, FunctionProfile> localMap) {
		this.localMap = localMap;
	}

	public HashMap<Integer, FunctionProfile> getRemoteMap() {
		return remoteMap;
	}

	public void setRemoteMap(HashMap<Integer, FunctionProfile> remoteMap) {
		this.remoteMap = remoteMap;
	}

	public DecisionFlag makeDecision(Method method, int inputSize) {
		DecisionFlag decision = DecisionFlag.NoDecision;
		FunctionProfile localProfile, remoteProfile;
		double localValue = 0.0, remoteValue = 0.0, diffValue;

		Integer chave = Integer.valueOf(generateProfileHash(method.getName(), method.getClass().getName()));
		if (localMap.containsKey(chave)) {
			localProfile = localMap.get(chave);
			if (localProfile.getMethodName().equals(method.getName())) {
				if (inputSize <= localProfile.getMethodMax() && inputSize >= localProfile.getMethodMin()) {
					localValue = (localProfile.getFactorA() * (double) inputSize) + localProfile.getFactorB();
					Log.i(clsName, "Execucao local: " + localProfile.getFactorA() + "*" + inputSize + " + "
							+ localProfile.getFactorB() + " = " + localValue);
				} else
					Log.i(clsName, "Execucao local: input " + inputSize + " fora do alcance ["
							+ localProfile.getMethodMin() + ", " + localProfile.getMethodMax() + "]");
			} else
				Log.i(clsName, "Execucao local: chave certa metodo errado");
		} else
			Log.i(clsName, "Execucao local: metodo nao encontrado");

		if (remoteMap.containsKey(chave) && localValue > 0.0) {
			remoteProfile = remoteMap.get(chave);
			if (remoteProfile.getMethodName().equals(method.getName())) {
				if (inputSize <= remoteProfile.getMethodMax() && inputSize >= remoteProfile.getMethodMin()) {
					remoteValue = (remoteProfile.getFactorA() * (double) inputSize) + remoteProfile.getFactorB();
					Log.i(clsName, "Execucao remota: " + remoteProfile.getFactorA() + "*" + inputSize + " + "
							+ remoteProfile.getFactorB() + " = " + remoteValue);
				} else
					Log.i(clsName, "Execucao remota: input " + inputSize + " fora do alcance ["
							+ remoteProfile.getMethodMin() + ", " + remoteProfile.getMethodMax() + "]");
			} else
				Log.i(clsName, "Execucao remota: chave certa metodo errado");
		} else
			Log.i(clsName, "Execucao remota: ou metodo nao encontrado ou nunca foi executado");

		diffValue = localValue - remoteValue;

		if (localValue == 0.0)
			decision = DecisionFlag.ForcedLocal;
		else if (remoteValue == 0.0)
			decision = DecisionFlag.ForcedOffload;
		else if (diffValue > supLimit)
			decision = DecisionFlag.GoOffload;
		else if (diffValue < infLimit)
			decision = DecisionFlag.GoLocal;

		return decision;
	}

	private int generateProfileHash(String methodName, String className) {
		int prime = 59;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());

		return result;
	}

	public void insertIntoLocalTable(Method method, int inputSize, long executionTime) {
		Integer chave = Integer.valueOf(generateProfileHash(method.getName(), method.getClass().getName()));

		if (localMap.containsKey(chave)) {
			FunctionProfile profile = localMap.get(chave);
			if (profile.getMethodName().equals(method.getName()))
				profile.updateValues(inputSize, (int) executionTime);

		} else {
			FunctionProfile profile = new FunctionProfile();
			profile.setMethodName(method.getName());
			profile.setMethodHash(chave.intValue());
			profile.setMethodMin(inputSize);
			profile.setMethodMax(inputSize);
			profile.setServerUrl("");
			profile.setConnType(ConnectionType.CONN_LOCAL);
			profile.updateValues(inputSize, (int) executionTime);
			localMap.put(chave, profile);
		}
	}

	public void insertIntoRemoteTable(Method method, int inputSize, long executionTime, String serverUrl,
			ConnectionType connType) {
		Integer chave = Integer.valueOf(generateProfileHash(method.getName(), method.getClass().getName()));

		if (remoteMap.containsKey(chave)) {
			FunctionProfile profile = remoteMap.get(chave);
			if (profile.getMethodName().equals(method.getName()))
				profile.updateValues(inputSize, (int) executionTime);
		} else {
			FunctionProfile profile = new FunctionProfile();
			profile.setMethodName(method.getName());
			profile.setMethodHash(chave.intValue());
			profile.setMethodMin(inputSize);
			profile.setMethodMax(inputSize);
			profile.setServerUrl(serverUrl);
			profile.setConnType(connType);
			profile.updateValues(inputSize, (int) executionTime);
			remoteMap.put(chave, profile);
		}
	}
}
