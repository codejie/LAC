
package jie.android.lac.service.aidl;

import jie.android.lac.data.WordData;

interface Access {
	int checkState();
	List<WordData> queryWordData(in String condition);
}