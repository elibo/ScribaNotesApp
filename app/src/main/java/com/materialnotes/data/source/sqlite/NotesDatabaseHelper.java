package com.materialnotes.data.source.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;

/**
 * Helper for the use of sqlLite databases
 * * @see <a href="http://bit.ly/1s5nUI5">Create a Database Using a SQL Helper</a>
 */
public class NotesDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = NotesDatabaseHelper.class.getSimpleName();
    private static final String DATABASE_SCHEMA_FILE_NAME_PATTERN = "notes_schema-v%s.sql";
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    private final Context context;

    /**
     * Creates a  NotesDatabaseHelper.
     *
     * @param context the context where NotesDatabaseHelper is created.
     */
    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "Creating database version " + DATABASE_VERSION + "...");
        InputStream fileStream = null;
        try {
            // reads file notes_schema-v%s.sql for extracting all the sql statements
            fileStream = context.getAssets().open(String.format(DATABASE_SCHEMA_FILE_NAME_PATTERN, DATABASE_VERSION));
            String[] statements = SQLFileParser.getSQLStatements(fileStream);
            // Executes the statements
            for (String statement : statements) {
                Log.v(TAG, statement);
                db.execSQL(statement);
            }
        } catch (IOException ex) {
            Log.e(TAG, "Unable to open schema file", ex);
        } finally {
            if (fileStream != null) {
                try {
                    fileStream.close();
                } catch (IOException ex) {
                    Log.e(TAG, "Unable to close stream", ex);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
        context.deleteDatabase(DATABASE_NAME);
        onCreate(db);
    }
}