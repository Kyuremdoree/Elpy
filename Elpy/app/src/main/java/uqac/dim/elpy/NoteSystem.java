package uqac.dim.elpy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.NotesListAdapter;

public class NoteSystem extends AppCompatActivity {
    private RecyclerView home_container;
    private NotesListAdapter notesListAdapter;
    private List<Note> notes = new ArrayList<>();

    private FloatingActionButton add_note_btn;
    private static final int ADD_NOTE_REQUEST_CODE = 101;
    private static final int EDIT_NOTE_REQUEST_CODE = 102;

    private RoomDB database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAllNotes();

        setContentView(R.layout.activity_note_system);

        home_container = findViewById(R.id.home_container);
        add_note_btn = findViewById(R.id.add_note_btn);

        updateRecycler();

        add_note_btn.setOnClickListener(v -> {
            Intent intent = new Intent(NoteSystem.this, NoteTaker.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            Note note = (Note) data.getSerializableExtra("note");
            if (requestCode == ADD_NOTE_REQUEST_CODE) {
                database.mainDAO().insertNote(note);
            }
            else if (requestCode == EDIT_NOTE_REQUEST_CODE) {
                database.mainDAO().updateNote(note.getId(), note.getTitle(), note.getContent());

            }
            notes.clear();
            notes.addAll(database.mainDAO().getAllNotes());
            notesListAdapter.notifyDataSetChanged();
        }
    }

    private void updateRecycler() {
        home_container.setHasFixedSize(true);
        home_container.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
        notesListAdapter = new NotesListAdapter(NoteSystem.this, notes, noteClickListener);
        home_container.setAdapter(notesListAdapter);
    }

    private final INoteClickListener noteClickListener = new INoteClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(NoteSystem.this, NoteTaker.class);
            intent.putExtra("old_note", note);
            startActivityForResult(intent, EDIT_NOTE_REQUEST_CODE);
        }

        @Override
        public void onLongClick(Note note, CardView view) {

        }
    };
}
