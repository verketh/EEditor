package cn.viewphoto.eedit.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import cn.viewphoto.eedit.editor.Table;

public class EEditText extends View {
	private static final int SCROLL_ORIENTATION_VERTICAL 	= 0;
	private static final int SCROLL_ORIENTATION_HORIZONTAL	= 1;
	private static final int MSG_SCROLL						= 100;
	private static final String TAG = "EEditText";
	private Table	mTable;
	private GestureDetector mGestureDetector;
	private int mScrollOrientation;
	private boolean mTouchUp = true;//手指是否已离开屏幕
	private Handler mHandler;
	private boolean mStopScroll;
	private Refresh	mRefresh;
	private OnSoftInput	mOnSoftInput;
	private int		mOldViewHeight;//旧的view的高度
	private boolean	mSoftInputOpen = false;
	private SelectMenu	mSelectMenu;
	public EEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public EEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public EEditText(Context context) {
		super(context);
		init();
	}
	private void init() {
		setFocusableInTouchMode(true);//可获取焦点
		//mEditor = new Editor();
		mSelectMenu = new SelectMenu(this);
		//mHintView = new HintView(this);
		mTable = new Table(this);
		mGestureDetector = new GestureDetector(getContext(), new DefaultOnGestureListener());
		mRefresh = new Refresh();
		postDelayed(mRefresh, 500);
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				switch(msg.what){
				case MSG_SCROLL:
					int value = msg.arg1<0 ? -100 : 100;
					if(mStopScroll==true || Math.abs(msg.arg1) - Math.abs(value) <= 0){ 
						mStopScroll = true;
						return;
					}
					if(mScrollOrientation==SCROLL_ORIENTATION_HORIZONTAL){
						mTable.onScroll(-value / 2, 0);
					}else{
						mTable.onScroll(0, -value / 2);
					}
					invalidate();
					Message m = mHandler.obtainMessage();
					m.what = MSG_SCROLL;
					m.arg1 = msg.arg1 - value;
					mHandler.sendMessage(m);
					break;
				}
			};
		};
	}
	/**
	 * 设置文本大小
	 * @param size
	 */
	public void setTextSize(float size){
		mTable.setTextSize(size);
		postInvalidate();
	}
	public Table getTable(){
		return mTable;
	}
	/*public void setEditor(Editor editor){
		mTable.setEditor(editor);
	}*/
	private void doKeyDown(int keyCode,KeyEvent event){
		if(KeyEvent.isModifierKey(keyCode) || isFocused()==false){
			//如为功能键返回
			return;
		}
		
		if(keyCode==KeyEvent.KEYCODE_MENU 
			|| keyCode==KeyEvent.KEYCODE_HOME 
			|| keyCode==KeyEvent.KEYCODE_BACK
			|| keyCode==KeyEvent.KEYCODE_VOLUME_UP
			|| keyCode==KeyEvent.KEYCODE_VOLUME_DOWN){
			return;
		}
		//////////////////
		//mHintView.show(getWidth(), getHeight());
		if(keyCode==KeyEvent.KEYCODE_UNKNOWN){//未知字符
			String str = event.getCharacters();
			//Log.i(TAG, "str:"+str);
			//mEditor.append(str);
			Log.i(TAG, "inputStr:"+str);
			mTable.onInput(this,str);
			invalidate();
		}else{
			char ch = (char) event.getUnicodeChar();
			Log.i(TAG, "inputCh:"+ch+"->"+(int)ch);
			//mEditor.append(unicode);
			mTable.onInput(this,String.valueOf(ch));
			invalidate();
		}
		
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		doKeyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		doKeyDown(keyCode, event);
		return super.onKeyMultiple(keyCode, repeatCount, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		requestFocus();//请求获取焦点
		switch(event.getAction()){
		case MotionEvent.ACTION_DOWN:
			mStopScroll = true;
			break;
		case MotionEvent.ACTION_UP:
			mTouchUp = true;
			//Log.i(TAG, "手指离开屏幕");
			break;
		}
		super.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);
		return true;
	}
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		mTable.setViewSize(getWidth(), getHeight());
		//输入法弹出时，view的高度会改变
		//Log.i(TAG, "高度改变："+getHeight());
		mTable.changeInputRowPosition();//调整scroll的位置
		if(mOldViewHeight==0 || mOldViewHeight==getHeight()){
			mOldViewHeight = getHeight();
			return;
		}
		if(mOnSoftInput!=null){
			if(mSoftInputOpen==true){
				mOnSoftInput.onClose();
				mSoftInputOpen = false;
			}else{
				mOnSoftInput.onOpen();
				mSoftInputOpen = true;
			}
			
		}
		mOldViewHeight = getHeight();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		mTable.draw(canvas);
	}
	private class DefaultOnGestureListener extends SimpleOnGestureListener{

		@Override
		public void onLongPress(MotionEvent e) {
			/*Log.i(TAG, "长按");*/
			Log.i(TAG, e.getX() + "," + e.getY());
			if(!mTable.getEnabled()) return;
			mTable.onClick(e.getX(), e.getY());
			mTable.vibrator();
			invalidate();
			Point point = mTable.getCursorPoint();
			mSelectMenu.showMenu((int)(point.x - getWidth() / 2), (int)(point.y - getHeight() / 2));
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			if(mTouchUp==true){
				mScrollOrientation = Math.abs(distanceX)>Math.abs(distanceY) ? SCROLL_ORIENTATION_HORIZONTAL : SCROLL_ORIENTATION_VERTICAL;
				mTouchUp = false;
			}
			distanceX = mScrollOrientation==SCROLL_ORIENTATION_HORIZONTAL ? distanceX : 0;
			distanceY = mScrollOrientation==SCROLL_ORIENTATION_VERTICAL ? distanceY : 0;
			mTable.onScroll(distanceX, distanceY);
			invalidate();
			return true;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			//Log.i(TAG, "快速滑动"+velocityX+","+velocityY);
			Message msg = mHandler.obtainMessage();
			msg.what = MSG_SCROLL;
			msg.arg1 = mScrollOrientation==SCROLL_ORIENTATION_HORIZONTAL ? (int) velocityX : (int)velocityY;
			mHandler.sendMessage(msg);
			mStopScroll = false;
			return true;
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			//Log.i(TAG, "onSingleTapUp");
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			//Log.i(TAG, "onSingleTapConfirmed");
			if(!mTable.getEnabled()) return true;
			mTable.onClick(e.getX(), e.getY());
			
			InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(EEditText.this, 0);
			
			//imm.showSoftInput(this, 0);//弹出软键盘
			//imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);//弹出或隐藏软键盘
			mTable.changeInputRowPosition();//调整scroll的位置
			invalidate();
			return super.onSingleTapConfirmed(e);
		}
		
	}
	private class Refresh implements Runnable{
		public void run() {
			// Call onDraw() to draw the cursor 
			invalidate();
	        // Wait 500 milliseconds before calling self again
	        postDelayed(this, 500);
		}
	}
	public void setOnSoftInput(OnSoftInput onSoftInput){
		mOnSoftInput = onSoftInput;
	}
	//输入法弹出或隐藏调用此接口
	public interface OnSoftInput{
		public void onOpen();
		public void onClose();
	}
}
