package com.example.amankumar.lucidme.UI;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.amankumar.lucidme.R;
import com.example.amankumar.lucidme.Utils.Utils;

public class ExploreActivity extends AppCompatActivity {
    Toolbar toolbar;
    String url = "";
    private static WebView exploreWeb = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_explore);
        toolbar = (Toolbar) findViewById(R.id.appBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        exploreWeb = (WebView) findViewById(R.id.webview);
        webInit();
        url = getIntent().getStringExtra("site");
        exploreWeb.loadUrl(url);
        if(!Utils.haveNetworkConnection(this)){
            Snackbar.make(toolbar,"No network connection",Snackbar.LENGTH_INDEFINITE).show();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.exploreWeb.canGoBack()) {
            this.exploreWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void webInit() {
        WebSettings webSettings = exploreWeb.getSettings();
        webSettings.setJavaScriptEnabled(true);
        getWindow().setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
        exploreWeb.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String title = view.getTitle();
                if (!title.equals(""))
                    getSupportActionBar().setTitle(title);
                else
                    getSupportActionBar().setTitle("Explore");
            }
        });
        exploreWeb.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {

                //Make the bar disappear after URL is loaded, and changes string to Loading...
                setTitle("Loading...");
                setProgress(progress * 100); //Make the bar disappear after URL is loaded
                // Return the app name after finish loading
                if (progress == 100)
                    setTitle(R.string.app_name);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (!title.equals(""))
                    getSupportActionBar().setTitle(title);
                else
                    getSupportActionBar().setTitle("Explore");
            }
        });
    }
}
