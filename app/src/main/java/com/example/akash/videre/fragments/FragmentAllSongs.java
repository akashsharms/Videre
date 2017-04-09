package com.example.akash.videre.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.akash.videre.R;
import com.example.akash.videre.data.Songs;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by akash on 08-03-2017.
 */

public class FragmentAllSongs extends Fragment {
    private RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    public static final String TAG = "hell";
    ArrayList<Songs> songs = new ArrayList<>();
    MyAdapter adapter = null;
    Cursor albumArtCursor;
    int counter = -1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_all_songs);
        AsyncTask<Void, Void, ArrayList<Songs>> asyncTask = new AsyncTask<Void, Void, ArrayList<Songs>>() {
            @Override
            protected ArrayList<Songs> doInBackground(Void... params) {
                ContentResolver resolver = getContext().getContentResolver();
                Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor musicCursor = resolver.query(musicUri, null, null, null, null);
                albumArtCursor = resolver.query(albumUri, null, null, null, null);
                if (musicCursor != null && musicCursor.moveToFirst()) {
                    int title_column = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                    int artist_column = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int id_column = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
                    int album = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int data = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

                    int album_id = albumArtCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
                    int album_art = albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                    int album_art_name = albumArtCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                    do {
                        String this_id = musicCursor.getString(id_column);
                        String this_title = musicCursor.getString(title_column);
                        String this_artist = musicCursor.getString(artist_column);
                        String this_data = musicCursor.getString(data);
                        String this_album = musicCursor.getString(album);
                        songs.add(new Songs(this_title, this_artist, this_id, this_album, null, null,null, this_data,null,null));
                    } while (musicCursor.moveToNext());
                    while (albumArtCursor.moveToNext()) {
                        String albumartpath = albumArtCursor.getString(album_art);
                        String albumname = albumArtCursor.getString(album_art_name);
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).getAlbum().equals(albumname))
                                songs.get(i).setArtPath(albumartpath);
                        }
                    }

                }
                return songs;
            }

            @Override
            protected void onPostExecute(ArrayList<Songs> aVoid) {
                super.onPostExecute(aVoid);
                linearLayoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter = new MyAdapter();
                recyclerView.setAdapter(adapter);
            }
        };
        asyncTask.execute();

        return rootView;
    }

    public void ArrayListReceive(ArrayList<Songs> arrayList) {
        songs = arrayList;
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        ImageView iv_thumbnail, iv_gif;
        TextView textView_song_name, textView_artist_name;

        public MyHolder(View itemView) {
            super(itemView);
            View v = itemView;
            iv_gif = (ImageView) v.findViewById(R.id.iv_gif);
            iv_thumbnail = (ImageView) v.findViewById(R.id.iv_thumbnail);
            textView_song_name = (TextView) v.findViewById(R.id.song_name);
            textView_artist_name = (TextView) v.findViewById(R.id.song_artist);

        }
    }

    public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            MyHolder holder = new MyHolder(getLayoutInflater(null).inflate(R.layout.card_layout_all_songs, parent, false));
            Log.d(TAG, "onCreateViewHolder: ");
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {

            final Songs currSong = songs.get(position);
            holder.textView_artist_name.setText(currSong.getArtist());
            holder.textView_song_name.setText(currSong.getTitle());

            try {
                holder.iv_thumbnail.setVisibility(View.VISIBLE);
                holder.iv_gif.setVisibility(View.GONE);
                if(currSong.getArtPath()==null){
                    Glide.with(getContext()).load(R.drawable.music).fitCenter().into(holder.iv_thumbnail);
                }
                Glide.with(getContext()).load(new File(currSong.getArtPath()))
                        .asBitmap().fitCenter().into(holder.iv_thumbnail);
            } catch (Exception e) {

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.notifyItemChanged(counter);
                    counter = position;
                    songs.get(counter).setPrevPlaying(true);
                    holder.iv_thumbnail.setVisibility(View.GONE);
                    holder.iv_gif.setVisibility(View.VISIBLE);
                    Glide.with(getContext()).load(R.drawable.disc).asGif()
                            .fitCenter().into(holder.iv_gif);

                }
            });
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }
    }
}
