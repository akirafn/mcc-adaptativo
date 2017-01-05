/*******************************************************************************
 * Copyright (C) 2014 Philipp B. Costa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.ufc.mdcc.mpos.persistence;

import java.util.ArrayList;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import br.ufc.mdcc.mpos.R;

/**
 * This class manage sqlite android database.
 * 
 * @author Philipp B. Costa
 */
final class DatabaseManager extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private Context context;

	private final ArrayList<String> tabelas = new ArrayList<String>(2);
	private static DatabaseManager dataManager = null;

	private DatabaseManager(Context con) {
		super(con, con.getString(R.string.database_name), null, DATABASE_VERSION);
		this.context = con;

		loadingTables();
	}
	
	public static DatabaseManager getInstance(Context context){
		if(dataManager == null)
			dataManager = new DatabaseManager(context);
		return dataManager;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (String tabela : tabelas){
			db.execSQL(tabela);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion != newVersion) {
			db.execSQL(context.getString(R.string.drop_table_netprofile));
			db.execSQL(context.getString(R.string.drop_table_user));
			db.execSQL(context.getString(R.string.drop_table_functiondata));
			db.execSQL(context.getString(R.string.drop_table_localexecutiondata));
			db.execSQL(context.getString(R.string.drop_table_remoteexecutiondata));
			db.execSQL(context.getString(R.string.drop_table_methoddata));
			onCreate(db);
		}
	}

	private void loadingTables() {
		tabelas.add(context.getString(R.string.create_table_netprofile));
		tabelas.add(context.getString(R.string.create_table_user));
		tabelas.add(context.getString(R.string.create_table_methoddata));
		tabelas.add(context.getString(R.string.create_table_localexecutiondata));
		tabelas.add(context.getString(R.string.create_table_remoteexecutiondata));
		tabelas.add(context.getString(R.string.create_table_functiondata));
	}
}