package com.moonturns.takeanote;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class NewNoteFragment extends Fragment {

    private int noteId = -1; // If NewNoteFragment is opening clicked cardView from RVAdapter it will be id value at sqlite database
    private String note = ""; // For etNote.getText().toString() or value that comes from evenbust
    private int notePriority = 1; // for note priority. It changes click events of txt1, txt2 and txt3 or value that comes from evenbust

    private Snackbar snackbar;

    private ScrollView fragmentNoteContainer;
    private ConstraintLayout fragmentNoteContent;
    private EditText etNote;
    private TextView txt1, txt2, txt3;
    private ImageView imgCheck1, imgCheck2, imgCheck3;
    private Button btnSave;

    // Initialize widgets from fragment_new_note
    private void crt(View view) {
        fragmentNoteContainer = view.findViewById(R.id.fragmentNoteContainer);
        fragmentNoteContent = view.findViewById(R.id.fragmentNoteContent);
        etNote = view.findViewById(R.id.etNote);
        txt1 = view.findViewById(R.id.txt1);
        txt2 = view.findViewById(R.id.txt2);
        txt3 = view.findViewById(R.id.txt3);
        imgCheck1 = view.findViewById(R.id.imgCheck1);
        imgCheck2 = view.findViewById(R.id.imgCheck2);
        imgCheck3 = view.findViewById(R.id.imgCheck3);
        btnSave = view.findViewById(R.id.btnSave);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_note, container, false);
        crt(view);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setBtnSave();
        txtClickEvents();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        EventBus.getDefault().unregister(this);
        EventBus.getDefault().removeAllStickyEvents();

        snackbarVisibleStatement();
    }

    // btnSave click event
    private void setBtnSave() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventEmptyNote();
            }
        });
    }

    // Check etNote is empty or not
    // If it is empty start an animation or not
    // save it to local database
    private void eventEmptyNote() {
        note = etNote.getText().toString();
        if (note.isEmpty()) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.shake_anim);
            fragmentNoteContent.startAnimation(animation);
        } else {
            if (noteId == -1) {
                saveNote();
            } else {
                updateNote();
            }
        }
    }

    // Saves note to local database
    private void saveNote() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseContact.NotesEntry.COLUMN_NOTE, note);
        contentValues.put(DatabaseContact.NotesEntry.COLUMN_PRIORITY, notePriority);

        Uri uri = getContext().getContentResolver().insert(NoteProvider.CONTENT_URI, contentValues);

        if (uri == null) {
            showToast("Error");
        } else {
            showSnackBar("Are you sure");
            completedNote();
        }
        sendMessageUsingEventBus();
    }

    // Update note at local database
    private void updateNote() {
        note = etNote.getText().toString();

        ContentValues updatedValues = new ContentValues();
        updatedValues.put(DatabaseContact.NotesEntry.COLUMN_NOTE, note);
        updatedValues.put(DatabaseContact.NotesEntry.COLUMN_PRIORITY, notePriority);

        String where = DatabaseContact.NotesEntry.COLUMN_ID + " = ?";

        int updatedItem = getContext().getContentResolver().update(NoteProvider.CONTENT_URI, updatedValues, where, new String[]{""+noteId});

        if (updatedItem > 0) {
            showToast("Updated");
            completedNote();
            sendMessageUsingEventBus();
        }
    }

    // If user clicks back button at snackbar
    // last note will be deleted
    private void deleteNote() {
        Cursor cursor = getContext().getContentResolver()
                .query(NoteProvider.CONTENT_URI, new String[]{DatabaseContact.NotesEntry.COLUMN_ID},  null, null,null);

        cursor.moveToLast();

        int id = cursor.getInt(cursor.getColumnIndex(DatabaseContact.NotesEntry.COLUMN_ID));

        String where = DatabaseContact.NotesEntry.COLUMN_ID + " = ?";
        String[] whereArgs = {""+id};

        int deletedItem = getContext().getContentResolver().delete(NoteProvider.CONTENT_URI, where, whereArgs);

        if (deletedItem > 0) {
            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_LONG).show();
            sendMessageUsingEventBus();
        }
    }

    // If saving note is completed etText be clear and imgCheck1 is visible
    private void completedNote() {
        etNote.getText().clear();
        checkImgChecks(1);
    }

    // txt1, txt2 and txt3 click events
    // They change value of notePriority
    // If txt1 is clicked it will be 1
    // If txt2 is clicked it will be 2
    // If txt3 is clicked it will be 3
    private void txtClickEvents() {
        txt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePriority = 1;
                checkImgChecks(notePriority);
            }
        });

        txt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePriority = 2;
                checkImgChecks(notePriority);
            }
        });

        txt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notePriority = 3;
                checkImgChecks(notePriority);
            }
        });
    }

    // check imgChecks for notePriority
    private void checkImgChecks(int value) {
        if (value == 1) {
            imgCheck1.setVisibility(View.VISIBLE);
            imgCheck2.setVisibility(View.GONE);
            imgCheck3.setVisibility(View.GONE);
        } else if (value == 2) {
            imgCheck1.setVisibility(View.GONE);
            imgCheck2.setVisibility(View.VISIBLE);
            imgCheck3.setVisibility(View.GONE);
        } else {
            imgCheck1.setVisibility(View.GONE);
            imgCheck2.setVisibility(View.GONE);
            imgCheck3.setVisibility(View.VISIBLE);
        }
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
    }

    private void snackbarVisibleStatement() {
            if (snackbar != null && snackbar.isShown()) {
                snackbar.dismiss();
            }
    }

    // Second parameter is about note id at local database
    private void showSnackBar(String msg) {
        snackbar = Snackbar.make(fragmentNoteContent, msg, Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("Back", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etNote.setText(note);
                checkImgChecks(notePriority);
                deleteNote();
            }
        });
        snackbar.show();
    }

    //EventBus
    @Subscribe(sticky = true)
    public void onEvent(EventBusNoteMessage message) {
        noteId = message.getNoteId();
        note = message.getNote();
        notePriority = message.getNotePriority();

        Log.e("Error", "onEvent()");

        etNote.setText(note);
        checkImgChecks(notePriority);
    }

    // When any changing at data be send a message to ActiviyMain to update recyclerview
    private void sendMessageUsingEventBus() {
        EventBus.getDefault().postSticky(new EventBusChangeMessage(true));
    }
}
