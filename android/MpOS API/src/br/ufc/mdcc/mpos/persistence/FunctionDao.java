package br.ufc.mdcc.mpos.persistence;

import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import br.ufc.mdcc.mpos.R;
import br.ufscar.mcc.model.ConnectionType;
import br.ufscar.mcc.model.FunctionProfile;

public class FunctionDao extends Dao {
	private String TABLE_FUNCTION;
	private String TABLE_METHOD;
	private String F_ID = "functionid";
	private String M_ID = "methodid";
	private String M_NAME = "methodname";
	private String M_COUNT = "methodcount";
	private String M_HASH = "methodhash";
	private String F_SERVERURL = "serverurl";
	private String F_CONNTYPE = "conntype";
	private String F_SUMX = "sumx";
	private String F_SUMY = "sumy";
	private String F_SUMXY = "sumxy";
	private String F_SUMSQRX = "sumsqrx";
	private String F_FACTORA = "factora";
	private String F_FACTORB = "factorb";
	private String clsnama = FunctionDao.class.getName();

	public FunctionDao(Context con) {
		super(con);
		TABLE_FUNCTION = con.getString(R.string.name_table_functiondata);
		TABLE_METHOD = con.getString(R.string.name_table_methoddata);
	}

	public void updateFunctionProfileList(HashMap<Integer, FunctionProfile> profileMap) {
		ContentValues cv = new ContentValues();
		String sql = "";
		openDatabase();

		for (FunctionProfile profile : profileMap.values()) {
			cv.put(F_SUMX, profile.getSumX());
			cv.put(F_SUMY, profile.getSumY());
			cv.put(F_SUMXY, profile.getSumXY());
			cv.put(F_SUMSQRX, profile.getSumSqrX());
			cv.put(F_FACTORA, profile.getFactorA());
			cv.put(F_FACTORB, profile.getFactorB());
			if (profile.getFunctionId() > 0) {
				sql = "FUNCTIONID = " + profile.getFunctionId();

				try {
					database.update(TABLE_FUNCTION, cv, sql, null);
				} catch (SQLException ex) {
					Log.e(clsnama, ex.getMessage());
				}
			} else {
				cv.put(M_ID, profile.getMethodId());
				cv.put(F_SERVERURL, profile.getServerUrl());
				cv.put(F_CONNTYPE, profile.getConnType().getValue());

				try {
					database.insert(TABLE_FUNCTION, null, cv);
				} catch (SQLException ex) {
					Log.e(clsnama, ex.getMessage());
				}
			}

			cv.clear();
		}

		closeDatabase();
	}

	public HashMap<Integer, FunctionProfile> getFunctionMapByServerConn(String serverUrl, ConnectionType connType) {
		String sql = "SELECT F.FUNCTIONID, F.METHODID, M.METHODNAME, M.METHODCOUNT, M.METHODHASH, F.SERVERURL, F.CONNTYPE, "
				+ "F.SUMX, F.SUMY, F.SUMXY, F.SUMSQRX, F.FACTORA, F.FACTORB FROM " + TABLE_FUNCTION + " F JOIN "
				+ TABLE_METHOD + " M ON M.METHODID = F.METHODID WHERE F.SERVERURL LIKE '%" + serverUrl
				+ "%' AND CONNTYPE = " + connType.getValue();

		return getResultMap(sql);
	}

	private HashMap<Integer, FunctionProfile> getResultMap(String sql) {
		HashMap<Integer, FunctionProfile> mapa = new HashMap<Integer, FunctionProfile>();
		Cursor cursor;
		int idx_functionid, idx_methodid, idx_methodname, idx_methodcount, idx_methodhash, idx_serverurl, idx_conntype,
				idx_sumx, idx_sumy, idx_sumxy, idx_sumsqrx, idx_factora, idx_factorb;

		openDatabase();
		cursor = database.rawQuery(sql, null);

		idx_functionid = cursor.getColumnIndex(F_ID);
		idx_methodid = cursor.getColumnIndex(M_ID);
		idx_methodname = cursor.getColumnIndex(M_NAME);
		idx_methodcount = cursor.getColumnIndex(M_COUNT);
		idx_methodhash = cursor.getColumnIndex(M_HASH);
		idx_serverurl = cursor.getColumnIndex(F_SERVERURL);
		idx_conntype = cursor.getColumnIndex(F_CONNTYPE);
		idx_sumx = cursor.getColumnIndex(F_SUMX);
		idx_sumy = cursor.getColumnIndex(F_SUMY);
		idx_sumxy = cursor.getColumnIndex(F_SUMXY);
		idx_sumsqrx = cursor.getColumnIndex(F_SUMSQRX);
		idx_factora = cursor.getColumnIndex(F_FACTORA);
		idx_factorb = cursor.getColumnIndex(F_FACTORB);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				FunctionProfile profile = new FunctionProfile();

				profile.setFunctionId(cursor.getInt(idx_functionid));
				profile.setMethodId(cursor.getInt(idx_methodid));
				profile.setMethodName(cursor.getString(idx_methodname));
				profile.setMethodCount(cursor.getInt(idx_methodcount));
				profile.setMethodHash(cursor.getInt(idx_methodhash));
				profile.setServerUrl(cursor.getString(idx_serverurl));
				profile.setConnType(cursor.getInt(idx_conntype));
				profile.setSumX(cursor.getInt(idx_sumx));
				profile.setSumY(cursor.getInt(idx_sumy));
				profile.setSumXY(cursor.getInt(idx_sumxy));
				profile.setSumSqrX(cursor.getInt(idx_sumsqrx));
				profile.setFactorA(cursor.getDouble(idx_factora));
				profile.setFactorB(cursor.getDouble(idx_factorb));

				mapa.put(Integer.valueOf(profile.getMethodHash()), profile);
			} while (cursor.moveToNext());
		}

		closeDatabase();

		return mapa;
	}
}
