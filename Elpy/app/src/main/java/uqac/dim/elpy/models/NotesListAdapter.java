package uqac.dim.elpy.models;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import uqac.dim.elpy.INoteClickListener;
import uqac.dim.elpy.R;

public class NotesListAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    private Context context;
    private List<Note> notes;
    private INoteClickListener listener;

    public NotesListAdapter(Context context, List<Note> notes, INoteClickListener listener) {
        this.context = context;
        this.notes = notes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotesViewHolder(LayoutInflater.from(context).inflate(R.layout.notes_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        holder.getNote_title().setText(notes.get(position).getTitle());
        holder.getNote_title().setSelected(true);

        holder.getNote_content().setText(notes.get(position).getContent());

        holder.getNote_date().setText(notes.get(position).getDate());
        holder.getNote_date().setSelected(true);

        holder.getPin().setVisibility(notes.get(position).isPinned() ? View.VISIBLE : View.INVISIBLE);

        holder.getNotes_containter().setCardBackgroundColor(notes.get(position).getColor());

        holder.getNotes_containter().setOnClickListener(v -> {
            listener.onClick(notes.get(holder.getAdapterPosition()));
        });

        holder.getNotes_containter().setOnLongClickListener(v -> {
            listener.onLongClick(notes.get(holder.getAdapterPosition()), holder.getNotes_containter());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void filterList(List<Note> filteredList) {
        notes = filteredList;
        notifyDataSetChanged();
    }
}

class NotesViewHolder extends RecyclerView.ViewHolder {
    private CardView notes_containter;
    private TextView note_title;
    private TextView note_content;
    private TextView note_date;
    private ImageView pin;

    public NotesViewHolder(@NonNull View itemView) {
        super(itemView);

        notes_containter = itemView.findViewById(R.id.notes_container);
        note_title = itemView.findViewById(R.id.note_title);
        note_content = itemView.findViewById(R.id.note_content_preview);
        note_date = itemView.findViewById(R.id.note_date);
        pin = itemView.findViewById(R.id.pin);
    }

    public CardView getNotes_containter() {
        return notes_containter;
    }

    public TextView getNote_title() {
        return note_title;
    }

    public TextView getNote_content() {
        return note_content;
    }

    public TextView getNote_date() {
        return note_date;
    }

    public ImageView getPin() {
        return pin;
    }
}