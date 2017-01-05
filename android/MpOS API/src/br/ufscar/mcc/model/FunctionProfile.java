package br.ufscar.mcc.model;

public class FunctionProfile {
	private int functionId;
	private int methodId;
	private String methodName;
	private int methodCount;
	private int methodHash;
	private String serverUrl;
	private ConnectionType connType;
	private int sumX;
	private int sumY;
	private int sumXY;
	private int sumSqrX;
	private double factorA;
	private double factorB;

	public FunctionProfile() {
		functionId = 0;
		methodId = 0;
		methodName = "";
		serverUrl = "";
	}

	public int getFunctionId() {
		return functionId;
	}

	public void setFunctionId(int functionId) {
		this.functionId = functionId;
	}

	public int getMethodId() {
		return methodId;
	}

	public void setMethodId(int methodId) {
		this.methodId = methodId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public int getMethodCount() {
		return methodCount;
	}

	public void setMethodCount(int methodCount) {
		this.methodCount = methodCount;
	}

	public int getMethodHash() {
		return methodHash;
	}

	public void setMethodHash(int methodHash) {
		this.methodHash = methodHash;
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

	public int getSumX() {
		return sumX;
	}

	public void setSumX(int sumX) {
		this.sumX = sumX;
	}

	public int getSumY() {
		return sumY;
	}

	public void setSumY(int sumY) {
		this.sumY = sumY;
	}

	public int getSumXY() {
		return sumXY;
	}

	public void setSumXY(int sumXY) {
		this.sumXY = sumXY;
	}

	public int getSumSqrX() {
		return sumSqrX;
	}

	public void setSumSqrX(int sumSqrX) {
		this.sumSqrX = sumSqrX;
	}

	public double getFactorA() {
		return factorA;
	}

	public void setFactorA(double factorA) {
		this.factorA = factorA;
	}

	public double getFactorB() {
		return factorB;
	}

	public void setFactorB(double factorB) {
		this.factorB = factorB;
	}
}
