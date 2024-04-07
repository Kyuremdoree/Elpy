package uqac.dim.elpy;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import com.arthenica.mobileffmpeg.FFmpeg;
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
import java.net.URLConnection;

import android.net.Uri;
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

        requestPermission();

        convertButton.setOnClickListener(v -> {
            String videoUrl = urlToConvert.getText().toString().trim();
            if (!videoUrl.isEmpty()) {
                // Vérifier si l'URL est valide
                if (isValidUrl(videoUrl)) {
                    // Lancer la tâche de conversion de la vidéo en audio
                    new ConvertAudioTask().execute(videoUrl);
                } else {
                    Toast.makeText(ConvertAudio.this, "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConvertAudio.this, "Please type a valid URL", Toast.LENGTH_SHORT).show();
            }
        });


    }

    protected void requestPermission() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("This app requires storage access to download the audio version of your link.")
                    .setTitle("Storage Permission")
                    .setCancelable(false)
                    .setPositiveButton("Ok", ((dialog, which) -> {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_CODE);
                        dialog.dismiss();
                    }))
                    .setNegativeButton("Cancel", ((dialog, which) -> {dialog.dismiss();}));
            builder.show();
        }
    }

    protected boolean isValidUrl(String url) {
        return android.util.Patterns.WEB_URL.matcher(url).matches();
    }


    private class ConvertAudioTask extends AsyncTask<String, Void, String> {

        private ProgressDialog progressDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ConvertAudio.this);
            progressDialog.setMessage("Download in progress...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            String videoUrl = urls[0];
            String videoFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath()+"/video.mp4";


            try {
                URL url = new URL(videoUrl);
                URLConnection connection = url.openConnection();
                connection.connect();
                // Obtenir la longueur de la vidéo si on doit l'utiliser
                int fileLength = connection.getContentLength();

                // Télécharger la vidéo
                InputStream input = new BufferedInputStream(url.openStream());
                FileOutputStream output = new FileOutputStream(videoFile);

                byte[] data = new byte[1024];
                int total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // Calcul progress du téléchargement si besoin de l'afficher
                    int progress = (int) ((total * 100) / fileLength);
                    Log.e("PASSAGE", "J'ECRIS LA VIDEO");
                    output.write(data, 0, count);
                }

            /*
            try {
                // Ouvrir une connexion HTTP pour télécharger la vidéo
                URL url = new URL(videoUrl);
                URLConnection connection = (URLConnection) url.openConnection();
                connection.connect();

                // Télécharger la vidéo et la sauvegarder dans le stockage externe
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES);
                //videoFile = new File(storageDir, "video.mp4");
                videoFile="";
                InputStream inputStream = new BufferedInputStream(url.openStream());
                FileOutputStream outputStream = new FileOutputStream(videoFile);


                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }


             */
                // Fermer les flux
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("DownloadVideoTask", "Error downloading the video", e);
            }
            return videoFile;
        }

        @Override
        protected void onPostExecute(String videoFile) {
            super.onPostExecute(videoFile);
            progressDialog.dismiss();
            if (videoFile != null) {
                /*
                // Déterminez le chemin de sortie pour le fichier audio
                File audioFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC), "audio.mp3");
                String audioFilePath = audioFile.getAbsolutePath();

                // Commande ffmpeg pour la conversion vidéo en audio
                String[] cmd = {"-i", videoFilePath, "-vn", "-acodec", "libmp3lame", "-ac", "2", "-ab", "160k", "-ar", "44100", audioFilePath};

                try {
                    // Exécutez la commande ffmpeg
                    Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();

                    // Attendre que le processus se termine
                    process.waitFor();
                    Log.e("afterProcessWaitFor", "J'ai fini d'attendre le process'");
                    // Vérifiez si le fichier audio existe après la conversion
                    if (audioFile.exists()) {
                        Log.e("audioFileExisting", "Je passe par ici donc ça existe");
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri audioUri = Uri.fromFile(audioFile);
                        intent.setDataAndType(audioUri, "audio/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                        startActivity(intent);

                        Toast.makeText(ConvertAudio.this, "Successful conversion", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {
                    Log.e("FFmpegConversion", "Error converting video to audio", e);
                    Toast.makeText(ConvertAudio.this, "Error converting video to audio", Toast.LENGTH_SHORT).show();
                }

                 */
                String audioPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath()+"/audio.mp3";
                String[] command = {"-i", videoFile, "-vn", "-acodec", "libmp3lame", "-ac", "2", "ab",
                        "160k", "-ar", "44100", audioPath };
                FFmpeg.execute(command);
                Toast.makeText(ConvertAudio.this, "Succesfully converted video",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ConvertAudio.this, "Error converting video",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                finish();
            } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Autorisations accordées", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
