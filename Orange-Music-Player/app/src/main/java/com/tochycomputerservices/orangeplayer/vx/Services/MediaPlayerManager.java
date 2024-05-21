package com.tochycomputerservices.orangeplayer.vx.Services;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.PowerManager;

/**
 *
 */

public class MediaPlayerManager {


    private MediaPlayer mMediaPlayer1;
    private MediaPlayer mMediaPlayer2;


    public MediaPlayerManager(Context context) {
        mMediaPlayer1 = new MediaPlayer();
        mMediaPlayer2 = new MediaPlayer();

        mMediaPlayer1 = new MediaPlayer();
        mMediaPlayer1.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer1.setAudioStreamType(AudioManager.STREAM_MUSIC);


        mMediaPlayer2 = new MediaPlayer();
        mMediaPlayer2.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer2.setAudioStreamType(AudioManager.STREAM_MUSIC);



    }
}
