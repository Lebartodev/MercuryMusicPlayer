package com.lebartodev.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.lebartodev.musicplayer.model.Track;
import com.lebartodev.musicplayer.ui.activity.PlayerActivity_;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    private int songPosn = 0;
    private List<Track> tracks;
    private final IBinder musicBind = new MusicBinder();
    private MediaPlayer player;
    private Timer pingTimer;
    private TimerTask pingTask;
    private NotificationCompat.Builder builder;
    private RemoteViews expandedView;
    private Notification notification;
    private NotificationManager nm;
    private boolean closed = false;

    public MusicService() {
    }

    BroadcastReceiver nextReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            nextTrack();
        }
    };
    BroadcastReceiver backReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            backTrack();

        }
    };
    BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Track track = intent.getParcelableExtra(Consts.TRACK_CONTENT);
            if (track.getPosition() != tracks.get(songPosn).getPosition())
                playNewTrack(track);
        }
    };
    BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            stopForeground(true);
            try {

                player.stop();
                //mPlayer.release();
                closed = true;
            } catch (Exception e) {
                Log.e("MusicService", e.getLocalizedMessage());

            }
        }
    };

    BroadcastReceiver playButtonReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playTrack();
        }
    };
    BroadcastReceiver listReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            MusicService.this.tracks = intent.getParcelableArrayListExtra(Consts.LIST_CONTENT);
        }
    };
    BroadcastReceiver trackRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Intent intent1 = new Intent(Consts.TRACK_INTENT);
            intent1.putExtra(Consts.TRACK_CONTENT, tracks.get(songPosn));
            sendBroadcast(intent1);
        }
    };
    BroadcastReceiver progressChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                player.seekTo(intent.getIntExtra(Consts.PROGRESS_CONTENT, 0) * player.getDuration() / 100);
            } catch (Exception e) {
                Log.e("MusicService", e.getLocalizedMessage());
            }

        }
    };


    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        player = new MediaPlayer();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);

        player.setOnErrorListener(this);
        IntentFilter playFilter = new IntentFilter(Consts.START_INTENT);
        registerReceiver(playReceiver, playFilter);
        IntentFilter backFilter = new IntentFilter(Consts.BACK_INTENT);
        registerReceiver(backReceiver, backFilter);
        IntentFilter nextFilter = new IntentFilter(Consts.NEXT_INTENT);
        registerReceiver(nextReceiver, nextFilter);
        final IntentFilter closeFilter = new IntentFilter(Consts.CLOSE_NOTIF);
        registerReceiver(closeReceiver, closeFilter);


        IntentFilter playButtonFilter = new IntentFilter(Consts.PLAY_INTENT);
        registerReceiver(playButtonReceiver, playButtonFilter);
        IntentFilter progressChangeFilter = new IntentFilter(Consts.SCROLL_INTENT);
        registerReceiver(progressChangeReceiver, progressChangeFilter);
        IntentFilter trackRequestFilter = new IntentFilter(Consts.TRACK_REQUEST_INTENT);
        registerReceiver(trackRequestReceiver, trackRequestFilter);
        IntentFilter listFilter = new IntentFilter(Consts.LIST_INTENT);
        registerReceiver(listReceiver, listFilter);

        if (pingTimer == null) {
            pingTimer = new Timer();
            int delay = 0;
            long period = 100;
            pingTask = new TimerTask() {
                public void run() {
                    Intent intent = new Intent(Consts.PLAY_RESPONSE_INTENT);
                    intent.putExtra(Consts.PLAYING_CONTENT, player.isPlaying());
                    if (expandedView != null && !closed)
                        updateTimeNotif();
                    if (tracks != null)
                        intent.putExtra(Consts.TRACK_CONTENT, tracks.get(songPosn));
                    try {
                        Log.d("MusicService", String.valueOf(player.getCurrentPosition() * 100 / player.getDuration()));
                        intent.putExtra(Consts.PROGRESS_CONTENT, player.getCurrentPosition() * 100 / player.getDuration());

                    } catch (Exception e) {
                        Log.e("MusicService", e.getLocalizedMessage());
                    }
                    sendBroadcast(intent);


                }
            };


            pingTimer.scheduleAtFixedRate(pingTask, delay, period);

        }


    }


    public void playNewTrack(Track track) {

        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.reset();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    nextTrack();
                }
            });
        }
        try {
            Log.d("MusicService", track.getFileName());
            player.setDataSource(track.getFileName());
            player.prepareAsync();
            songPosn = track.getPosition();
            createNotification();
        } catch (IOException e) {

        }


    }

    private void nextTrack() {
        songPosn++;
        if (songPosn > tracks.size() - 1) {
            songPosn = 0;
        }
        playNewTrack(tracks.get(songPosn));

    }

    private void backTrack() {
        songPosn--;
        if (songPosn < 0) {
            songPosn = tracks.size() - 1;
        }
        playNewTrack(tracks.get(songPosn));
    }

    private void playTrack() {
        if (player.isPlaying()) {
            player.pause();
            expandedView.setImageViewResource(R.id.notif_play, R.drawable.ic_play_white);

        } else {
            player.start();
            expandedView.setImageViewResource(R.id.notif_play, R.drawable.ic_pause);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        expandedView.setImageViewResource(R.id.notif_play, R.drawable.ic_pause);
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }


    private void createNotification() {
        closed = false;
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        builder = new NotificationCompat.Builder(this);
        Intent i = PlayerActivity_.intent(this).track(tracks.get(songPosn)).get();
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        builder.setTicker("Play");
        builder.setSmallIcon(R.drawable.ic_play_white);
        builder.setAutoCancel(false);


        notification = builder.build();
        notification.priority = Notification.PRIORITY_MAX;
        expandedView = new RemoteViews(getPackageName(), R.layout.notiflayout);


        expandedView.setTextViewText(R.id.title, tracks.get(songPosn).getTrackName());
        expandedView.setTextViewText(R.id.text, tracks.get(songPosn).getArtistName());


        notification.contentView = expandedView;
        // setNotifListeners(expandedView);
        // startTimer();

        NotificationTarget notificationTarget = new NotificationTarget(
                this,
                expandedView,
                R.id.notif_song_image,
                notification,
                2001);
        try {
            Glide
                    .with(this) // safer!
                    .load(R.drawable.test_album_image)
                    .asBitmap()
                    .into(notificationTarget);
        } catch (IllegalArgumentException e) {
            Glide
                    .with(this) // safer!
                    .load(R.drawable.test_album_image)
                    .asBitmap()
                    .into(notificationTarget);
        }


        //}
        setNotifListeners(expandedView);
        startForeground(2001, notification);


    }

    private void setNotifListeners(RemoteViews contentView) {


        //status.putExtra(Consts.PLAY_INTENT, CrouleConsts.PLAY_MUSIC);
        PendingIntent pPlay = PendingIntent.getBroadcast(this, 10, new Intent(Consts.PLAY_INTENT), 0);
        contentView.setOnClickPendingIntent(R.id.notif_play, pPlay);

        //status.putExtra(CrouleConsts.MUSIC_STATUS, CrouleConsts.CLOSE_NOTIF);
        PendingIntent pClose = PendingIntent.getBroadcast(this, 20, new Intent(Consts.CLOSE_NOTIF), 0);
        contentView.setOnClickPendingIntent(R.id.close_notif, pClose);

    }

    private void updateTimeNotif() {
        try {
            Log.d("MusicService", "update" + " " + String.valueOf(player.getCurrentPosition()));

            String[] str = customFormat(player.getCurrentPosition());
            expandedView.setTextViewText(R.id.notif_time, str[0] + ":" + str[1]);
            nm.notify(2001, notification);
        } catch (Exception e) {
            String[] str = customFormat(0);
            expandedView.setTextViewText(R.id.notif_time, str[0] + ":" + str[1]);
            nm.notify(2001, notification);
        }


    }

    public String[] customFormat(int value) {
        int res0 = value / 1000;
        int res1 = res0 / 60;
        int res2 = res0 % 60;
        if (res2 < 10) {

        }
        String[] res = {String.valueOf(res1), String.valueOf(res2)};
        if (res2 < 10) {
            res[1] = "0" + res[1];
        }
        return res;
    }


}
