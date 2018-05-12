package com.shahzaib.playaudio_handleaudiointereptions;

import android.annotation.SuppressLint;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity implements AudioManager.OnAudioFocusChangeListener {
    static final String LOG_TAG = "123456";

    MediaPlayer mediaPlayer;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "Activity Stopped");
        stopMusic(findViewById(R.id.playBtn));
    }


    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_LOSS:
                // stop playback, i.e user started some other playback app.
                // remember, unregistered your controls/buttons and release focus
                Log.i(LOG_TAG, "Focus Loss permanently, So releaseMediaPlayer & Focus");
                abandonFocus_releaseMediaPlayer();
                break;

            case AudioManager.AUDIOFOCUS_GAIN:
                // you hold the audio focus again, i.e phone call ends
                Log.i(LOG_TAG, "Audio Focus Granted Again, So startAgain");

                startAgain();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                // your audio focus is temporarily stolen, but will back soon (i.e for phone calls)
                Log.i(LOG_TAG, "Pause Playback, so pauseMusic");
                pauseMusic();
                break;

            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                // lower the volume, because someone also also playing music over you, i.e notifications
                Log.i(LOG_TAG, "Lower the volume, Or pause Music");

                pauseMusic();
                break;
        }
    }














    //******* helper functions
    @SuppressLint("NewApi")
    private AudioFocusRequest getAudioFocusRequest()
    { // get AudioFocusRequest for devices >= android O
        return new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setOnAudioFocusChangeListener(this)
                .setAcceptsDelayedFocusGain(true)
                .setAudioAttributes(getAudioAttributes())
                .setWillPauseWhenDucked(true)
                .build();
    }

    @SuppressLint("NewApi")
    private AudioAttributes getAudioAttributes()
    {
        return new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();
    }

    public void playMusic(View view) {

            int requestResult;

            //********* Requesting For Audio Focus
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                Log.i(LOG_TAG, "Device is >= android O");
                requestResult = audioManager.requestAudioFocus(getAudioFocusRequest());
            }
            else // if device < O
            {
                requestResult = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            }


            //********** checking for requestResult
            if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                Log.i(LOG_TAG, "Focus Request Failed");
            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                Log.i(LOG_TAG, "Focus Request Delayed, when focus granted, it will inform you");

            } else if (requestResult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.i(LOG_TAG, "Focus Request Granted");

                startMusic();
            }
    }
    private void startMusic() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                mediaPlayer = MediaPlayer.create(this, R.raw.music); // just play because attributes are already set
            } else { // if device is greater than lollipop && less than android O
                mediaPlayer = MediaPlayer.create(this, R.raw.music,getAudioAttributes(),1);
            }
        }
        else
        {
            mediaPlayer = MediaPlayer.create(this,R.raw.music);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
        mediaPlayer.start();
        Log.i(LOG_TAG,"Music is playing......");
    }
    private void startAgain() {
        if (mediaPlayer != null)
        {
            mediaPlayer.start();
            Log.i(LOG_TAG,"Music is playing again......");
        }
        else{
            Log.i(LOG_TAG, "Can't StartAgain, MediaPlayer is Null");
        }

    }
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.pause();
            Log.i(LOG_TAG,"Music Paused");
        }
        else{
            Log.i(LOG_TAG, "Can't Pause, MediaPlayer is Null OR Not playing");

        }

    }
    public void stopMusic(View view) {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
            Log.i(LOG_TAG,"Music Stopped");
            abandonFocus_releaseMediaPlayer();
        }
        else{
            Log.i(LOG_TAG, "Can't Stop, MediaPlayer is Null OR is not playing");
        }
    }
    private void abandonFocus_releaseMediaPlayer() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(getAudioFocusRequest());
            }
            else {
                audioManager.abandonAudioFocus(this);
            }

            mediaPlayer.release();
            mediaPlayer = null;
            Log.i(LOG_TAG, "Focus Abandoned && Media player released");
        }






   /* public void playMusic(View view) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                    .setUsage(AudioAttributes.USAGE_VOICE_COMMUNICATION)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build();
            mediaPlayer = MediaPlayer.create(this, R.raw.music, attributes, 1);
        } else {
            mediaPlayer = MediaPlayer.create(this, R.raw.music);
        }
        mediaPlayer.start();
    }

    public void stopMusic(View view) {
        mediaPlayer.stop();
    }*/
    }

}
