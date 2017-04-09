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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.akash.videre.R;
import com.example.akash.videre.data.Songs;

import java.util.ArrayList;

/**
 * Created by akash on 09-03-2017.
 */

public class FragmentArtist extends Fragment {
    RecyclerView recyclerView;
    Cursor musicCursor,artistCursor,albumCursor;
    ArrayList<Songs> songs=new ArrayList<>();
    String this_album;
    LinearLayoutManager linearLayoutManager;
    MyAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View rootView=inflater.inflate(R.layout.fragment_artist,container,false);
        recyclerView= (RecyclerView) rootView.findViewById(R.id.rv_aritist);
        AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver resolver=getContext().getContentResolver();
                Uri musicUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri artistUri=MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
                Uri albumUri=MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                albumCursor=resolver.query(albumUri,null,null,null,null);
                musicCursor=resolver.query(musicUri,null,null,null,null);
                artistCursor=resolver.query(artistUri,null,null,null,null);
                if(musicCursor!=null&&musicCursor.moveToNext()){
                    int album_name=musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int artist_column=artistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
                    int artist_no_of_songs=artistCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);
                    int artist_no_albums=artistCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
                    int album_art_display=albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                    int album_art_name=albumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                    while(artistCursor.moveToNext()){

                    String this_artist_coloumn=artistCursor.getString(artist_column);
                        String this_artist_no_of_songs=artistCursor.getString(artist_no_of_songs);
                        String this_artist_no_albums=artistCursor.getString(artist_no_albums);
                        while(musicCursor.moveToNext()){
                            this_album=musicCursor.getString(album_name);

                        }
                        songs.add(new Songs(null,this_artist_coloumn,null,this_album,null,null,null,null,this_artist_no_of_songs,this_artist_no_albums));
                    }
                    while (albumCursor.moveToNext()){

                        String this_album_art_display=albumCursor.getString(album_art_display);
                        String this_name=albumCursor.getString(album_art_name);
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).getAlbum().equals(this_name))
                                songs.get(i).setArtPath(this_album_art_display);}

                    }
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                linearLayoutManager=new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(linearLayoutManager);
                adapter=new MyAdapter();
                recyclerView.setAdapter(adapter);
            }

        };
        asyncTask.execute();
        return rootView;
    }
    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_artist_name,tv_artist_songs,tv_artist_albums;
        ImageView iv_artist;
        public MyHolder(View itemView) {
            super(itemView);
            View v=itemView;
            tv_artist_name= (TextView) v.findViewById(R.id.tv_artist_name);
            tv_artist_songs= (TextView) v.findViewById(R.id.tv_artist_songs);
            tv_artist_albums= (TextView) v.findViewById(R.id.tv_artist_albums);
            iv_artist= (ImageView) v.findViewById(R.id.iv_artist_image);
        }
    }
    public class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder holder=new MyHolder(getLayoutInflater(null).inflate(R.layout.card_layout_artist,parent,false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyHolder holder, int position) {
        Songs currSongs=songs.get(position);
            holder.tv_artist_name.setText(currSongs.getArtist());
            holder.tv_artist_songs.setText(currSongs.getNo_of_songs()+" songs");
            holder.tv_artist_albums.setText(currSongs.getNo_of_albums()+" albums");
            if(currSongs.getArtPath()==null){
                Glide.with(getContext()).load(R.drawable.albums_back).centerCrop().into(holder.iv_artist);
            }
            else{
                Glide.with(getContext()).load(currSongs.getArtPath()).centerCrop().into(holder.iv_artist);
            }
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }
    }
}
