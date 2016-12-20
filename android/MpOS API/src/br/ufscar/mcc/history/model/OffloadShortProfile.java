package br.ufscar.mcc.history.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class OffloadShortProfile {
	private int profileHash;
	private double profileDecision;
	private int profileCount;
	private Date profileDate;
	private String clsName = OffloadLogProfile.class.getName();

	public OffloadShortProfile() {
		profileHash = -1;
		profileDecision = 0.0;
		profileCount = 0;
		profileDate = new Date();
	}

	public int getProfileHash() {
		return profileHash;
	}

	public void setProfileHash(int profileHash) {
		this.profileHash = profileHash;
	}

	public double getProfileDecision() {
		return profileDecision;
	}

	public void setProfileDecision(double profileDecision) {
		this.profileDecision = profileDecision;
	}

	public int getProfileCount() {
		return profileCount;
	}

	public void setProfileCount(int profileCount) {
		this.profileCount = profileCount;
	}

	public Date getProfileDate() {
		return profileDate;
	}

	public void setProfileDate(Date profileDate) {
		this.profileDate = profileDate;
	}
	
	public void setProfileDate(String profileDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try{
			this.profileDate = sdf.parse(profileDate);
		}
		catch(ParseException ex){
			Log.e(clsName, ex.getMessage());
		}
	}
	
	public void countUp(){
		this.profileCount ++;
	}
}
