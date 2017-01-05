package br.ufc.mdcc.mpos.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import br.ufc.mdcc.mpos.R;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.ExecutionProfile;

public class ExecutionDao extends Dao {
	private String TABLE_METHOD;
	private String TABLE_LOCALEXECUTION;
	private String TABLE_REMOTEEXECUTION;
	private String M_ID = "methodid";
	private String E_ID = "executionid";
	private String E_INPUTSIZE = "inputsize";
	private String E_OUTPUTSIZE = "outputsize";
	private String E_SERVERURL = "serverurl";
	private String E_CONNTYPE = "conntype";
	private String E_EXECUTIONDATE = "EXECDATE";
	private String E_EXECUTIONTIME = "executiontime";
	private String E_EXECUTIONDECISION = "executiondecision";
	private final String clsname = ExecutionDao.class.getName();

	public ExecutionDao(Context con) {
		super(con);
		TABLE_METHOD = con.getString(R.string.name_table_methoddata);
		TABLE_LOCALEXECUTION = con.getString(R.string.name_table_localexecutiondata);
		TABLE_REMOTEEXECUTION = con.getString(R.string.name_table_remoteexecutiondata);
	}

	public void insertExecutionProfile(ExecutionProfile profile) {
		openDatabase();

		ContentValues cv = new ContentValues();
		cv.put(M_ID, profile.getMethodId());
		cv.put(E_INPUTSIZE, profile.getInputSize());
		cv.put(E_OUTPUTSIZE, profile.getOutputSize());
		cv.put(E_SERVERURL, profile.getServerUrl());
		cv.put(E_CONNTYPE, profile.getConnType().getValue());
		cv.put(E_EXECUTIONTIME, profile.getExecutionTime());
		cv.put(E_EXECUTIONDECISION, profile.getExecutionDecision());

		try {
			if(profile.getExecutionDecision() == 1)
				database.insert(TABLE_REMOTEEXECUTION, null, cv);
			else
				database.insert(TABLE_LOCALEXECUTION, null, cv);
		} catch (SQLException ex) {
			Log.e(clsname, ex.getMessage());
		}

		closeDatabase();
	}

	public ExecutionProfile getRemoteExecutionByMethodInputLocalConn(String methodName, String className, int inputSize,
			String serverUrl, ConnectionType connType) {
		String sql = "SELECT E.EXECUTIONID, E.METHODID, E.INPUTSIZE, E.OUTPUTSIZE, E.SERVERURL, E.CONNTYPE, STRFTIME('%Y-%m-%d %H:%M:%S', E.EXECUTIONDATE) AS EXECDATE, E.EXECUTIONTIME, E.EXECUTIONDECISION FROM "
				+ TABLE_REMOTEEXECUTION + " E JOIN " + TABLE_METHOD
				+ " M ON M.METHODID = E.METHODID WHERE M.METHODNAME LIKE '%" + methodName + "%' AND M.CLASSNAME LIKE '%"
				+ className + "%' AND E.INPUTSIZE = " + inputSize + " AND E.SERVERURL LIKE '%" + serverUrl
				+ "%' AND E.CONNTYPE = " + connType.getValue() + " ORDER BY E.EXECUTIONDATE DESC LIMIT 1";

		return getResult(sql);
	}

	public ExecutionProfile getRemoteExecutionByMethodInputConn(String methodName, String className, int inputSize,
			ConnectionType connType) {
		String sql = "SELECT E.EXECUTIONID, E.METHODID, E.INPUTSIZE, E.OUTPUTSIZE, E.SERVERURL, E.CONNTYPE, STRFTIME('%Y-%m-%d %H:%M:%S', E.EXECUTIONDATE) AS EXECDATE, E.EXECUTIONTIME, E.EXECUTIONDECISION FROM "
				+ TABLE_REMOTEEXECUTION + " E JOIN " + TABLE_METHOD
				+ " M ON M.METHODID = E.METHODID WHERE M.METHODNAME LIKE '%" + methodName + "%' AND M.CLASSNAME LIKE '%"
				+ className + "%' AND E.INPUTSIZE = " + inputSize + " AND E.CONNTYPE = " + connType.getValue()
				+ " ORDER BY E.EXECUTIONDATE DESC LIMIT 1";

		return getResult(sql);
	}
	
	public ExecutionProfile getLocalExecutionByMethodInput(String methodName, String className, int inputSize) {
		String sql = "SELECT E.EXECUTIONID, E.METHODID, E.INPUTSIZE, E.OUTPUTSIZE, E.SERVERURL, E.CONNTYPE, STRFTIME('%Y-%m-%d %H:%M:%S', E.EXECUTIONDATE) AS EXECDATE, E.EXECUTIONTIME, E.EXECUTIONDECISION FROM "
				+ TABLE_LOCALEXECUTION + " E JOIN " + TABLE_METHOD
				+ " M ON M.METHODID = E.METHODID WHERE M.METHODNAME LIKE '%" + methodName + "%' AND M.CLASSNAME LIKE '%"
				+ className + "%' AND E.INPUTSIZE = " + inputSize + " ORDER BY E.EXECUTIONDATE DESC LIMIT 1";

		return getResult(sql);
	}

	private ExecutionProfile getResult(String sql) {
		openDatabase();
		Cursor cursor = database.rawQuery(sql, null);

		ExecutionProfile profile = new ExecutionProfile();
		
		Log.i(clsname, "Consulta '"+sql+"' realizada com "+cursor.getCount()+" resultados e "+cursor.getColumnCount()+" colunas");

		// obtem todos os indices das colunas da tabela
		int idx_executionid = cursor.getColumnIndex(E_ID);
		Log.i(clsname, "Lido Execution ID");		
		int idx_methodid = cursor.getColumnIndex(M_ID);
		Log.i(clsname, "Lido Method ID");
		int idx_inputsize = cursor.getColumnIndex(E_INPUTSIZE);
		Log.i(clsname, "Lido Input Size");
		int idx_outputsize = cursor.getColumnIndex(E_OUTPUTSIZE);
		Log.i(clsname, "Lido Output Size");
		int idx_serverurl = cursor.getColumnIndex(E_SERVERURL);
		Log.i(clsname, "Lido "+E_SERVERURL+" com indice "+idx_serverurl);
		int idx_conntype = cursor.getColumnIndex(E_CONNTYPE);
		Log.i(clsname, "Lido "+E_CONNTYPE+" com indice "+idx_conntype);
		int idx_executiondate = cursor.getColumnIndex(E_EXECUTIONDATE);
		Log.i(clsname, "Lido "+E_EXECUTIONDATE+" com indice "+idx_executiondate);
		Log.i(clsname, "Lido coluna de nome "+cursor.getColumnName(6)+" para "+E_EXECUTIONDATE);
		int idx_executiontime = cursor.getColumnIndex(E_EXECUTIONTIME);
		Log.i(clsname, "Lido "+E_EXECUTIONTIME+" com indice "+idx_executiontime);
		int idx_executiondecision = cursor.getColumnIndex(E_EXECUTIONDECISION);
		Log.i(clsname, "Lido "+E_EXECUTIONDECISION+" com indice "+idx_executiondecision );

		if (cursor != null && cursor.moveToFirst()) {
			profile.setExecutionId(cursor.getInt(idx_executionid));
			profile.setMethodId(cursor.getInt(idx_methodid));
			profile.setInputSize(cursor.getInt(idx_inputsize));
			profile.setOutputSize(cursor.getInt(idx_outputsize));
			profile.setServerUrl(cursor.getString(idx_serverurl));
			profile.setConnType(cursor.getInt(idx_conntype));
			profile.setExecutionDate(cursor.getString(idx_executiondate));
			profile.setExecutionTime(cursor.getInt(idx_executiontime));
			profile.setExecutionDecision(cursor.getInt(idx_executiondecision));
		}

		cursor.close();
		closeDatabase();

		return profile;
	}

	public void clean() {
		openDatabase();

		database.delete(TABLE_LOCALEXECUTION, null, null);
		database.delete(TABLE_REMOTEEXECUTION, null, null);

		closeDatabase();
	}
}
