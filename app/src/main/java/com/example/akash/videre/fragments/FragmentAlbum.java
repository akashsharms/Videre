package com.example.akash.videre.fragments;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.akash.videre.R;
import com.example.akash.videre.data.Songs;

import java.util.ArrayList;

/**
 * Created by akash on 09-03-2017.
 */

public class FragmentAlbum extends Fragment {
    RecyclerView recyclerView_album;
    GridLayoutManager gridLayoutManager;
    MyAdapter adapter;
    int i=0,j=0;
    ArrayList<Songs> songs=new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_albums,container,false);
        recyclerView_album= (RecyclerView) rootView.findViewById(R.id.rv_albums);

        AsyncTask<Void,Void,Void> asyncTask=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                ContentResolver contentResolver=getContext().getContentResolver();
                Uri musicUri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                Uri albumUri=MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
                Cursor music=contentResolver.query(musicUri,null,null,null,null);
                Cursor album_art=contentResolver.query(albumUri,null,null,null,null);
                if(music!=null && music.moveToFirst()){
                    int music_album_name=music.getColumnIndex(MediaStore.Audio.Media.ALBUM);
                    int music_artist_name=music.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                    int album_art_display=album_art.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
                    int album_art_name=album_art.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
                    while(music.moveToNext()){
                    String this_album_name=music.getString(music_album_name);
                        String this_artist_name=music.getString(music_artist_name);
                        songs.add(new Songs(null,this_artist_name,null,this_album_name,null,null,null,null,null,null));
                    }
                    while (album_art.moveToNext()){
                        String this_album_art_display=album_art.getString(album_art_display);
                        String this_art_name=album_art.getString(album_art_name);
                        for (int i = 0; i < songs.size(); i++) {
                            if (songs.get(i).getAlbum().equals(this_art_name))
                                songs.get(i).setArtPath(this_album_art_display);
                        }
                    }

                }
                for(i=0;i<songs.size();i++){
                    for(j=i+1;j<songs.size();j++){
                        if(songs.get(i).getAlbum().equals(songs.get(j).getAlbum())){
                            songs.remove(j);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                gridLayoutManager=new GridLayoutManager(getContext(),2);

                recyclerView_album.setLayoutManager(gridLayoutManager);
                 adapter=new MyAdapter();
                recyclerView_album.setAdapter(adapter);
            }
        };
        asyncTask.execute();
        return rootView;
    }
    public class MyHolder extends RecyclerView.ViewHolder{
        TextView tv_album_name,tv_artist_name;
        ImageView iv_album;
        public MyHolder(View itemView) {
            super(itemView);
            tv_album_name= (TextView) itemView.findViewById(R.id.album_name);
            tv_artist_name= (TextView) itemView.findViewById(R.id.artist_name);
            iv_album= (ImageView) itemView.findViewById(R.id.image_view_album);
        }
    }
    public class MyAdapter extends RecyclerView.Adapter<MyHolder>{

        @Override
        public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyHolder holder=new MyHolder(getLayoutInflater(null).inflate(R.layout.card_layout_albums,parent,false));
            return holder;

        }

        @Override
        public void onBindViewHolder(final MyHolder holder, final int position) {
            Songs currSong=songs.get(position);

            holder.tv_album_name.setText(currSong.getAlbum());
            holder.tv_artist_name.setText(currSong.getArtist());
            if(currSong.getArtPath()==null){
                Glide.with(getContext()).load(R.drawable.albums_back).centerCrop().into(holder.iv_album);
            }
            else{
                Glide.with(getContext()).load(currSong.getArtPath()).centerCrop().into(holder.iv_album);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "album"+songs.get(position).getAlbum(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }
    }
}
