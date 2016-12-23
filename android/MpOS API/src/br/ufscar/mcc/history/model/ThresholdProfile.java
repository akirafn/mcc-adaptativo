package br.ufscar.mcc.history.model;

public class ThresholdProfile {

	private int thresholdId;
	private int methodId;
	private String methodName;
	private int methodHash;
	private int methodCount;
	private String serverUrl;
	private ConnectionType connType;
	private int unfavMin;
	private int unfavMed;
	private int unfavMax;
	private int favMin;
	private int favMed;
	private int favMax;

	public ThresholdProfile() {
		thresholdId = 0;
		methodId = 0;
		methodHash = -1;
		methodCount = 0;
		connType = ConnectionType.CONN_LOCAL;
		unfavMin = 0;
		unfavMed = 0;
		unfavMax = 0;
		favMin = 0;
		favMed = 0;
		favMax = 0;
	}

	public int getThresholdId() {
		return thresholdId;
	}

	public void setThresholdId(int thresholdId) {
		this.thresholdId = thresholdId;
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

	public int getMethodHash() {
		return methodHash;
	}

	public void setMethodHash(int methodHash) {
		this.methodHash = methodHash;
	}

	public int getMethodCount() {
		return methodCount;
	}

	public void setMethodCount(int methodCount) {
		this.methodCount = methodCount;
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

	public int getUnfavMin() {
		return unfavMin;
	}

	public void setUnfavMin(int unfavMin) {
		this.unfavMin = unfavMin;
	}

	public int getUnfavMed() {
		return unfavMed;
	}

	public void setUnfavMed(int unfavMed) {
		this.unfavMed = unfavMed;
	}

	public int getUnfavMax() {
		return unfavMax;
	}

	public void setUnfavMax(int unfavMax) {
		this.unfavMax = unfavMax;
	}

	public int getFavMin() {
		return favMin;
	}

	public void setFavMin(int favMin) {
		this.favMin = favMin;
	}

	public int getFavMed() {
		return favMed;
	}

	public void setFavMed(int favMed) {
		this.favMed = favMed;
	}

	public int getFavMax() {
		return favMax;
	}

	public void setFavMax(int favMax) {
		this.favMax = favMax;
	}
}
