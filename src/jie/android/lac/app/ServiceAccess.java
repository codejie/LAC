package jie.android.lac.app;

import jie.android.lac.service.aidl.Access;
import jie.android.lac.service.aidl.Callback;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ServiceAccess {
	private static final String Tag = ServiceAccess.class.getSimpleName();

	private Access access = null;
	private LACActivity activity = null;
	
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			Log.d(Tag, "Service connected.");
			access = Access.Stub.asInterface(binder);			
			onConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			access = null;
			onDisconnected();			
		}		
	};
		
	private CallbackStub callbackStub = null;
	
	public ServiceAccess(LACActivity lacActivity) {
		activity = lacActivity;		
	}

	public final ServiceConnection getServiceConnection() {
		return connection;
	}

	protected void onDisconnected() {
		
		Log.d(Tag, "unregister callback.");
		
		try {
			access.unregisterCallback(0xF1);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		activity.onServiceDisconnected();
	}

	protected void onConnected() {
		Log.d(Tag, "register callback.");
		
		callbackStub = new CallbackStub(activity.getHandler());
		
		try {
			access.registerCallback(0xF1, callbackStub);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		activity.onServiceConnected();
	}

	public void bindService() {
		
		Log.d(Tag, "Bind Service.");
		Intent intent = new Intent("lacService");
		activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
		
	}

	public void unbindService() {
		Log.d(Tag, "Unbind Service.");
		activity.unbindService(connection);
	}
	

	public final Access getAccess() {
		return access;
	}
	
	
}
