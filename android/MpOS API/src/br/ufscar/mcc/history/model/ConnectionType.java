package br.ufscar.mcc.history.model;

public enum ConnectionType {
	CONN_3G("3G"),
	CONN_WiFi("WIFI"),
	CONN_LOCAL("Local");
	
	private final String text;
	
	private ConnectionType(String text){
		this.text = text;
	}
	
	@Override
	public String toString(){
		return text;
	}
}
