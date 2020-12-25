package com.card.splitter_pro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class videoPlayer extends AppCompatActivity {


    String DIR_OUTPUT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/Video Splitter";
    String VIDEO = "";
    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
        {
            VIDEO = getIntent().getStringExtra("video");
        }


        setContentView(R.layout.activity_video_player);
        videoView = findViewById(R.id.videoView);

        videoView.setVideoPath(DIR_OUTPUT+"/"+VIDEO);
        videoView.start();
        MediaController controller = new MediaController(videoPlayer.this);
        controller.setAnchorView(videoView);
        videoView.setMediaController(controller);

    }


    @Override
    public void onBackPressed() {
        videoView.pause();
        finish();
    }

}