package br.ufscar.mcc.offload;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.ExecutionProfile;

public class Layer02Interaction {
	private Context context;
	private String serverUrl;
	private double downRate;
	private double upRate;
	private ConnectionType connType;

	public Layer02Interaction(Context context) {
		this.serverUrl = "";
		this.downRate = 0;
		this.upRate = 0;
		this.context = context;
		this.connType = ConnectionType.CONN_LOCAL;
	}

	// Getters and Setters
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServerUrl(){
		return this.serverUrl;
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

	public ConnectionType getConnectionType() {
		return this.connType;
	}
	
	public void setThroughputRate(String upRate, String downRate) {
		this.upRate = Double.parseDouble(upRate) * 1000000;
		this.downRate = Double.parseDouble(downRate) * 1000000;
	}
	
	public DecisionFlag makeDecision(ExecutionProfile localProfile, ExecutionProfile remoteProfile){
		DecisionFlag decision = DecisionFlag.NoDecision;
		double offloadTime = 0.0;
		double differTime = 0.0;
		
		if(localProfile.getExecutionId() != 0){
			if(remoteProfile.getExecutionId() != 0){
				offloadTime = (upRate * (double)remoteProfile.getInputSize()) + (downRate * (double)remoteProfile.getOutputSize()) + (double)remoteProfile.getExecutionTime();
				differTime = (double)localProfile.getExecutionTime() - offloadTime;
				
				if(differTime > 0.0)
					decision = DecisionFlag.GoOffload;
				else
					decision = DecisionFlag.GoLocal;
			}
			else
				decision = DecisionFlag.ForcedOffload;
		}
		else
			decision = DecisionFlag.ForcedLocal;
		
		return decision;
	}
}
