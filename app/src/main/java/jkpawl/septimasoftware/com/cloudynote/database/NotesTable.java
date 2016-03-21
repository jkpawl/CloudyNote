package jkpawl.septimasoftware.com.cloudynote.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import jkpawl.septimasoftware.com.cloudynote.Note;

/**
 * Created by J.Pawluczuk on 3/21/16.
 */
public class NotesTable {

    // Database table
    public static final String NOTE_TABLE_NAME = "notes";

    public static final String NOTE_COLUMN_ID = "_id";
    public static final String NOTE_COLUMN_TITLE = "title";
    public static final String NOTE_COLUMN_DATE = "date";
    public static final String NOTE_COLUMN_MSG = "msg";
    public static final String NOTE_COLUMN_COLOR = "color";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + NOTE_TABLE_NAME
            + "(" + NOTE_COLUMN_ID + " integer primary key autoincrement not null, "
            + NOTE_COLUMN_TITLE + " text, "
            + NOTE_COLUMN_DATE + " text, "
            + NOTE_COLUMN_MSG + " text, "
            + NOTE_COLUMN_COLOR + " integer);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(NotesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE_NAME);
        onCreate(database);
    }


    public static ArrayList<Note> cursorToNoteList(Cursor cursor) {
        ArrayList<Note> resultNoteList = new ArrayList<Note>();

        Note note = null;

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            note = new Note();

            note.setId(cursor.getInt(cursor.getColumnIndex(NOTE_COLUMN_ID)));
            note.setTitle(cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_TITLE)));
//            note.setMessage(cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_MSG)));

            note.setDate(cursor.getString(cursor.getColumnIndex(NOTE_COLUMN_DATE)));
//            note.setColor(cursor.getInt(cursor.getColumnIndex(NOTE_COLUMN_COLOR)));


            resultNoteList.add(note);
            cursor.moveToNext();
        }
        return resultNoteList;
    }
}