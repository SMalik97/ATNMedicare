package com.atnmedicareapp;


import android.Manifest;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;

import android.os.Build;

import android.os.Bundle;

import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;

import android.webkit.WebChromeClient;

import android.webkit.WebView;

import android.webkit.WebViewClient;

import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class Home extends AppCompatActivity {
    static WebView mWebView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    SwipeRefreshLayout swipe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home);


            getSupportActionBar().hide();
        //requested for storage permission
        if (Build.VERSION.SDK_INT>=23) {
            runtimePermission();
        }





        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipe=(SwipeRefreshLayout)findViewById(R.id.swipeRefresh);



        //swipe refresh...........
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mWebView.loadUrl(mWebView.getUrl());
                swipe.setRefreshing(true);
            }
        });

        mWebView=(WebView)findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowFileAccess(true);
        mWebView.getSettings().setAllowContentAccess(true);
        mWebView.loadUrl("http://atnmedicare.com/atnkunmadmunmedicare/");//https://atnbanglagroup.com/atnkunmadmunmedicare


        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String      contentDisposition , String mimeType, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimeType);
//------------------------COOKIE!!------------------------
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
//------------------------COOKIE!!------------------------
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_LONG).show();
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override

            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                super.onPageStarted(view, url, favicon);
            }

            @Override

            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                swipe.setRefreshing(false);
            }

            //to make call
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url){
                if (url.startsWith("tel:")){
                    Intent intent=new Intent(Intent.ACTION_DIAL,Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if (url.startsWith("mailto:")){
                    Intent f = new Intent(Intent.ACTION_SEND);
                    f.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@atnmedicare.com"});
                    f.putExtra(Intent.EXTRA_SUBJECT, "Feedback from ATN Medicare");
                    f.putExtra(Intent.EXTRA_TEXT, "Message: ");
                    f.setType("message/email");
                    f.setPackage("com.google.android.gm");
                    startActivity(Intent.createChooser(f, "Send Feedback"));
                    return true;
                }
                return false;
            }

        });


        mWebView.setWebChromeClient(new WebChromeClient() {
            // For 3.0+ Devices (Start)

            @Override

            public void onProgressChanged(WebView view, int newProgress) {

                super.onProgressChanged(view, newProgress);
            }
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }
            @Override

            public void onReceivedTitle(WebView view, String title) {

                super.onReceivedTitle(view, title);
                //getSupportActionBar().setTitle(title);


            }



            @Override

            public void onReceivedIcon(WebView view, Bitmap icon) {

                super.onReceivedIcon(view, icon);


            }


            // For Lollipop 5.0+ Devices
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = fileChooserParams.createIntent();
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    Toast.makeText(getApplicationContext(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (requestCode == REQUEST_SELECT_FILE)
            {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        }
        else if (requestCode == FILECHOOSER_RESULTCODE)
        {
            if (null == mUploadMessage)
                return;
// Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
// Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
        else
            Toast.makeText(getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();

    }



    @Override

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater myMenuInflater = getMenuInflater();

        myMenuInflater.inflate(R.menu.super_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }



    @Override

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {



            case R.id.exit:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);

                dialog.setTitle("Exit App");

                dialog.setMessage("Do you want to exit?");

                dialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        finishAffinity();

                    }

                });

                dialog.setCancelable(false);

                dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override

                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }

                }).show();

                break;



        }

        return true;

    }




    @Override

    public void onBackPressed() {

        if (mWebView.canGoBack()) {

            mWebView.goBack();

        } else {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);

            dialog.setTitle("Exit App");

            dialog.setMessage("Do you want to exit?");

            dialog.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override

                public void onClick(DialogInterface dialog, int which) {


                    finishAffinity();

                }

            });

            dialog.setCancelable(false);

            dialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override

                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();

                }

            }).show();



        }

    }
    //method for runtime permission
    public void runtimePermission(){
        Dexter.withActivity(this).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {


            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {

            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }



}


