package uqac.dim.elpy;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.NotesListAdapter;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NoteSystem extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final int ADD_NOTE_REQUEST_CODE = 101;
    private static final int EDIT_NOTE_REQUEST_CODE = 102;

    private NotesListAdapter notesListAdapter;
    private List<Note> notes = new ArrayList<>();
    private Note selectedNote;
    private RoomDB database;

    private RecyclerView note_home_container;
    private FloatingActionButton note_add;
    private SearchView note_home_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database = RoomDB.getInstance(this);
        notes = database.mainDAO().getAllNotes();

        setContentView(R.layout.activity_note_system);

        note_home_container = findViewById(R.id.note_home_container);
        note_add = findViewById(R.id.note_add);
        note_home_search = findViewById(R.id.note_home_search);

        updateRecycler();

        note_add.setOnClickListener(v -> {
            Intent intent = new Intent(NoteSystem.this, NoteTaker.class);
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE);
        });

        note_home_search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return true;
            }
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
                database.mainDAO().updateNote(note.getId(), note.getTitle(), note.getContent(), note.getColor());
            }
            refreshNotes();
        }
    }

    private void filter(String text) {
        List<Note> filteredList = new ArrayList<>();
        for (Note note : notes) {
            if (note.getTitle().toLowerCase().contains(text.toLowerCase())
                    || note.getContent().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(note);
            }
        }
        notesListAdapter.filterList(filteredList);
    }

    private void updateRecycler() {
        note_home_container.setHasFixedSize(true);
        note_home_container.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
        notesListAdapter = new NotesListAdapter(NoteSystem.this, notes, noteClickListener);
        note_home_container.setAdapter(notesListAdapter);
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
            selectedNote = note;
            showPopup(view);
        }
    };

    private void showPopup(CardView view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.note_longclick_popup);
        popupMenu.show();
    }
    
    private void refreshNotes() {
        notes.clear();
        notes.addAll(database.mainDAO().getAllNotes());
        notesListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.note_pin) {
            database.mainDAO().toggleNotePin(selectedNote.getId(), !selectedNote.isPinned());
            Toast.makeText(NoteSystem.this,
                    selectedNote.isPinned() ? String.valueOf(R.string.note_unpinned) : String.valueOf(R.string.note_pinned),
                    Toast.LENGTH_SHORT).show();

            refreshNotes();
            return true;
        }
        else if (item.getItemId() == R.id.note_delete) {
            database.mainDAO().deleteNote(selectedNote);
            notes.remove(selectedNote);
            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(NoteSystem.this,
                    String.valueOf(R.string.note_deleted),
                    Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}
