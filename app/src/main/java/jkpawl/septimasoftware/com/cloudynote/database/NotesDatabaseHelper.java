package jkpawl.septimasoftware.com.cloudynote.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by J.Pawluczuk on 3/21/16.
 */
public class NotesDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "CloudyNote.db";
    private static final int DATABASE_VERSION = 8;

    public NotesDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        NotesTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        NotesTable.onUpgrade(database, oldVersion, newVersion);
    }

}