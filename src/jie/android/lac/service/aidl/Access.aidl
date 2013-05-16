
package jie.android.lac.service.aidl;

import jie.android.lac.service.aidl.Callback;
import jie.android.lac.data.Word;
import jie.android.lac.data.Dictionary;

interface Access {
	void registerCallback(in int id, in Callback callback);
	void unregisterCallback(in int id);
	int checkState();
	//Misc.
	//Word
	List<Word.Info> queryWordInfo(in String condition, in int offset, in int limit);
	Word.XmlResult queryWordXmlResult(in int index);
	//Dictionary
//	List<Dictionary.Info> queryDictionaryInfo();
	List<Dictionary.SimpleInfo> queryDictionarySimpleInfo();
	void setDictionaryOrder(in int index, in int order);
	void setEnableDictionary(in int index, in boolean enable);
	
}