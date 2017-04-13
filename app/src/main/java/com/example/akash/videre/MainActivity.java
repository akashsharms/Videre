package com.example.akash.videre;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.example.akash.videre.data.Songs;
import com.example.akash.videre.fragments.FragmentAlbum;
import com.example.akash.videre.fragments.FragmentAllSongs;
import com.example.akash.videre.fragments.FragmentArtist;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.io.File;
import java.util.ArrayList;

import static com.example.akash.videre.R.id.textView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager viewPager;
    ImageButton stop;
    int  initial_playing = -1;
    SlidingUpPanelLayout slidingPaneLayout;
    MediaPlayer mediaPlayer;
    Boolean playing = false;
    private ArrayList<Songs> songs=new ArrayList<>();
    Cursor albumArtCursor;
    FragmentAllSongs fragmentAllSongs=null;
    AsyncTask<Void, Void, ArrayList<Songs>> asyncTask=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        stop = (ImageButton) findViewById(R.id.stop_button);
        viewPager= (ViewPager) findViewById(R.id.container);
        sectionsPagerAdapter=new SectionsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        slidingPaneLayout= (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        slidingPaneLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(slideOffset>=0.5){
                    stop.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            if(newState== SlidingUpPanelLayout.PanelState.EXPANDED){
                stop.setVisibility(View.VISIBLE);
                stop.setImageResource(R.drawable.ic_equaliser);
            }
            else {
                stop.setVisibility(View.VISIBLE);
                if(playing)
                    stop.setImageResource(R.drawable.ic_play);
                else stop.setImageResource(R.drawable.ic_pause);
            }
            }
        });
        tabLayout.setupWithViewPager(viewPager);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
//        if(slidingPaneLayout.getPanelState()== SlidingUpPanelLayout.PanelState.EXPANDED){
//            stop.setImageResource(R.drawable.ic_equaliser);
//        }
         asyncTask = new AsyncTask<Void, Void, ArrayList<Songs>>() {
            @Override
            protected ArrayList<Songs> doInBackground(Void... params) {
                ContentResolver resolver = getContentResolver();
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
                        songs.add(new Songs(this_title, this_artist, this_id, this_album, null, null,false, this_data,null,null));
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
            protected void onPostExecute(ArrayList<Songs> songses) {
                super.onPostExecute(songses);

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    mediaPlayer.pause();
                    playing = false;
                    stop.setImageResource(R.drawable.ic_play);
                    fragmentAllSongs.gifStop();

                } else if (initial_playing != -1) {
                    mediaPlayer.start();
                    playing = true;
                    stop.setImageResource(R.drawable.ic_pause);
                } else if (songs.size() != 0) {
                    playSong(songs.get(0).getId(), 0);
                }
            }
        });



            }
        };
        asyncTask.execute();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment=null;
            if (position == 0) {
                fragmentAllSongs=new FragmentAllSongs();
                if (fragmentAllSongs!=null){
                fragmentAllSongs.setOnFragmentReady(new FragmentAllSongs.OnFragmentReady() {
                    @Override
                    public void OnReady() {
                        fragmentAllSongs.ArrayListReceive(songs);

                    }

                    @Override
                    public void OnClicked(int pos) {
                        playSong(songs.get(pos).getData(),pos);

                    }

                    @Override
                    public void OnClicked() {

                         slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                        stop.setImageResource(R.drawable.ic_equaliser);



                    }


                });}
                else{
                    fragmentAllSongs=new FragmentAllSongs();
                    fragmentAllSongs.setOnFragmentReady(new FragmentAllSongs.OnFragmentReady() {
                        @Override
                        public void OnReady() {
                            fragmentAllSongs.ArrayListReceive(songs);
                        }

                        @Override
                        public void OnClicked(int pos) {
                            playSong(songs.get(pos).getData(),pos);
                        }

                        @Override
                        public void OnClicked() {
//                            if(slidingPaneLayout.getPanelState()!= SlidingUpPanelLayout.PanelState.EXPANDED)
//                            {slidingPaneLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
//                            stop.setImageResource(R.drawable.ic_equaliser);}
                        }

                    });
                }
                return fragmentAllSongs;
            }
            if (position == 1) {
                fragment=new FragmentAlbum();
            }
            if (position == 2) {
                fragment=new FragmentArtist();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "All Songs";
                case 1:
                    return "Albums";
                case 2:
                    return "Artist";
            }
            return null;
        }
    }
    void playSong(String data, int p) {

        try {
            if (playing)
                mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(MainActivity.this, Uri.fromFile(new File(data)));

            playing = true;

            mediaPlayer.prepareAsync();


            mediaPlayer.start();
            initial_playing = p;
            stop.setImageResource(R.drawable.ic_pause);
        } catch (IllegalStateException e) {
            mediaPlayer.start();
            initial_playing = p;
            stop.setImageResource(R.drawable.ic_pause);
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stop.setImageResource(R.drawable.ic_play);
            }
        });

    }

}
