package jie.android.lac.app;

import jie.android.lac.service.aidl.Access;
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
			access = Access.Stub.asInterface(binder);			
			onConnected();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			access = null;
			onDisconnected();			
		}		
	};
	
	public ServiceAccess(LACActivity lacActivity) {
		activity = lacActivity;
	}

	public final ServiceConnection getServiceConnection() {
		return connection;
	}

	protected void onDisconnected() {
		activity.onServiceDisconnected();
	}

	protected void onConnected() {
		activity.onServiceConnected();
		
		try {
			int value = access.checkState();
			Log.d(Tag, "state - " + value);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void bindService() {
		
		Intent intent = new Intent("lacService");
		activity.bindService(intent, connection, Context.BIND_AUTO_CREATE);
		
	}

	public void unbindService() {
		activity.unbindService(connection);
	}
	

	public final Access getAccess() {
		return access;
	}
	
	
}
