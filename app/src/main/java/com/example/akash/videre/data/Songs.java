package com.example.akash.videre.data;

import android.graphics.Bitmap;

/**
 * Created by akash on 09-03-2017.
 */

public class Songs {
    String title;
    String artist;
    String id;
    String album;
    String artPath;
    Bitmap albumArt;
    Boolean prevPlaying=false;
    String data;
    String no_of_songs;
    String no_of_albums;


    public Songs(String title, String artist, String id, String album, String artPath, Bitmap albumArt, Boolean prevPlaying, String data, String no_of_songs, String no_of_albums) {
        this.title = title;
        this.artist = artist;
        this.id = id;
        this.album = album;
        this.artPath = artPath;
        this.albumArt = albumArt;
        this.prevPlaying = prevPlaying;
        this.data = data;
        this.no_of_songs = no_of_songs;
        this.no_of_albums = no_of_albums;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtPath() {
        return artPath;
    }

    public void setArtPath(String artPath) {
        this.artPath = artPath;
    }

    public Bitmap getAlbumArt() {
        return albumArt;
    }

    public void setAlbumArt(Bitmap albumArt) {
        this.albumArt = albumArt;
    }

    public Boolean getPrevPlaying() {
        return prevPlaying;
    }

    public void setPrevPlaying(Boolean prevPlaying) {
        this.prevPlaying = prevPlaying;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getNo_of_songs() {
        return no_of_songs;
    }

    public void setNo_of_songs(String no_of_songs) {
        this.no_of_songs = no_of_songs;
    }

    public String getNo_of_albums() {
        return no_of_albums;
    }

    public void setNo_of_albums(String no_of_albums) {
        this.no_of_albums = no_of_albums;
    }
}
