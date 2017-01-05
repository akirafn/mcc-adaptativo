package br.ufscar.mcc.offload;

import java.lang.reflect.Method;
import java.util.HashMap;

import android.content.Context;
import android.util.Log;
import br.ufc.mdcc.mpos.persistence.ExecutionDao;
import br.ufc.mdcc.mpos.persistence.FunctionDao;
import br.ufc.mdcc.mpos.persistence.MethodDao;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.ExecutionProfile;
import br.ufscar.mcc.model.FunctionProfile;
import br.ufscar.mcc.model.MethodProfile;

public class Layer03Time {
	private MethodDao methodDao;
	private ExecutionDao executionDao;
	private FunctionDao functionDao;
	private final String clsName = Layer03Time.class.getName();

	public Layer03Time(Context context, int delay) {
		this.methodDao = new MethodDao(context);
		this.executionDao = new ExecutionDao(context);
	}

	// --------------------------------
	// Methods for data manipulation
	// --------------------------------
	public MethodProfile getMethodProfileByName(Method method) {
		MethodProfile profile;
		try {
			profile = methodDao.getMethodProfle(method.getName(), method.getClass().getName());
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			profile = new MethodProfile();
		}
		return profile;
	}

	public void setMethodProfile(Method method, int methodCount) {
		MethodProfile profile = new MethodProfile();
		profile.setMethodName(method.getName());
		profile.setClassName(method.getClass().getName());
		profile.setMethodCount(methodCount);

		try {
			methodDao.insertMethodProfile(profile);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
		}
	}

	public ExecutionProfile getRemoteExecutionProfileByServerConn(Method method, int inputSize, String serverUrl,
			ConnectionType connType) {
		ExecutionProfile profile;
		try {
			profile = executionDao.getRemoteExecutionByMethodInputLocalConn(method.getName(),
					method.getClass().getName(), inputSize, serverUrl, connType);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			profile = new ExecutionProfile();
		}

		return profile;
	}

	public ExecutionProfile getRemoteExecutionProfileByConn(Method method, int inputSize, ConnectionType connType) {
		ExecutionProfile profile;
		try {
			profile = executionDao.getRemoteExecutionByMethodInputConn(method.getName(), method.getClass().getName(),
					inputSize, connType);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			profile = new ExecutionProfile();
		}

		return profile;
	}

	public ExecutionProfile getLocalExecutionProfileByInput(Method method, int inputSize) {
		ExecutionProfile profile;
		try {
			profile = executionDao.getLocalExecutionByMethodInput(method.getName(), method.getClass().getName(),
					inputSize);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			profile = new ExecutionProfile();
		}

		return profile;
	}

	public void setExecutionProfile(Method method, int inputSize, int outputSize, int executionTime, String serverUrl,
			ConnectionType connType, DecisionFlag decision) {
		MethodProfile mtProfile;
		ExecutionProfile exProfile = new ExecutionProfile();

		exProfile.setInputSize(inputSize);
		exProfile.setOutputSize(outputSize);
		exProfile.setServerUrl(serverUrl);
		exProfile.setConnType(connType);
		exProfile.setExecutionTime(executionTime);
		exProfile.setExecutionDecision(decision);

		try {
			mtProfile = methodDao.getMethodProfle(method.getName(), method.getClass().getName());
			if (mtProfile.getMethodId() == 0) {
				mtProfile.setMethodName(method.getName());
				mtProfile.setClassName(method.getClass().getName());
				mtProfile.setMethodCount(1);
				methodDao.insertMethodProfile(mtProfile);
				Log.i(clsName,
						"Inserido método " + mtProfile.getMethodName() + " da classe " + mtProfile.getClassName());
				mtProfile = methodDao.getMethodProfle(method.getName(), method.getClass().getName());
			}
			exProfile.setMethodId(mtProfile.getMethodId());
			Log.i(clsName, "Inserida execucao referente ao metodo " + exProfile.getMethodId());

			executionDao.insertExecutionProfile(exProfile);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
		}
	}

	public HashMap<Integer, FunctionProfile> getFunctionMap(String serverUrl, ConnectionType connType) {
		HashMap<Integer, FunctionProfile> mapa;

		try {
			mapa = functionDao.getFunctionMapByServerConn(serverUrl, connType);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
			mapa = new HashMap<Integer, FunctionProfile>();
		}

		return mapa;
	}

	public void updateFunctionMap(HashMap<Integer, FunctionProfile> mapa) {
		try {
			functionDao.updateFunctionProfileList(mapa);
		} catch (Exception ex) {
			Log.e(clsName, ex.getMessage());
		}
	}
}
