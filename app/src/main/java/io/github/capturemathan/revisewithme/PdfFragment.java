package io.github.capturemathan.revisewithme;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_OK;

public class PdfFragment extends Fragment {

    Button b;
    ListView l;
    String ipaddr;
    String filePath, response, parsedText = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_pdf, null);
        b = (Button) rootview.findViewById(R.id.pdfbtn);
        ipaddr = getString(R.string.ip_address);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Here", "Entering");
                new MaterialFilePicker()
                        .withSupportFragment(getTargetFragment())
                        .withRequestCode(1000)
                        .withHiddenFiles(true) // Show hidden files and folders
                        .start();
            }
        });

        l = (ListView) rootview.findViewById(R.id.listpdf);
        l.setVisibility(View.GONE);
        return rootview;
    }

    private class getresponse extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getActivity(), "Json Data is downloading", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpHandler sh = new HttpHandler();
            // Making a request to url and getting response
            String modText = parsedText.replaceAll("/", " ");
            String url = ipaddr + modText;
            String jsonStr = sh.makeServiceCall(url);

            Log.e("MainActivity", "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    response = jsonObj.getString("q");

                } catch (final JSONException e) {
                    Log.e("MainActivity", "Json parsing error: " + e.getMessage());
                }

            } else {
                Log.e("MainActivity", "Couldn't get json from server.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            String quesarray[] = response.split("\\?");
            for (int i=0;i<quesarray.length;i++) {
                quesarray[i]+=" ?";
            }
            l.setVisibility(View.VISIBLE);
            ArrayAdapter adapter = new ArrayAdapter<String>(getContext(), R.layout.listitem, quesarray);
            l.setAdapter(adapter);
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("Herem", "Entering");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            try {
                final String dirPath = filePath;
                Log.v("herem", filePath);
                PdfReader reader = new PdfReader(dirPath);
                int n = reader.getNumberOfPages();
                for (int i = 0; i < n; i++) {
                    parsedText = parsedText + PdfTextExtractor.getTextFromPage(reader, i + 1).trim() + "\n"; //Extracting the content from the different pages
                }
                reader.close();
            } catch (Exception e) {
                System.out.println(e);
            }
            new getresponse().execute();
            // Do anything with file
        }
    }

}
