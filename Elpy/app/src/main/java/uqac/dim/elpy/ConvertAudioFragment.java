package uqac.dim.elpy;
import com.arthenica.mobileffmpeg.FFmpeg;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ConvertAudioFragment extends Fragment {

    private Button convertButton;
    private EditText urlToConvert;
    private static final int REQUEST_PERMISSION_CODE = 100;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.convertaudio, container, false);

        urlToConvert = view.findViewById(R.id.urlToConvert);
        convertButton = view.findViewById(R.id.convertButton);

        requestPermission();

        convertButton.setOnClickListener(v -> {
            String videoUrl = urlToConvert.getText().toString().trim();
            if (!videoUrl.isEmpty()) {
                // Vérifier si l'URL est valide
                if (isValidUrl(videoUrl)) {
                    // Lancer la tâche de conversion de la vidéo en audio
                    new ConvertAudioFragment.ConvertAudioTask().execute(videoUrl);
                } else {
                    Toast.makeText(requireContext(), "Invalid URL", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "Please type a valid URL", Toast.LENGTH_SHORT).show();
            }
        });


        return view;
    }

    protected void requestPermission() {
        if (ActivityCompat.checkSelfPermission(requireActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage("This app requires storage access to download the audio version of your link.")
                    .setTitle("Storage Permission")
                    .setCancelable(false)
                    .setPositiveButton("Ok", ((dialog, which) -> {
                        ActivityCompat.requestPermissions(requireActivity(),
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
            progressDialog = new ProgressDialog(requireContext());
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

                String audioPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        .getAbsolutePath()+"/audio.mp3";
                String[] command = {"-i", videoFile, "-vn", "-acodec", "libmp3lame", "-ac", "2", "ab",
                        "160k", "-ar", "44100", audioPath };
                FFmpeg.execute(command);
                Toast.makeText(requireContext(), "Succesfully converted video",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "Error converting video",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getActivity().finish();
            } else if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(requireContext(), "Autorisations accordées", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
