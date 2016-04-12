package com.yiqiding.lhplayerservicedebug;

import com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback;
import com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer;
import com.yiqiding.lhplayerservicedebug.ijkplayer.IjkPlayer;
import com.yiqiding.lhplayerservicedebug.vlcplayer.VLCPlayer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Surface;

import java.lang.reflect.Method;


public class LHPlayerService extends Service {
    private static final String TAG = "LHPlayerServiceDebug";
    
    private static final Object LOCK_AIDL = new Object();
    
    private static final String VERSION = "5.0.160323";


    private enum LHState {
        UNKNOWN, IDLE, INITIALIZED, PREPARED, STARTED, PAUSED, STOPPED, ERROR
    }

    private LHState mState = LHState.UNKNOWN;
    private IPlayer mPlayer;
    private ILHPlayer.Stub mBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
        mBinder = new MyBinder();


//        IjkMediaPlayer.loadLibrariesOnce(null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return START_STICKY;
    }
    
    private class MyBinder extends ILHPlayer.Stub {
        @Override
        public void setDataSource(final String path) throws RemoteException {
            Log.d(TAG, "setDataSource(" + path + ")"+"---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    mPlayer.setDataSource(path);
                } else
                    Log.e(TAG, "mPlayer is null");
                mState = LHState.INITIALIZED;
            }
            Log.e(TAG, "setDataSource---end");
        }

        @Override
        public void prepare() throws RemoteException {
            Log.d(TAG, "prepare---begin");
            synchronized (LOCK_AIDL) {
                boolean bRet = false;
                if (mPlayer != null) {
                    bRet = mPlayer.prepare();
                } else{
                    Log.e(TAG, "mPlayer is null");
                }

                if(bRet){
                    mState = LHState.PREPARED;
                }else{
                    Log.e(TAG, "prepare fail!");
                }
            }
            Log.d(TAG, "prepare---end");
        }

        @Override
        public void setDisplay(Surface surf) throws RemoteException {
            synchronized (LOCK_AIDL) {
                Log.d(TAG, "setDisplay(" + surf + ")");
                mPlayer.setDisplay(surf);
            }
        }

        @Override
        public void start() throws RemoteException {
            Log.d(TAG, "start---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    mPlayer.start();
                    mState = LHState.STARTED;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "start---end");
        }

        @Override
        public void stop() throws RemoteException {
            Log.d(TAG, "stop---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    mPlayer.stop();
                } else
                    Log.e(TAG, "mPlayer is null");
                mState = LHState.STOPPED;
            }
            Log.d(TAG, "stop---end");
        }

        @Override
        public void pause() throws RemoteException {
            Log.d(TAG, "pause---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    mPlayer.pause();
                } else
                    Log.e(TAG, "mPlayer is null");
                mState = LHState.PAUSED;
            }
            Log.d(TAG, "pause---end");
        }

        @Override
        public void release() throws RemoteException {
            Log.d(TAG, "release---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    mPlayer.release();
                    mPlayer = null;
//                    IjkMediaPlayer.native_profileEnd();
                } else
                    Log.e(TAG, "mPlayer is null");
                mState = LHState.UNKNOWN;
            }
            Log.d(TAG, "release---end");
        }

        @Override
        public int getVideoWidth() throws RemoteException {
            Log.d(TAG, "getVideoWidth---begin");
            int ret = 0;
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    int retval = mPlayer.getVideoWidth();
                    Log.d(TAG, "    return " + retval);
                    ret = retval;
                } else{
                    Log.e(TAG, "mPlayer is null");
                }

            }
            Log.d(TAG, "getVideoWidth---end");
            return ret;
        }

        @Override
        public int getVideoHeight() throws RemoteException {
            int ret = 0;
            Log.d(TAG, "getVideoHeight---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    int retval = mPlayer.getVideoHeight();
                    Log.d(TAG, "    return " + retval);
                    ret = retval;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "getVideoHeight---end");
            return ret;
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            boolean bRet = false;
            Log.d(TAG, "isPlaying---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    if (mState != LHState.STARTED)
                        return false;
                    boolean retval = mPlayer.isPlaying();
                    if (mState == LHState.STARTED && retval == false)
                        retval = true;
                    Log.d(TAG, "    return " + retval);
                    bRet = retval;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "isPlaying---end,bRet="+bRet);
            return bRet;
        }

        @Override
        public boolean isSeekable() throws RemoteException {
            boolean bRet = false;
            Log.d(TAG, "isSeekable---start");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    boolean retval = mPlayer.isSeekable();
                    Log.d(TAG, "    return " + retval);
                    bRet = retval;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "isSeekable---end,bRet=,"+bRet);
            return bRet;
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            int nRet = 0;
            Log.d(TAG, "getCurrentPosition---begin");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    int retval = mPlayer.getCurrentPosition();
                    Log.d(TAG, "    return " + retval);
                    nRet = retval;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "getCurrentPosition---end,nRet="+nRet);
            return nRet;
        }

        @Override
        public int getDuration() throws RemoteException {
            int nRet = 0;
            Log.d(TAG, "getDuration---start");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    int retval = mPlayer.getDuration();
                    Log.d(TAG, "    return " + retval);
                    nRet = retval;
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "getDuration---end,nRet="+nRet);
            return nRet;
        }

        @Override
        public long getTime() throws RemoteException {
            return 0;
        }

        @Override
        public void setTime(long time) throws RemoteException {
            return;
        }

        @Override
        public int getVolume() throws RemoteException {
            return 0;
        }

        @Override
        public int setVolume(int volume) throws RemoteException {
            int nRet = 0;
            Log.d(TAG, "setVolume(" + volume + ")---start");

            synchronized (LOCK_AIDL) {
                if (mPlayer != null)
                    nRet = mPlayer.setVolume(volume);
                else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "setVolume(" + volume + ")---end,nRet="+nRet);
            return nRet;
        }

        @Override
        public void setPosition(float pos) throws RemoteException {
            return;
        }

        @Override
        public int getAudioTracksCount() throws RemoteException {
            int nRet = 0;
            Log.d(TAG, "getAudioTracksCount---start");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    nRet = mPlayer.getAudioTracksCount();
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "getAudioTracksCount---end,nRet="+nRet);
            return nRet;
        }

        @Override
        public int getAudioTrack() throws RemoteException {
            int nRet = 0;
            Log.d(TAG, "getAudioTrack---start");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null) {
                    nRet = mPlayer.getAudioTrack();
                } else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "getAudioTrack---end,nRet="+nRet);
            return nRet;
        }

        @Override
        public boolean setAudioTrack(int index) throws RemoteException {
            return false;
        }

        @Override
        public void seekTo(int delta) throws RemoteException {
            Log.d(TAG, "seekTo(" + delta + ")---start");
            synchronized (LOCK_AIDL) {
                if (mPlayer != null)
                    mPlayer.seekTo(delta);
                else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "seekTo(" + delta + ")---end");
        }
        
        private static final int SELECT_TRACK = 0x9877;
        private final Handler mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                case SELECT_TRACK:
                    _selectTrack(msg.arg1);
                    break;
                default:
                    super.handleMessage(msg);
                }
            }
        };
        
        private void _selectTrack(int index) {
            Log.d(TAG, "selectTrack(" + index + ")---start");
            synchronized (LOCK_AIDL) {

                if (mPlayer != null)
                    mPlayer.selectTrack(index);
                else
                    Log.e(TAG, "mPlayer is null");
            }
            Log.d(TAG, "selectTrack(" + index + ")---end");
        }

        @Override
        public void selectTrack(int index) throws RemoteException {
            _selectTrack(index);
        }

        @Override
        public void registerClientCallback(ILHClientCallback cb) throws RemoteException {
            Log.d(TAG, "registerClientCallback---start");
            synchronized (LOCK_AIDL) {
                if(mPlayer != null){
                    mPlayer.registerClientCallback(cb);
                }else{
                    Log.e(TAG, "mPlayer is null");
                }
            }
            Log.d(TAG, "registerClientCallback---end");
        }

        private String getprop(String key, String defaultValue) {
            String value = defaultValue;
            try {
                Class<?> c = Class.forName("android.os.SystemProperties");
                Method get = c.getMethod("get", String.class, String.class );
                value = (String)(   get.invoke(c, key, "unknown" ));
            } catch (Exception e) {
                Log.d(TAG, "get property error, " + e.getMessage());
            }
            Log.d(TAG, "get property, " + key + " = " + value);
            return value;
        }

        @Override
        public void create() throws RemoteException {
            Log.d(TAG, "create---begin");
            synchronized (LOCK_AIDL) {
                String playertype = getprop("persist.sys.lhplayer.type", "1");
                Log.d(TAG, "Player Type="+playertype);

                if (mPlayer == null) {

                    if(playertype.equals("1")){
//                        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
                        mPlayer = new IjkPlayer(mBinder);

                    }else if(playertype.equals("2")){
                        mPlayer = new VLCPlayer(LHPlayerService.this,mBinder);
                    }else{
//                        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
                        mPlayer = new IjkPlayer(mBinder);
                    }
                    Log.d(TAG, "create, version: " + VERSION);
                }else
                    Log.e(TAG, "mPlayer is not null");
            }
            Log.d(TAG, "create---end");
        }
    };
}