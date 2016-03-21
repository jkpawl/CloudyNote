package jkpawl.septimasoftware.com.cloudynote;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import jkpawl.septimasoftware.com.cloudynote.contentprovider.NotesContentProvider;
import jkpawl.septimasoftware.com.cloudynote.database.NotesTable;
import jkpawl.septimasoftware.com.cloudynote.utils.CustomLinearLayoutManager;
import jkpawl.septimasoftware.com.cloudynote.utils.RecycleViewItemClickSupport;
import me.drakeet.materialdialog.MaterialDialog;

public class NoteListActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, INoteClicks {

    private static final boolean DEBUG = true;
    private static final String LOG_TAG = "NoteListActivity";

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.note_list)
    RecyclerView mNoteListRecyclerView;

    private NoteAdapter mAdapter;
    private List<Note> mNoteList;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewNote();
            }
        });

        RecycleViewItemClickSupport.addTo(mNoteListRecyclerView).setOnItemClickListener(new OnNoteClickedListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
        if (null != mNoteList) {
            refreshData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_author) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewNote() {
        Intent intent = new Intent(this, NoteEditActivity.class);
        startActivity(intent);
    }

    private void refreshData() {
        getLoaderManager().initLoader(0, null, this);
    }

    private void refreshGui() {
        mAdapter = new NoteAdapter(mNoteList, this);
        mAdapter.notifyDataSetChanged();

        mNoteListRecyclerView.setAdapter(mAdapter);
        mNoteListRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNoteListRecyclerView.setLayoutManager(new CustomLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onLoadFinished");
        }
        String[] projection = {NotesTable.NOTE_COLUMN_ID, NotesTable.NOTE_COLUMN_TITLE, NotesTable.NOTE_COLUMN_DATE};
        CursorLoader cursorLoader = new CursorLoader(this,
                NotesContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onLoadFinished");
        }
        mNoteList = NotesTable.cursorToNoteList(data);
        if (DEBUG) {
            Log.d(LOG_TAG, "mNoteList.size=" + mNoteList.size());
        }
        refreshGui();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onNoteDelete(int position) {
        if (DEBUG) {
            Log.d(LOG_TAG, "onNoteDelete > position=" + position);
        }

        final int noteId = mNoteList.get(position).getId();
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);

        mMaterialDialog.setTitle(R.string.dialog_note_del_title)
                .setMessage(R.string.dialog_note_del_title_del_msg)
                .setPositiveButton(R.string.dialog_note_del_accept, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(NotesContentProvider.CONTENT_URI + "/"
                                + noteId);
                        getContentResolver().delete(uri, null, null);
                        refreshData();

                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.dialog_note_del_cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();

    }

    private class OnNoteClickedListener implements RecycleViewItemClickSupport.OnItemClickListener {
        @Override
        public void onItemClicked(RecyclerView recyclerView, int position, View v) {

            if (DEBUG) {
                Log.d(LOG_TAG, "mNoteList.size=" + mNoteList.size() + ", position=" + position);
            }

            Intent i = new Intent(NoteListActivity.this, NoteEditActivity.class);

            Note note = mNoteList.get(position);
            Integer noteId = note.getId();

            Uri noteUri = Uri.parse(NotesContentProvider.CONTENT_URI + "/" + noteId);
            i.putExtra(NotesContentProvider.CONTENT_ITEM_TYPE, noteUri);

            startActivity(i);
        }
    }
}
