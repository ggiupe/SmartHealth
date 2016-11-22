package com.example.giuseppe.smarthealth;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class VisualizzatoreWeb extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizzatore_web);
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.file_di_salvataggio_preferenze), Context.MODE_PRIVATE);
        String defaultServer = getResources().getString(R.string.server_addr_default);
        String server_addr = sharedPref.getString(getString(R.string.server_addr_current), defaultServer);
        WebView myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        myWebView.setWebViewClient(new WebViewClient());
        myWebView.loadUrl(server_addr);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebView myWebView = (WebView) findViewById(R.id.webView);
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        myWebView.clearCache(true);
        return super.onKeyDown(keyCode, event);
    }

    public void torna_alla_home(View view) {
        WebView myWebView = (WebView) findViewById(R.id.webView);
        myWebView.clearCache(true);
        super.onBackPressed();
    }
}
