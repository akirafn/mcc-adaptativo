package br.ufscar.mcc.offload;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import br.ufc.mdcc.mpos.persistence.MethodExecutionDao;
import br.ufscar.mcc.history.model.ConnectionType;
import br.ufscar.mcc.history.model.MethodExecutionProfile;

public class Layer03Time {
	private String serverUrl;
	private Context context;
	private MethodExecutionDao methodDao;
	private ConnectionType connType;
	private final String clsName = Layer03Time.class.getName();

	public Layer03Time(Context context, String serverUrl, int delay) {
		this.serverUrl = serverUrl;
		this.context = context;
		this.methodDao = new MethodExecutionDao(context);
	}

	//----------------------
	// Getters and Setters
	//----------------------
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public void setConnectionType() {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

		if (activeNetwork != null) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				this.connType = ConnectionType.CONN_WiFi;
			else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				this.connType = ConnectionType.CONN_3G;
			else
				this.connType = ConnectionType.CONN_LOCAL;
		} else {
			this.connType = ConnectionType.CONN_LOCAL;
		}
	}
	
	public ConnectionType getConnectionType(){
		return this.connType;
	}

	// --------------------------------
	// Methods for data manipulation
	// --------------------------------
	public MethodExecutionProfile getMethodProfileByName(Method method, int inputSize) {
		MethodExecutionProfile methodProfile;
		try {
			Log.i(clsName, "Consultar metodo " + method.getName() + " com argumento " + String.valueOf(inputSize)
					+ " e servidor " + serverUrl);
			methodProfile = methodDao.getMethodProfileByName(method.getName(), method.getClass().getName(), inputSize,
					serverUrl);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			methodProfile = new MethodExecutionProfile();
		}
		Log.i(clsName, "Encontrado metodo " + methodProfile.getMethodName() + " com argumento "
				+ methodProfile.getInputSize() + " e servidor " + methodProfile.getServerUrl());
		return methodProfile;
	}
	
	public void setLocalExecutionProfile(Method method, int inputSize, int outputSize, int localTime) {
		MethodExecutionProfile methodProfile = new MethodExecutionProfile();
		methodProfile.setMethodName(method.getName());
		methodProfile.setClassName(method.getClass().getName());
		methodProfile.setInputSize(inputSize);
		methodProfile.setOutputSize(outputSize);
		methodProfile.setLocalTime(localTime);
		methodProfile.generateProfileHash();

		try {
			methodDao.insertLocalExecution(methodProfile);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
		}
	}

	public void setRemoteExecutionProfile(int methodId, int remoteTime) {
		MethodExecutionProfile methodProfile = new MethodExecutionProfile();
		methodProfile.setProfileId(methodId);
		methodProfile.setServerUrl(serverUrl);
		methodProfile.setRemoteTime(remoteTime);

		try {
			methodDao.insertRemoteExecution(methodProfile);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
		}
	}
	
	public void setLogProfile(int methodId, int remoteId){
		
	}

	// --------------------------------
	// Decision Taking Management
	// --------------------------------
	public DecisionFlag makeDecision(String methodName, String className, int inputSize) {
		DecisionFlag decision = DecisionFlag.NoDecision;

		return decision;
	}
}
