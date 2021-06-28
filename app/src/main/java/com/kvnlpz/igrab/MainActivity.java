package com.kvnlpz.igrab;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.time.chrono.MinguoChronology;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Storage Permissions variables
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    public boolean downloadBool, downloadVideoBool;
    private DownloadManager dm;
    private Button quitButton, downloadButton;
    private WebView browser;
    private String url;
    private MenuItem videoMode;
    private DownloadHelper downloadHelper;

    //permission method.
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have read or write permission
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (writePermission != PackageManager.PERMISSION_GRANTED || readPermission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dl = (DrawerLayout) findViewById(R.id.drawer_part);
        t = new ActionBarDrawerToggle(this, dl, R.string.Open, R.string.Close);
        t.setDrawerIndicatorEnabled(true);
        videoMode = (MenuItem) findViewById(R.id.lower_quality_item);
//        videoMode.setOnMenuItemClickListener((MenuItem item) ->{
//            downloadVideoBool = !downloadVideoBool;
//            return true;
//        });

        dl.addDrawerListener(t);
        t.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nv);


        downloadBool = false;
        downloadVideoBool = false;
        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        downloadHelper = new DownloadHelper(dm, getApplicationContext(), downloadVideoBool);
        verifyStoragePermissions(this);
        browser = (WebView) findViewById(R.id.web_view);
        quitButton = (Button) findViewById(R.id.Quit);
        quitButton.setOnClickListener((View v) -> finishAndRemoveTask());
        downloadButton = (Button) findViewById(R.id.Download);
        nv.setNavigationItemSelectedListener(this);
        nv.bringToFront();


//        nv.setNavigationItemSelectedListener(item -> {
//            Log.d("menu", "Menu item clicked!");
//            return true;
//
//        });


        downloadButton.setOnClickListener((View v) -> {
            downloadBool = !downloadBool;   //set it to true if you click it the first time
            if (downloadBool) {
                downloadButton.setBackgroundColor(Color.GREEN);
                downloadButton.setText(R.string.download_status_ON);
                Log.d("STATUS", "ON");
            } else {
                downloadButton.setBackgroundColor(getResources().getColor(R.color.blackColorPrimary));
                downloadButton.setText(R.string.download_satus_OFF);
                Log.d("STATUS", "OFF");
            }
        });
        url = "https://www.instagram.com";
        setUpBrowser();
        browser.loadUrl(url);


    }


    @SuppressLint("SetJavaScriptEnabled")
    private void setUpBrowser() {
        browser.setWebViewClient(new MyBrowser());
        browser.getSettings().setLoadsImagesAutomatically(true);
        browser.getSettings().setJavaScriptEnabled(true);
        browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        browser.getSettings().setSupportMultipleWindows(false);
        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(browser.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }

        if(WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
            WebSettingsCompat.setForceDarkStrategy(browser.getSettings(), WebSettingsCompat.DARK_STRATEGY_PREFER_WEB_THEME_OVER_USER_AGENT_DARKENING);
        }
        browser.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 200) {
            boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
    }


    private void downloadImage(String filename, String downloadUrlOfImage) {
        try {
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "IGrab" + File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(this, "Image download started.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }
    private void downloadVideo(String filename, String downloadUrlOfVideo) {
        try {
            Uri downloadUri = Uri.parse(downloadUrlOfVideo);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("video/mp4") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "IGrab" + File.separator + filename + ".mp4");
            dm.enqueue(request);
            Toast.makeText(this, "Video download started.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Video download failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if(id == R.id.lower_quality_item){
                Log.d("MENU", "MODE ONE CLICKED");
                Toast.makeText(this, "LOWMODE: "+ !downloadVideoBool, Toast.LENGTH_SHORT).show();
                downloadHelper.setDownloadLowerResolution(!downloadVideoBool);
            }
            else if(id == R.id.downloads){
                Log.d("MENU", "DOWNLOADS ONE CLICKED");
                Toast.makeText(this, "DOWNLOADS MENUITEM PRESSED", Toast.LENGTH_SHORT).show();
            }

            else if(id == R.id.settings){
                Log.d("MENU", "SETTINGS ONE CLICKED");
                Toast.makeText(this, "SETTINGS MENUITEM PRESSED", Toast.LENGTH_SHORT).show();
            }
            else if(id == R.id.donation_page){
                Log.d("MENU", "DONATIONS ONE CLICKED");
            Toast.makeText(this, "DONATIONS MENUITEM PRESSED", Toast.LENGTH_SHORT).show();
            showDonationPage(findViewById(R.id.main_view));

        }

        return true;
    }


    private class MyBrowser extends WebViewClient {


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
//            try{
//                Log.d("AppDisabler", "attempting to close popup");
//                String js = "javascript:(function(){"+
//                        "l=document.getElementsByClassName('aOOlW   HoLwm')[0];"+
//                        "l.click();"+
//                        "})()";
//                browser.evaluateJavascript(js, new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String s) {
//                        String result = s;
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try{
//                Log.d("AppDisabler", "attempting to close popup");
//                String js = "javascript:(function(){"+
//                        "l=document.getElementsByClassName('sqdOP yWX7d    y3zKF   cB_4K  ')[0];"+
//                        "l.click();"+
//                        "})()";
//                browser.evaluateJavascript(js, new ValueCallback<String>() {
//                    @Override
//                    public void onReceiveValue(String s) {
//                        String result = s;
//                    }
//                });
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            browser.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            if (url.contains(".jpg")) {
                try {
                    //Log.println(Log.VERBOSE, "web", url);
                    //new DownloadFileFromURL().execute(url);
                } catch (Exception e) {
                    Log.d("BROWSER", "Exception", e);
                    e.printStackTrace();
                }
            }
            return super.shouldInterceptRequest(view, request);
        }


        @SuppressLint("NewApi")
        @Override
        public void onLoadResource(WebView view, String url) {
            if(downloadBool){
                Log.d("ONLOAD","CALLING DOWNLOADHELPER");
                downloadHelper.checkURL(url);
            }
        }
    }


    public void showDonationPage(View view){
        Intent intent = new Intent(this, donation.class);
        startActivity(intent);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    public void useAppDisabler(){

    }

}