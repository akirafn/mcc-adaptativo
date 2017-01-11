package br.ufscar.mcc.model;

import android.util.Log;

public class FunctionProfile {
	private int functionId;
	private int methodId;
	private String methodName;
	private int methodCount;
	private int methodHash;
	private int methodMin;
	private int methodMax;
	private String serverUrl;
	private ConnectionType connType;
	private int sumX;
	private int sumY;
	private int sumXY;
	private int sumSqrX;
	private double factorA;
	private double factorB;
	private String clsName = FunctionProfile.class.getName();

	public FunctionProfile() {
		functionId = 0;
		methodId = 0;
		methodName = "";
		methodCount = 0;
		methodMin = 0;
		methodMax = 0;
		serverUrl = "";
		sumX = 0;
		sumSqrX = 0;
		sumY = 0;
		sumXY = 0;
		factorA = 0.0;
		factorB = 0.0;
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

	public int getMethodMin() {
		return methodMin;
	}

	public void setMethodMin(int methodMin) {
		this.methodMin = methodMin;
	}

	public int getMethodMax() {
		return methodMax;
	}

	public void setMethodMax(int methodMax) {
		this.methodMax = methodMax;
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

	public void updateValues(int valueX, int valueY) {
		int upSide, downSide;

		if (valueX < methodMin)
			methodMin = valueX;
		if (valueX > methodMax)
			methodMax = valueX;

		// Atualização das somatórias a serem utilizadas no cálculo dos fatores
		sumX += valueX;
		sumSqrX += valueX * valueX;
		sumY += valueY;
		sumXY += valueX * valueY;
		methodCount += 1;

		// Cálculo dos fatores para função seguindo método dos mínimos quadrados
		downSide = (methodCount * sumXY) - (sumX * sumY);
		upSide = (methodCount * sumSqrX) - (sumX * sumX);
		if (upSide == downSide)
			factorA = 1.0;
		else if (downSide == 0)
			factorA = 1000.0;
		else
			factorA = (double)upSide / (double)downSide;

		factorB = (double)sumY - ((double)sumX / factorA);
		factorB /= (double)methodCount;

		Log.i(clsName, "X: " + sumX + ", X^2: " + sumSqrX + ", XY: " + sumXY + ", Y: " + sumY + ", A: " + factorA
				+ ", B: " + factorB + ", nro de chamadas: " + methodCount);
	}
}
