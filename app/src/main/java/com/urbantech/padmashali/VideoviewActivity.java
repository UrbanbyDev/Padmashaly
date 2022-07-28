package com.urbantech.padmashali;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoviewActivity extends AppCompatActivity {

    // Your Video URL
    String videoUrl = "https://media.geeksforgeeks.org/wp-content/uploads/20201217192146/Screenrecorder-2020-12-17-19-17-36-828.mp4?_=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        Bundle bundle = getIntent().getExtras();
        videoUrl = bundle.getString("url");
        // finding videoview by its id
        VideoView videoView = findViewById(R.id.videoView);

        // Uri object to refer the
        // resource from the videoUrl
        Uri uri = Uri.parse(videoUrl);

        // sets the resource from the
        // videoUrl to the videoView
        videoView.setVideoURI(uri);

        // creating object of
        // media controller class
        MediaController mediaController = new MediaController(this);

        // sets the anchor view
        // anchor view for the videoView
        mediaController.setAnchorView(videoView);

        // sets the media player to the videoView
        mediaController.setMediaPlayer(videoView);

        // sets the media controller to the videoView
        videoView.setMediaController(mediaController);

        // starts the video
        videoView.start();
    }
}