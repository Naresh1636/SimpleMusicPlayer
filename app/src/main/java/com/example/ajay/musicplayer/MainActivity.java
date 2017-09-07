package com.example.ajay.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int number=0;
    int playingSong=-1;


    List<String> musicFiles;
    Button refreshBtn;
    MediaPlayer player;
    Boolean isInErrorState=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        number++;
        Log.d("main","in On create number:"+number);

        refreshBtn=(Button)findViewById(R.id.refreshBtn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchAudioFiles();
            }
        });

        searchAudioFiles();


    }

    @Override
    protected void onStop() {
        super.onStop();
        if(player!=null&&!isInErrorState&&player.isPlaying())
        {
            Log.d("main","Stoping song...");
            player.pause();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(player!=null)
        {
            player.release();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(player!=null&&!isInErrorState)
        {
            Log.d("main","Restarting the song..");
            player.start();
        }



    }

    private void searchAudioFiles() {
        if (musicFiles == null) {
            musicFiles = new ArrayList<>();

            ContentResolver resolver = getContentResolver();

            Cursor audioFiles = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

            int colCount = 0;

            if (audioFiles.getCount() > 0) {
                colCount = audioFiles.getColumnCount();
            }

            audioFiles.moveToFirst();

            for (int i = 0; i < audioFiles.getCount(); i++) {

                Log.d("main", "I am inside");
                    try {
                        String file = new String(audioFiles.getString(audioFiles.getColumnIndex("_data")));
                        Log.d("main",file);
                        musicFiles.add(file);
                    } catch (IndexOutOfBoundsException e) {
                        Log.e("main", "_data coloumn not found");
                }

                audioFiles.moveToNext();
            }

            if (musicFiles.size() > 0) {
                setContentView(R.layout.music_list);
                setData();

            }
        }
    }
    private void setData()
    {
        ListView musicList=(ListView)findViewById(R.id.songsList);

        final ArrayAdapter<String> songList=new SongArrayAdapter(this,R.layout.song_item_layout,R.id.textView2,musicFiles);

                musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        Log.d("main", adapterView.getItemAtPosition(i) + "");
                        ImageView playImg = view.findViewById(R.id.playImg);
                        if (playImg.getVisibility() == View.VISIBLE) {
                            playImg.setVisibility(View.INVISIBLE);
                        } else {
                            playImg.setVisibility(View.VISIBLE);
                        }

                        ImageView pauseImg = view.findViewById(R.id.pauseImg);
                        if (pauseImg.getVisibility() == View.VISIBLE) {
                            pauseImg.setVisibility(View.INVISIBLE);
                        } else {
                            pauseImg.setVisibility(View.VISIBLE);
                        }


                        if (player == null) {
                            player = intializePlayer();
                        }

                        if (playingSong == i && player.isPlaying()) {
                            player.pause();
                            Log.d("main", "Song Paused");
                        } else {
                            if (playingSong == i) {
                                player.start();
                            } else {

                                try {
                                    player.reset();
                                    player.setDataSource(MainActivity.this, Uri.parse("file://" + adapterView.getItemAtPosition(i)));
                                } catch (IOException e) {
                                    Log.d("main", "Error occured..");
                                    showToast("Unable to load file");
                                    return;
                                }
                                try {
                                    player.prepare();
                                } catch (IOException e) {
                                    Log.d("main", "Unable to prepare audio...");
                                    showToast("Unable to play file...");
                                    return;
                                }

                                player.start();
                                playingSong = i;
                            }
                        }

                    }
                });

        musicList.setAdapter(songList);
//        songList.notifyDataSetChanged();


    }


    private MediaPlayer intializePlayer()
    {
        if(player!=null)
        {
            player.release();
        }
        playingSong=-1;
        isInErrorState=false;

        Log.d("main","Intializing player...");

        final MediaPlayer mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("main","Song playig finished");
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                isInErrorState=true;
                switch (i1)
                {
                    case MediaPlayer.MEDIA_ERROR_IO: {
                        Log.d("main", "MEDIA_ERROR_IO");
                        showToast("MEDIA_ERROR_IO");
                        break;
                    }
                    case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    {
                        Log.d("main","MEDIA_ERROR_MALFORMED");
                        showToast("file corrupted...");
                        break;}

                    case MediaPlayer.MEDIA_ERROR_UNSUPPORTED: {
                        Log.d("main", "MEDIA_ERROR_UNSUPPORTED");
                        showToast("unsupported file format...");
                        break;
                    }
                    case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    {
                        Log.d("main","MEDIA_ERROR_TIMED_OUT");

                        showToast("Seems too  big file..");
                        break;}

                    default: {
                        Log.d("main", "Unknown error occured..");
                        showToast("problem while loading media...");
                        break;
                    }
                }
                player.reset();
                isInErrorState=false;
                return true;
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("main","Media playing completed...");
            }
        });


        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("main","ready to play the song");
            }
        });

        mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                Log.d("main","onseek request complete...");
            }
        });

        return mediaPlayer;


    }
    private  void showToast(String error)
    {
        Toast.makeText(this,error, Toast.LENGTH_LONG).show();
    }

}
