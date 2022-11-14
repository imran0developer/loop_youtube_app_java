package com.unitapplications.loopyoutubeapp;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;

public class MainActivity extends YouTubeBaseActivity {
    YouTubePlayerView youTubePlayerView;
    private YouTubePlayer player;
    private Handler handler;

    private MyPlayerStateChangeListener playerStateChangeListener;
    private MyPlaybackEventListener playbackEventListener;
    String getLink;
    ImageView btStart, btStop;
    Button btPaste;
    ImageView start_icon , stop_icon;
    int curTime_start , curTime_stop,dot_index;
    String got_vid_id ;
    int cur_time;
    String sharedText;
    Runnable task;
    ProgressBar progressBar;
    ImageView how;
    TextView openYoutube,tx;
    Intent mgetLink;
    String id = "5MTIoYAR7BM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        playerStateChangeListener = new MyPlayerStateChangeListener();
        playbackEventListener = new MyPlaybackEventListener();
        tx  = findViewById(R.id.appName);

        youTubePlayerView = findViewById(R.id.youtube_player_view);

        //  etLink = findViewById(R.id.etLink);
        //    btPaste = findViewById(R.id.btPaste);

        btStart = findViewById(R.id.loop_start);
        btStop = findViewById(R.id.loop_stop);
        start_icon = findViewById(R.id.start_icon);
        stop_icon = findViewById(R.id.stop_icon);
        how = findViewById(R.id.how);
        progressBar = findViewById(R.id.progressBar_cyclic);


        YouTubePlayer.OnInitializedListener listener = new YouTubePlayer.OnInitializedListener() {
            private YouTubePlayer player;

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean b) {
                if (null == player) return;
                this.player = player;
                player.setShowFullscreenButton(false);

                how.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        player.cueVideo("5MTIoYAR7BM");
                        //   showInterAd();
                    }
                });

                btStop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        handler.removeCallbacks(task);
                        player.pause();
                        stop_icon.setImageResource(R.drawable.stop2);
                        start_icon.setImageResource(R.drawable.start2);

                        btStart.setVisibility(View.VISIBLE);
                        btStop.setVisibility(View.GONE);

                    }
                });

                start_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curTime_start = player.getCurrentTimeMillis();
                        start_icon.setImageResource(R.drawable.start1);
                        player.pause();



                    }
                });
                stop_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        curTime_stop = player.getCurrentTimeMillis();
                        stop_icon.setImageResource(R.drawable.stop1);
                        player.pause();


                    }
                });

                btStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        player.seekToMillis(curTime_start);
                        player.play();

                        task = new Runnable() {
                            @Override
                            // Thank you for this code https://stackoverflow.com/a/35723383/15973865
                            public void run() {
                                cur_time = player.getCurrentTimeMillis() / 1000;
                                if (cur_time == curTime_stop / 1000) {
                                    player.seekToMillis(curTime_start);
                                    player.play();
                                }
                                handler.postDelayed(this, 0);
                            }
                        };
                        handler.post(task);
                        btStart.setImageResource(R.drawable.loop_start);



                        btStart.setVisibility(View.GONE);
                        btStop.setVisibility(View.VISIBLE);
                    }
                });


                Intent intent = getIntent();
                String action = intent.getAction();
                String type = intent.getType();
                if (Intent.ACTION_SEND.equals(action) && type != null) {
                    if ("text/plain".equals(type)) {
                        handleSendText(intent); // Handle text being sent
                        dot_index = sharedText.lastIndexOf(".");
                        got_vid_id = sharedText.substring(dot_index + 4, dot_index + 15);

                        player.loadVideo(got_vid_id);

                    }}

                progressBar.setVisibility(View.GONE);
                youTubePlayerView.setVisibility(View.VISIBLE);

                //setRecyclerView();

                openYoutube = findViewById(R.id.openYoutube);
                openYoutube.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("https://www.youtube.com/"));
                        try {
                            MainActivity.this.startActivity(webIntent);
                        } catch (ActivityNotFoundException ex) {
                        }
                    }
                });

            }//initialization

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d("show", "intitalization failure");
            }
        };
        youTubePlayerView.initialize("AIzaSyBiwsg9tCg0cj0XKpknsLIP0r-pJ8xM4G0", listener);
        //youTubePlayerView.initialize("AIzaSyBiwsg9tCg0cj0XKpknsLIP0r-pJ8xM4G0", listener);




    }//onCreate

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }




    // making functions for these methods
    // tutorial this is
    // https://github.com/sitepoint-editors/Android-VideoTube/blob/master/app/src/main/java/com/echessa/videotube/MainActivity.java

    private final class MyPlaybackEventListener implements YouTubePlayer.PlaybackEventListener {

        @Override
        public void onPlaying() {
            // Called when playback starts, either due to user action or call to play().
        }

        @Override
        public void onPaused() {
            // Called when playback is paused, either due to user action or call to pause().
        }

        @Override
        public void onStopped() {
            // Called when playback stops for a reason other than being paused.
        }

        @Override
        public void onBuffering(boolean b) {
            // Called when buffering starts or ends.
        }

        @Override
        public void onSeekTo(int i) {
            // Called when a jump in playback position occurs, either
            // due to user scrubbing or call to seekRelativeMillis() or seekToMillis()
        }

    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
            //showMessage("video started");


        }

        @Override
        public void onVideoEnded() {
            // Called when the video reaches its end.
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    }
    public void log(String l) {
        Log.d("TAG", l);
    }

     /*
    public void pc_link_format(String pc_link){
        link = pc_link;
    equal_index = link.indexOf("=");
    got_vid_id = link.substring(equal_index+1 , equal_index+11);
    }*/

    /*
    public void mobile_link_format(String mobile_link){
        link = mobile_link;
        dot_index = mobile_link.indexOf(".");
        got_vid_id = mobile_link.substring(equal_index+4 , equal_index+15);

    }

     */
    void handleSendText(Intent intent) {
        sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {

            // Update UI to reflect text being shared
        }
    }



}