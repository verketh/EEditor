package cn.viewphoto.eedit.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import cn.viewphoto.eedit.editor.Config;

public class Cursor {
	public static final String TAG = "Cursor";
	private int		mBackgroundColor;
	private Paint	mPaint;
	private RectF	mTemRectF;
	private RectF	mRectF;
	private int		mAlpha;
	public Cursor() {
		mBackgroundColor = 0xFFE8F2FE;
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStyle(Paint.Style.FILL);
		mRectF = new RectF();
		mTemRectF = new RectF();
		mAlpha = 255;
	}
	public void setPosition(float x,float y){
		mRectF.set(x, y, x + mRectF.width(), y + mRectF.height());
	}
	public void setRect(float left,float top,float right,float bottom){
		mRectF.set(left, top, right, bottom);
	}
	public void draw(Canvas canvas,float scrollX,float scrollY){
		mAlpha = (mAlpha == 0) ? 255 : 0;
		mPaint.setColor(Config.cursorColor);
        mPaint.setAlpha(mAlpha);
        float x = mRectF.left - scrollX;
        float y = mRectF.top - scrollY;
        mTemRectF.set(x, y, x + mRectF.width(), y + mRectF.height());
        canvas.drawRect(mTemRectF, mPaint);
	}
	public void drawBackgroud(Canvas canvas,float width,float scrollY){
		mPaint.setColor(mBackgroundColor);
		mPaint.setAlpha(255);
		float x = 0;
	    float y = mRectF.top - scrollY;
		mTemRectF.set(x, y, x + width, y + mRectF.height());
	    canvas.drawRect(mTemRectF, mPaint);
	}
	public float getX(){
		return mRectF.left;
	}
	public float getY(){
		return mRectF.top;
	}
	public float getWidth(){
		return mRectF.width();
	}
}
