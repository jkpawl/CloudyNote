package jkpawl.septimasoftware.com.cloudynote;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import jkpawl.septimasoftware.com.cloudynote.contentprovider.NotesContentProvider;
import jkpawl.septimasoftware.com.cloudynote.database.NotesTable;
import me.drakeet.materialdialog.MaterialDialog;

public class NoteEditActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "NoteEditActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.title_field)
    EditText mTitleField;

    @Bind(R.id.message_field)
    EditText mMsgField;

    @Bind(R.id.data_field)
    TextView mDataField;

    private Menu mMenu;
    private Uri mNoteUri;

    private boolean isEditingMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
        Bundle extras = getIntent().getExtras();

        mNoteUri = (savedInstanceState == null) ? null : (Uri) savedInstanceState
                .getParcelable(NotesContentProvider.CONTENT_ITEM_TYPE);


        if (extras != null) {
            mNoteUri = extras.getParcelable(NotesContentProvider.CONTENT_ITEM_TYPE);
            refreshData(mNoteUri);
            isEditingMode = false;
        } else {
            isEditingMode = true;
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        mMenu = menu;

        refreshMenus();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                isEditingMode = true;
                refreshMenus();
                refreshGui();
                return true;

            case R.id.menu_accept:
                promptAcceptChanges();
                return true;

            case R.id.menu_cancel:
                promptRejectChanges();
                return true;

            case android.R.id.home:
                if (isEditingMode) {
                    promptRejectChanges();
                } else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (isEditingMode) {
            promptRejectChanges();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshMenus();
        refreshGui();
    }

    private void promptAcceptChanges() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);

        mMaterialDialog.setTitle(R.string.dialog_changes_accept_title)
                .setMessage(R.string.dialog_changes_accept_msg)
                .setPositiveButton(R.string.dialog_changes_accept_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (attemptSave()) {

                            isEditingMode = false;
                            refreshMenus();
                            refreshGui();
                            refreshData(mNoteUri);
                        }
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_changes_accept_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();

    }

    private void promptRejectChanges() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);

        mMaterialDialog.setTitle(R.string.dialog_changes_reject_title)
                .setMessage(R.string.dialog_changes_reject_msg)
                .setPositiveButton(R.string.dialog_changes_reject_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refreshData(mNoteUri);
                        isEditingMode = false;
                        refreshMenus();
                        refreshGui();
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_changes_reject_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();
    }

    private void refreshMenus() {

        //refresh menus
        if (null != mMenu) {
            mMenu.findItem(R.id.menu_edit).setVisible(!isEditingMode);
            mMenu.findItem(R.id.menu_accept).setVisible(isEditingMode);
            mMenu.findItem(R.id.menu_cancel).setVisible(isEditingMode);
        }
    }

    private void refreshGui() {
        mTitleField.setEnabled(isEditingMode);
        mMsgField.setEnabled(isEditingMode);

    }

    private boolean validateFields() {
        // Reset errors.
        mTitleField.setError(null);

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mTitleField.getText().toString().trim())) {
            mTitleField.setError(getString(R.string.error_title_required));
            if (!cancel) {
                focusView = mTitleField;
            }
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean attemptSave() {
        if (validateFields()) {
            saveNote();
            return true;
        }
        return false;
    }

    private void saveNote() {
        String title = mTitleField.getText().toString();
        String msg = mMsgField.getText().toString();

        SimpleDateFormat iso8601Format = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
        String date = iso8601Format.format(new Date());

        ContentValues values = new ContentValues();
        values.put(NotesTable.NOTE_COLUMN_TITLE, title);
//        values.put(NotesTable.NOTE_COLUMN_ID, title);
        values.put(NotesTable.NOTE_COLUMN_MSG, msg);
        values.put(NotesTable.NOTE_COLUMN_DATE, date);

        if (mNoteUri == null) {
            // New note
            mNoteUri = getContentResolver().insert(
                    NotesContentProvider.CONTENT_URI, values);
        } else {
            // Update note
            getContentResolver().update(mNoteUri, values, null, null);
        }


    }

    private void refreshData(Uri uri) {
        if (null == uri) {
            return;
        }

        if (DEBUG) {
            Log.d(LOG_TAG, "refreshData(), uri=" + uri.toString());
        }

        String[] projection = {NotesTable.NOTE_COLUMN_TITLE, NotesTable.NOTE_COLUMN_ID,
                NotesTable.NOTE_COLUMN_MSG, NotesTable.NOTE_COLUMN_DATE, NotesTable.NOTE_COLUMN_COLOR};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();

            /*
            Integer color = cursor.getInt(cursor
                    .getColumnIndexOrThrow(NotesTable.NOTE_COLUMN_COLOR));

            for (int i = 0; i < mCategory.getCount(); i++) {

                String s = (String) mCategory.getItemAtPosition(i);
                if (s.equalsIgnoreCase(category)) {
                    mCategory.setSelection(i);
                }
            }
            */
            String date = cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.NOTE_COLUMN_DATE));
            if (null != date) {
                mDataField.setText(getResources().getString(R.string.label_date_modification) + date);
            }
            mTitleField.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.NOTE_COLUMN_TITLE)));
            mMsgField.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(NotesTable.NOTE_COLUMN_MSG)));

            cursor.close();
        }
    }

}
