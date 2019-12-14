package com.example.iotpoc;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main2Activity extends AppCompatActivity {

    private TextView textView;
    private ProgressBar progressBar;
    private String apiLink;
    private String ssid;
    final private Handler handler = new Handler();
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        textView = findViewById(R.id.textView);
        progressBar = findViewById(R.id.progressBar_cyclic);

        ssid = getIntent().getExtras().getString("ssid");
        password = getIntent().getExtras().getString("password");
        apiLink = getIntent().getExtras().getString("gateway");

        progressBar.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new HitWebApi().execute();
            }
        },1000);

        //progressBar = findViewById(R.id.p)
    }

    private class HitWebApi extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
//            progressBar.setVisibility(View.VISIBLE);
            textView.setText("");
        }

        protected String doInBackground(Void... urls) {
            // Do some validation here

            apiLink = apiLink + "/wifisave?s=" + ssid + "&p=" + password;
            StringBuilder builder = new StringBuilder();

            try {
//                Document doc = Jsoup.connect("https:/www.google.com/").get();
                Document doc = Jsoup.connect(apiLink).get();
                Elements links = doc.select("div");
                builder.append(links.text()).toString();
                return builder.toString();
            }
            catch (IOException e) {
                String error = builder.append("Error : ").append(e.getMessage()).append("\n").toString();
                return error;
            }

            /*try {
                apiLink = apiLink + "/wifisave?s=" + ssid + "&p=" + password;
                URL url = new URL(apiLink);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }*/
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }

            Log.i("INFO", response);
            textView.setText(response);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
