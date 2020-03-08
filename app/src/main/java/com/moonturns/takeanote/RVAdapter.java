package com.moonturns.takeanote;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.MyViewHolder> {

    private Context mContext; // content
    private List<Integer> noteIdList;
    private List<String> mNoteList; // Note list
    private List<Integer> mNotePriority; // List for priority of notes

    public RVAdapter(Context mContext, List<Integer> noteIdList, List<String> mNoteList, List<Integer> mNotePriority) {
        this.mContext = mContext;
        this.noteIdList = noteIdList;
        this.mNoteList = mNoteList;
        this.mNotePriority = mNotePriority;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.card_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        // From card_layout
        private CardView cardView;
        private TextView txtNote;
        private TextView txtPriority;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            txtNote = itemView.findViewById(R.id.txtNote);
            txtPriority = itemView.findViewById(R.id.txtPriority);
        }

        // Take a position parameter and set data to widgets.
        public void setData(int position) {
            int noteId = noteIdList.get(position);
            String note = mNoteList.get(position);
            String shorNote = note; // if length of note is bigger than 10 it will be take value of note and also ...
            int notePriority = mNotePriority.get(position);

            if (note.length() > 10) {
                shorNote = note.substring(0, 11) + " ...";
            }

            txtNote.setText(shorNote);
            txtPriority.setText("" + notePriority);

            controlPriorityColor(notePriority);
            clickEventCardView(noteId, note, notePriority);
            cardViewPopupMenu(noteId, position);
        }

        // Any cardView clicked event
        private void clickEventCardView(final int noteId, final String note, final int notePrioity) {
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Used EventBus for sending data to fragment
                    EventBus.getDefault().postSticky(new EventBusNoteMessage(noteId, note, notePrioity));
                    ((MainActivity) mContext).openNewNoteFragment();
                }
            });
        }

        // When cardView is clicked for a long time
        // show a popup menu
        private void cardViewPopupMenu(final int id, final int position) {
            cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final PopupMenu popupMenu = new PopupMenu(mContext, cardView);
                    popupMenu.getMenuInflater().inflate(R.menu.cardview_popup_menu, popupMenu.getMenu());
                    popupMenu.show();

                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if (item.getItemId() == R.id.action_delete) {
                                RVAdapter.this.deleteNoteFromLocalDatabase(id, position);
                            }
                            return true;
                        }
                    });
                    return true;
                }
            });
        }

        // Deleting note
        // Takes a position parameter and
        // deletes datas from local database
        private void deleteNoteFromLocalDatabase() {
            DatabaseHelper helper = new DatabaseHelper(mContext);
            SQLiteDatabase db = helper.getWritableDatabase();
        }

        // This method take parameter and this parameter is about notePriority.
        //If parameter is 1 txtPrioritys background will be holo_red_dark
        //If parameter is 2 txtPrioritys background will be holo_blue_light
        //If parameter is 3 txtPrioritys background will be holo_green_light
        private void controlPriorityColor(int notePriority) {
            if (notePriority == 1) {
                txtPriority.getBackground().setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);
            } else if (notePriority == 2) {
                txtPriority.getBackground().setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_blue_light), PorterDuff.Mode.SRC_ATOP);
            } else {
                txtPriority.getBackground().setColorFilter(ContextCompat.getColor(mContext, android.R.color.holo_green_light), PorterDuff.Mode.SRC_ATOP);
            }
        }
    }

    // Deleting note
    // Takes two parameter, id and position
    // id is note is at local database and position on note position on recyclerview
    // delete datas from local database
    private void deleteNoteFromLocalDatabase(int id, int position) {
        noteIdList.remove(position);
        mNoteList.remove(position);
        mNotePriority.remove(position);

        DatabaseHelper helper = new DatabaseHelper(mContext);
        SQLiteDatabase db = helper.getReadableDatabase();

        String where = DatabaseContact.NotesEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {"" + id};

        int deletedId = db.delete(DatabaseContact.NotesEntry.TABLE_NAME, where, whereArgs);
        if (deletedId > 0) {
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mNoteList.size());
        }
    }
}
