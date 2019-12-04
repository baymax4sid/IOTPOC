package com.example.iotpoc;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ArduinoConnector extends AppCompatActivity {

    private Button buttonScan;
    private WifiManager wifiManager;
    private ArrayAdapter adapter;
    private ListView listView;
    private Dialog myDialog;
    private ArrayList arrayList;
    private String gateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arduino_connector);

        arrayList = new ArrayList();
        buttonScan = findViewById(R.id.scanBtn);
        listView = findViewById(R.id.list);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        buttonScan.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //getWifiGateway();
               adapter.clear();
               new DeviceWifiManager().execute();
               adapter.notifyDataSetChanged();
           }
       });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                /*Toast.makeText(getApplicationContext(),
                        "Click ListItem Number " + position, Toast.LENGTH_LONG)
                        .show();
                */
                callLoginDialog((String) arrayList.get(position));
            }
        });

    }

    public void getWifiGateway(){
        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        String ipAddress = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        gateway = new WebViewEx().convertIpToDefaultGateway(ipAddress);
    }

    private class DeviceWifiManager extends AsyncTask<String, Void, List> {

        private ProgressDialog pd;

        // onPreExecute called before the doInBackgroud start for display
        // progress dialog.
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = ProgressDialog.show(ArduinoConnector.this, "", "Loading", true,
                    false); // Create and show Progress dialog
        }

        @Override
        protected List doInBackground(String... strings) {
            return getHtmlDoc();
        }

        @Override
        protected void onPostExecute(List result) {
            pd.dismiss();
            adapter.notifyDataSetChanged();
        }

        public List getHtmlDoc() {
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                final StringBuilder builder = new StringBuilder();

                try {
                    Document doc = Jsoup.connect("https://www.google.com/").get();
                    String title = doc.title();
                    Elements links = doc.select("a[href]");

                    builder.append(title).append("\n");

                    for (Element link : links) {
                        //builder.append("\n").append("Link : ").append(link.attr("href"))
                        //builder.append("\n").append("Text : ").append(link.text());
                        arrayList.add(builder.append(link.text()).toString());
                        adapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    builder.append("Error : ").append(e.getMessage()).append("\n");
                    System.out.println(builder.toString());
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //textView.setText(builder.toString());
                    }
                });
            }
        }).start();*/

            StringBuilder builder = new StringBuilder();

            try {
                Document doc = Jsoup.connect("https:/www.google.com/").get();
                String title = doc.title();
                Elements links = doc.select("a[href]");

                for (Element link : links) {
                    StringBuilder builder1 = new StringBuilder();
                    //builder.append("\n").append("Link : ").append(link.attr("href"))
                    //builder.append("\n").append("Text : ").append(link.text());
                    arrayList.add(builder1.append(link.text()).toString());
                }
                return arrayList;
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
                System.out.println(builder.toString());
                return null;
            }

        /*runOnUiThread(new Runnable() {

            @Override
            public void run() {
                //textView.setText(builder.toString());
            }
        });*/

        }
    }

    private void callLoginDialog(final String ssid)
    {
        myDialog = new Dialog(this);
        myDialog.setContentView(R.layout.credential_dialog);
        myDialog.setCancelable(false);

        TextView textView = myDialog.findViewById(R.id.username);
        final EditText editText= myDialog.findViewById(R.id.password);

        textView.setText(ssid);

        Button button= myDialog.findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        Button button1= myDialog.findViewById(R.id.cancel);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.show();
    }

}
