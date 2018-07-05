package com.example.project1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerView;

public class tab3_VideoActivity extends YouTubeBaseActivity implements

        YouTubePlayer.OnInitializedListener {

    final String serverKey="AIzaSyC6qPl9B3BO6oNCgEtLhpVH0w86niS4eQE";
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3_activity_video);
        youTubePlayerView = findViewById(R.id.youtubeplayer);
        youTubePlayerView.initialize(serverKey, this);
    }

    @Override
    public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) {
        Toast.makeText(this, "Initialization Fail", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasrestored) {
        youTubePlayer = player;
        Intent intent =getIntent();
        youTubePlayer.loadVideo(intent.getStringExtra("id"));
    }
}
