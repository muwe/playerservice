package com.yiqiding.lhplayerservicedebug;

import android.view.Surface;

import com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback;

/**
 * Created by aiven on 16/3/25.
 */
public interface IPlayer {
    void setDataSource(String path);
    boolean prepare();
    void setDisplay(Surface surf);
    void start();
    void stop();
    void pause();
    void release();
    int getVideoWidth();
    int getVideoHeight();
    boolean isPlaying();
    boolean isSeekable();
    int getCurrentPosition();
    int getDuration();
    long getTime();
    void setTime(long time);
    int getVolume();
    int setVolume(int volume);
    void setPosition(float pos);
    int getAudioTracksCount();
    int getAudioTrack();
    boolean setAudioTrack(int index);
    void seekTo(int delta);
    void selectTrack(int index);
    void registerClientCallback(ILHClientCallback cb);
}
