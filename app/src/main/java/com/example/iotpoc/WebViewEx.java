package com.example.iotpoc;

import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

public class WebViewEx extends AppCompatActivity {

    private String defaultGateway;
    private String ipAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_ex);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);

        WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        ipAddress = Formatter.formatIpAddress(wifiInfo.getIpAddress());
        defaultGateway=convertIpToDefaultGateway(ipAddress);

        final WebView webView= findViewById(R.id.webView);
        webView.setWebViewClient(new MyBrowser());

        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);

        webView.setWebViewClient(new MyBrowser());

        Toast.makeText(this, ipAddress, Toast.LENGTH_LONG).show();
        webView.loadUrl(defaultGateway);

        Button button = findViewById(R.id.refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });
    }

    public String convertIpToDefaultGateway(String ip)
    {
        ip=ip.substring(0,ip.lastIndexOf('.')+1);
        ip=ip+"1";
        //Toast.makeText(this, ip, Toast.LENGTH_LONG).show();
        return ip;
    }

    private class MyBrowser extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            view.loadUrl(url);
            return true;

        }
    }
}
