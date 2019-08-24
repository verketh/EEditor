package cn.viewphoto.eedit.editor;

import java.util.ArrayList;
import java.util.List;

public class Editor {
	private StringBuilder mStrBuilder;
	private List<LineInfo> mLineInfoList;
	
	public Editor() {
		mStrBuilder = new StringBuilder();
		mLineInfoList = new ArrayList<LineInfo>();
		mLineInfoList.add(new LineInfo(0,0));
	}
	/**
	 * 取总行数
	 * @return
	 */
	public int getLineCount(){
		return mLineInfoList.size();
	}
	/**
	 * 获取指定行的内容
	 * @param lineIndex
	 * @return 索引超出范围返回"";
	 */
	public String getLine(int lineIndex){
		String result = "";
		if(lineIndex>=0 && lineIndex<mLineInfoList.size()){
			LineInfo line = mLineInfoList.get(lineIndex);
			if(line.getStartIndex()<mStrBuilder.length() && line.getEndIndex()<mStrBuilder.length()){
				result = mStrBuilder.substring(line.getStartIndex(), line.getEndIndex() + 1);
			}
		}
		
		return result;
	}
	@Override
	public String toString() {
		return mStrBuilder.toString();
	}
	/**
	 * 设置内容为指定的str
	 * @param str
	 */
	public void setText(String str){
		mStrBuilder = new StringBuilder(str);
		modifiLineInfo(0);
	}
	/**
	 * 将字符添加到末尾
	 * @param ch
	 */
	public void append(char ch){
		int start = mStrBuilder.length() - 1<0 ? 0 : mStrBuilder.length() - 1;
		mStrBuilder.append(ch);
		modifiLineInfo(start);
	}
	/**
	 * 将字符串添加的末尾
	 * @param str
	 */
	public void append(String str){
		int start = mStrBuilder.length() - 1<0 ? 0 : mStrBuilder.length() - 1;
		mStrBuilder.append(str);
		modifiLineInfo(start);
	}
	/**
	 * 插入字符
	 * @param lineIndex插入的行索引
	 * @param chIndexOfLine插入行的起始插入位置
	 * @param ch
	 * @throws StringIndexOutOfBoundsException
	 */
	public void insert(int lineIndex,int chIndexOfLine,char ch) throws StringIndexOutOfBoundsException{
		insert(lineIndex,chIndexOfLine,String.valueOf(ch));
	}
	/**
	 * 插入字符串
	 * @param lineIndex插入的行索引
	 * @param strIndexOfLine插入行的起始插入位置
	 * @param str
	 * @throws StringIndexOutOfBoundsException
	 */
	public void insert(int lineIndex,int strIndexOfLine,String str) throws StringIndexOutOfBoundsException{
		if(lineIndex<0 || lineIndex>=mLineInfoList.size() || strIndexOfLine>=mLineInfoList.get(lineIndex).length()){
			throw new StringIndexOutOfBoundsException();
		}
		LineInfo line = mLineInfoList.get(lineIndex);
		int insertIndex = line.getStartIndex() + strIndexOfLine;
		mStrBuilder.insert(insertIndex, str);
		modifiLineInfo(insertIndex);
	}
	/**
	 * 删除指定位置的字符串
	 * @param lineIndex行索引
	 * @param strIndexOfLine行起始删除位置
	 * @param lenght删除的长度，如大于起始位置到行末的lenght,则只删除到行末
	 * @throws StringIndexOutOfBoundsException
	 */
	public void delete(int lineIndex,int strIndexOfLine,int lenght) throws StringIndexOutOfBoundsException{
		if(lineIndex<0 || lineIndex>=mLineInfoList.size() || strIndexOfLine>=mLineInfoList.get(lineIndex).length()){
			throw new StringIndexOutOfBoundsException();
		}
		LineInfo line = mLineInfoList.get(lineIndex);
		int startIndex = line.getStartIndex() + strIndexOfLine;
		int endIndex = line.getStartIndex() + ((strIndexOfLine + lenght)>line.length() ? line.length() - 1 : strIndexOfLine + lenght);
		mStrBuilder.delete(startIndex, endIndex);
		modifiLineInfo(startIndex);
	}
	/**
	 * 修改行信息
	 * @param startIndex起始修改的索引
	 */
	private void modifiLineInfo(int startIndex){
		int lineIndex = 0;
		LineInfo line;
		// 得到起始修改的行索引
		for(int i=0;i<mLineInfoList.size();i++){
			line = mLineInfoList.get(i);
			if(line.getStartIndex() <= startIndex){
				lineIndex = i;
			}else{
				break;
			}
		}
		//从起始行开始修改后面所有的行信息
		char ch;
		for(int i=startIndex;i<mStrBuilder.length();i++){
			ch = mStrBuilder.charAt(i);
			if(ch=='\n'){
				//添加新行信息并修改当前行信息
				line = mLineInfoList.get(lineIndex);
				line.setEndIndex(i);
				lineIndex++;
				if(lineIndex>=mLineInfoList.size()){
					mLineInfoList.add(new LineInfo(i + 1, i + 1));
				}else{
					//复用line对象
					line = mLineInfoList.get(lineIndex);
					line.setStartIndex(i + 1);
				}
			}
		}
		//设置最后一行的结束索引
		line = mLineInfoList.get(lineIndex);
		line.setEndIndex(mStrBuilder.length() - 1);
		//将后面多余的行信息移除
		for(int i=mLineInfoList.size() - 1;i>lineIndex;i--){
			mLineInfoList.remove(i);
		}
	}
	private class LineInfo{
		private int mLineStartIndex;//该行的起始索引
		private int mLineEndIndex;	//该行的结束索引(包含换行符)
		
		public LineInfo(int startIndex,int endIndex) {
			mLineStartIndex = startIndex;
			mLineEndIndex = endIndex;
		}
		public int getStartIndex(){
			return mLineStartIndex;
		}
		public void setStartIndex(int index){
			mLineStartIndex = index;
		}
		public int getEndIndex(){
			return mLineEndIndex;
		}
		public void setEndIndex(int index){
			mLineEndIndex = index;
		}
		public int length(){
			int result = mLineEndIndex - mLineStartIndex + 1;
			if(result<0){
				result = 0;
			}
			return result;
		}
	}
}
