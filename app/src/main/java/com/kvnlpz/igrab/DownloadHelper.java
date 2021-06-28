package com.kvnlpz.igrab;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;
import java.io.File;
import java.security.PrivateKey;

public class DownloadHelper{

    String URLpart = "https://scontent-lax3-2.cdninstagram.com";
    String URLpart2 = "https://scontent-lax3-1.cdninstagram.com";



    boolean downloadLowerResolution = false;
    DownloadManager dm;
    Context context;
    public DownloadHelper(DownloadManager dm, Context ctx, boolean downloadLowerResolution){
        this.context = ctx;
        this.dm = dm;
        this.downloadLowerResolution = downloadLowerResolution;
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//    }

    private String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();


//    private DownloadManager dm =

    public void checkURL(String url) {
        Log.d("URLCHECKER", "checking the URL");

        //URL used for images that are 1080 resolution
        if (((url.contains(URLpart)) || url.contains(URLpart2)) && url.contains("1080") && url.contains("jpg")) {
            try {
                if (!doesFileExist(url, ".jpg")) {
                    downloadImage(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        //URL used for lower resolutions
        if (((url.contains(URLpart)) || url.contains(URLpart2)) && !url.contains("p1080") && url.contains("jpg") && downloadLowerResolution) {
            try {
                Log.d("filechecker","CHECKing if file exists");
                if (!doesFileExist(url, ".jpg")) {
                    downloadImage(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (((url.contains(URLpart)) || url.contains(URLpart2)) && url.contains("mp4")) {
            try {
                boolean exists = doesFileExist(url, ".mp4");
                if (!exists) {
                    Log.d("filechecker","CHECKing if file exists");
                    downloadVideo(url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void downloadImage(String downloadUrlOfImage) {
        try {
            String filename = makeFileTitle(downloadUrlOfImage);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "IGrab" + File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(context, "Image download started.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }

    public void downloadVideo(String downloadUrlOfVideo) {
        try {
            Log.d("downloader","Downloading video");
            String filename = makeFileTitle(downloadUrlOfVideo);
            Uri downloadUri = Uri.parse(downloadUrlOfVideo);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("video/mp4") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "IGrab" + File.separator + filename + ".mp4");
            dm.enqueue(request);
            Toast.makeText(context, "Video download successful.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "Video download failed.", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean doesFileExist(String url, String extension) {
        Log.d("doesFileExist", "inside function");
        File myDir = new File(root + "/IGrab");
        String title = url.substring((url.lastIndexOf('/') + 1), url.lastIndexOf('?') - 5);
        //String title = url.substring(url.lastIndexOf('/'), url.lastIndexOf('?')-5);
        File file = new File(myDir + File.separator + title + extension);
        Log.d("Location:", myDir + File.separator + title + extension);
        if (file.isFile()) {
            Log.d("resource", "FILE NOT FOUND");
            return true;
        } else {
            Log.d("resource", "FILE FOUND ALREADY");
            return false;
        }
    }

    private String makeFileTitle(String url) {
        return url.substring((url.lastIndexOf('/') + 1), url.lastIndexOf('?') - 5);

    }

    public static void showToast(Context mContext,String message){
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public void setDownloadLowerResolution(boolean bool){
        this.downloadLowerResolution = bool;
    }

}
