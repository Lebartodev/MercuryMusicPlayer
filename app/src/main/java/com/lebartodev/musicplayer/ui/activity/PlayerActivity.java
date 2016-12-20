package com.lebartodev.musicplayer.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.lebartodev.musicplayer.Consts;
import com.lebartodev.musicplayer.MusicService;
import com.lebartodev.musicplayer.R;
import com.lebartodev.musicplayer.model.Track;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;


@EActivity(R.layout.activity_player)
public class PlayerActivity extends AppCompatActivity {
    private MusicService musicSrv;
    private Intent playIntent;
    @Extra
    Track track;
    @ViewById
    SeekBar progress;
    @ViewById(R.id.track_name)
    TextView trackName;
    @ViewById(R.id.track_artist)
    TextView trackArtist;
    @ViewById(R.id.controllers_layout)
    RelativeLayout controllersLayout;
    @ViewById
    ImageView album_art;
    @ViewById(R.id.back_button)
    ImageView backButton;
    @ViewById(R.id.forward_button)
    ImageView nextButton;
    @ViewById(R.id.play_button)
    ImageView playButton;
    private boolean first = true;

    private boolean isLayoutShow = true;


    public static final String TAG = "TrackFragment";

    BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MusicService", String.valueOf(intent.getBooleanExtra(Consts.PLAYING_CONTENT, false)));
            if (intent.getBooleanExtra(Consts.PLAYING_CONTENT, false)) {
                playButton.setImageResource(R.drawable.ic_pause);
            } else {
                playButton.setImageResource(R.drawable.ic_play_white);
            }
            setProgressInt(intent.getIntExtra(Consts.PROGRESS_CONTENT, 0));
            initTrack((Track)intent.getParcelableExtra(Consts.TRACK_CONTENT));


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter playingIntent = new IntentFilter(Consts.PLAY_RESPONSE_INTENT);
        registerReceiver(playReceiver, playingIntent);
    }

    @AfterViews
    void initFragment() {
        AlphaAnimation fadeTo = new AlphaAnimation(0.5f, 1f);
        fadeTo.setFillAfter(true);
        fadeTo.setDuration(500);
        playButton.startAnimation(fadeTo);





        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    Intent intent = new Intent(Consts.SCROLL_INTENT);
                    intent.putExtra(Consts.PROGRESS_CONTENT, i);
                    sendBroadcast(intent);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        initTrack(track);
        setupMediaPlayer();

    }

    private void initTrack(Track track) {
        try {
            trackName.setText(track.getTrackName());
            trackArtist.setText(track.getArtistName());
            //setupMediaPlayer();

        } catch (Exception e) {
           // Intent intent = new Intent(Consts.TRACK_REQUEST_INTENT);
            //sendBroadcast(intent);
        }
    }

    private void setProgressInt(int progressValue) {
        progress.setMax(100);
        progress.setProgress(progressValue);
    }

    public void setupMediaPlayer() {

        Intent trackIntent = new Intent(Consts.START_INTENT);
        trackIntent.putExtra(Consts.TRACK_CONTENT, track);
        sendBroadcast(trackIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(playReceiver);
    }


    @Click(R.id.forward_button)
    void nextButton() {
        Intent nextIntent = new Intent(Consts.NEXT_INTENT);
        sendBroadcast(nextIntent);
    }

    @Click(R.id.back_button)
    void backButton() {
        Intent backIntent = new Intent(Consts.BACK_INTENT);
        sendBroadcast(backIntent);
    }

    @Click(R.id.play_button)
    void playButton() {
        Intent playIntent = new Intent(Consts.PLAY_INTENT);
        sendBroadcast(playIntent);
    }


}
