package jie.android.lac.app;

import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import jie.android.lac.data.Constants.MSG;
import jie.android.lac.service.aidl.Callback;

public class CallbackStub extends Callback.Stub {

	private static final String Tag = CallbackStub.class.getSimpleName();
	
	private final Handler handler;
	
	public CallbackStub(final Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void onServiceState(int state) throws RemoteException {
		Log.d(Tag, "service state = " + state);
		Message msg = Message.obtain(handler, MSG.SERVICE_STATE_NOTIFY, state, 0, null);
		msg.sendToTarget();
//		handler.sendEmptyMessage(0);
	}

}
