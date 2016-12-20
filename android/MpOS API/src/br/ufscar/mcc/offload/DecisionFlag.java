package br.ufscar.mcc.offload;

/**/
/* Código de mensagens a pensar: */
/* 2 - Nunca foi executado remotamente: mandar para offloading */
/* 1 - Local demora mais: mandar para offloading */
/* 0 - Nada a dizer */
/* -1 - Offloading demora mais: manter local */
/* -2 - Nunca foi executado localmente: manter local */
/**/
public enum DecisionFlag {
	ForcedOffload(2), ForcedLocal(-2),  GoOffload(1), GoLocal(-1), NoDecision(0);
	private int value;
	
	private DecisionFlag(int value){
		this.value = value;
	}
	
	int getValue(){
		return this.value;
	}
};
