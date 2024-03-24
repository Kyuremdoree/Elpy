package uqac.dim.elpy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import uqac.dim.elpy.models.Note;
import yuku.ambilwarna.AmbilWarnaDialog;

public class NoteTaker extends AppCompatActivity {
    private EditText noteTaker_title;
    private EditText noteTaker_content;
    private ImageView noteTaker_colorPicker;
    private ImageView noteTaker_save;

    private Note note;
    private int noteColor;
    boolean isOldNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taker);

        noteTaker_title = findViewById(R.id.noteTaker_title);
        noteTaker_content = findViewById(R.id.noteTaker_content);
        noteTaker_colorPicker = findViewById(R.id.noteTaker_color_picker);
        noteTaker_save = findViewById(R.id.noteTaker_save);

        note = (Note) getIntent().getSerializableExtra("old_note");
        if (note != null) {
            noteTaker_title.setText(note.getTitle());
            noteTaker_content.setText(note.getContent());
            noteTaker_colorPicker.setBackgroundColor(note.getColor());
            noteColor = note.getColor();
            isOldNote = true;
        }

        noteTaker_colorPicker.setOnClickListener(v -> {
            AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, noteColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
                @Override
                public void onCancel(AmbilWarnaDialog dialog) {}

                @Override
                public void onOk(AmbilWarnaDialog dialog, int color) {
                    noteColor = color;
                    noteTaker_colorPicker.setBackgroundColor(color);
                }
            });
            ambilWarnaDialog.show();
        });

        noteTaker_save.setOnClickListener(v -> {
            String title = String.valueOf(noteTaker_title.getText());
            String content = String.valueOf(noteTaker_content.getText());

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(NoteTaker.this,
                        title.isEmpty() ? String.valueOf(R.string.title_required) : String.valueOf(R.string.content_required),
                        Toast.LENGTH_SHORT).show();
            }
            else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();

                if (!isOldNote) {
                    note = new Note(formatter.format(date));
                }
                note.setTitle(title);
                note.setContent(content);
                note.setColor(noteColor);

                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}