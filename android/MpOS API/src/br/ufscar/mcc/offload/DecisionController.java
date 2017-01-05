package br.ufscar.mcc.offload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.ExecutionProfile;
import br.ufscar.mcc.model.MethodProfile;

public class DecisionController {
	private static Layer01Stimulus layerStimulus;
	private static Layer02Interaction layerInteraction;
	private static Layer03Time layerTime;
	private final String clsName = DecisionController.class.getName();

	public DecisionController(Context context) {
		layerStimulus = new Layer01Stimulus(0.5);
		layerInteraction = new Layer02Interaction(context);
		layerTime = new Layer03Time(context, 0);
	}

	// ----------------------------
	// Getters e Setters
	// ----------------------------
	public void setServerUrl(String serverUrl) {
		layerInteraction.setServerUrl(serverUrl);
		Log.i(clsName, "Servidor remoto marcado: " + serverUrl);
	}

	public void setConnectionType() {
		layerInteraction.setConnectionType();
	}

	public void setThroughputRate(String upRate, String downRate) {
		layerInteraction.setThroughputRate(upRate, downRate);
		Log.i(clsName, "Taxas atualizadas: down " + downRate + ", up " + upRate);
	}

	public void setExecutionProfile(Method method, int paramSize, Object returnValue, long executionTime, DecisionFlag decision) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int returnSize;
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(returnValue);
			oos.flush();
			returnSize = baos.toByteArray().length;
			
			layerTime.setExecutionProfile(method, paramSize, returnSize, (int)executionTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType(), decision);
			
		} catch (IOException e) {
			Log.e(clsName, e.getMessage());
		}
	}

	public void setLocalExecutionProfile(Method method, int paramSize, Object returnValue, long executionTime) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int returnSize;
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(returnValue);
			oos.flush();
			returnSize = baos.toByteArray().length;
			
			layerTime.setExecutionProfile(method, paramSize, returnSize, (int)executionTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType(), DecisionFlag.ForcedLocal);
			
		} catch (IOException e) {
			Log.e(clsName, e.getMessage());
		}
	}
	
	public void setRemoteExecutionProfile(Method method, int paramSize, Object returnValue, long executionTime) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int returnSize;
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(returnValue);
			oos.flush();
			returnSize = baos.toByteArray().length;
			
			layerTime.setExecutionProfile(method, paramSize, returnSize, (int)executionTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType(), DecisionFlag.ForcedOffload);
			
		} catch (IOException e) {
			Log.e(clsName, e.getMessage());
		}
	}
	
	public void setExecutionProfile(Method method) {
		layerTime.setMethodProfile(method, 1);
	}

	// --------------------------------
	// Decision Taking Management
	// --------------------------------
	public DecisionFlag makeDecision(Method method, int paramSize) {
		DecisionFlag decision = DecisionFlag.GoOffload;
		ExecutionProfile localProfile = layerTime.getLocalExecutionProfileByInput(method, paramSize);
		ExecutionProfile remoteProfile = layerTime.getRemoteExecutionProfileByServerConn(method, paramSize,
				layerInteraction.getServerUrl(), layerInteraction.getConnectionType());
		
		decision = layerInteraction.makeDecision(localProfile, remoteProfile);

		switch (decision) {
		case ForcedLocal:
			Log.i(clsName, "Decision for method " + method.getName() + ": forced local execution.");
			break;
		case ForcedOffload:
			Log.i(clsName, "Decision for method " + method.getName() + ": forced remote execution.");
			break;
		case GoLocal:
			Log.i(clsName, "Decision for method " + method.getName() + ": local execution favorable.");
			break;
		case GoOffload:
			Log.i(clsName, "Decision for method " + method.getName() + ": remote execution favorable.");
			break;
		default:
			Log.i(clsName, "Decision for method " + method.getName() + ": none was taken.");
			break;
		}

		if (decision == DecisionFlag.NoDecision) {
			decision = DecisionFlag.GoOffload;
			Log.i(clsName, "As none was taken, send to offload.");
		}

		return decision;
	}
}
