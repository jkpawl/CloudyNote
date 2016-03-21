package jkpawl.septimasoftware.com.cloudynote.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import jkpawl.septimasoftware.com.cloudynote.database.NotesDatabaseHelper;
import jkpawl.septimasoftware.com.cloudynote.database.NotesTable;

public class NotesContentProvider extends ContentProvider {

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/notes";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/note";
    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "NotesContentProvider";
    // Used for the UriMacher
    private static final int NOTES = 10;
    private static final int NOTES_ID = 20;
    private static final String AUTHORITY = "jkpawl.septimasoftware.com.cloudynote.contentprovider";
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);
    private static final UriMatcher sURIMatcher = new UriMatcher(
            UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES_ID);
    }

    // mDatabase
    private NotesDatabaseHelper mDatabase;

    @Override
    public boolean onCreate() {
        mDatabase = new NotesDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        if (DEBUG) {
            Log.d(LOG_TAG, "query(), uri=" + uri.toString());
        }
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        checkColumns(projection);

        queryBuilder.setTables(NotesTable.NOTE_TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case NOTES:
                break;
            case NOTES_ID:
                queryBuilder.appendWhere(NotesTable.NOTE_COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // Make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) {
            Log.d(LOG_TAG, "insert(), uri=" + uri.toString());
        }

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case NOTES:
                id = sqlDB.insert(NotesTable.NOTE_TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) {
            Log.d(LOG_TAG, "delete(), uri=" + uri.toString());
        }
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case NOTES:
                rowsDeleted = sqlDB.delete(NotesTable.NOTE_TABLE_NAME, selection,
                        selectionArgs);
                break;
            case NOTES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(NotesTable.NOTE_TABLE_NAME,
                            NotesTable.NOTE_COLUMN_ID + "=" + id, null);
                } else {
                    rowsDeleted = sqlDB.delete(NotesTable.NOTE_TABLE_NAME,
                            NotesTable.NOTE_COLUMN_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        if (DEBUG) {
            Log.d(LOG_TAG, "update(), uri=" + uri.toString());
        }
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = mDatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case NOTES:
                rowsUpdated = sqlDB.update(NotesTable.NOTE_TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case NOTES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(NotesTable.NOTE_TABLE_NAME, values,
                            NotesTable.NOTE_COLUMN_ID + "=" + id, null);
                } else {
                    rowsUpdated = sqlDB.update(NotesTable.NOTE_TABLE_NAME, values,
                            NotesTable.NOTE_COLUMN_ID + "=" + id + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

    private void checkColumns(String[] projection) {
        String[] available = {NotesTable.NOTE_COLUMN_COLOR,
                NotesTable.NOTE_COLUMN_TITLE,
                NotesTable.NOTE_COLUMN_MSG,
                NotesTable.NOTE_COLUMN_DATE,
                NotesTable.NOTE_COLUMN_ID};

        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(
                    Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(
                    Arrays.asList(available));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException(
                        "Unknown columns in projection");
            }
        }
    }

}
