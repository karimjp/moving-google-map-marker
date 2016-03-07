/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /Applications/Android/workspace/jana.karim.hw5/src/jana/karim/hw5/AlienService.aidl
 */
package jana.karim.hw5;
public interface AlienService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements jana.karim.hw5.AlienService
{
private static final java.lang.String DESCRIPTOR = "jana.karim.hw5.AlienService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an jana.karim.hw5.AlienService interface,
 * generating a proxy if needed.
 */
public static jana.karim.hw5.AlienService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof jana.karim.hw5.AlienService))) {
return ((jana.karim.hw5.AlienService)iin);
}
return new jana.karim.hw5.AlienService.Stub.Proxy(obj);
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
case TRANSACTION_add:
{
data.enforceInterface(DESCRIPTOR);
jana.karim.hw5.UFOPositionReporter _arg0;
_arg0 = jana.karim.hw5.UFOPositionReporter.Stub.asInterface(data.readStrongBinder());
this.add(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_remove:
{
data.enforceInterface(DESCRIPTOR);
jana.karim.hw5.UFOPositionReporter _arg0;
_arg0 = jana.karim.hw5.UFOPositionReporter.Stub.asInterface(data.readStrongBinder());
this.remove(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements jana.karim.hw5.AlienService
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
@Override public void add(jana.karim.hw5.UFOPositionReporter reporter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((reporter!=null))?(reporter.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_add, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void remove(jana.karim.hw5.UFOPositionReporter reporter) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((reporter!=null))?(reporter.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_remove, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_add = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_remove = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void add(jana.karim.hw5.UFOPositionReporter reporter) throws android.os.RemoteException;
public void remove(jana.karim.hw5.UFOPositionReporter reporter) throws android.os.RemoteException;
}
