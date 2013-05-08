
package jie.android.lac.service.aidl;

import jie.android.lac.data.Word;
import jie.android.lac.data.Dictionary;

interface Access {
	int checkState();
	List<Word.Info> queryWordInfo(in String condition, in int offset, in int limit);
	Word.XmlResult queryWordXmlResult(in int index);
//	List<Dictionary.Info> queryDictionaryInfo();
	List<Dictionary.SimpleInfo> queryDictionarySimpleInfo();
	void setDictionaryOrder(in int index, in int order);
	void setEnableDictionary(in int index, in boolean enable);
	
}