package com.moonturns.takeanote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private final String FRAGMENT_TAG_NAME = "newNoteFragment"; // Fragment tag name

    private int count = 0; // recyclerview scroll control

    private ArrayList<Integer> noteIdList; // This list takes integer data from sqlite
    private ArrayList<String> noteList; // This list takes string data from sqlite
    private ArrayList<Integer> notePriorityList; // This list takes integer data from sqlite

    private ConstraintLayout contentContainer;
    private RecyclerView rvNote;
    private FloatingActionButton fabNewNote;

    // Initialize widgets from main_activity
    private void crt() {
        contentContainer = this.findViewById(R.id.contentContainer);
        rvNote = this.findViewById(R.id.rvNote);
        fabNewNote = this.findViewById(R.id.fabNewNote);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        crt();
        clickEvenFab();
        getDataFromLocalDtabase();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
}

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If findFragmentByTag does not retrun null
        // contentContainer will be GONE
        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_NAME) != null) {
            contentContainer.setVisibility(View.GONE);
        }
    }

    // Click event fabNewNote
    // Open NewNoteFragment
    private void clickEvenFab() {
        fabNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewNoteFragment();
            }
        });
    }

    // Set adapter and layout manager for rvNote.
    private void setRvNote() {

        final RVAdapter adapter = new RVAdapter(this, noteIdList, noteList, notePriorityList);
        final StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_fall_down);

        rvNote.setLayoutAnimation(controller);
        rvNote.setAdapter(adapter);
        rvNote.setLayoutManager(gridLayoutManager);
        rvNote.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && count == 0) {
                    fabNewNote.animate().translationXBy(500).start();
                    count = 1;
                }else if (dy < 0 && count == 1) {
                    fabNewNote.animate().translationX(0).start();
                    count = 0;
                }
            }
        });

        rvNote.setLayoutAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //gridLayoutManager.findViewByPosition(0).clearAnimation();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // Conncet to local database and read data then add datas to noteList and notePriority
    private void getDataFromLocalDtabase() {
        noteIdList = new ArrayList<>();
        noteList = new ArrayList<>();
        notePriorityList = new ArrayList<>();

        String[] projections = {DatabaseContact.NotesEntry.COLUMN_ID, DatabaseContact.NotesEntry.COLUMN_NOTE, DatabaseContact.NotesEntry.COLUMN_PRIORITY};
        Cursor cursor = getContentResolver().query(NoteProvider.CONTENT_URI, projections, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                noteIdList.add(cursor.getInt(cursor.getColumnIndex(DatabaseContact.NotesEntry.COLUMN_ID)));
                noteList.add(cursor.getString(cursor.getColumnIndex(DatabaseContact.NotesEntry.COLUMN_NOTE)));
                notePriorityList.add(cursor.getInt(cursor.getColumnIndex(DatabaseContact.NotesEntry.COLUMN_PRIORITY)));
            }
        }
        cursor.close();

        setRvNote();
        cursor.close();
    }

    // This method is used to open NewOpenFragment and
    // contentContainer be GONE
    // When fragment is closed contentContainer will be VISIBLE at onBackPress()
    public void openNewNoteFragment() {
        NewNoteFragment newNoteFragment = new NewNoteFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.add(R.id.mainContainer, newNoteFragment, FRAGMENT_TAG_NAME);
        //transaction.addToBackStack(null);
        transaction.commit();

        contentContainer.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_NAME) != null) {
            transaction.remove(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG_NAME));
            transaction.commit();
            contentContainer.setVisibility(View.VISIBLE);
        }else {
            super.onBackPressed();
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(EventBusChangeMessage message) {
        getDataFromLocalDtabase();
        EventBus.getDefault().removeAllStickyEvents();
    }
}
