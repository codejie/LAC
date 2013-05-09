package jie.android.lac.app;

import android.os.RemoteException;
import jie.android.lac.service.aidl.Callback;

public class CallbackStub extends Callback.Stub {

	public CallbackStub() {
		
	}
	
	@Override
	public void onServiceStartup(int result) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
