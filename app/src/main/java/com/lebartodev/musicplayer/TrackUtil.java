package com.lebartodev.musicplayer;

import android.media.MediaMetadataRetriever;

import com.lebartodev.musicplayer.model.Track;

import java.io.File;

/**
 * Created by Александр on 15.12.2016.
 */

public class TrackUtil {
    public static Track getTrack(String path,int i){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);

        String albumName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        String artistName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String trackName = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        int durationInt = 0;
        if (duration != null) {
            durationInt = Integer.parseInt(duration)/1000;
        }



        return new Track(path,trackName,durationInt,artistName,albumName,i);

    }
}
