package cn.viewphoto.eedit.util;

import java.util.Locale;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;

public class PinYinUtil {
	
	private static HanyuPinyinOutputFormat	mFormat;
	static{
		mFormat = new HanyuPinyinOutputFormat();
		mFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
		mFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	}
	/**
	 * 获取字符的拼音
	 * @param ch
	 * @return 返回该字符的拼音，非汉字返回null
	 */
	public static String[] getPinYin(char ch){
		String[] result = null;
		try{
			result = PinyinHelper.toHanyuPinyinStringArray(ch, mFormat);
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 匹配字符串的拼音首字母
	 * @param firstPinYin拼音首字母
	 * @param str匹配的字符串
	 * @return 匹配返回true
	 */
	public static boolean matchFirstPinYin(String firstPinYin,String str){
		boolean result = false;
		if(firstPinYin==null || str==null || firstPinYin.length()>str.length()){//匹配字符串必须大于或等于拼音首字母的长度
			return result;
		}
		char[] firstArr = firstPinYin.toLowerCase(Locale.getDefault()).toCharArray();
		char[] strArr	= str.toLowerCase(Locale.getDefault()).toCharArray();
		try{
			char[] pinYinCharArr;
			String[] pinYinStrArr;
			boolean match;
			int i=0;
			for(;i<firstArr.length && i<strArr.length;i++){
				pinYinStrArr = PinyinHelper.toHanyuPinyinStringArray(strArr[i], mFormat);
				if(pinYinStrArr==null){//非汉字直接比较
					if(firstArr[i]!=strArr[i]){
						return false;
					}
				}else{
					match = false;
					for(int j=0;j<pinYinStrArr.length;j++){//多音字匹配，只要有一个匹配上，就算匹配
						pinYinCharArr = pinYinStrArr[j].toCharArray();
						if(firstArr[i]==pinYinCharArr[0]){
							match = true;
							break;
						}
					}
					if(match==false){
						return false;
					}
				}
			}
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
}
