package io.github.capturemathan.revisewithme;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TypeFragment extends Fragment {

    EditText e;
    Button b;
    ListView l;
    String txt, response;
    String ipaddr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_type, null);
        b = (Button) rootview.findViewById(R.id.typebtn);
        ipaddr = getString(R.string.ip_address);
        e = (EditText) rootview.findViewById(R.id.type);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt = e.getText().toString();
                new getresponse().execute();
            }
        });

        l = (ListView) rootview.findViewById(R.id.list);
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
            String modText = txt.replaceAll("/", " ");
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

}
