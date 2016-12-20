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
import br.ufscar.mcc.history.model.OffloadLogProfile;
import br.ufscar.mcc.history.model.OffloadShortProfile;

public class LogDecisionDao extends Dao {

	private final String TABLE_NAME;
	private final String L_ID = "logid";
	private final String L_PROFILEID = "profileid";
	private final String L_REMOTEID = "remoteid";
	private final String L_PROFILEHASH = "profilehash";
	private final String L_CONNTYPE = "conntype";
	private final String L_EXECUTIONDATE = "executiondate";
	private final String L_EXECUTIONTIME = "executiontime";
	private final String L_EXECUTIONDECISION = "executiondecision";
	private final String clsname = LogDecisionDao.class.getName();

	public LogDecisionDao(Context con) {
		super(con);
		TABLE_NAME = con.getString(R.string.name_table_logdecision);
	}

	public void insertLogDecision(OffloadLogProfile logProfile) {
		openDatabase();

		ContentValues cv = new ContentValues();
		cv.put(L_PROFILEID, logProfile.getProfileId());
		cv.put(L_REMOTEID, logProfile.getRemoteId());
		cv.put(L_PROFILEHASH, logProfile.getProfileHash());
		cv.put(L_CONNTYPE, logProfile.getConnType());
		cv.put(L_EXECUTIONTIME, logProfile.getExecutionTime());
		cv.put(L_EXECUTIONDECISION, logProfile.getDecision());

		try {
			database.insert(TABLE_NAME, null, cv);
		} catch (SQLException ex) {
			Log.e(clsname, ex.getMessage());
		}

		closeDatabase();
	}

	public OffloadLogProfile getDecisionByServer(int profileId, int remoteId, ConnectionType connType) {
		String sql = "SELECT LOGID, PROFILEID, REMOTEID, PROFILEHASH, CONNTYPE, DATETIME('now') AS EXECUTIONDATE, "
				+ "AVG(EXECUTIONTIME), AVG(EXECUTIONDECISION) FROM " + TABLE_NAME + " WHERE PROFILEID = "
				+ String.valueOf(profileId) + " AND REMOTEID = " + String.valueOf(remoteId) + " AND CONNTYPE LIKE '%"
				+ connType.toString() + "%'";

		return getResult(sql);
	}

	public OffloadLogProfile getDecisionByServer(int profileId, ConnectionType connType) {
		String sql = "SELECT LOGID, PROFILEID, REMOTEID, PROFILEHASH, CONNTYPE, DATETIME('now') AS EXECUTIONDATE, "
				+ "AVG(EXECUTIONTIME), AVG(EXECUTIONDECISION) FROM " + TABLE_NAME + " WHERE PROFILEID = "
				+ String.valueOf(profileId) + " AND CONNTYPE LIKE '%" + connType.toString() + "%'";

		return getResult(sql);
	}
	
	// ------------------------------------
	// Métodos privados de consulta básica
	// ------------------------------------
	private ArrayList<OffloadLogProfile> getLastResults(String sql, int count) throws JSONException, ParseException {
		openDatabase();
		int length;

		Cursor cursor = database.rawQuery(sql, null);

		if (count < cursor.getColumnCount())
			length = count;
		else
			length = cursor.getColumnCount();

		ArrayList<OffloadLogProfile> lista = new ArrayList<OffloadLogProfile>(length);

		// obtem todos os indices das colunas da tabela
		int idx_logid = cursor.getColumnIndex(L_ID);
		int idx_profileid = cursor.getColumnIndex(L_PROFILEID);
		int idx_remoteid = cursor.getColumnIndex(L_REMOTEID);
		int idx_profilehash = cursor.getColumnIndex(L_PROFILEHASH);
		int idx_conntype = cursor.getColumnIndex(L_CONNTYPE);
		int idx_executiondate = cursor.getColumnIndex(L_EXECUTIONDATE);
		int idx_executiontime = cursor.getColumnIndex(L_EXECUTIONTIME);
		int idx_executiondecision = cursor.getColumnIndex(L_EXECUTIONDECISION);

		if (cursor != null && cursor.moveToFirst()) {
			do {
				OffloadLogProfile offloadProfile = new OffloadLogProfile();
				offloadProfile.setLogId(cursor.getInt(idx_logid));
				offloadProfile.setProfileId(cursor.getInt(idx_profileid));
				offloadProfile.setRemoteId(cursor.getInt(idx_remoteid));
				offloadProfile.setProfileHash(cursor.getInt(idx_profilehash));
				offloadProfile.setConnType(cursor.getString(idx_conntype));
				offloadProfile.setDecisionDate(cursor.getString(idx_executiondate));
				offloadProfile.setExecutionTime(cursor.getInt(idx_executiontime));
				offloadProfile.setDecision(cursor.getInt(idx_executiondecision));

				lista.add(offloadProfile);
			} while (cursor.moveToNext() && lista.size() < length);
		}

		lista.remove(0);

		cursor.close();
		closeDatabase();

		return lista;
	}

	private OffloadLogProfile getResult(String sql) {
		openDatabase();
		Cursor cursor = database.rawQuery(sql, null);

		OffloadLogProfile offloadProfile = new OffloadLogProfile();

		// obtem todos os indices das colunas da tabela
		int idx_logid = cursor.getColumnIndex(L_ID);
		int idx_profileid = cursor.getColumnIndex(L_PROFILEID);
		int idx_remoteid = cursor.getColumnIndex(L_REMOTEID);
		int idx_profilehash = cursor.getColumnIndex(L_PROFILEHASH);
		int idx_conntype = cursor.getColumnIndex(L_CONNTYPE);
		int idx_executiondate = cursor.getColumnIndex(L_EXECUTIONDATE);
		int idx_executiontime = cursor.getColumnIndex(L_EXECUTIONTIME);
		int idx_executiondecision = cursor.getColumnIndex(L_EXECUTIONDECISION);

		Log.i("Teste", "Foram encontrados " + String.valueOf(cursor.getCount()) + " registros!");

		if (cursor != null && cursor.moveToFirst()) {

			Log.i("Teste", "Foram encontrados " + String.valueOf(cursor.getCount()) + " registros!");

			offloadProfile.setLogId(cursor.getInt(idx_logid));
			offloadProfile.setProfileId(cursor.getInt(idx_profileid));
			offloadProfile.setRemoteId(cursor.getInt(idx_remoteid));
			offloadProfile.setProfileHash(cursor.getInt(idx_profilehash));
			offloadProfile.setConnType(cursor.getString(idx_conntype));
			offloadProfile.setDecisionDate(cursor.getString(idx_executiondate));
			offloadProfile.setExecutionTime(cursor.getInt(idx_executiontime));
			offloadProfile.setDecision(cursor.getInt(idx_executiondecision));
		}

		cursor.close();
		closeDatabase();

		return offloadProfile;
	}

	public void clean() {
		openDatabase();

		database.delete(TABLE_NAME, null, null);

		closeDatabase();
	}
}
