package cn.viewphoto.eedit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.viewphoto.eedit.editor.Config;

public class ConfigColorActivity extends Activity implements View.OnClickListener{
	private static final int		CursorIndex				=0;
	private static final int		ClassFieldHeadIndex		=1;
	private static final int 	MethodParamHeadIndex	=2;
	private static final int 	LocalHeadIndex			=3;
	private static final int		ClassNameIndex			=4;
	private static final int		FieldNameIndex			=5;
	private static final int 	MethodNameIndex			=6;
	private static final int		ParamNameIndex			=7;
	private static final int		LocalNameIndex			=8;
	private static final int		TypeNameIndex			=9;
	private static final int		ExplainIndex			=10;
	private static final int 	selectGridIndex			=11;
	private TextView[] mTextViews;
	private int mIndex = -1;
	private int mColor;
	private int mDialogTitle;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_config_color);
		findViewById(R.id.config_color_tv_back_btn).setOnClickListener(this);
		findViewById(R.id.config_color_cursor_color).setOnClickListener(this);
		findViewById(R.id.config_color_class_field_head_bg_color).setOnClickListener(this);
		findViewById(R.id.config_color_method_param_head_bg_color).setOnClickListener(this);
		findViewById(R.id.config_color_local_head_bg_color).setOnClickListener(this);
		findViewById(R.id.config_color_class_name_color).setOnClickListener(this);
		findViewById(R.id.config_color_field_name_color).setOnClickListener(this);
		findViewById(R.id.config_color_method_name_color).setOnClickListener(this);
		findViewById(R.id.config_color_param_name_color).setOnClickListener(this);
		findViewById(R.id.config_color_local_name_color).setOnClickListener(this);
		findViewById(R.id.config_color_type_color).setOnClickListener(this);
		findViewById(R.id.config_color_explain_color).setOnClickListener(this);
		findViewById(R.id.config_color_select_grid_color).setOnClickListener(this);
		String str = "       ";
		mTextViews = new TextView[12];
		mTextViews[CursorIndex] = (TextView) findViewById(R.id.config_color_tv_cursor_color);
		mTextViews[ClassFieldHeadIndex] = (TextView) findViewById(R.id.config_color_tv_class_field_head_bg_color);
		mTextViews[MethodParamHeadIndex] = (TextView) findViewById(R.id.config_color_tv_method_param_head_bg_color);
		mTextViews[LocalHeadIndex] = (TextView) findViewById(R.id.config_color_tv_local_head_bg_color);
		mTextViews[ClassNameIndex] = (TextView) findViewById(R.id.config_color_tv_class_name_color);
		mTextViews[FieldNameIndex] = (TextView) findViewById(R.id.config_color_tv_field_name_color);
		mTextViews[MethodNameIndex] = (TextView) findViewById(R.id.config_color_tv_method_name_color);
		mTextViews[ParamNameIndex] = (TextView) findViewById(R.id.config_color_tv_param_name_color);
		mTextViews[LocalNameIndex] = (TextView) findViewById(R.id.config_color_tv_local_name_color);
		mTextViews[TypeNameIndex] = (TextView) findViewById(R.id.config_color_tv_type_color);
		mTextViews[ExplainIndex] = (TextView) findViewById(R.id.config_color_tv_explain_color);
		mTextViews[selectGridIndex] = (TextView) findViewById(R.id.config_color_tv_select_grid_color);
		for(TextView v:mTextViews){
			v.setText(str);
		}
		load();
	}
	private void load() {
		mTextViews[CursorIndex].setBackgroundColor(Config.cursorColor);
		mTextViews[ClassFieldHeadIndex].setBackgroundColor(Config.classFieldHeadBgColor);
		mTextViews[MethodParamHeadIndex].setBackgroundColor(Config.methodParamHeadBgColor);
		mTextViews[LocalHeadIndex].setBackgroundColor(Config.localHeadBgColor);
		mTextViews[ClassNameIndex].setBackgroundColor(Config.textColorClassName);
		mTextViews[FieldNameIndex].setBackgroundColor(Config.textColorFieldName);
		mTextViews[MethodNameIndex].setBackgroundColor(Config.textColorMethodName);
		mTextViews[ParamNameIndex].setBackgroundColor(Config.textColorParamName);
		mTextViews[LocalNameIndex].setBackgroundColor(Config.textColorLocalName);
		mTextViews[TypeNameIndex].setBackgroundColor(Config.textColorType);
		mTextViews[ExplainIndex].setBackgroundColor(Config.textColorExplain); 
		mTextViews[selectGridIndex].setBackgroundColor(Config.selectGridContentColor);
	}
	private void setColor(int color){
		if(mIndex<0 || mIndex>=mTextViews.length) return;
		switch(mIndex){
		case CursorIndex:
			Config.cursorColor = color;
			return;
		case ClassFieldHeadIndex:
			Config.classFieldHeadBgColor = color;
			return;
		case MethodParamHeadIndex:
			Config.methodParamHeadBgColor = color;
			return;
		case LocalHeadIndex:
			Config.localHeadBgColor = color;
			return;
		case ClassNameIndex:
			Config.textColorClassName = color;
			return;
		case FieldNameIndex:
			Config.textColorFieldName = color;
			return;
		case MethodNameIndex:
			Config.textColorMethodName = color;
			return;
		case ParamNameIndex:
			Config.textColorParamName = color;
			return;
		case LocalNameIndex:
			Config.textColorLocalName = color;
			return;
		case TypeNameIndex:
			Config.textColorType = color;
			return;
		case ExplainIndex:
			Config.textColorExplain = color;
			return;
		case selectGridIndex:
			Config.selectGridContentColor = color;
			return;
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.config_color_tv_back_btn:
			finish();
			return;
		case R.id.config_color_cursor_color:
			mIndex = CursorIndex;
			mColor = Config.cursorColor;
			mDialogTitle = R.string.cursor_color;
			break;
		case R.id.config_color_class_field_head_bg_color:
			mIndex = ClassFieldHeadIndex;
			mColor = Config.classFieldHeadBgColor;
			mDialogTitle = R.string.class_field_head_bg_color;
			break;
		case R.id.config_color_method_param_head_bg_color:
			mIndex = MethodParamHeadIndex;
			mColor = Config.methodParamHeadBgColor;
			mDialogTitle = R.string.method_param_head_bg_color;
			break;
		case R.id.config_color_local_head_bg_color:
			mIndex = LocalHeadIndex;
			mColor = Config.localHeadBgColor;
			mDialogTitle = R.string.local_head_bg_color;
			break;
		case R.id.config_color_class_name_color:
			mIndex = ClassNameIndex;
			mColor = Config.textColorClassName;
			mDialogTitle = R.string.class_name_color;
			break;
		case R.id.config_color_field_name_color:
			mIndex = FieldNameIndex;
			mColor = Config.textColorFieldName;
			mDialogTitle = R.string.field_name_color;
			break;
		case R.id.config_color_method_name_color:
			mIndex = MethodNameIndex;
			mColor = Config.textColorMethodName;
			mDialogTitle = R.string.method_name_color;
			break;
		case R.id.config_color_param_name_color:
			mIndex = ParamNameIndex;
			mColor = Config.textColorParamName;
			mDialogTitle = R.string.param_name_color;
			break;
		case R.id.config_color_local_name_color:
			mIndex = LocalNameIndex;
			mColor = Config.textColorLocalName;
			mDialogTitle = R.string.local_name_color;
			break;
		case R.id.config_color_type_color:
			mIndex = TypeNameIndex;
			mColor = Config.textColorType;
			mDialogTitle = R.string.type_color;
			break;
		case R.id.config_color_explain_color:
			mIndex = ExplainIndex;
			mColor = Config.textColorExplain;
			mDialogTitle = R.string.explain_color;
			break;
		case R.id.config_color_select_grid_color:
			mIndex = selectGridIndex;
			mColor = Config.selectGridContentColor;
			mDialogTitle = R.string.select_grid_color;
			break;
			default:
				mIndex = -1;
				mColor = 0;
		}
		showColorDialog();
	}
	private void showColorDialog(){
		if(mIndex<0 || mIndex>=mTextViews.length) return;
		View v = getLayoutInflater().inflate(R.layout.layout_color_select, null);
		((TextView)v.findViewById(R.id.color_select_title)).setText(mDialogTitle);
		View showColorTv = (View) v.findViewById(R.id.color_select_show_color);
		EditText alphaEdit = (EditText) v.findViewById(R.id.color_select_alpha);
		EditText redEdit = (EditText) v.findViewById(R.id.color_select_red);
		EditText greenEdit = (EditText) v.findViewById(R.id.color_select_green);
		EditText blueEdit = (EditText) v.findViewById(R.id.color_select_blue);
		final TextChange textChange = new TextChange(alphaEdit, redEdit, greenEdit, blueEdit, showColorTv,mColor);
		alphaEdit.addTextChangedListener(textChange);
		redEdit.addTextChangedListener(textChange);
		greenEdit.addTextChangedListener(textChange);
		blueEdit.addTextChangedListener(textChange);
		new AlertDialog.Builder(this).setView(v)
									 .setNeutralButton("确定", new AlertDialog.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											setColor(textChange.getColor());
											load();
											dialog.dismiss();
										}
									})
									 .setNegativeButton("取消", null)
									 .show();
	}
	private class TextChange implements TextWatcher{
		private EditText mAlphaEdit;
		private EditText mRedEdit;
		private EditText mGreenEdit;
		private EditText mBlueEdit;
		private View 	mView;
		public TextChange(EditText alpha,EditText red,EditText green,EditText blue,View v,int color) {
			mAlphaEdit = alpha;
			mRedEdit = red;
			mGreenEdit = green;
			mBlueEdit = blue;
			mView = v;
			mView.setBackgroundColor(color);
			mAlphaEdit.setText(String.valueOf((color>>24) & 0xFF));
			mRedEdit.setText(String.valueOf((color>>16) & 0xFF));
			mGreenEdit.setText(String.valueOf((color>>8) & 0xFF));
			mBlueEdit.setText(String.valueOf(color & 0xFF));
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			check(mAlphaEdit);
			check(mRedEdit);
			check(mGreenEdit);
			check(mBlueEdit);
			mView.setBackgroundColor(getColor());
		}
		private void check(EditText edit){
			int value = 0;
			try{
				value = Integer.valueOf(edit.getText().toString());
			}catch(Exception e){}
			if(value<0){
				edit.setText("0");
			}
			if(value>255){
				edit.setText("255");
			}
		}
		@Override
		public void afterTextChanged(Editable s) {
			
		}
		public int getColor(){
			int a = 0,r = 0,g = 0,b = 0;
			try{
				a = Integer.valueOf(mAlphaEdit.getText().toString());
			}catch(Exception e){}
			try{
				r = Integer.valueOf(mRedEdit.getText().toString());
			}catch(Exception e){}
			try{
				g = Integer.valueOf(mGreenEdit.getText().toString());
			}catch(Exception e){}
			try{
				b = Integer.valueOf(mBlueEdit.getText().toString());
			}catch(Exception e){}
			return (a<<24) | (r<<16) | (g<<8) | b;
		}
	}
}
