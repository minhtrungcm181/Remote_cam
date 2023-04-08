package com.example.remotecamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.VideoView;

import com.example.remotecamera.mqtt.MQTTHelper;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.WeakHashMap;


public class  MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    SwitchCompat cameraSwitch;

    ProgressDialog progressDialog;
    String httpLink = "http://192.168.1.38:5000/video_feed";
    WebView webview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webview = findViewById(R.id.webview1);
        webview.setWebViewClient(new WebViewClient());
        webview.loadUrl(httpLink);

    }

    }
