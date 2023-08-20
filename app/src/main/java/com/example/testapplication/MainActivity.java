package com.example.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private WebView mWebView;
    private ValueCallback<Uri[]> mFilePathCallback;

    String url;
    //url = "http://esp8266";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //String url;
        String upurl;
        url = "http://esp8266";
        //upurl = "http://esp8266/update";
        //upurl = "https://web.metl-group.xyz";
        upurl = "https://google.com";

        String finalUrl = url;
        String updateUrl = upurl;

        mWebView = findViewById(R.id.webview);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.loadUrl(url);
        //mWebView.loadUrl(finalUrl);


        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                mFilePathCallback = filePathCallback;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 0);
                return true;
            }
        });

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {
            if (mWebView.getVisibility() == View.VISIBLE) {
                mWebView.setVisibility(View.GONE);
                mWebView.loadUrl(finalUrl);
            } else {
                mWebView.loadUrl(updateUrl);
                mWebView.setVisibility(View.VISIBLE);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String finalUrl) {
                super.onPageFinished(view, finalUrl);

                // Hier wird der Button-Text Version ausgelesen und in einem Button in der App dargestellt
                mWebView.evaluateJavascript("document.querySelector('button[name=\"version\"]').innerText;", value -> {
                    Button versionButton = findViewById(R.id.version_button);
                    versionButton.setText(value.replace("\"", ""));
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK) {
            if (mFilePathCallback != null) {
                Uri[] result = new Uri[]{data.getData()};
                mFilePathCallback.onReceiveValue(result);
                mFilePathCallback = null;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static class HttpRequestTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    // Verarbeiten Sie die Antwort hier
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("HTTP-Verbindung", e.getMessage(), e);
                return null;
            }
            return null;
        }
    }

    public void onButtonClick(View view, int inputNumber) {
        String url = "http://esp8266";
        String finalUrl = "http://esp8266";
        String subUrl = url + "/get?input" + inputNumber + "=Submit";
        new HttpRequestTask().execute(subUrl);
    }

    public void onStartShutdownClick(View view) {
        onButtonClick(view, 1);
    }

    public void onRestartClick(View view) {
        onButtonClick(view, 2);
    }

    public void onForceShutdownClick(View view) {
        onButtonClick(view, 3);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }
}
