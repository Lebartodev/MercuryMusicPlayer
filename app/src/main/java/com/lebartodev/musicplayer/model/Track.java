package com.lebartodev.musicplayer.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Александр on 13.12.2016.
 */

public class Track implements Parcelable {
    private String fileName;
    private String artistName,trackName,albumName;
    private long duration;
    private int position;

    protected Track(Parcel in) {
        fileName = in.readString();
        artistName = in.readString();
        trackName = in.readString();
        albumName = in.readString();
        duration = in.readLong();
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(artistName);
        dest.writeString(trackName);
        dest.writeString(albumName);
        dest.writeLong(duration);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Track> CREATOR = new Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Track(String fileName, String trackName, long duration, String artistName, String albumName,int position) {
        this.fileName = fileName;
        this.trackName = trackName;
        this.duration = duration;
        this.artistName = artistName;
        this.albumName = albumName;
        this.position=position;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }


}
