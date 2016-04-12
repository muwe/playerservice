/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Users/aiven/Works/AndroidApp/LHPlayerServiceDebug/app/src/main/aidl/com/yiqiding/lhplayerservicedebug/aidl/ILHClientCallback.aidl
 */
package com.yiqiding.lhplayerservicedebug.aidl;
public interface ILHClientCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback
{
private static final java.lang.String DESCRIPTOR = "com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback interface,
 * generating a proxy if needed.
 */
public static com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback))) {
return ((com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback)iin);
}
return new com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_onPrepared:
{
data.enforceInterface(DESCRIPTOR);
com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer _arg0;
_arg0 = com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer.Stub.asInterface(data.readStrongBinder());
this.onPrepared(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onCompletion:
{
data.enforceInterface(DESCRIPTOR);
com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer _arg0;
_arg0 = com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer.Stub.asInterface(data.readStrongBinder());
this.onCompletion(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_onBufferingUpdateListener:
{
data.enforceInterface(DESCRIPTOR);
com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer _arg0;
_arg0 = com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer.Stub.asInterface(data.readStrongBinder());
int _arg1;
_arg1 = data.readInt();
this.onBufferingUpdateListener(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_onError:
{
data.enforceInterface(DESCRIPTOR);
com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer _arg0;
_arg0 = com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer.Stub.asInterface(data.readStrongBinder());
int _arg1;
_arg1 = data.readInt();
int _arg2;
_arg2 = data.readInt();
boolean _result = this.onError(_arg0, _arg1, _arg2);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.yiqiding.lhplayerservicedebug.aidl.ILHClientCallback
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void onPrepared(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((player!=null))?(player.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_onPrepared, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onCompletion(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((player!=null))?(player.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_onCompletion, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void onBufferingUpdateListener(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player, int percent) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((player!=null))?(player.asBinder()):(null)));
_data.writeInt(percent);
mRemote.transact(Stub.TRANSACTION_onBufferingUpdateListener, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public boolean onError(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player, int what, int extra) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((player!=null))?(player.asBinder()):(null)));
_data.writeInt(what);
_data.writeInt(extra);
mRemote.transact(Stub.TRANSACTION_onError, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_onPrepared = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_onCompletion = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_onBufferingUpdateListener = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_onError = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public void onPrepared(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player) throws android.os.RemoteException;
public void onCompletion(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player) throws android.os.RemoteException;
public void onBufferingUpdateListener(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player, int percent) throws android.os.RemoteException;
public boolean onError(com.yiqiding.lhplayerservicedebug.aidl.ILHPlayer player, int what, int extra) throws android.os.RemoteException;
}
