package com.example.remotecamera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.VideoView;

import com.example.remotecamera.mqtt.MQTTHelper;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;


public class  MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    SwitchCompat cameraSwitch;
    PlayerView playerView;
    SimpleExoPlayer exoPlayer;
    ProgressDialog progressDialog;
    String rtspLink = "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mp4";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        playerView = findViewById(R.id.view1);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("buffering");
        progressDialog.setCancelable(true);
        playVideo();
    }
    private void playVideo() {
        try {
            exoPlayer = new SimpleExoPlayer.Builder(this).build();
            playerView.setPlayer(exoPlayer);
            MediaSource mediaSource = new RtspMediaSource.Factory()
                    .createMediaSource(MediaItem.fromUri(rtspLink));
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.play();

        }catch (Exception e){
            progressDialog.dismiss();

        }
    }
}