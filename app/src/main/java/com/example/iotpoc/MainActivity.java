package com.example.iotpoc;

        import android.Manifest;
        import android.app.Dialog;
        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.content.pm.PackageManager;
        import android.net.Uri;
        import android.net.wifi.ScanResult;
        import android.net.wifi.WifiConfiguration;
        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.os.Build;
        import android.os.Handler;
        import android.support.annotation.RequiresApi;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.text.format.Formatter;
        import android.view.View;
        import android.widget.*;

        import java.security.Permission;
        import java.util.ArrayList;
        import java.util.List;
        import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private ListView listView;
    private Button buttonScan;
    private int size = 0;
    private List<ScanResult> results;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayAdapter adapter;
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1001;
    private Dialog myDialog;
    private Switch aSwitch;
    final Handler handler = new Handler();
    private WifiInfo wifiInfo;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScan = findViewById(R.id.scanBtn);
        aSwitch=findViewById(R.id.wifiSwitch);
        progressBar = findViewById(R.id.progressBar_cyclic);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkRequiredPermissions();
            }
        });

        listView = findViewById(R.id.wifiList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String ssid= adapter.getItem(position).toString();
                callLoginDialog(ssid);
            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);
        checkRequiredPermissions();

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            wifiManager.setWifiEnabled(false);
                            buttonScan.setVisibility(View.INVISIBLE);
                            listView.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            wifiManager.setWifiEnabled(true);
                            buttonScan.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.VISIBLE);
                            checkRequiredPermissions();
                        }
                    });
                }
            }
        });
    }

    private void scanWifi() {
        arrayList.clear();
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning for WiFi connections...", Toast.LENGTH_SHORT).show();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();

            for (ScanResult scanResult : results) {
                arrayList.add(scanResult.SSID);
                adapter.notifyDataSetChanged();
            }

            unregisterReceiver(this);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            checkRequiredPermissions();
        }
    }

    private void checkRequiredPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

        }else{
            scanWifi();
            //doing, permission was previously granted; or legacy device
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
                connectToWiFi(ssid ,editText.getText().toString());
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void connectToWiFi(String ssid, String key)
    {
        final WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", key);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);

        progressBar.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(wifiManager.getConnectionInfo().getNetworkId()!=-1){
                    progressBar.setVisibility(View.INVISIBLE);
                    goToNextActivity();
                }
                else{
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "Incorrect Password. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        },4000);

/*
        while(!isWifiConnectionSuccessful())
        {

        }*/

//        goToNextActivity();
    }

    private void goToNextActivity()
    {
        Intent intent = new Intent(getApplicationContext(),ArduinoConnector.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private boolean isWifiConnectionSuccessful()
    {

        do{
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    wifiInfo=wifiManager.getConnectionInfo();
                }
            },500);
        }
        while(wifiInfo.getNetworkId()==-1 || Objects.equals(wifiInfo.getNetworkId(),null));

        return true;
    }
}