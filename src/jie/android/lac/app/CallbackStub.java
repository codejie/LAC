package jie.android.lac.app;

import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import jie.android.lac.service.aidl.Callback;

public class CallbackStub extends Callback.Stub {

	private static final String Tag = CallbackStub.class.getSimpleName();
	
	private final Handler handler;
	
	public CallbackStub(final Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void onServiceStartup(int result) throws RemoteException {
		Log.d(Tag, "result = " + result);
		handler.sendEmptyMessage(0);
	}

}
