package jie.android.lac.service.aidl;

interface ImportDatabaseListener {
	void onStarted(in String text);
	void onImported(in String text);
	void onCompleted(in String text);
	void onFailed(in String what);
}