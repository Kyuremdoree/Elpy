package uqac.dim.elpy;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.elpy.database.RoomDB;
import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.NotesListAdapter;

public class NoteSystem extends Fragment implements PopupMenu.OnMenuItemClickListener {
    private static final int ADD_NOTE_REQUEST_CODE = 101;
    private static final int EDIT_NOTE_REQUEST_CODE = 102;

    private NotesListAdapter notesListAdapter;
    private List<Note> notes = new ArrayList<>();
    private Note selectedNote;
    private RoomDB database;

    private RecyclerView note_home_container;
    private FloatingActionButton note_add;
    private SearchView note_home_search;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.note_system, container, false);
        database = RoomDB.getInstance(requireContext());

        note_home_container = view.findViewById(R.id.note_home_container);
        note_add = view.findViewById(R.id.note_add);
        note_home_search = view.findViewById(R.id.note_home_search);

        updateRecycler();

        note_add.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), NoteTaker.class);
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

        refreshNotes();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            Note note = (Note) data.getSerializableExtra("note");
            if (requestCode == ADD_NOTE_REQUEST_CODE) {
                database.mainDAO().insertNote(note);
            }
            else if (requestCode == EDIT_NOTE_REQUEST_CODE) {
                assert note != null;
                database.mainDAO().updateNote(note.getId(), note.getTitle(), note.getContent(), note.getColor());
            }
            resetFilter();
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
        notesListAdapter.setList(filteredList);
    }

    private void updateRecycler() {
        note_home_container.setHasFixedSize(true);
        note_home_container.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayout.VERTICAL));
        notesListAdapter = new NotesListAdapter(requireContext(), notes, noteClickListener);
        note_home_container.setAdapter(notesListAdapter);
    }

    private final INoteClickListener noteClickListener = new INoteClickListener() {
        @Override
        public void onClick(Note note) {
            Intent intent = new Intent(requireActivity(), NoteTaker.class);
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
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.note_longclick_popup);
        popupMenu.show();
    }
    
    private void refreshNotes() {
        notes.clear();
        notes.addAll(database.mainDAO().getAllNotes());
        notes.sort((o1, o2) -> {
            if (o1.isPinned() && !o2.isPinned()) {
                return -1;
            } else if (!o1.isPinned() && o2.isPinned()) {
                return 1;
            } else {
                return 0;
            }
        });
        notesListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.note_pin) {
            database.mainDAO().toggleNotePin(selectedNote.getId(), !selectedNote.isPinned());
            Toast.makeText(requireContext(),
                    selectedNote.isPinned() ? getString(R.string.note_unpinned) : getString(R.string.note_pinned),
                    Toast.LENGTH_SHORT).show();

            resetFilter();
            refreshNotes();
            return true;
        }
        else if (item.getItemId() == R.id.note_delete) {
            database.mainDAO().deleteNote(selectedNote);
            notes.remove(selectedNote);
            notesListAdapter.notifyDataSetChanged();
            Toast.makeText(requireContext(),
                    getString(R.string.note_deleted),
                    Toast.LENGTH_SHORT).show();
            resetFilter();
            return true;
        }
        return false;
    }

    private void resetFilter() {
        note_home_search.setQuery(null, false);
        notesListAdapter.setList(notes);
    }
}
