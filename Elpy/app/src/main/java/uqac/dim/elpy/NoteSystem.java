package uqac.dim.elpy;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.elpy.models.Note;
import uqac.dim.elpy.models.NotesListAdapter;

public class NoteSystem extends AppCompatActivity {
    RecyclerView home_container;
    NotesListAdapter notesListAdapter;
    List<Note> notes = new ArrayList<>();

    FloatingActionButton add_note_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        home_container = findViewById(R.id.home_container);
        add_note_btn = findViewById(R.id.add_note_btn);

        updateRecycler();

        add_note_btn.setOnClickListener(v -> {
            
        });
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

        }

        @Override
        public void onLongClick(Note note, CardView view) {

        }
    };
}
