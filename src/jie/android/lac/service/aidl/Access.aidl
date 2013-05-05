
package jie.android.lac.service.aidl;

import jie.android.lac.data.Word;

interface Access {
	int checkState();
	List<Word.Info> queryWordInfo(in String condition, in int offset, in int limit);
	String queryWordResult(in int index);
//	List<Dictionary.Info> queryDictionaryInfo();
	void setDictionaryOrder(in int index, in int order);
	void enableDictionary(in int index, in boolean enable);
	
}