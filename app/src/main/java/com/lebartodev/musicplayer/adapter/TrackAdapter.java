package com.lebartodev.musicplayer.adapter;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lebartodev.musicplayer.Consts;
import com.lebartodev.musicplayer.R;
import com.lebartodev.musicplayer.model.Track;
import com.lebartodev.musicplayer.ui.activity.PlayerActivity;
import com.lebartodev.musicplayer.ui.activity.PlayerActivity_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Александр on 13.12.2016.
 */

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.ArticleVH> {
    private List<Track> tracks;
    private Context context;
    private int selectedPos = -1;
    private Fragment fragment;

    public TrackAdapter(List<Track> tracks, Context context, Fragment fragment) {
        this.fragment = fragment;
        this.tracks = tracks;
        this.context = context;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
        notifyDataSetChanged();
    }

    @Override
    public ArticleVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.i_track, parent, false);
        return new ArticleVH(v);
    }

    @Override
    public void onBindViewHolder(final ArticleVH holder, int position) {

        holder.trackTitle.setText(tracks.get(position).getTrackName());


        long h = tracks.get(position).getDuration() / 3600;
        long m = (tracks.get(position).getDuration() - h * 3600) / 60;
        long s = tracks.get(position).getDuration() - (h * 3600 + m * 60);

        String  duration;

        if(h!=0){
            duration = h+" : "+m+" : "+s;
        }
        else
            duration = m+" : "+s;



        holder.trackDuration.setText(duration);
        holder.trackArtist.setText(tracks.get(position).getArtistName());

        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Intent listIntent = new Intent(Consts.LIST_INTENT);
                listIntent.putParcelableArrayListExtra(Consts.LIST_CONTENT, (ArrayList<? extends Parcelable>) tracks);
                context.sendBroadcast(listIntent);
                Intent intent = PlayerActivity_.intent(context).track(tracks.get(holder.getAdapterPosition())).get();
                Pair<View, String> p1 = Pair.create((View)holder.trackTitle, "titleTransition");
                Pair<View, String> p2 = Pair.create((View)holder.trackArtist, "artistTransition");
                Pair<View, String> p3 = Pair.create((View)holder.trackImage, "imageTransition");



                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((AppCompatActivity)context, p1, p2,p3);
                context.startActivity(intent, options.toBundle());


            }
        });


    }

    @Override
    public int getItemCount() {
        return tracks.size();
    }

    class ArticleVH extends RecyclerView.ViewHolder {
        private TextView trackTitle;
        private TextView trackArtist;
        private TextView trackDuration;
        private ImageView trackImage;
        private View mainView;


        public ArticleVH(View itemView) {


            super(itemView);
            this.mainView = itemView;


            trackTitle = (TextView) itemView.findViewById(R.id.song_name);
            trackArtist = (TextView) itemView.findViewById(R.id.artist_name);
            trackDuration = (TextView) itemView.findViewById(R.id.songDuration);
            trackImage = (ImageView) itemView.findViewById(R.id.trackImage);


        }
    }
}
