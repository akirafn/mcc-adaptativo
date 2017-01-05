package br.ufscar.mcc.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;
import br.ufscar.mcc.offload.DecisionFlag;

public class ExecutionProfile {

	private int executionId;
	private int methodId;
	private int inputSize;
	private int outputSize;
	private String serverUrl;
	private ConnectionType connType;
	private Date executionDate;
	private int executionTime;
	private int executionDecision;
	private String clsName = ExecutionProfile.class.getName();

	public ExecutionProfile() {
		executionId = 0;
		methodId = 0;
		connType = ConnectionType.CONN_LOCAL;
		executionDate = new Date();
		executionTime = -1;
		executionDecision = 0;
	}

	public int getExecutionId() {
		return executionId;
	}

	public void setExecutionId(int executionId) {
		this.executionId = executionId;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	public int getInputSize() {
		return inputSize;
	}

	public void setInputSize(int inputSize) {
		this.inputSize = inputSize;
	}

	public int getOutputSize() {
		return outputSize;
	}

	public void setOutputSize(int outputSize) {
		this.outputSize = outputSize;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public ConnectionType getConnType() {
		return connType;
	}

	public void setConnType(ConnectionType connType) {
		this.connType = connType;
	}

	public void setConnType(int connType) {
		if (connType == ConnectionType.CONN_WiFi.getValue())
			this.connType = ConnectionType.CONN_WiFi;
		else if (connType == ConnectionType.CONN_3G.getValue())
			this.connType = ConnectionType.CONN_3G;
		else
			this.connType = ConnectionType.CONN_LOCAL;
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	public void setExecutionDate(Date executionDate) {
		this.executionDate = executionDate;
	}

	public void setExecutionDate(String executionDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			this.executionDate = sdf.parse(executionDate);
		}
		catch(ParseException ex){
			Log.e(clsName, ex.getMessage());
		}
	}

	public int getExecutionTime() {
		return executionTime;
	}

	public void setExecutionTime(int executionTime) {
		this.executionTime = executionTime;
	}

	public int getExecutionDecision() {
		return executionDecision;
	}

	public void setExecutionDecision(int executionDecision) {
		this.executionDecision = executionDecision;
	}
	
	public void setExecutionDecision(DecisionFlag decision){
		if(decision == DecisionFlag.ForcedOffload || decision == DecisionFlag.GoOffload)
			this.executionDecision = 1;
		else
			this.executionDecision = 0;
	}
}
