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
import br.ufscar.mcc.history.model.ConnectionType;
import br.ufscar.mcc.history.model.ThresholdProfile;

public class ThresholdDao extends Dao {
	private String TABLE_THRESHOLD;
	private String TABLE_METHOD;
	private String T_ID = "thresholdid";
	private String M_ID = "methodid";
	private String M_METHODNAME = "methodname";
	private String M_CLASSNAME = "classname";
	private String M_METHODCOUNT = "methodcount";
	private String M_METHODHASH = "methodhash";
	private String T_SERVERURL = "serverurl";
	private String T_CONNTYPE = "conntype";
	private String T_UNFAVMIN = "unfavmin";
	private String T_UNFAVMED = "unfavmed";
	private String T_UNFAVMAX = "unfavmax";
	private String T_FAVMIN = "favmin";
	private String T_FAVMED = "favmed";
	private String T_FAVMAX = "favmax";
	private int returnLimit;

	private final String clsname = ThresholdDao.class.getName();

	public ThresholdDao(Context con) {
		super(con);
		TABLE_THRESHOLD = con.getString(R.string.name_table_thresholddata);
		TABLE_METHOD = con.getString(R.string.name_table_methoddata);
		returnLimit = 10;
	}

	public void insertThresholdProfile(ThresholdProfile profile) {
		openDatabase();

		ContentValues cv = new ContentValues();
		cv.put(M_ID, profile.getMethodId());
		cv.put(T_SERVERURL, profile.getServerUrl());
		cv.put(T_CONNTYPE, profile.getConnType().getValue());
		cv.put(T_UNFAVMIN, profile.getUnfavMin());
		cv.put(T_UNFAVMED, profile.getUnfavMed());
		cv.put(T_UNFAVMAX, profile.getUnfavMax());
		cv.put(T_FAVMIN, profile.getFavMin());
		cv.put(T_FAVMED, profile.getFavMed());
		cv.put(T_FAVMAX, profile.getFavMax());

		try {
			database.insert(TABLE_THRESHOLD, null, cv);
		} catch (SQLException ex) {
			Log.e(clsname, ex.getMessage());
		}

		closeDatabase();
	}

	public void updateThresholdProfile(ArrayList<ThresholdProfile> profileList) {
		openDatabase();

		ContentValues cv = new ContentValues();
		String sql;
		
		for (ThresholdProfile profile : profileList){
			cv.put(T_UNFAVMIN, profile.getUnfavMin());
			cv.put(T_UNFAVMED, profile.getUnfavMed());
			cv.put(T_UNFAVMAX, profile.getUnfavMax());
			cv.put(T_FAVMIN, profile.getFavMin());
			cv.put(T_FAVMED, profile.getFavMed());
			cv.put(T_FAVMAX, profile.getFavMax());

			sql = "thresholdid = " + profile.getThresholdId();

			try {
				database.insert(TABLE_THRESHOLD, null, cv);
			} catch (SQLException ex) {
				Log.e(clsname, ex.getMessage());
			}
			
		}

		closeDatabase();

	}

	public ArrayList<ThresholdProfile> getListByServerConn(String serverUrl, ConnectionType connType)
			throws JSONException, ParseException {
		String sql = "SELECT T.THRESHOLDID, T.METHODID, M.METHODNAME, M.METHODCOUNT, M.METHODHASH, T.SERVERURL, T.CONNTYPE, "
				+ "T.UNFAVMIN, T.UNFAVMED, T.UNFAVMAX, T.FAVMIN, T.FAVMED, T.FAVMAX FROM " + TABLE_THRESHOLD
				+ " T JOIN " + TABLE_METHOD + " M ON M.METHODID = T.METHODID WHERE T.SERVERURL LIKE '%" + serverUrl
				+ "%' AND T.CONNTYPE = " + connType.getValue() + " ORDER BY M.METHODCOUNT DESC LIMIT " + returnLimit;

		return getLastResults(sql);
	}

	// ------------------------------------
	// Métodos privados de consulta básica
	// ------------------------------------
	private ThresholdProfile getResult(String sql) {
		openDatabase();
		Cursor cursor = database.rawQuery(sql, null);

		ThresholdProfile profile = new ThresholdProfile();

		// obtem todos os indices das colunas da tabela
		int idx_thresholdid = cursor.getColumnIndex(T_ID);
		int idx_methodid = cursor.getColumnIndex(M_ID);
		int idx_methodname = cursor.getColumnIndex(M_METHODNAME);
		int idx_methodcount = cursor.getColumnIndex(M_METHODCOUNT);
		int idx_methodhash = cursor.getColumnIndex(M_METHODHASH);
		int idx_serverurl = cursor.getColumnIndex(T_SERVERURL);
		int idx_conntype = cursor.getColumnIndex(T_CONNTYPE);
		int idx_unfavmin = cursor.getColumnIndex(T_UNFAVMIN);
		int idx_unfavmed = cursor.getColumnIndex(T_UNFAVMED);
		int idx_unfavmax = cursor.getColumnIndex(T_UNFAVMAX);
		int idx_favmin = cursor.getColumnIndex(T_FAVMIN);
		int idx_favmed = cursor.getColumnIndex(T_FAVMED);
		int idx_favmax = cursor.getColumnIndex(T_FAVMAX);

		if (cursor != null && cursor.moveToFirst()) {
			profile.setThresholdId(cursor.getInt(idx_thresholdid));
			profile.setMethodId(cursor.getInt(idx_methodid));
			profile.setMethodName(cursor.getString(idx_methodname));
			profile.setMethodCount(cursor.getInt(idx_methodcount));
			profile.setMethodHash(cursor.getInt(idx_methodhash));
			profile.setServerUrl(cursor.getString(idx_serverurl));
			profile.setConnType(cursor.getInt(idx_conntype));
			profile.setUnfavMin(cursor.getInt(idx_unfavmin));
			profile.setUnfavMed(cursor.getInt(idx_unfavmed));
			profile.setUnfavMax(cursor.getInt(idx_unfavmax));
			profile.setFavMin(cursor.getInt(idx_favmin));
			profile.setFavMed(cursor.getInt(idx_favmed));
			profile.setFavMax(cursor.getInt(idx_favmax));
		}

		cursor.close();
		closeDatabase();

		return profile;
	}

	private ArrayList<ThresholdProfile> getLastResults(String sql) throws JSONException, ParseException {
		openDatabase();
		int length;

		Cursor cursor = database.rawQuery(sql, null);

		if (returnLimit < cursor.getColumnCount())
			length = returnLimit;
		else
			length = cursor.getColumnCount();

		ArrayList<ThresholdProfile> lista = new ArrayList<ThresholdProfile>(length);

		// obtem todos os indices das colunas da tabela
		int idx_thresholdid = cursor.getColumnIndex(T_ID);
		int idx_methodid = cursor.getColumnIndex(M_ID);
		int idx_methodname = cursor.getColumnIndex(M_METHODNAME);
		int idx_methodcount = cursor.getColumnIndex(M_METHODCOUNT);
		int idx_methodhash = cursor.getColumnIndex(M_METHODHASH);
		int idx_serverurl = cursor.getColumnIndex(T_SERVERURL);
		int idx_conntype = cursor.getColumnIndex(T_CONNTYPE);
		int idx_unfavmin = cursor.getColumnIndex(T_UNFAVMIN);
		int idx_unfavmed = cursor.getColumnIndex(T_UNFAVMED);
		int idx_unfavmax = cursor.getColumnIndex(T_UNFAVMAX);
		int idx_favmin = cursor.getColumnIndex(T_FAVMIN);
		int idx_favmed = cursor.getColumnIndex(T_FAVMED);
		int idx_favmax = cursor.getColumnIndex(T_FAVMAX);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				ThresholdProfile profile = new ThresholdProfile();
				profile.setThresholdId(cursor.getInt(idx_thresholdid));
				profile.setMethodId(cursor.getInt(idx_methodid));
				profile.setMethodName(cursor.getString(idx_methodname));
				profile.setMethodCount(cursor.getInt(idx_methodcount));
				profile.setMethodHash(cursor.getInt(idx_methodhash));
				profile.setServerUrl(cursor.getString(idx_serverurl));
				profile.setConnType(cursor.getInt(idx_conntype));
				profile.setUnfavMin(cursor.getInt(idx_unfavmin));
				profile.setUnfavMed(cursor.getInt(idx_unfavmed));
				profile.setUnfavMax(cursor.getInt(idx_unfavmax));
				profile.setFavMin(cursor.getInt(idx_favmin));
				profile.setFavMed(cursor.getInt(idx_favmed));
				profile.setFavMax(cursor.getInt(idx_favmax));

				lista.add(profile);
			} while (cursor.moveToNext() && lista.size() < length);
		}

		lista.remove(0);

		cursor.close();
		closeDatabase();

		return lista;
	}

	public void clean() {
		openDatabase();

		database.delete(TABLE_THRESHOLD, null, null);

		closeDatabase();
	}
}
