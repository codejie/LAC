package jie.android.lac.service.aidl;

interface ImportDatabaseListener {
	void onStarted();
	void onCompleted(in int total);
	void onImported(in String text);
}