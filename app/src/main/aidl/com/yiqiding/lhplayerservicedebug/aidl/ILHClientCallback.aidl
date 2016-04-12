package com.yiqiding.lhplayerservicedebug.aidl;

import com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer;

interface ILHClientCallback {
    void onPrepared(ILHPlayer player);
    void onCompletion(ILHPlayer player);
    void onBufferingUpdateListener(ILHPlayer player, int percent);
    boolean onError(ILHPlayer player, int what, int extra);
}