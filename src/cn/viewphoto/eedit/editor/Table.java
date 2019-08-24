package cn.viewphoto.eedit.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Vibrator;
import cn.viewphoto.eedit.view.Cursor;
import cn.viewphoto.eedit.view.EEditText;
import cn.viewphoto.eedit.view.HintView;

public class Table {
	public static final String TAG = "Table";
	private List<Row> mRowList;
	private Paint mPaint;
	private float mScrollX;
	private float mScrollY;
	private int mMaxWidth;//内容的最大宽度
	private int mMaxHeight;//内容的最大高度
	private int mRowHeight;//行的高度
	private int mViewWidth;//View的宽度
	private int mViewHeight;//View的高度
	private float mMarginX;//文本距离view左边的距离
	private float mMarginY;//文本距离行底部的距离
	private Cursor	mCursor;
	private float 	mCursorMarginBottom;//输入行距离底部的行数
	private	HandleInput	mHandleInput;
	private boolean mEnabled = false;
	private Vibrator	mVibrator;
	private long[]		mPattern;
	private HintView	mHintView;//代码提示view
	public Table(EEditText eEditText){
		Context context = eEditText.getContext();
		mRowList = new ArrayList<Row>();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setTextSize(Config.fontSize);
		mCursor = new Cursor();
		mHandleInput = new HandleInput(context);
		mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		mPattern = new long[]{0,50};
		mHintView = new HintView(eEditText);
	}
	/**
	 * 震动
	 */
	public void vibrator(){
		if(Config.vibrator==false) return;
		mVibrator.vibrate(mPattern, -1);
	}
	public int getRowHeight(){
		return mRowHeight;
	}
	public Cursor getCursor(){
		return mCursor;
	}
	/**
	 * 获取光标的屏幕位置
	 * @return
	 */
	public Point getCursorPoint(){
		return new Point((int)(mCursor.getX() - mScrollX),(int)(mCursor.getY() - mScrollY));
	}
	public Editor getEditor(){
		Editor editor = new Editor();
		String str;
		for(int i=0;i<mRowList.size();i++){
			str = mRowList.get(i).getRowContent();
			//将换行符转为windows形式
			if(str.endsWith("\n")==true && str.endsWith("\r\n")==false){
				str = str.replaceAll("\n", "\r\n");
			}
			editor.append(str);
		}
		return editor;
	}
	public boolean getEnabled(){
		return mEnabled;
	}
	public void addMethod(){
		if(canAddMethod()){
			Row row = new Row();
			Parser.parserHead(row, Parser.TYPE_METHOD_HEAD);
			mRowList.add(row);
			row = new Row();
			Parser.parser(row, Parser.STR_METHOD + "方法\n");
			mRowList.add(row);
			row = new Row();
			Parser.parser(row, "\n");
			mRowList.add(row);
			measure();
			onScroll(0, mMaxHeight);
			measureCursorPosition(0, mViewHeight);
		}
	}
	public void addLocal(){
		int index=getInsertLocalIndex();
		if(index>=0){
			if(mRowList.get(index - 1).getRowType()!=Parser.TYPE_LOCAL && mRowList.get(index - 1).getRowType()!=Parser.TYPE_LOCAL_HEAD){
				Row row = new Row();
				Parser.parserHead(row, Parser.TYPE_LOCAL_HEAD);
				insert(index, row);
				index++;
			}
			Row row = new Row();
			Parser.parser(row, Parser.STR_LOCAL + "变量\n");
			insert(index, row);
			measure();
			mCursor.setPosition(mMarginX * 2, row.getY());
			changeInputRowPosition();
		}
	}
	public boolean canAddMethod(){
		return mEnabled;
	}
	public boolean canAddLocal(){
		return getInsertLocalIndex()>=0;
	}
	/**
	 * 光标所在位置是否可以插局部变量
	 * @return
	 */
	private int getInsertLocalIndex(){
		int index = getInputRowIndex();
		if(index>=0 && index<mRowList.size()){
			 int type = mRowList.get(index).getRowType();
			if(type==Parser.TYPE_CODE){
				for(int i=index;i>=0;i--){
					type = mRowList.get(i).getRowType();
					if(type==Parser.TYPE_LOCAL || type==Parser.TYPE_LOCAL_HEAD
						|| type==Parser.TYPE_PARAM || type==Parser.TYPE_PARAM_HEAD
						|| type==Parser.TYPE_METHOD	|| type==Parser.TYPE_METHOD_HEAD){
						return i + 1;
					}
				}
			}else if(type==Parser.TYPE_LOCAL || type==Parser.TYPE_LOCAL_HEAD
					|| type==Parser.TYPE_PARAM || type==Parser.TYPE_PARAM_HEAD
					|| type==Parser.TYPE_METHOD	|| type==Parser.TYPE_METHOD_HEAD){
				for(int i=index;i<mRowList.size();i++){
					type = mRowList.get(i).getRowType();
					if(type==Parser.TYPE_CODE){
						return i;
					}
				}
			}
		}
		return -1;
	}
	/**
	 * 将内容解析到表格
	 * @param editor
	 */
	public void setEditor(Editor editor){
		if(mRowList.size()<editor.getLineCount()){
			for(int i=mRowList.size();i<editor.getLineCount();i++){
				mRowList.add(new Row());
			}
		}
		if(mRowList.size()>editor.getLineCount()){
			for(int i=mRowList.size() - 1;i>=editor.getLineCount();i--){
				mRowList.remove(i);
			}
		}
		int lastType = -1,type;
		Row row;
		ArrayList<HashMap<Integer, Row>> headList = new ArrayList<HashMap<Integer, Row>>();
		HashMap<Integer, Row> map;
		for(int i=0;i<editor.getLineCount();i++){
			row = mRowList.get(i);
			Parser.parser(row, editor.getLine(i));
			type = row.getRowType();
			if(lastType!=type){
				map = new HashMap<Integer, Row>();
				switch(type){
				case Parser.TYPE_CLASS:
					row = new Row();
					Parser.parserHead(row, Parser.TYPE_CLASS_HEAD);
					map.put(i,row);
					headList.add(map);
					break;
				case Parser.TYPE_FIELD:
					row = new Row();
					Parser.parserHead(row, Parser.TYPE_FIELD_HEAD);
					map.put(i,row);
					headList.add(map);
					break;
				case Parser.TYPE_METHOD:
					row = new Row();
					Parser.parserHead(row, Parser.TYPE_METHOD_HEAD);
					map.put(i,row);
					headList.add(map);
					break;
				case Parser.TYPE_PARAM:
					row = new Row();
					Parser.parserHead(row, Parser.TYPE_PARAM_HEAD);
					map.put(i,row);
					headList.add(map);
					break;
				case Parser.TYPE_LOCAL:
					row = new Row();
					Parser.parserHead(row, Parser.TYPE_LOCAL_HEAD);
					map.put(i,row);
					headList.add(map);
					break;
				}
			}
			lastType = type;
		}
		//将各个分类的表头添加到列表
		int value = 0;
		for(HashMap<Integer, Row> head:headList){
			for(int index:head.keySet()){
				mRowList.add(index + value, head.get(index));
			}
			value++;
		}
		mScrollX = 0;
		mScrollY = 0;
		measure();
		measureCursorPosition(0,0);
		mEnabled = true;
	}
	public void draw(Canvas canvas){
		if(mRowList.size()<=0) return;
		//绘制光标所在行的背景色
		if(mCursor.getY() + mRowHeight >= mScrollY && mCursor.getY()<=mScrollY + mViewHeight){
			mCursor.drawBackgroud(canvas, mMaxWidth, mScrollY);
		}
		Painter.setScroll(mScrollX, mScrollY);
		int start = (int) (mScrollY / mRowHeight - 1);
		int  end = start + mViewHeight / mRowHeight + 1;
		start = start<0 ? 0 : start;
		end = end>=mRowList.size() ? mRowList.size() - 1 : end;
		for(int i=start;i<=end;i++){
			
			Painter.draw(canvas, mPaint, mRowList.get(i));
		}
		//绘制光标
		if(mCursor.getY() + mRowHeight >= mScrollY && mCursor.getY()<=mScrollY + mViewHeight){
			mCursor.draw(canvas,mScrollX,mScrollY);
		}
	}
	public void setViewSize(int width,int height){
		mViewWidth = width;
		mViewHeight = height;
		mMaxWidth = mMaxWidth<mViewWidth ? mViewWidth : mMaxWidth;
		mMaxHeight = mMaxHeight<mViewHeight ? mViewHeight : mMaxHeight;
		mCursorMarginBottom = height / 2;
	}
	/**
	 * 计算各个行的宽高
	 */
	public void measure(){
		mMaxWidth = 0;
		mMaxHeight = 0;
		float[] cloumns = new float[6];
		mRowHeight = (int) (mPaint.getTextSize() + mPaint.getTextSize() * 0.3);//计算行高
		//计算x、y的距离值
		mMarginX = mPaint.getTextSize() * 0.2f;
		mMarginY = mRowHeight - mPaint.getTextSize() * 0.96f;
		Painter.setMargin(mMarginX, mMarginY);
		//计算table的最大高度
		mMaxHeight = (int) (mRowHeight * mRowList.size() + mCursorMarginBottom + 1);
		int start = 0,end = 0;
		int lastType = 0,type;
		//Log.i(TAG, "rows:"+mRowList.size());
		for(int i=0;i<mRowList.size();i++){
			type = changeType(mRowList.get(i).getRowType());
			if(lastType!=type){
				getGridsWidth(start, end, cloumns);
				setGridsPosition(start, end, cloumns, mRowHeight);
				start = i;
				end = i;
			}else{
				end++;
			}
			
			lastType = type;
		}
		//设置最后的分组
		if(start<mRowList.size() && end<mRowList.size()){
			getGridsWidth(start, end, cloumns);
			setGridsPosition(start, end, cloumns, mRowHeight);
		}
		mMaxWidth += mMarginX * 2;//最大宽度加上x的边距
		mMaxWidth = mMaxWidth<mViewWidth ? mViewWidth : mMaxWidth;
		mMaxHeight = mMaxHeight<mViewHeight ? mViewHeight : mMaxHeight;
		//Log.i(TAG, "最大宽度"+mMaxWidth+" 最大高度"+mMaxHeight);
	}
	/**
	 * 
	 * @param x点击屏幕的x坐标
	 * @param y点击屏幕y坐标
	 */
	private void measureCursorPosition(float x,float y){
		float width = mRowHeight * 0.133f,height = mRowHeight;
		//将屏幕坐标转换为整个table中的坐标
		float left = mMarginX - width / 2;//应该根据row的文本确定
		float top = 0;
		if(mRowList.size() > 0){
			/*int start = (int) (mScrollY / mRowHeight - 1);//取出起始行的索引
			start = start<0 ? 0 : start;
			start = start>=mRowList.size() ? mRowList.size() - 1 : start;
			y -= mRowList.get(start).getY() - mScrollY;//求当前屏幕坐标到起始行的距离
*/			//转换为表格的位置
			y += mScrollY;
			x += mScrollX;
			//int index = start + (int) (y / mRowHeight);//起始行+相距的行数
			int index = (int) (y / mRowHeight);
			index = index>=mRowList.size() ? mRowList.size() - 1 : index;
			top = mRowList.get(index).getY();
			left = mRowList.get(index).getCursorX(this, x, mPaint) - width / 2;
		}
		
		mCursor.setRect(left, top, left + width, top + height);
	}
	/**
	 * 将各个行进行分组
	 * @param type
	 * @return
	 */
	private int changeType(int type){
		int result = 0;
		if(type==Parser.TYPE_CLASS || type==Parser.TYPE_CLASS_HEAD || type==Parser.TYPE_FIELD || type==Parser.TYPE_FIELD_HEAD){
			result = 1;
		}
		if(type==Parser.TYPE_METHOD || type==Parser.TYPE_METHOD_HEAD || type==Parser.TYPE_PARAM || type==Parser.TYPE_PARAM_HEAD){
			result = 2;
		}
		if(type==Parser.TYPE_LOCAL || type==Parser.TYPE_LOCAL_HEAD){
			result = 3;
		}
		if(type==Parser.TYPE_CODE){
			result = 4;
		}
		return result;
	}
	/**
	 * 设置指定行的各个列的大小
	 * @param start
	 * @param end
	 * @param cloumns
	 * @param rowHeight
	 */
	private void setGridsPosition(int start,int end,float[] cloumns,int rowHeight){
		Row row;
		float rowX = 0,rowY = start * rowHeight;
		//计算表格的最宽大小
		int width = 0;
		for(int i=0;i<cloumns.length;i++){
			width += cloumns[i];
		}
		mMaxWidth = width>mMaxWidth ? width : mMaxWidth;
		//如行类型为方法与参数,方法第四列宽度为以下宽度
		float cloumn3 = cloumns[3]>(cloumns[4] * 2 + cloumns[5]) ? cloumns[3] : (cloumns[4] * 2 + cloumns[5]);
		int type;
		for(int i=start;i<=end;i++){
			row = mRowList.get(i);
			type = row.getRowType();
			if(type==Parser.TYPE_METHOD || type==Parser.TYPE_METHOD_HEAD){
				for(int j=0;j<3;j++){
					row.setGridSize(j, rowX, rowY, cloumns[j], rowHeight);
				}
				row.setGridSize(3, rowX, rowY, cloumn3, rowHeight);
				cloumns[3] = cloumns[4];
				cloumns[5] = cloumn3 - cloumns[3] - cloumns[4];
			}else{
				for(int j=0;j<cloumns.length;j++){
					row.setGridSize(j, rowX, rowY, cloumns[j], rowHeight);
				}
			}
			rowY += rowHeight;
		}
	}
	private void getGridsWidth(int start,int end,float[] cloumns){
		//初始化
		for(int i=0;i<cloumns.length;i++){
			cloumns[i] = 0;
		}
		Row row;
		float width;
		for(int i=start;i<=end;i++){
			row = mRowList.get(i);
			for(int j=0;j<cloumns.length;j++){
				width = row.getGridContentWidth(j, mPaint);
				if(cloumns[j]<width){
					cloumns[j] = width;
				}
			}
		}
		for(int i=0;i<cloumns.length;i++){
			cloumns[i] +=mMarginX * 2;
		}
	}
	public void setTextSize(float size){
		mPaint.setTextSize(size);
		measure();
		measureCursorPosition(0, 0);
		changeInputRowPosition();
		Painter.setMargin(mMarginX, mMarginY);
	}
	/**
	 * 处理滚动条滚动
	 */
	public void onScroll(float distanceX, float distanceY){
		mScrollX +=distanceX;
		mScrollY +=distanceY;
		mScrollX = mScrollX<0 ? 0 : mScrollX;
		mScrollX = mScrollX>(mMaxWidth - mViewWidth) ? (mMaxWidth - mViewWidth) : mScrollX;
		mScrollY = mScrollY<0 ? 0 : mScrollY;
		mScrollY = mScrollY>(mMaxHeight - mViewHeight) ? (mMaxHeight - mViewHeight) : mScrollY;
		//Log.i(TAG, "scrollX:"+mScrollX+" scrollY:"+mScrollY);
		//Log.i(TAG, "viewWidth:"+mViewWidth+" viewHeight:"+mViewHeight+" maxWidth:"+mMaxWidth+" maxHeight:"+mMaxHeight);
	}
	/**
	 * 处理点击事件
	 * @param x
	 * @param y
	 */
	public void onClick(float x,float y){
		int inputRow = getInputRowIndex();
		measureCursorPosition(x, y);
		if(inputRow!=getInputRowIndex()){//输入行位置被切换，关闭代码提示view
			mHintView.dismiss();
		}
	}
	/**
	 * 处理字符输入
	 * @param input
	 */
	public void onInput(EEditText view,String input){
		int rowIndex = getInputRowIndex();
		if(rowIndex<0 || rowIndex>=mRowList.size()) return;
		int gridIndex = mRowList.get(rowIndex).getInputGridIndex(mCursor,mMarginX);
		if(gridIndex<0) return;
		mHandleInput.handle(view, this, rowIndex, gridIndex, mCursor , input);
		mHintView.checkAndShow(mViewWidth, mViewHeight);//检查是否显示代码提示view
	}
	/**
	 * 获取光标所在行
	 * @return
	 */
	public int getInputRowIndex(){
		if(mRowList.size()<=0){
			return -1;
		}
		int index = (int) (mCursor.getY() / mRowHeight);
		if(index>=0 && index<mRowList.size()){
			return index;
		}
		return -1;
	}
	public float getMarginX(){
		return mMarginX;
	}
	public float getMarginY(){
		return mMarginY;
	}
	/**
	 * 改变输入行的显示位置，使光标可见
	 */
	public void changeInputRowPosition(){
		//view的高度会随软键盘的弹出或关闭而改变
		if(mCursor.getY()<mScrollY){
			float distanceY = mCursor.getY() - (mScrollY + mViewHeight);//下个if会重新调整位置
			onScroll(0, distanceY);
		}
		if(mCursor.getY()>mScrollY + mViewHeight - mCursorMarginBottom){
			float distanceY = mCursor.getY() - mScrollY - mViewHeight + mCursorMarginBottom;
			onScroll(0, distanceY);
		}
		if(mScrollY + mViewHeight>mMaxHeight){
			float distanceY = mMaxHeight - mScrollY - mViewHeight;
			onScroll(0, distanceY);
		}
		if(mCursor.getX()<mScrollX){
			float distanceX =  mCursor.getX() - mViewWidth * 0.2f - mScrollX;
			onScroll(distanceX, 0);
		}
		if(mCursor.getX()>mScrollX + mViewWidth){
			float distanceX = mCursor.getX() - mScrollX - mViewWidth + mViewWidth * 0.2f;
			onScroll(distanceX, 0);
		}
	}
	
	/**
	 * 插入行,需调用measure重新计算行列的宽高
	 * @param row
	 */
	public void insert(int index,Row row){
		index = index<0 ? 0 : index;
		index = index>mRowList.size() ? mRowList.size() : index;
		mRowList.add(index, row);
	}
	/**
	 * 删除行,需调用measure重新计算行列的宽高
	 * @param index
	 */
	public void delete(int index){
		mRowList.remove(index);
	}
	public Row getRow(int index){
		if(index<0 || index>=mRowList.size()) return null;
		return mRowList.get(index);
	}
	public Paint getPaint(){
		return mPaint;
	}
	/**
	 * 获取所有成员、参数、局部变量行
	 * @param list存储结果的list
	 */
	public void getFieldParamLocalRow(List<Row> list){
		int type;
		//获取成员变量
		for(Row row:mRowList){
			type = row.getRowType();
			if(type==Parser.TYPE_METHOD_HEAD || type==Parser.TYPE_METHOD){
				break;
			}
			if(type==Parser.TYPE_FIELD){
				list.add(row);
			}
		}
		int inputRowIndex = getInputRowIndex();
		if(inputRowIndex<0) return;
		Row row;
		//获取参数与局部变量
		for(int i=inputRowIndex;i>=0;i--){
			row = mRowList.get(i);
			type = row.getRowType();
			if(type==Parser.TYPE_METHOD_HEAD || type==Parser.TYPE_METHOD){
				break;
			}
			if(type==Parser.TYPE_LOCAL || type==Parser.TYPE_PARAM){
				list.add(row);
			}
		}
	}
}
