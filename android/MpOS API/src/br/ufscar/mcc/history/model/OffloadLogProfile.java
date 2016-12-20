package br.ufscar.mcc.history.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class OffloadLogProfile {
	private int logId;
	private int profileId;
	private int remoteId;
	private int profileHash;
	private ConnectionType connType;
	private Date decisionDate;
	private int executionTime;
	private int decision;
	private String clsName = OffloadLogProfile.class.getName();

	public OffloadLogProfile() {
		this.logId = 0;
		this.profileId = 0;
		this.remoteId = 0;
		this.profileHash = 0;
		this.executionTime = 0;
		this.decision = 0;
	}

	public int getLogId() {
		return logId;
	}

	public void setLogId(int logId) {
		this.logId = logId;
	}

	public int getProfileId() {
		return profileId;
	}

	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}

	public int getRemoteId() {
		return remoteId;
	}

	public void setRemoteId(int remoteId) {
		this.remoteId = remoteId;
	}
	
	public int getProfileHash() {
		return profileHash;
	}
	
	public void setProfileHash (int profileHash){
		this.profileHash = profileHash;
	}
	
	public String getConnType() {
		return connType.toString();
	}

	public void setConnType(ConnectionType connType) {
		this.connType = connType;
	}
	
	public void setConnType(String connType){
		if(connType.equals(ConnectionType.CONN_WiFi.toString()))
			this.connType = ConnectionType.CONN_WiFi;
		else if(connType.equals(ConnectionType.CONN_3G.toString()))
			this.connType = ConnectionType.CONN_3G;
		else
			this.connType = ConnectionType.CONN_LOCAL;
	}

	public Date getDecisionDate() {
		return decisionDate;
	}

	public void setDecisionDate(Date decisionDate) {
		this.decisionDate = decisionDate;
	}
	
	public void setDecisionDate(String decisionDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try{
			this.decisionDate = sdf.parse(decisionDate);
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

	public int getDecision() {
		return decision;
	}

	public void setDecision(int decision) {
		this.decision = decision;
	}
}
