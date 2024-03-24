package uqac.dim.elpy;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

public class ConvertAudio extends AppCompatActivity {

    private Button convertButton;
    private EditText urlToConvert;
    private static final int REQUEST_PERMISSION_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.convertaudio);

        urlToConvert = findViewById(R.id.urlToConvert);
        convertButton = findViewById(R.id.convertButton);

        if (!checkPermissionFromDevice())
            requestPermission();
/*
        convertButton.setOnClickListener(v -> {
            String videoUrl = urlToConvert.getText().toString().trim();
            if (!videoUrl.isEmpty()) {
                // Vérifier si l'URL est valide
                if (isValidUrl(videoUrl)) {
                    // Lancer la tâche de conversion de la vidéo en audio
                    new ConvertVideoToAudioTask().execute(videoUrl);
                } else {
                    Toast.makeText(VideoToAudioConverterActivity.this, "URL invalide", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VideoToAudioConverterActivity.this, "Veuillez entrer une URL", Toast.LENGTH_SHORT).show();
            }
        });
        */
 
    }

    protected boolean checkPermissionFromDevice() {
        int writeExternalStorageResult = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int internetResult = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.INTERNET);
        return writeExternalStorageResult == PackageManager.PERMISSION_GRANTED &&
                internetResult == PackageManager.PERMISSION_GRANTED;
    }

    protected void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET
        }, REQUEST_PERMISSION_CODE);
    }

    protected boolean isValidUrl(String url) {
        return android.util.Patterns.WEB_URL.matcher(url).matches();
    }

}