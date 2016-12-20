package com.lebartodev.musicplayer.ui.activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lebartodev.musicplayer.MusicService;
import com.lebartodev.musicplayer.R;
import com.lebartodev.musicplayer.ui.fragment.TrackListFragment;
import com.lebartodev.musicplayer.ui.fragment.TrackListFragment_;

import org.androidannotations.annotations.EActivity;

import java.util.Calendar;

@EActivity(R.layout.activity_list)
public class ListTracksActivity extends AppCompatActivity {
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startService(new Intent(this,MusicService.class));
        fragmentManager = getFragmentManager();
        Fragment contentFragment = fragmentManager.findFragmentByTag(TrackListFragment.TAG);
        if (contentFragment == null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, TrackListFragment_.builder().build(), TrackListFragment.TAG)
                    .commit();

        }

    }

}
