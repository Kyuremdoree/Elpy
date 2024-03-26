package uqac.dim.elpy;


import android.app.ProgressDialog;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;



import java.io.BufferedInputStream;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast; 

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

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoUrl = urlToConvert.getText().toString().trim();
                if (!videoUrl.isEmpty()) {
                    // Vérifier si l'URL est valide
                    if (isValidUrl(videoUrl)) {
                        // Lancer la tâche de conversion de la vidéo en audio
                        new ConvertAudioTask().execute(videoUrl);
                    } else {
                        Toast.makeText(ConvertAudio.this, "URL invalide", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ConvertAudio.this, "Veuillez entrer une URL", Toast.LENGTH_SHORT).show();
                }
            }
        });
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


    private class ConvertAudioTask extends AsyncTask<String, Void, File> {

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConvertAudio.this);
            progressDialog.setMessage("Téléchargement de la vidéo en cours...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected File doInBackground(String... urls) {
            String videoUrl = urls[0];
            File videoFile = null;
            try {
                // Ouvrir une connexion HTTP pour télécharger la vidéo
                URL url = new URL(videoUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Télécharger la vidéo et la sauvegarder dans le stockage externe
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                videoFile = new File(storageDir, "video.mp4");
                FileOutputStream outputStream = new FileOutputStream(videoFile);
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                // Fermer les flux
                outputStream.close();
                inputStream.close();
            } catch (Exception e) {
                Log.e("DownloadVideoTask", "Erreur lors du téléchargement de la vidéo", e);
            }
            return videoFile;
        }

        @Override
        protected void onPostExecute(File videoFile) {
            super.onPostExecute(videoFile);
            progressDialog.dismiss();
            if (videoFile != null) {
                // Si la vidéo est téléchargée avec succès, vous pouvez gérer la conversion ici
                // Assurez-vous d'ajouter la logique de conversion appropriée
                // Vous pouvez utiliser une bibliothèque tierce ou implémenter votre propre logique de conversion
                // Une fois la conversion terminée, vous pouvez télécharger le fichier audio ou effectuer d'autres actions
                // Pour l'exemple, nous allons simplement afficher un message
                Toast.makeText(ConvertAudio.this, "Vidéo téléchargée avec succès", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConvertAudio.this, "Erreur lors du téléchargement de la vidéo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Autorisations accordées", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Autorisations refusées", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
