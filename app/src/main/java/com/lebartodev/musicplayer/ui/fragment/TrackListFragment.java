package com.lebartodev.musicplayer.ui.fragment;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.lebartodev.musicplayer.Consts;
import com.lebartodev.musicplayer.R;
import com.lebartodev.musicplayer.TrackUtil;
import com.lebartodev.musicplayer.adapter.TrackAdapter;
import com.lebartodev.musicplayer.model.Track;
import com.lebartodev.musicplayer.ui.activity.PlayerActivity_;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.fragment_track_list)
public class TrackListFragment extends Fragment {
    @ViewById(R.id.tracks_list)
    RecyclerView tracksList;
    @ViewById(R.id.track_container)
    FrameLayout miniTrackLayout;
    @ViewById(R.id.track_name)
    TextView miniTrackName;
    @ViewById(R.id.track_artist)
    TextView miniTrackArtist;
    @ViewById(R.id.play_button)
    ImageView miniTrackButton;
    private TrackAdapter adapter;
    Track track;
    public static final String TAG = "TrackListFragment";
    BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("MusicService", String.valueOf(intent.getBooleanExtra(Consts.PLAYING_CONTENT, false)));
            if (intent.getBooleanExtra(Consts.PLAYING_CONTENT, false)) {
                miniTrackButton.setImageResource(R.drawable.ic_pause);
            } else {
                miniTrackButton.setImageResource(R.drawable.ic_play_white);
            }
            try {

                track = intent.getParcelableExtra(Consts.TRACK_CONTENT);
                initMiniLayout(track);

            } catch (Exception e) {

            }


        }
    };

    private void initMiniLayout(Track track) {
        if (track == null) {
            getActivity().findViewById(R.id.mini_track_layout).setVisibility(View.INVISIBLE);
        } else {
            getActivity().findViewById(R.id.mini_track_layout).setVisibility(View.VISIBLE);
            miniTrackArtist.setText(track.getArtistName());
            miniTrackName.setText(track.getTrackName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    public TrackListFragment() {

    }

    @Click(R.id.track_container)
    void clickMini() {
        Intent intent = PlayerActivity_.intent(this).track(track).get();
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.from_bottom, R.anim.fade);
    }

    @Click(R.id.play_button)
    void playClick() {
        Intent playIntent = new Intent(Consts.PLAY_INTENT);
        getActivity().sendBroadcast(playIntent);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter playingIntent = new IntentFilter(Consts.PLAY_RESPONSE_INTENT);
        getActivity().registerReceiver(playReceiver, playingIntent);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());

        tracksList.setLayoutManager(mLayoutManager);
        tracksList.setAdapter(adapter);

        if (adapter == null) {
            adapter = new TrackAdapter(new ArrayList<Track>(), getActivity(), this);
            tracksList.setAdapter(adapter);
            fillList();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(playReceiver);
    }

    private void fillList() {
        List<Track> trackList = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().toString() + "/Music/imagine_dragons/Album1/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        for (int i = 0; i < files.length; i++) {

            Track track = TrackUtil.getTrack(files[i].getAbsolutePath(), i);
            trackList.add(track);

        }
        adapter.setTracks(trackList);


    }

}
