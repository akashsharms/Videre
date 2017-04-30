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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.example.akash.videre.MySingleton;
import com.example.akash.videre.R;
import com.example.akash.videre.data.Songs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by akash on 09-03-2017.
 */

public class FragmentArtist extends Fragment {
    RecyclerView recyclerView;
    Cursor musicCursor,artistCursor,albumCursor;
    ArrayList<Songs> songs=new ArrayList<>();
    String this_album;
    GridLayoutManager gridLinearLayout;
    String url;
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
                gridLinearLayout=new GridLayoutManager(getContext(),2);
                recyclerView.setLayoutManager(gridLinearLayout);
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
        final Songs currSongs=songs.get(position);
            holder.tv_artist_name.setText(currSongs.getArtist());
            try {
                url="http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + URLEncoder.encode
                        (currSongs.getArtist().trim(), "UTF-8")
                        +
                        "&" + "api_key=427b002d583bb1e76ec74d77e3737bfc&format=json";
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest artistArtRequest=new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject artist=response.getJSONObject("artist");
                                JSONArray images=artist.getJSONArray("image");
                                for(int i=0;i<images.length();i++){
                                    JSONObject temp= images.getJSONObject(i);
                                    if(temp.getString("size").equals("large")){

                                        currSongs.setUrl(temp.getString("#text"));
                                        Log.d("glide", "onResponse: "+currSongs.getUrl());
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                        }
                    });
            MySingleton.getInstance(getContext()).addToRequestQueue(artistArtRequest);
            Log.d("glide", "onBindViewHolder: "+currSongs.getUrl());
            holder.tv_artist_songs.setText(currSongs.getNo_of_songs()+" songs");
            holder.tv_artist_albums.setText(currSongs.getNo_of_albums()+" albums");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), " "+currSongs.getUrl(), Toast.LENGTH_SHORT).show();
                }
            });


                            Picasso.with(getContext()).load(currSongs.getUrl()).fit().centerCrop().into(holder.iv_artist);

        }

        @Override
        public int getItemCount() {
            return songs.size();
        }
    }
}
