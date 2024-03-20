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

public class NoteTaker extends AppCompatActivity {
    private EditText noteTaker_title;
    private EditText noteTaker_content;
    private ImageView save_note;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_taker);

        noteTaker_title = findViewById(R.id.noteTaker_title);
        noteTaker_content = findViewById(R.id.noteTaker_content);
        save_note = findViewById(R.id.save_note);

        save_note.setOnClickListener(v -> {
            String title = String.valueOf(noteTaker_title.getText());
            String content = String.valueOf(noteTaker_content.getText());

            if (content.isEmpty()) {
                Toast.makeText(NoteTaker.this, "Vous devez écrire un contenu pour cette note", Toast.LENGTH_SHORT).show();
            }
            else {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm a");
                Date date = new Date();
                note = new Note(0, title, content, formatter.format(date), R.color.orange, false);

                Intent intent = new Intent();
                intent.putExtra("note", note);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
}