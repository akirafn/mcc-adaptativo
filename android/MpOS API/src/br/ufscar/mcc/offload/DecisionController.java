package br.ufscar.mcc.offload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import android.content.Context;
import android.util.Log;
import br.ufscar.mcc.model.ExecutionProfile;

public class DecisionController {
	private static Layer01Stimulus layerStimulus;
	private static Layer02Interaction layerInteraction;
	private static Layer03Time layerTime;
	private final String clsName = DecisionController.class.getName();

	public DecisionController(Context context) {
		layerStimulus = new Layer01Stimulus(3.0);
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
			
			paramSize /= 1024;
			returnSize /= 1024;
			executionTime /= 1000;
			
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
			
			paramSize /= 1024;
			returnSize /= 1024;
			executionTime /= 1000;
			
			layerTime.setExecutionProfile(method, paramSize, returnSize, (int)executionTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType(), DecisionFlag.ForcedLocal);
			layerStimulus.insertIntoLocalTable(method, paramSize, executionTime);
		} catch (IOException e) {
			Log.e(clsName, e.getMessage());
		}
	}
	
	public void setRemoteExecutionProfile(Method method, int paramSize, Object returnValue, long executionTime, long remoteTime) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int returnSize;
		try {
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			
			oos.writeObject(returnValue);
			oos.flush();
			returnSize = baos.toByteArray().length;
			
			paramSize /= 1024;
			returnSize /= 1024;
			
			executionTime /= 1000;
			remoteTime /= 1000;
			
			layerTime.setExecutionProfile(method, paramSize, returnSize, (int)executionTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType(), DecisionFlag.ForcedOffload);
			layerStimulus.insertIntoRemoteTable(method, paramSize, remoteTime, layerInteraction.getServerUrl(), layerInteraction.getConnectionType());
		} catch (IOException e) {
			Log.e(clsName, e.getMessage());
		}
	}
	
	public void setExecutionProfile(Method method) {
		layerTime.setMethodProfile(method);
	}

	// --------------------------------
	// Decision Taking Management
	// --------------------------------
	public DecisionFlag makeDecision(Method method, int paramSize) {
		DecisionFlag decision = DecisionFlag.GoOffload;
		paramSize /= 1024;
		decision = layerStimulus.makeDecision(method, paramSize);

		if(decision == DecisionFlag.NoDecision)
		{
			ExecutionProfile localProfile = layerTime.getLocalExecutionProfileByInput(method, paramSize);
			ExecutionProfile remoteProfile = layerTime.getRemoteExecutionProfileByServerConn(method, paramSize,
					layerInteraction.getServerUrl(), layerInteraction.getConnectionType());
			decision = layerInteraction.makeDecision(localProfile, remoteProfile);
			Log.i(clsName, "Camada de interacao tomou decisao.");
		}
		else
			Log.i(clsName, "Camada de estimulo tomou decisao.");
		
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
