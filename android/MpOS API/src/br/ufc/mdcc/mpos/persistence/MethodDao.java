package br.ufc.mdcc.mpos.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import br.ufc.mdcc.mpos.R;
import br.ufscar.mcc.history.model.MethodProfile;

public class MethodDao extends Dao {
	private String TABLE_NAME;
	private String L_ID = "methodid";
	private String L_METHODNAME = "methodname";
	private String L_CLASSNAME = "classname";
	private String L_METHODCOUNT = "methodcount";
	private String L_METHODHASH = "methodhash";
	private final String clsname = MethodDao.class.getName();

	public MethodDao(Context con) {
		super(con);
		TABLE_NAME = con.getString(R.string.name_table_methoddata);
	}

	public void insertMethodProfile(MethodProfile profile) {
		openDatabase();

		ContentValues cv = new ContentValues();
		cv.put(L_METHODNAME, profile.getMethodName());
		cv.put(L_CLASSNAME, profile.getClassName());
		cv.put(L_METHODHASH, profile.hashCode());
		
		try {
			database.insert(TABLE_NAME, null, cv);
		} catch (SQLException ex) {
			Log.e(clsname, ex.getMessage());
		}

		closeDatabase();
	}

	public void updateMethodProfile(int methodId, int methodCount) {
		openDatabase();

		ContentValues cv = new ContentValues();
		cv.put(L_METHODCOUNT, methodCount);

		String sql = "methodId = " + methodId;

		try {
			database.update(TABLE_NAME, cv, sql, null);
		} catch (SQLException ex) {
			Log.e(clsname, ex.getMessage());
		}

		closeDatabase();
	}

	public MethodProfile getMethodProfle(String methodName, String className) {
		String sql = "SELECT METHODID, METHODNAME, CLASSNAME, METHODCOUNT FROM " + TABLE_NAME + " WHERE METHODNAME LIKE '%"
				+ methodName + "%' AND CLASSNAME LIKE '%" + className + "%'";

		return getResult(sql);
	}

	private MethodProfile getResult(String sql) {
		openDatabase();
		Cursor cursor = database.rawQuery(sql, null);

		MethodProfile profile = new MethodProfile();

		// obtem todos os indices das colunas da tabela
		int idx_logid = cursor.getColumnIndex(L_ID);
		int idx_methodname = cursor.getColumnIndex(L_METHODNAME);
		int idx_classname = cursor.getColumnIndex(L_CLASSNAME);
		int idx_methodcount = cursor.getColumnIndex(L_METHODCOUNT);

		if (cursor != null && cursor.moveToFirst()) {
			profile.setMethodId(cursor.getInt(idx_logid));
			profile.setMethodName(cursor.getString(idx_methodname));
			profile.setClassName(cursor.getString(idx_classname));
			profile.setMethodCount(cursor.getInt(idx_methodcount));
		}

		cursor.close();
		closeDatabase();

		return profile;
	}

	public void clean() {
		openDatabase();

		database.delete(TABLE_NAME, null, null);

		closeDatabase();
	}
}
