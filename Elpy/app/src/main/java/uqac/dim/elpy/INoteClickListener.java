package uqac.dim.elpy;

import androidx.cardview.widget.CardView;

import uqac.dim.elpy.models.Note;

public interface INoteClickListener {
    void onClick(Note note);
    void onLongClick(Note note, CardView view);
}
