package br.ufscar.mcc.offload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;

import org.apache.http.conn.routing.RouteInfo.LayerType;

import android.content.Context;
import android.util.Log;
import br.ufscar.mcc.history.model.ConnectionType;
import br.ufscar.mcc.history.model.MethodExecutionProfile;

public class DecisionController {
	private static Layer02Interaction layerInteraction;
	private static Layer03Time layerTime;
	private static ConnectionType connType;
	private final String clsName = DecisionController.class.getName();

	public DecisionController(Context context) {
		layerInteraction = new Layer02Interaction(context, "", 0.0);
		layerTime = new Layer03Time(context, "", 0);
	}

	// ----------------------------
	// Getters e Setters
	// ----------------------------
	public void setServerUrl(String serverUrl) {
		layerInteraction.setServerUrl(serverUrl);
		layerTime.setServerUrl(serverUrl);
		connType = ConnectionType.CONN_LOCAL;
		Log.i(clsName, "Servidor remoto marcado: " + serverUrl);
	}
	
	public void setConnectionType(){
		layerTime.setConnectionType();
		this.connType = layerTime.getConnectionType();
	}

	public void setThroughputRate(String upRate, String downRate) {
		layerInteraction.setThroughputRate(upRate, downRate);
		Log.i(clsName, "Taxas atualizadas: down "+downRate+", up "+upRate);
	}

	public void setLocalExecutionProfile(Method method, Object[] params, Object returnValue, long localExecutionTime) {
		int paramSize = 0;
		int returnSize = 0;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(params);
			oos.flush();

			paramSize = dataPattern(baos.toByteArray().length);
			baos.reset();
			oos.reset();

			oos.writeObject(returnValue);
			oos.flush();
			oos.close();

			returnSize = dataPattern(baos.toByteArray().length);
			
			layerTime.setLocalExecutionProfile(method, paramSize, returnSize, (int) localExecutionTime);
			Log.i(clsName,
					"Gravada as informações: " + method.getName() + ", " + method.getClass().getName() + ", entrada: "
							+ Integer.toString(paramSize) + ", saida: " + Integer.toString(returnSize)
							+ ", processamento: " + Long.toString(localExecutionTime));
		} catch (IOException ioe) {
			Log.w(clsName, "Erro durante medicao dos argumentos");
		}
	}

	public void setRemoteExecutionProfile(Method method, Object[] params, long remoteExecutionTime) {
		int paramSize = 0;

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(params);
			oos.flush();
			oos.close();

			paramSize = dataPattern(baos.toByteArray().length);

			MethodExecutionProfile methodProfile = layerTime.getMethodProfileByName(method, paramSize);
			if (methodProfile.getProfileId() != 0) {
				layerTime.setRemoteExecutionProfile(methodProfile.getProfileId(), (int) remoteExecutionTime);
				Log.i(clsName,
						"Gravada as informações: " + method.getName() + ", " + method.getClass().getName()
								+ ", entrada: " + Integer.toString(paramSize) + ", processamento remoto: "
								+ Long.toString(remoteExecutionTime));
			} else {
				Log.i(clsName, "Informações de método não encontradas.");
			}

		} catch (IOException ioe) {
			Log.w(clsName, "Erro durante medicao dos argumentos");
		}

	}

	// --------------------------------
	// Decision Taking Management
	// --------------------------------
	public DecisionFlag makeDecision(Method method, Object[] params) {
		DecisionFlag decision = DecisionFlag.GoOffload;
		int paramSize = 0;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);

			oos.writeObject(params);
			oos.flush();
			oos.close();

			paramSize = dataPattern(baos.toByteArray().length);

			MethodExecutionProfile methodProfile = layerTime.getMethodProfileByName(method, paramSize);
			decision = layerInteraction.makeDecision(methodProfile);

		} catch (IOException ioe) {
			Log.w(clsName, "Erro durante medicao dos argumentos");
		}

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
		
		if(decision == DecisionFlag.NoDecision){
			decision = DecisionFlag.GoOffload;
			Log.i(clsName, "As none was taken, send to offload.");
		}

		return decision;
	}
	
	private int dataPattern(int dataSize){
		int size;
		
		if(dataSize > 1048576)
			size = dataSize - dataSize%1048576;
		else if(dataSize > 1024)
			size = dataSize - dataSize%1024;
		else
			size = dataSize;
		
		return size;
	}
}
