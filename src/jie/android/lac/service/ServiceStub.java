package jie.android.lac.service;

public class ServiceStub extends Access.Stub {
	
	private DBAccess dbAccess = null;
			
	public ServiceStub(DBAccess dbAccess) {
		this.dbAccess = dbAccess;
	}
	
	@Override
	public int checkState() {
		return dbAccess.getState();
		//return 100;
	}
}
