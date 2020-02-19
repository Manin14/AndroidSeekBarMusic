package com.manin.androidplaymusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView imageplaypause;
    private AppCompatTextView textcurrentTime,textTotalDuration;
    private SeekBar playerSeekbar;
    private MediaPlayer mediaPlayer;
    private Handler handler=new Handler();



    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageplaypause=findViewById(R.id.id_image_pause_play);
        textcurrentTime=findViewById(R.id.id_textCurrentTime);
        textTotalDuration=findViewById(R.id.id_textTotalDuration);
        playerSeekbar=findViewById(R.id.id_playerSeekbar);
        mediaPlayer=new MediaPlayer();

        playerSeekbar.setMax(100);

        imageplaypause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()){
                    handler.removeCallbacks(updater);
                    mediaPlayer.pause();
                    imageplaypause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                }
                else {
                    mediaPlayer.start();
                    imageplaypause.setImageResource(R.drawable.ic_pause_circle_filled_black_24dp);
                    updateSeekbar();
                }
            }
        });

        preparemediaPLAYER();

        //untuk mempercepat seekbar
        playerSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SeekBar seekBar=(SeekBar) v;
                int playposition =(mediaPlayer.getDuration() / 100) * seekBar.getProgress();
                mediaPlayer.seekTo(playposition);
                textcurrentTime.setText(milliSecondToTimer(mediaPlayer.getCurrentPosition()));
                return false;
            }
        });


        //lalu ini buat buffering
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                playerSeekbar.setSecondaryProgress(percent);
            }
        });

        //lalu ini buat setelah music selesai main
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerSeekbar.setProgress(0);
                imageplaypause.setImageResource(R.drawable.ic_play_circle_filled_black_24dp);
                textcurrentTime.setText(R.string.zero);
                textTotalDuration.setText(R.string.zero);
                mediaPlayer.reset();
                preparemediaPLAYER();
            }
        });
    }


    private void preparemediaPLAYER(){
        try {
           // mediaPlayer.setDataSource(R.raw.lagunya);
         //   mediaPlayer.setDataSource("http://infinityandroid.com/music/good_times.no3"); // url of music file
           // mediaPlayer.setDataSource("https://soundcloud.com/jksonxie/on-my-way-alan-walker");

            mediaPlayer =  MediaPlayer.create(getBaseContext(), R.raw.lagunya);
            mediaPlayer.prepare();
            textTotalDuration.setText(milliSecondToTimer(mediaPlayer.getDuration()));
            //textTotalDuration.setText(mediaPlayer.getDuration());
        }
        catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private Runnable updater=new Runnable() {
        @Override
        public void run() {
            updateSeekbar();
            long currentDuration=mediaPlayer.getCurrentPosition();
            textcurrentTime.setText(milliSecondToTimer(currentDuration));

        }
    };

    private  void updateSeekbar(){
        if (mediaPlayer.isPlaying()){
            playerSeekbar.setProgress((int) (((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100 ));}
            handler.postDelayed(updater, 1000);
    }

    private String milliSecondToTimer(long milliSeconds){
        String timerString="";
        String secondString="";

        int hours=(int)(milliSeconds / (1000 * 60 * 60));
        int minutes=(int) (milliSeconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds=(int) ((milliSeconds % (1000 * 60 * 60)) % (1000 * 60) / 1000 );

        if (hours > 0) {     timerString= hours +"0"; }

        if (seconds < 10){ secondString="0"+seconds ; }
        else { secondString="" +seconds ; }

        timerString=timerString + minutes + " " + secondString;
        return timerString;
    }
}
