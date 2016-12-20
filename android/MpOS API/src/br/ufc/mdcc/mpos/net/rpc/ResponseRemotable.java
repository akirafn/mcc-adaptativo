package br.ufc.mdcc.mpos.net.rpc;

public final class ResponseRemotable {
	public int code;
	public Object methodReturn;
	public String except;
	public int sendTime;
	public int receiveTime;
	public long executionTime;
}
