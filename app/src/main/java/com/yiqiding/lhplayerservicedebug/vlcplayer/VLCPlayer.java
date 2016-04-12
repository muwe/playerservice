package com.yiqiding.lhplayerservicedebug.vlcplayer;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.AndroidUtil;
import org.videolan.libvlc.util.VLCUtil;

import com.yiqiding.lhplayerservicedebug.IPlayer;
import com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback;
import com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer;
import com.yiqiding.lhplayerservicedebug.vlcplayer.media.MediaWrapper;
import com.yiqiding.lhplayerservicedebug.vlcplayer.util.VLCInstance;
import com.yiqiding.lhplayerservicedebug.vlcplayer.util.VLCOptions;

import android.content.Context;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;


public class VLCPlayer implements IVLCVout.Callback, LibVLC.HardwareAccelerationError,IPlayer {
    private static final String TAG = "VLC/IPlayerClient";
    
    private Context mContext = null;
    private Uri mUri = null;
    private MediaPlayer mMediaPlayer = null;
    private boolean mSeekable = false;
    private boolean mPausable = false;
    
    private int mVideoHeight = 0;
    private int mVideoWidth = 0;
    private boolean mSurfacesAttached = false;
    
    private boolean mPrepared = false;
    private ILHClientCallback mCallBack;
    private ILHPlayer.Stub mBinder = null;


    public VLCPlayer(Context context, ILHPlayer.Stub binder) {
        if (!VLCInstance.testCompatibleCPU(context)) {
            Log.e(TAG, "testCompatibleCPU failed: " + VLCUtil.getErrorMsg());
            return;
        }
        
        mContext = context;
        mBinder = binder;
        mMediaPlayer = newMediaPlayer();
    }

    private LibVLC LibVLC() {
        return VLCInstance.get(mContext);
    }

    private MediaPlayer newMediaPlayer() {
        final MediaPlayer mp = new MediaPlayer(LibVLC());
        mp.setAudioOutput(VLCOptions.getAout());
        mp.getVLCVout().addCallback(this);
        return mp;
    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum,
            int sarDen) {
        if (width * height == 0)
            return;
        
        mVideoWidth = width;
        mVideoHeight = height;
    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {
    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {
        mSurfacesAttached = false;
    }
    
    private void validatePlayer() {
        if (mMediaPlayer == null)
            throw new IllegalArgumentException("mMediaPlayer is already released"); 
    }
    
    public void setDataSource(String path) {
        validatePlayer();
        
        if (path == null || path.isEmpty())
            throw new IllegalArgumentException();
        
        if (path.startsWith("/"))
            mUri = AndroidUtil.PathToUri(path);
        else
            mUri = AndroidUtil.LocationToUri(path);
    }
    
    public boolean prepare(){
        validatePlayer();
        
        LibVLC().setOnHardwareAccelerationError(this);
        
        /* Pausable and seekable are true by default */
        mPausable = mSeekable = true;
        final Media media = new Media(VLCInstance.get(mContext), mUri);
        int flags = MediaWrapper.MEDIA_PAUSED | MediaWrapper.MEDIA_VIDEO;
        VLCOptions.setMediaOptions(media, flags);
        media.setEventListener(mMediaListener);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.setEventListener(mMediaPlayerListener);
        mMediaPlayer.play();
        return true;
    }
    
    private final Media.EventListener mMediaListener = new Media.EventListener() {
        @Override
        public void onEvent(Media.Event event) {
            switch (event.type) {
                case Media.Event.MetaChanged:
                    Log.i(TAG, "Media.Event.MetaChanged: " + event.getMetaId());

                case Media.Event.ParsedChanged:
                    Log.i(TAG, "Media.Event.ParsedChanged");
                    break;
            }
        }
    };
    
    private final MediaPlayer.EventListener mMediaPlayerListener = new MediaPlayer.EventListener() {
        @Override
        public void onEvent(MediaPlayer.Event event) {
            switch (event.type) {
                case MediaPlayer.Event.Playing:
                    Log.i(TAG, "MediaPlayer.Event.Playing");
                    break;
                case MediaPlayer.Event.Buffering:
                    int percentage = (int) event.getBuffering();
                    // Log.i(TAG, "MediaPlayer.Event.Buffering " + percentage);
                    // UnMark the following lines if client wants the buffering event
                    // if (mOnBufferingUpdateListener != null)
                    //     mOnBufferingUpdateListener.onBufferingUpdate(VLCPlayer.this, percentage);
                    if (!mPrepared && percentage == 100) {
                        mPrepared = true;
                        Log.d(TAG, "Time to trigger OnPrepared event");
                            onPrepared();
                    }
                    break;
                case MediaPlayer.Event.Paused:
                    Log.i(TAG, "MediaPlayer.Event.Paused");
                    break;
                case MediaPlayer.Event.Stopped:
                    Log.i(TAG, "MediaPlayer.Event.Stopped");
                    break;
                case MediaPlayer.Event.EndReached:
                    Log.i(TAG, "MediaPlayer.Event.EndReached");
                        onCompletion();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    Log.e(TAG, String.format("The location %s cannot be played.", mUri));
                        onError(-1001, 0);
                    break;
                case MediaPlayer.Event.TimeChanged:
                    break;
                case MediaPlayer.Event.PositionChanged:
                    break;
                case MediaPlayer.Event.Vout:
                    break;
                case MediaPlayer.Event.ESAdded:
                    break;
                case MediaPlayer.Event.ESDeleted:
                    break;
                case MediaPlayer.Event.PausableChanged:
                    mPausable = event.getPausable();
                    break;
                case MediaPlayer.Event.SeekableChanged:
                    mSeekable = event.getSeekable();
                    break;
            }
        }
    };
    
    public void setDisplay(Surface surf) {
        validatePlayer();
        
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.detachViews();
        vlcVout.setVideoSurface(surf, null);
        mSurfacesAttached = true;
        vlcVout.attachViews();
    }
    
    public void start() throws IllegalStateException {
        validatePlayer();
        
        mMediaPlayer.play();
    }

    public void stop() throws IllegalStateException {
        validatePlayer();
        
        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.removeCallback(this);
        if (mSurfacesAttached) {
            vlcVout.detachViews();
            mSurfacesAttached = false;
        }
        final Media media = mMediaPlayer.getMedia();
        if (media != null) {
            media.setEventListener(null);
            mMediaPlayer.setEventListener(null);
            mMediaPlayer.stop();
            mMediaPlayer.setMedia(null);
            media.release();
        }
    }

    public void pause() throws IllegalStateException {
        validatePlayer();
        
        if (mPausable) {
            mMediaPlayer.pause();
        }
    }
    
    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mSeekable = false;
            mPausable = false;
            mMediaPlayer = null;
            mContext = null;
        }
    }
    
    public int getVideoWidth() {
        validatePlayer();
        
        return mVideoWidth;
    }

    public int getVideoHeight() {
        validatePlayer();
        
        return mVideoHeight;
    }

    public boolean isPlaying() {
        validatePlayer();
        
        return mMediaPlayer.isPlaying();
    }
    
    public boolean isSeekable() {
        validatePlayer();
        
        return mSeekable;
    }
    
    public boolean isPausable() {
        validatePlayer();
        
        return mPausable;
    }
    
    public boolean isVideoPlaying() {
        validatePlayer();
        
        return mMediaPlayer.getVLCVout().areViewsAttached();
    }
    
    public long getTime() {
        validatePlayer();
        
        return mMediaPlayer.getTime();
    }
    
    public int getCurrentPosition() {
        validatePlayer();
        
        return (int) mMediaPlayer.getTime();
    }
    
    public long getLength() {
        validatePlayer();
        
        return mMediaPlayer.getLength();
    }
    
    public int getDuration() {
        validatePlayer();
        
        return (int) mMediaPlayer.getLength();
    }
    
    public void setTime(long time) {
        validatePlayer();
        
        if (isSeekable())
            mMediaPlayer.setTime(time);
    }
    
    public float getRate() {
        validatePlayer();
        
        return mMediaPlayer.getRate();
    }
    
    public void setRate(float rate) {
        validatePlayer();
        
        mMediaPlayer.setRate(rate);
    }
    
    public int getVolume() {
        validatePlayer();
        
        return mMediaPlayer.getVolume();
    }
    
    public int setVolume(int volume) {
        validatePlayer();
        
        return mMediaPlayer.setVolume(volume);
    }
    
    public void setPosition(float pos) {
        validatePlayer();
        
        if (isSeekable())
            mMediaPlayer.setPosition(pos);
    }
    
    public int getAudioTracksCount() {
        validatePlayer();
        
        return mMediaPlayer.getAudioTracksCount();
    }
    
    public MediaPlayer.TrackDescription[] getAudioTracks() {
        validatePlayer();
        
        return mMediaPlayer.getAudioTracks();
    }
    
    public int getAudioTrack() {
        validatePlayer();
        
        return mMediaPlayer.getAudioTrack();
    }
    
    public boolean setAudioTrack(int index) {
        validatePlayer();
        
        return mMediaPlayer.setAudioTrack(index);
    }
    
    public int getVideoTracksCount() {
        validatePlayer();
        
        return mMediaPlayer.getVideoTracksCount();
    }
    
    public int getVideoTrack() {
        validatePlayer();
        
        return mMediaPlayer.getVideoTrack();
    }
    
    public boolean setAudioDelay(long delay) {
        validatePlayer();
        
        return mMediaPlayer.setAudioDelay(delay);
    }
    
    public long getAudioDelay() {
        validatePlayer();
        
        return mMediaPlayer.getAudioDelay();
    }
    
    private void seek(long position, float length) {
        if (length == 0f)
            setTime(position);
        else
            setPosition(position / length);
    }
    
    public void seekTo(int delta) throws IllegalStateException {
        validatePlayer();
        
        if (getLength() <= 0 || !isSeekable()) return;
        
        long position = getTime() + delta;
        if (position < 0) position = 0;
        seek(position, 0);
    }
    
    public void selectTrack(int index) throws IllegalStateException {
        setAudioTrack(index);
    }

    @Override
    public void eventHardwareAccelerationError() {
        Log.e(TAG, "hardware acceleration error");
            onError(-1001, 0);
    }



    public void registerClientCallback(ILHClientCallback cb){
        mCallBack = cb;
    }
    ///////////////////////////////////////////////////////////////////////

    public void onPrepared() {

        if (mCallBack != null) {
            Log.d(TAG, "Client's onPrepared will be called");
            try {
                mCallBack.onPrepared(mBinder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCompletion() {
        if (mCallBack != null) {
            Log.d(TAG, "Client's onCompletion will be called");
            try {
                mCallBack.onCompletion(mBinder);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public void onBufferingUpdate(int percent) {
        if (mCallBack != null) {
            Log.d(TAG, "Client's onBufferingUpdate will be called");
            try {
                mCallBack.onBufferingUpdateListener(mBinder, percent);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


    public boolean onError(int what, int extra) {
        if (mCallBack != null) {
            Log.d(TAG, "Client's onError will be called");
            try {
                return mCallBack.onError(mBinder, what, extra);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public boolean onInfo(int what, int extra) {
        return false;
    }

}
