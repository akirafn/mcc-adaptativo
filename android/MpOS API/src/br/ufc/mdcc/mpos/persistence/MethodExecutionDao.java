package br.ufc.mdcc.mpos.persistence;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import br.ufc.mdcc.mpos.R;
import br.ufscar.mcc.history.model.MethodExecutionProfile;

public class MethodExecutionDao extends Dao {
	private final String TABLE_LOCAL;
	private final String TABLE_REMOTE;
	private final String L_ID = "profileid";
	private final String L_METHODNAME = "methodname";
	private final String L_CLASSNAME = "classname";
	private final String L_INPUTSIZE = "inputsize";
	private final String L_OUTPUTSIZE = "outputsize";
	private final String L_PROFILEHASH = "profilehash";
	private final String L_LOCALTIME = "localprocesstime";
	private final String R_ID = "remoteid";
	private final String R_SERVERURL = "serverurl";
	private final String R_REMOTETIME = "remoteprocesstime";
	private final String clsName = MethodExecutionDao.class.getName();

	public MethodExecutionDao(Context con) {
		super(con);

		TABLE_LOCAL = con.getString(R.string.name_table_localexecution);
		TABLE_REMOTE = con.getString(R.string.name_table_remoteexecution);
	}

	public void insertLocalExecution(MethodExecutionProfile methodProfile) {
		if (methodProfile.getInputSize() > 0) {
			openDatabase();

			ContentValues cv = new ContentValues();
			cv.put(L_METHODNAME, methodProfile.getMethodName());
			cv.put(L_CLASSNAME, methodProfile.getClassName());
			cv.put(L_INPUTSIZE, methodProfile.getInputSize());
			cv.put(L_OUTPUTSIZE, methodProfile.getOutputSize());
			cv.put(L_PROFILEHASH, methodProfile.getProfileHash());
			cv.put(L_LOCALTIME, methodProfile.getLocalTime());

			//Log.i(clsName,
			//		"Tentar inserir metodo " + methodProfile.getMethodName() + ", classe: "
			//				+ methodProfile.getClassName() + ", com input " + methodProfile.getInputSize() + " e tempo "
			//				+ methodProfile.getLocalTime());

			try {
				database.insert(TABLE_LOCAL, null, cv);
			} catch (SQLException se) {
				Log.e(clsName, se.getMessage());
			}

			closeDatabase();
		}
	}

	public void insertRemoteExecution(MethodExecutionProfile methodProfile) {
		if (methodProfile.getProfileId() > 0 && methodProfile.getRemoteTime() > 0) {
			openDatabase();

			ContentValues cv = new ContentValues();
			cv.put(L_ID, methodProfile.getProfileId());
			cv.put(R_SERVERURL, methodProfile.getServerUrl());
			cv.put(R_REMOTETIME, methodProfile.getRemoteTime());

			database.insert(TABLE_REMOTE, null, cv);

			closeDatabase();
		}
	}

	public MethodExecutionProfile getMethodProfileByName(String methodName, String className, int inputSize,
			String serverUrl) {
		String sql = "SELECT L.PROFILEID, L.METHODNAME, L.CLASSNAME, L.INPUTSIZE, L.OUTPUTSIZE, L.PROFILEHASH, L.LOCALPROCESSTIME,"
				+ " R.REMOTEID, R.SERVERURL, R.REMOTEPROCESSTIME FROM " + TABLE_LOCAL + " L LEFT JOIN " + TABLE_REMOTE
				+ " R ON R.PROFILEID = L.PROFILEID WHERE L.METHODNAME LIKE '%" + methodName + "%' AND CLASSNAME LIKE '%"
				+ className + "%' AND L.INPUTSIZE = " + inputSize + " AND (R.SERVERURL LIKE '%" + serverUrl + "%' OR R.SERVERURL IS NULL) ORDER BY R.REMOTEID";
		return getResult(sql);
	}

	public ArrayList<MethodExecutionProfile> getBestProfileList(String serverUrl, int count)
			throws JSONException, ParseException {
		String sql = "SELECT L.PROFILEID, L.METHODNAME, L.CLASSNAME, L.INPUTSIZE, L.OUTPUTSIZE, L.PROFILEHASH, L.LOCALPROCESSTIME,"
				+ " R.REMOTEID, R.SERVERURL, R.REMOTEPROCESSTIME, (L.LOCALPROCESSTIME - R.REMOTEPROCESSTIME) AS DIFFERPROCESSTIME FROM "
				+ TABLE_LOCAL + " L LEFT JOIN " + TABLE_REMOTE + " R ON ON R.PROFILEID = L.PROFILEID"
				+ " WHERE R.SERVERURL LIKE '%" + serverUrl + "%' ORDER BY DIFFERPROCESSTIME DESC LIMIT " + count;
		return getLastResults(sql, count);
	}

	// ------------------------------------
	// Métodos privados de consulta básica
	// ------------------------------------
	private ArrayList<MethodExecutionProfile> getLastResults(String sql, int count)
			throws JSONException, ParseException {
		openDatabase();
		int length;

		Cursor cursor = database.rawQuery(sql, null);

		if (count < cursor.getColumnCount())
			length = count;
		else
			length = cursor.getColumnCount();

		ArrayList<MethodExecutionProfile> lista = new ArrayList<MethodExecutionProfile>(length);

		// obtem todos os indices das colunas da tabela
		int idx_lprofileid = cursor.getColumnIndex(L_ID);
		int idx_lmethodname = cursor.getColumnIndex(L_METHODNAME);
		int idx_lclassname = cursor.getColumnIndex(L_CLASSNAME);
		int idx_linputsize = cursor.getColumnIndex(L_INPUTSIZE);
		int idx_loutputsize = cursor.getColumnIndex(L_OUTPUTSIZE);
		int idx_lprofilehash = cursor.getColumnIndex(L_PROFILEHASH);
		int idx_llocaltime = cursor.getColumnIndex(L_LOCALTIME);
		int idx_rremoteid = cursor.getColumnIndex(R_ID);
		int idx_rserverurl = cursor.getColumnIndex(R_SERVERURL);
		int idx_rremotetime = cursor.getColumnIndex(R_REMOTETIME);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				MethodExecutionProfile methodProfile = new MethodExecutionProfile();
				methodProfile.setProfileId(cursor.getInt(idx_lprofileid));
				methodProfile.setMethodName(cursor.getString(idx_lmethodname));
				methodProfile.setClassName(cursor.getString(idx_lclassname));
				methodProfile.setInputSize(cursor.getInt(idx_linputsize));
				methodProfile.setOutputSize(cursor.getInt(idx_loutputsize));
				methodProfile.setProfileHash(cursor.getInt(idx_lprofilehash));
				methodProfile.setLocalTime(cursor.getInt(idx_llocaltime));
				methodProfile.setRemoteId(cursor.getInt(idx_rremoteid));
				methodProfile.setServerUrl(cursor.getString(idx_rserverurl));
				methodProfile.setRemoteTime(cursor.getInt(idx_rremotetime));

				lista.add(methodProfile);
			} while (cursor.moveToNext() && lista.size() < length);
		}

		lista.remove(0);

		cursor.close();
		closeDatabase();

		return lista;
	}

	private MethodExecutionProfile getResult(String sql) {
		openDatabase();
		Cursor cursor = database.rawQuery(sql, null);

		MethodExecutionProfile methodProfile = new MethodExecutionProfile();

		// obtem todos os indices das colunas da tabela
		int idx_lprofileid = cursor.getColumnIndex(L_ID);
		int idx_lmethodname = cursor.getColumnIndex(L_METHODNAME);
		int idx_lclassname = cursor.getColumnIndex(L_CLASSNAME);
		int idx_linputsize = cursor.getColumnIndex(L_INPUTSIZE);
		int idx_loutputsize = cursor.getColumnIndex(L_OUTPUTSIZE);
		int idx_lprofilehash = cursor.getColumnIndex(L_PROFILEHASH);
		int idx_llocaltime = cursor.getColumnIndex(L_LOCALTIME);
		int idx_rremoteid = cursor.getColumnIndex(R_ID);
		int idx_rserverurl = cursor.getColumnIndex(R_SERVERURL);
		int idx_rremotetime = cursor.getColumnIndex(R_REMOTETIME);

		if (cursor != null && cursor.moveToFirst()) {
			methodProfile.setProfileId(cursor.getInt(idx_lprofileid));
			methodProfile.setMethodName(cursor.getString(idx_lmethodname));
			methodProfile.setClassName(cursor.getString(idx_lclassname));
			methodProfile.setInputSize(cursor.getInt(idx_linputsize));
			methodProfile.setOutputSize(cursor.getInt(idx_loutputsize));
			methodProfile.setProfileHash(cursor.getInt(idx_lprofilehash));
			methodProfile.setLocalTime(cursor.getInt(idx_llocaltime));
			methodProfile.setRemoteId(cursor.getInt(idx_rremoteid));
			methodProfile.setServerUrl(cursor.getString(idx_rserverurl));
			methodProfile.setRemoteTime(cursor.getInt(idx_rremotetime));
		}

		cursor.close();
		closeDatabase();

		return methodProfile;
	}

	public void clean() {
		openDatabase();

		database.delete(TABLE_REMOTE, null, null);
		database.delete(TABLE_LOCAL, null, null);

		closeDatabase();
	}
}
