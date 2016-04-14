package com.yiqiding.lhplayerservicedebug.ijkplayer;

import java.io.File;
import java.io.IOException;

import android.net.Uri;
import android.os.RemoteException;
import android.view.Surface;
import android.util.Log;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import com.yiqiding.lhplayerservicedebug.IPlayer;
import com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback;
import com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.misc.IjkTrackInfo;

public class IjkPlayer implements IPlayer {
    private String TAG = "Ijk/IPlayerClient";
    private Uri mUri = null;
    private IjkMediaPlayer mMediaPlayer;
    private ILHClientCallback mCallBack;
    private ILHPlayer.Stub mBinder = null;
    private ReentrantLock lock = new ReentrantLock();
    private Condition con = lock.newCondition();
    private boolean bPrepareResult = false;

    public IjkPlayer(ILHPlayer.Stub binder) {
        IjkMediaPlayer ijkMediaPlayer = null;
        ijkMediaPlayer = new IjkMediaPlayer();
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "http-detect-range-support", 0);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48);
        mMediaPlayer = ijkMediaPlayer;
        mBinder = binder;
    }
            
    public void setDataSource(String path) {
        if (path.startsWith("/"))
            mUri = Uri.fromFile(new File(path));
        else
            mUri = Uri.parse(path);
        
        try {
            mMediaPlayer.setDataSource(mUri.toString());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        mMediaPlayer.setOnCompletionListener(mCompletionListener);
        mMediaPlayer.setOnPreparedListener(mPreparedListener);
        mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
        mMediaPlayer.setOnErrorListener(mErrorListener);
        mMediaPlayer.setOnInfoListener(mInfoListener);
    }
    
    public void setDisplay(Surface surf) {
        mMediaPlayer.setSurface(surf);
    }
    
    public boolean prepare() {
        lock.lock();
        Log.d(TAG, "prepare start!");
        bPrepareResult = false;
        mMediaPlayer.prepareAsync();
        try {
            Log.d(TAG, "wait signal!");
            con.await();
        } catch (InterruptedException e) {
            Log.e(TAG, "con.await fail!");
            e.printStackTrace();
        }
        lock.unlock();
        Log.d(TAG, "prepare end---bPrepareResult="+bPrepareResult);
        return bPrepareResult;
    }

    public void start() {
        mMediaPlayer.start();
    }
    
    public void pause() {
        mMediaPlayer.pause();
    }
    
    public void stop() {
        mMediaPlayer.stop();
    }
    
    public void release() {
        mMediaPlayer.release();
    }
    
    public int getVideoWidth() {
        return mMediaPlayer.getVideoWidth();
    }
    
    public int getVideoHeight() {
        return mMediaPlayer.getVideoHeight();
    }
    
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }
    
    public boolean isSeekable() {
        return true;
    }
    
    public int getCurrentPosition() {
        return (int) mMediaPlayer.getCurrentPosition();
    }
    
    public int getDuration() {
        return (int) mMediaPlayer.getDuration();
    }

    public void seekTo(int delta) {
        int current = getCurrentPosition();
        current += delta;
        if (current < 0) current = 0;
        int duration = getDuration();
        if (current > duration) current = duration;
        mMediaPlayer.seekTo(current);
    }
    
    public void selectTrack(int index) {
        mMediaPlayer.selectTrack(index);
    }
    
    public int getAudioTrack() {
        return mMediaPlayer.getSelectedTrack(ITrackInfo.MEDIA_TRACK_TYPE_AUDIO);
    }
    
    public int setVolume(int volume) {
        float fvol = (float) volume/(float) 100.0;
        mMediaPlayer.setVolume(fvol, fvol);
        return 0;
    }
    
    public int getAudioTracksCount() {
        IjkTrackInfo tracks[] = mMediaPlayer.getTrackInfo();
        int count = 0;
        if (tracks != null) {
            for (IjkTrackInfo track : tracks) {
                if (track.getTrackType() == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO)
                    ++count;
            }
        }
        return count;
    }

    public void registerClientCallback(ILHClientCallback cb){
        mCallBack = cb;
    }
    public long getTime(){
        return 0;
    }

    public int getVolume(){
        return 0;
    }

//    int getAudioTracksCount(){
//        return 0;
//    }

    public boolean setAudioTrack(int index){

        return true;
    }

    public void setPosition(float pos){

        return;
    }

    public void setTime(long time){
        return;
    }
    ///////////////////////////////////////////////////////////////////////

    private IMediaPlayer.OnPreparedListener mPreparedListener = new IMediaPlayer.OnPreparedListener() {
        public void onPrepared(IMediaPlayer mp) {

                if (mCallBack != null) {
                    Log.d(TAG, "Client's onPrepared will be called");
                    try {
                        mCallBack.onPrepared(mBinder);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                lock.lock();
                bPrepareResult = true;
                con.signal();
                Log.d(TAG, "send signal success!");
                lock.unlock();

            }

    };
    private IMediaPlayer.OnCompletionListener mCompletionListener = new IMediaPlayer.OnCompletionListener() {
        public void onCompletion(IMediaPlayer mp) {
            if (mCallBack != null) {
                Log.d(TAG, "Client's onCompletion will be called");
                try {
                    mCallBack.onCompletion(mBinder);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };


    private IMediaPlayer.OnBufferingUpdateListener mBufferingUpdateListener = new IMediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(IMediaPlayer mp, int percent) {
            if (mCallBack != null) {
                Log.d(TAG, "Client's onBufferingUpdate will be called");
                try {
                    mCallBack.onBufferingUpdateListener(mBinder, percent);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private IMediaPlayer.OnErrorListener mErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            boolean bRet = false;
            if (mCallBack != null) {
                Log.d(TAG, "Client's onError will be called,what="+what+",extra="+extra);
                try {
                    bRet = mCallBack.onError(mBinder, 1001, extra);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            lock.lock();
            con.signal();
            bPrepareResult = false;
            Log.d(TAG, "send signal fail!");
            lock.unlock();

            return bRet;
        }
    };
    private IMediaPlayer.OnInfoListener mInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            return false;
        }
    };
}