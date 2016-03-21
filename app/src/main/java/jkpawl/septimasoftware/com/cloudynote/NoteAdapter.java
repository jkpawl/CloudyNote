package jkpawl.septimasoftware.com.cloudynote;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private static INoteClicks mCallback = null; //TODO make it not static
    private List<Note> mNoteList;


    public NoteAdapter(@NonNull final List<Note> personPreferenceList, INoteClicks callback) {
        this.mNoteList = personPreferenceList;
        this.mCallback = callback;
    }

    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_item_layout, null);
        ViewHolder viewHolder = new ViewHolder(itemLayoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.mNoteTitle.setText(mNoteList.get(position).getTitle());
        viewHolder.mNoteDate.setText(mNoteList.get(position).getDate());

    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mNoteDate, mNoteTitle;
        public ImageView mIconDelete;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mNoteTitle = (TextView) itemLayoutView.findViewById(R.id.note_title);
            mNoteDate = (TextView) itemLayoutView.findViewById(R.id.note_date);

            mIconDelete = (ImageView) itemLayoutView.findViewById(R.id.note_delete);
            mIconDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCallback) {
                        mCallback.onNoteDelete(getAdapterPosition());
                    }
                }
            });
        }
    }
}