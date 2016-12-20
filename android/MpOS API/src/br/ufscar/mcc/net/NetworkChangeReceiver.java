package br.ufscar.mcc.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import br.ufc.mdcc.mpos.MposFramework;

public class NetworkChangeReceiver extends BroadcastReceiver {

	public NetworkChangeReceiver() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		MposFramework.getInstance().getDecisionController().setConnectionType();
	}

}
