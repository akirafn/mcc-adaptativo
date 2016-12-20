package br.ufscar.mcc.history.model;

public class MethodExecutionProfile {
	private int profileId;
	private String methodName;
	private String className;
	private int inputSize;
	private int outputSize;
	private int profileHash;
	private int localTime;
	private int remoteId;
	private String serverUrl;
	private int remoteTime;
	
	public MethodExecutionProfile() {
		profileId = 0;
		inputSize = 0;
		outputSize = 0;
		profileHash = 0;
		localTime = 0;
		remoteId = 0;
		remoteTime = 0;
	}

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
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

	public int getProfileHash() {
		return profileHash;
	}

	public void setProfileHash(int profileHash) {
		this.profileHash = profileHash;
	}

	public int getLocalTime() {
		return localTime;
	}

	public void setLocalTime(int localTime) {
		this.localTime = localTime;
	}

	public int getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}

	public String getServerUrl() {
		return serverUrl;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public int getRemoteTime() {
		return remoteTime;
	}

	public void setRemoteTime(int remoteTime) {
		this.remoteTime = remoteTime;
	}

	public void generateProfileHash(){
		final int prime = 59;
		profileHash = 1;
		profileHash = prime * profileHash + ((className == null) ? 0 : className.hashCode());
		profileHash = prime * profileHash + inputSize;
		profileHash = prime * profileHash + ((methodName == null) ? 0 : methodName.hashCode());
	}	
}
