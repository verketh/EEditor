package cn.viewphoto.eedit.editor;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Paint;
import android.graphics.RectF;
import cn.viewphoto.eedit.view.Cursor;

public class Row {
	private List<Grid>	mGridList;
	private int 		mRowType;
	public Row(){
		this(Parser.TYPE_CODE);
	}
	public Row(int type){
		mGridList = new ArrayList<Grid>();
		mRowType = type;
	}
	/*public void draw(Canvas canvas,Paint paint){
		if(mRowType==Parser.TYPE_VERSION){
			return;
		}
		for(Grid grid:mGridList){
			grid.draw(canvas, paint,mRowType);
		}
		
	}*/
	public void setRowType(int type){
		mRowType = type;
	}
	public int getRowType(){
		return mRowType & 0xFFFF;
	}
	public int getCodeType(){
		return mRowType & 0xFFFF0000;
	}
	public List<Grid> getGridList(){
		return mGridList;
	}
	public void add(Grid grid){
		mGridList.add(grid);
	}
	public void insert(Grid grid,int index){
		if(index>=0 && index<mGridList.size()){
			mGridList.add(index, grid);
		}else{
			mGridList.add(grid);
		}
	}
	public void remove(int index){
		if(index>=0 && index<mGridList.size()){
			mGridList.remove(index);
		}
	}
	/**
	 * 获取行内容
	 * @return
	 */
	public String getRowContent(){
		
		return Parser.getRowContent(this);
	}
	/**
	 * 设置行内容
	 * @param line
	 */
	public void setRowContent(String line){
		Parser.parser(this, line);
	}
	/**
	 * 获取表格内容的宽度
	 * @param index
	 * @param paint
	 * @return
	 */
	public float getGridContentWidth(int index,Paint paint){
		if(index>=0 && index<mGridList.size()){
			return mGridList.get(index).getContentWidth(paint);
		}
		return 0;
	}
	/**
	 * 设置表格的宽高
	 * @param index
	 * @param width
	 * @param height
	 */
	public void setGridSize(int index,int width,int height){
		if(index>=0 && index<mGridList.size()){
			mGridList.get(index).setSize(width, height);
		}
	}
	public void setHead(){
		
	}
	public Grid getGrid(int index){
		if(index>=0 && index<mGridList.size()){
			return mGridList.get(index);
		}
		return null;
	}
	/**
	 * 设置grid的位置与宽高
	 * @param index Grid的索引
	 * @param rowX Row的起始位置x
	 * @param rowY Row的起始位置Y
	 * @param width  Grid的宽度
	 * @param height Grid的高度
	 */
	public void setGridSize(int index,float rowX,float rowY,float width,float height){
		if(index>=0 && index<mGridList.size()){
			mGridList.get(index).setSize(width, height);
			float x = rowX;
			for(int i=0;i<index;i++){
				x += mGridList.get(i).getWidth();
			}
			mGridList.get(index).setPosition(x, rowY);
		}
	}
	public int getGridCount(){
		return mGridList.size();
	}
	public boolean getRowRect(RectF rect){
		if(mGridList.size()>0){
			RectF r = mGridList.get(0).getRect();
			rect.set(r.left, r.top, r.right, r.bottom);
			for(int i=1;i<mGridList.size();i++){
				r = mGridList.get(i).getRect();
				rect.set(rect.left, rect.top, rect.right + r.width(), rect.bottom);
			}
			return true;
		}
		return false;
	}
	public float getX(){
		for(Grid grid:mGridList){
			return grid.getRect().left;
		}
		return 0;
	}
	public float getY(){
		for(Grid grid:mGridList){
			return grid.getRect().top;
		}
		return 0;
	}
	/**
	 * 获取光标所在的列
	 * @param cursor
	 * @return
	 */
	public int getInputGridIndex(Cursor cursor,float marginX){
		float cursorX = cursor.getX() + cursor.getWidth() / 2 - marginX;
		float cursorY = cursor.getY();
		Grid grid;
		for(int i=0;i<mGridList.size();i++){
			grid = mGridList.get(i);
			if(grid.contains(cursorX, cursorY)==true){
				return i;
			}
		}
		if(mGridList.size()>0){
			if(cursorX<getX()){
				return 0;
			}else{
				return mGridList.size() - 1;
			}
		}
		
		return -1;
	}
	/**
	 * 取光标的x坐标
	 * @param table
	 * @param x表格中被点击的x位置
	 * @param paint
	 * @return
	 */
	public float getCursorX(Table table,float x,Paint paint){
		float result = 0;
		float y = getY();
		int rowType = getRowType();
		for(Grid grid:mGridList){
			if(grid.contains(x, y)==true){
				result = grid.getCursorX(x, paint,true);
				result += table.getMarginX();
				if(rowType!=Parser.TYPE_CODE){//非代码需多加1个边距
					result += table.getMarginX();
				}
				if(grid.isSelectMod()==true) setLineBreak();//如为选择模式，更改换行符的位置
				return result;
			}
		}
		//如点击位置在所有单元格外，执行以下逻辑
		if(mGridList.size()>0){
			Grid grid;
			if(x<getX()){
				//小于行的起始位置，光标设置在行首
				grid = mGridList.get(0);
				/*result = grid.getRect().left;*/
			}else{
				//大于行的内容宽度，设置光标到行末
				grid = mGridList.get(mGridList.size() - 1);
				/*String content = grid.getContent();
				result = grid.getRect().left;
				if(content.endsWith("\n")){
					result += paint.measureText(content, 0, content.length() - 1);//不计算末尾换行符的宽度
				}else{
					result += paint.measureText(content);
				}*/
			}
			result = grid.getCursorX(x, paint,true);
		}
		if(rowType!=Parser.TYPE_CODE){//非代码需多加1个边距
			result += table.getMarginX();
		}
		result += table.getMarginX();
		return result;
	}
	/**
	 * 设置换行符的位置
	 */
	public void setLineBreak(){
		boolean has = false;
		String content;
		for(int i=mGridList.size() - 1;i>=0;i--){
			content = mGridList.get(i).getContent();
			if(has==false && "".equals(content)==false){
				if(content.endsWith("\n")==false){//添加换行符
					mGridList.get(i).setContent(content + "\n");
				}
				has = true;
				continue;
			}
			if(has==true){
				if(content.endsWith("\n")==true){//去掉换行符
					mGridList.get(i).setContent(content.substring(0, content.length() - 1));
				}
			}
		}
		if(has==false && mGridList.size()>0){
			content = mGridList.get(0).getContent();
			mGridList.get(0).setContent(content + "\n");
		}
	}
}
