package cn.viewphoto.eedit.editor;

import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Painter {
	public static final String TAG = "Painter";
	private static RectF	mRect = new RectF();
	private static float mScrollX;
	private static float mScrollY;
	private static float mMarginX;
	private static float mMarginY;
	
	public static void setMargin(float marginX,float marginY){
		mMarginX = marginX;
		mMarginY = marginY;
	}
	private static void drawHeadBackground(Canvas canvas,Paint paint,Row row){
		if(row.getRowRect(mRect)==false){
			return;
		}
		paint.setStyle(Paint.Style.FILL);
		float x = mRect.left - mScrollX + mMarginX;
		float y = mRect.top - mScrollY;
		mRect.set(x, y, x + mRect.width(), y + mRect.height());
		switch(row.getRowType()){
		case Parser.TYPE_CLASS_HEAD:
			paint.setColor(Config.classFieldHeadBgColor);
			canvas.drawRect(mRect, paint);
			break;
		case Parser.TYPE_FIELD_HEAD:
			paint.setColor(Config.classFieldHeadBgColor);
			canvas.drawRect(mRect, paint);
			break;
		case Parser.TYPE_METHOD_HEAD:
			paint.setColor(Config.methodParamHeadBgColor);
			canvas.drawRect(mRect, paint);
			break;
		case Parser.TYPE_PARAM_HEAD:
			paint.setColor(Config.methodParamHeadBgColor);
			canvas.drawRect(mRect, paint);
			break;
		case Parser.TYPE_LOCAL_HEAD:
			paint.setColor(Config.localHeadBgColor);
			canvas.drawRect(mRect, paint);
			break;
		}
	}
	public static void draw(Canvas canvas,Paint paint,Row row){
		int type = row.getRowType();
		/*if(row.getGridCount()<=0 || type==Parser.TYPE_VERSION){
			return;
		}*/
		drawHeadBackground(canvas, paint, row);
		List<Grid> list = row.getGridList();
		float x,y;
		Grid grid;
		int count = list.size();
		for(int i=0;i<count;i++){
			grid = list.get(i);
			grid.getRect(mRect);
			//设置代码文本绘制的位置或矩形左边绘制的位置
			x = mRect.left - mScrollX + mMarginX;
			y = mRect.bottom - mScrollY - mMarginY;
			if(type!=Parser.TYPE_CODE){//绘制矩形
				//marginX = (mRect.width() - grid.getContentWidth(paint)) / 2;
				//设置表格边框的位置
				mRect.set(x, mRect.top - mScrollY, x + mRect.width(), mRect.top - mScrollY + mRect.height());
				//设置表格内文本距离左边的距离增加mMarginX
				x +=mMarginX;
				paint.setStyle(Paint.Style.STROKE);
				paint.setColor(Color.BLACK);
				canvas.drawRect(mRect, paint);
			}
			if(grid.getContentType()==Parser.CONTENT_TYPE_TEXT){
				drawText(canvas, paint, type, grid.getContent(), count, i, x, y);
			}else{
				//绘制勾
				if("".equals(grid.getContent())==false){
					float w = mRect.width();
					float h = mRect.height();
					paint.setColor(Config.selectGridContentColor);
					float stroke = paint.getStrokeWidth();
					paint.setStrokeWidth(paint.getTextSize() * 0.12f);
					canvas.drawLine(mRect.left + w * 0.25f, mRect.top + h * 0.5f, mRect.left + w * 0.4f, mRect.top + h * 0.75f, paint);
					canvas.drawLine(mRect.left + w * 0.39f, mRect.top + h * 0.75f, mRect.left + w * 0.70f, mRect.top + h * 0.25f, paint);
					paint.setStrokeWidth(stroke);
				}
			}
			
		}
	}
	private static void drawText(Canvas canvas,Paint paint,int type,String str,int count,int index,float x,float y){
		if((type==Parser.TYPE_CLASS || type==Parser.TYPE_FIELD 
			|| type==Parser.TYPE_METHOD || type==Parser.TYPE_PARAM 
			|| type==Parser.TYPE_LOCAL)){
			if((index + 1)==count){//最后一项必为备注
				paint.setColor(Config.textColorExplain);
			}
			if(type!=Parser.TYPE_CLASS && index==1){//类型文本颜色
				paint.setColor(Config.textColorType);
			}
			if(index==0){
				if(type==Parser.TYPE_CLASS){//类名
					paint.setColor(Config.textColorClassName);
				}
				if(type==Parser.TYPE_FIELD){//成员名
					paint.setColor(Config.textColorFieldName);
				}
				if(type==Parser.TYPE_METHOD){//方法名
					paint.setColor(Config.textColorMethodName);
				}
				if(type==Parser.TYPE_PARAM){//参数名
					paint.setColor(Config.textColorParamName);
				}
				if(type==Parser.TYPE_LOCAL){//变量名
					paint.setColor(Config.textColorLocalName);
				}
			}
		}else{
			paint.setColor(Color.BLACK);
		}
		canvas.drawText(str, x, y, paint);
	}
	public static void setScroll(float scrollX,float scrollY){
		mScrollX = scrollX;
		mScrollY = scrollY;
	}
}
