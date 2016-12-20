package br.ufscar.mcc.offload;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.content.Context;
import br.ufc.mdcc.mpos.persistence.LogDecisionDao;
import br.ufscar.mcc.history.model.OffloadShortProfile;

public class Layer01Stimulus {
	private OffloadShortProfile lastProfile;
	private HashMap<Integer, OffloadShortProfile> profileMap;
	private LogDecisionDao logDao;
	private String serverUrl;
	private int mapLimit = 15;
	private final String clsName = Layer03Time.class.getName();
	
	public Layer01Stimulus(Context context, String serverUrl){
		this.serverUrl = serverUrl;
		this.profileMap = new HashMap<Integer, OffloadShortProfile>();
		this.lastProfile = null;
	}
	
	public DecisionFlag makeDecision(String methodName, String className, int inputSize) {
		DecisionFlag decision = DecisionFlag.NoDecision;

		Integer chave = Integer.valueOf(generateProfileHash(methodName, className, inputSize));
		OffloadShortProfile shortProfile;
		
		if(profileMap.containsKey(chave)){
			shortProfile = profileMap.get(chave);
			if(shortProfile.getProfileDecision() > 0.6)
				decision = DecisionFlag.GoOffload;
			else if(shortProfile.getProfileDecision() < 0.4)
				decision = DecisionFlag.GoLocal;
		}
		
		return decision;
	}
	
	public int generateProfileHash(String methodName, String className, int inputSize){
		int prime = 59;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + inputSize;
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		
		return result;
	}	
	
	public void insertIntoTable(String methodName, String className, int inputSize, boolean offloadDecision){
		Integer chave = Integer.valueOf(generateProfileHash(methodName, className, inputSize));
		OffloadShortProfile shortProfile;

		if(profileMap.containsKey(chave)){
			shortProfile = profileMap.get(chave);
			shortProfile.countUp();
		}
		else if(profileMap.size() <= mapLimit){
			shortProfile = new OffloadShortProfile();
			shortProfile.setProfileCount(1);
			shortProfile.setProfileHash(chave.intValue());
			if(offloadDecision)
				shortProfile.setProfileDecision(1.0);
			else
				shortProfile.setProfileDecision(0.0);
			
			profileMap.put(chave, shortProfile);
		}
	}
	
	private void adjustTable(){
		if(profileMap.size() > mapLimit){
			profileMap.remove(lastProfile);
			lastProfile = null;
		}
		
		Set profileSet = profileMap.entrySet();
		Iterator profileIterator = profileSet.iterator();
		while(profileIterator.hasNext()){

		}
	}
}
