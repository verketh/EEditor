package cn.viewphoto.eedit.editor;

import android.graphics.Paint;
import android.graphics.RectF;
import cn.viewphoto.eedit.view.Cursor;

public class Grid {
	public static final String TAG = "Grid";
	
	private String 	mContent;
	private RectF	mRect;
	private int 	mContentType;
	public Grid(){
		mContent = "";
		mRect = new RectF();
	}
	/*public void draw(Canvas canvas,Paint paint,int type){
		float x = mRect.left;
		float y = mRect.bottom;
		if(type!=Parser.TYPE_CODE){
			Painter.drawHeadBackground(canvas, paint, mRect, type);
			paint.setStyle(Paint.Style.STROKE);
			paint.setColor(Color.BLACK);
			canvas.drawRect(mRect, paint);
		}
		paint.setColor(Color.BLACK);
		canvas.drawText(mContent, x, y, paint);
	}*/
	/**
	 * 取表格的内容
	 * @return
	 */
	public String getContent(){
		return mContent;
	}
	/**
	 * 设置表格内容
	 * @param content
	 */
	public void setContent(String content){
		mContent = content;
		//mRect.set(0, 0, 0, 0);
	}
	/**
	 * 获取表格内容的宽度
	 * @param paint
	 * @return
	 */
	public float getContentWidth(Paint paint){
		//paint.getTextBounds(mContent, 0, mContent.length(), mTemRect);
		//return mTemRect.width();
		return paint.measureText(mContent);
	}
	/**
	 * 表格模式是否为选择模式
	 * @return
	 */
	public boolean isSelectMod(){
		return mContentType!=Parser.CONTENT_TYPE_TEXT;
	}
	/**
	 * 是否为选中状态
	 * @return 选中返回true
	 */
	public boolean isSelect(){
		if("".equals(mContent)){
			return false;
		}
		return true;
	}
	/**
	 * 设置选中
	 * @param select
	 */
	public void select(boolean select){
		if(select==false){
			mContent = "";
			return;
		}
		mContent = Parser.getSelectStr(mContentType);
	}
	public int getContentType(){
		return mContentType;
	}
	public void setContentType(int type){
		mContentType = type;
	}
	/**
	 * 设置表格的起始位置
	 * @param x
	 * @param y
	 */
	public void setPosition(float x,float y){
		mRect.set(x, y, x + mRect.width(), y + mRect.height());
	}
	public void setSize(float width,float height){
		mRect.set(mRect.left, mRect.top, mRect.left + width, mRect.top + height);
	}
	/**
	 * 设置表格的宽度
	 * @param width
	 */
	public void setWidth(int width){
		mRect.set(mRect.left, mRect.top, mRect.left + width, mRect.bottom);
	}
	/**
	 * 设置表格的高度
	 * @param height
	 */
	public void setHeight(int height){
		mRect.set(mRect.left, mRect.top, mRect.right, mRect.top + height);
	}
	public float getWidth(){
		return mRect.width();
	}
	public float getHeight(){
		return mRect.height();
	}
	/**
	 * 指定的点是否在当前表格内
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(float x,float y){
		return mRect.contains(x, y);
	}
	public void getRect(RectF rect){
		rect.set(mRect);
	}
	public RectF getRect(){
		return mRect;
	}
	/**
	 * 获取输入位置的字符起始索引
	 * @param cursor
	 * @param paint
	 * @return
	 */
	public int getInputIndex(Cursor cursor,Paint paint,float marginX){
		float startPos = mRect.left + marginX;
		int result = 0;
		float x = cursor.getX();
		float min = x - startPos,width,value;
		for(int i=0;i<mContent.length();i++){
			//不包含末尾换行符
			if(i+1>=mContent.length() && mContent.charAt(i)=='\n'){
				break;
			}
			if(i+1>=mContent.length() && mContent.charAt(i)=='\n'){
				break;
			}
			width = paint.measureText(mContent, 0, i + 1);
			value = x - startPos - width;
			if(Math.abs(value)>Math.abs(min)){
				break;
			}else{
				min = value;
				//result = startPos + mTemRect.width();
				result = i + 1;
			}
		}
		
		return result;
	}
	public float getCursorX(int index,Paint paint){
		float startPos = mRect.left;
		float result = startPos;
		//如为选择模式，直接返回起始位置
		if(isSelectMod()==true){
			return result;
		}
		index = index<0 ? 0 : index;
		if(mContent.endsWith("\n")){
			index = index>mContent.length()-1 ? mContent.length()-1 : index;
		}else{
			index = index>mContent.length() ? mContent.length() : index;
		}
		result += paint.measureText(mContent, 0, index);
		return result;
	}
	/**
	 * 根据点击位置获取光标位置
	 * @param x点击的位置
	 * @param paint
	 * @param isClick是否为用户点击
	 * @return
	 */
	public float getCursorX(float x,Paint paint,boolean isClick){
		float startPos = mRect.left;
		float result = startPos;
		//如为选择模式，直接设置内容，并返回起始位置
		if(isClick==true && isSelectMod()==true){
			select(isSelect() ? false : true); 
			return result;
		}
		float min = x - result,value,width;
		//计算与当前点击位置最近的字符插入位置
		for(int i=0;i<mContent.length();i++){
			//paint.getTextBounds(mContent, 0, i+1, mTemRect);
			//value = x - startPos - mTemRect.width();
			//不计算末尾换行符的宽度
			if(i+1>=mContent.length() && mContent.charAt(i)=='\n'){
				break;
			}
			width = paint.measureText(mContent, 0, i + 1);
			value = x - startPos - width;
			if(Math.abs(value)>Math.abs(min)){
				break;
			}else{
				min = value;
				//result = startPos + mTemRect.width();
				result = startPos + width;
			}
		}
		return result;
	}
	public float getX(){
		return mRect.left;
	}
	public float getY(){
		return mRect.top;
	}
}
