package jie.android.lac.service.aidl;

interface ImportDatabaseListener {
	void onStarted(in String file);
	void onImported(in int position, in String text);
	void onCompleted(in int total);
	void onFailed(in String what);
}