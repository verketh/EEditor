package cn.viewphoto.eedit.adapter;

public class HintItem {
	private String 	mContent;
	private String 	mLibName;//支持库英文名
	private String  mLibCnName;//支持库中文名
	private String 	mTitle;
	
	public String getLibCnName() {
		return mLibCnName;
	}
	public void setLibCnName(String mLibCnName) {
		this.mLibCnName = mLibCnName;
	}
	public String getTitle() {
		return mTitle;
	}
	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}
	public String getContent(){
		return mContent;
	}
	public void setContent(String content){
		mContent = content;
	}
	public String getLibName(){
		return mLibName;
	}
	public void setLibName(String libName){
		mLibName = libName;
	}
	/**
	 * 测试该提示项是否匹配
	 * @param match匹配字符串
	 * @param start起始匹配位置
	 * @return
	 */
	public boolean Match(String match,int start){
		
		
		return true;
	}
}
