package br.ufscar.mcc.history.model;

public enum ConnectionType {
	CONN_3G(2),
	CONN_WiFi(1),
	CONN_LOCAL(0);
	
	private int value;
	
	private ConnectionType(int value){
		this.value = value;
	}
	
	public int getValue(){
		return value;
	}
}
