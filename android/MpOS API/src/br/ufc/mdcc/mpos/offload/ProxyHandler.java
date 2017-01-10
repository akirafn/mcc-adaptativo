/*******************************************************************************
 * Copyright (C) 2014 Philipp B. Costa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.ufc.mdcc.mpos.offload;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import br.ufc.mdcc.mpos.MposFramework;
import br.ufc.mdcc.mpos.net.endpoint.ServerContent;
import br.ufc.mdcc.mpos.net.rpc.ResponseRemotable;
import br.ufc.mdcc.mpos.net.rpc.RpcClient;
import br.ufc.mdcc.mpos.net.rpc.model.RpcProfile;
import br.ufc.mdcc.mpos.net.rpc.util.RpcException;
import br.ufc.mdcc.mpos.net.rpc.util.RpcSerializable;
import br.ufscar.mcc.offload.DecisionController;
import br.ufscar.mcc.offload.DecisionFlag;

/**
 * Proxy Handler, decides execution with is local or remote using rpc!
 * 
 * @author Philipp B. Costa
 */
public final class ProxyHandler implements InvocationHandler {
	private static final String clsName = ProxyHandler.class.getName();
	private final Map<String, Remotable> methodCache;
	private final RpcClient rpc;

	private Object objOriginal;
	private boolean manualSerialization = false;
	
	private ProxyHandler(Object objProxy, Class<?> interfaceType) {
		this.objOriginal = objProxy;

		manualSerialization = objProxy instanceof RpcSerializable;
		rpc = new RpcClient();

		methodCache = new HashMap<String, Remotable>(5);
		buildMethodCache(interfaceType);
	}

	public static Object newInstance(Class<?> cls, Class<?> interfaceType) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		Log.i(clsName, "Creating proxy with interface: " + interfaceType.getSimpleName() + ", for class: " + cls.getSimpleName());

		Object objectInstance = Class.forName(cls.getName()).newInstance();
		return Proxy.newProxyInstance(cls.getClassLoader(), new Class<?>[] { interfaceType }, new ProxyHandler(objectInstance, interfaceType));
	}

	@Override
	public Object invoke(Object original, Method method, Object[] params) throws Throwable{
		Remotable remotable = methodCache.get(generateKeyMethod(method));
		DecisionFlag decision = DecisionFlag.NoDecision;
		Object returnObject = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		int paramSize;
		
		if(remotable != null)
		{
			DecisionController decisionController = MposFramework.getInstance().getDecisionController();
			ServerContent server = MposFramework.getInstance().getEndpointController().selectPriorityServer(remotable.cloudletPrority());
			
			Log.i(clsName, "Realizar chamada de tomada de decisão auto-adaptativa.");
			
			oos.writeObject(params);
			oos.flush();

			paramSize = baos.toByteArray().length;
			
			if(server != null){
				try{

					
					decision = decisionController.makeDecision(method, paramSize);
					if(decision == DecisionFlag.GoOffload || decision == DecisionFlag.ForcedOffload){
						returnObject = invokeRemotable(server, decision == DecisionFlag.ForcedOffload, method, params, paramSize);
					}
				}
				catch(ConnectException ce){
					Log.w(clsName, ce);
					MposFramework.getInstance().getEndpointController().rediscoveryServices(server);
					returnObject = null;
				}
				catch(RpcException re){
					Log.w(clsName, re);
					returnObject = null;
				}
				catch(Exception ex)
				{
					Log.w(clsName, ex);
					returnObject = null;
				}
			}
			
			//not remotable avaliable and need debug, get cpu time
			//forced local execution. needs to write local execution profile
			if (decision == DecisionFlag.GoLocal || decision == DecisionFlag.ForcedLocal) {
				long initCpu = System.currentTimeMillis();
				returnObject = method.invoke(objOriginal, params);
				long localExecutionTime = System.currentTimeMillis() - initCpu;
				
				MposFramework.getInstance().getEndpointController().rpcProfile.setExecutionCpuTimeLocal(localExecutionTime);
				Log.i(clsName, MposFramework.getInstance().getEndpointController().rpcProfile.toString());										

				decisionController.setLocalExecutionProfile(method, paramSize, returnObject, localExecutionTime);
			}
		}
		
		if(returnObject == null)
			returnObject = method.invoke(objOriginal, params);
		
		return returnObject;
	}

	private Object invokeRemotable(ServerContent server, boolean needProfile, Method method, Object params[], int paramSize) throws RpcException, ConnectException {
		rpc.setupServer(server);
		long initCpu = System.currentTimeMillis();
		ResponseRemotable response = rpc.call(needProfile, manualSerialization, objOriginal, method.getName(), params);
		
		if (needProfile) {
			RpcProfile profile = rpc.getProfile();
			Log.i(clsName, profile.toString());
			MposFramework.getInstance().getEndpointController().rpcProfile = profile;
		}
		long remoteTime = System.currentTimeMillis() - initCpu;

		if (response != null) {
			MposFramework.getInstance().getDecisionController().setRemoteExecutionProfile(method, paramSize, response.methodReturn, response.executionTime, remoteTime);

			return response.methodReturn;
		} else {
			throw new RpcException("Method (failed): " + method.getName() + ", return 'null' value from remotable method");
		}
	}

	private String generateKeyMethod(Method method) {
		StringBuilder key = new StringBuilder();
		key.append(method.getName()).append("-");
		for (Class<?> cls : method.getParameterTypes()) {
			key.append(":").append(cls.getName());
		}

		return key.toString();
	}

	private void buildMethodCache(Class<?> interfaceType) {
		Method methods[] = interfaceType.getDeclaredMethods();
		for (Method method : methods) {
			Remotable remotable = method.getAnnotation(Remotable.class);
			if (remotable != null) {
				methodCache.put(generateKeyMethod(method), remotable);
			}
		}
	}
}