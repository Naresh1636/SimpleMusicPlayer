package com.example.ajay.musicplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class PlaySong extends AppCompatActivity {

    MediaPlayer player;
    Boolean isInErrorState=false;
    Boolean isInPause=false;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(savedInstanceState!=null)
        {
            if(player!=null&&!isInErrorState)
            {
                Log.d("main","Restarting the song..");
                player.start();
            }
        }

        Button btn=(Button)findViewById(R.id.playSong);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(player==null||isInErrorState) {

                    player =intializePlayer();
                    try {
                        player.setDataSource(PlaySong.this, Uri.parse("file:///storage/emulated/0/DCIM/349fd28acbae145bd33696f3f1833f55.mp4"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("unable play media");
                        return;
                    }
                    try {
                        player.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                        showToast("unable to prepare media.");
                    }
                    player.start();
                }
            }
        });


        Button pauseBtn=(Button)findViewById(R.id.pauseSong);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInErrorState&&player.isPlaying())
                {
                    player.pause();
                    isInPause=true;
                }
            }
        });


        Button restartBtn=(Button)findViewById(R.id.restartSong);

        restartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInErrorState&&(player.isPlaying()))
                {
                    player.seekTo(0);
                }
            }
        });


        Button resume=(Button)findViewById(R.id.resumeSong);

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isInErrorState)
                {
                    player.start();
                }
            }
        });


    }


    private MediaPlayer intializePlayer()
    {
        if(player!=null)
        {
            player.release();
        }
        isInErrorState=false;
        Log.d("main","Intializing player...");

        MediaPlayer player=new MediaPlayer();
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Log.d("main","Song playig finished");
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
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
                return true;
            }
        });


        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                Log.d("main","ready to play the song");
            }
        });

        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(MediaPlayer mediaPlayer) {
                Log.d("main","onseek request complete...");
            }
        });

        return player;


    }
    private  void showToast(String error)
    {
        Toast.makeText(this,error, Toast.LENGTH_LONG).show();
    }


}
