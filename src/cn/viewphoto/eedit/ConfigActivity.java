package cn.viewphoto.eedit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cn.viewphoto.eedit.editor.Config;

public class ConfigActivity extends Activity implements View.OnClickListener{
	private TextView	mTvFontSize;
	private TextView	mTvFontCode;
	private TextView	mTvVibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_config);
		findViewById(R.id.config_tv_back_btn).setOnClickListener(this);
		findViewById(R.id.config_btn_default).setOnClickListener(this);
		findViewById(R.id.config_text_size).setOnClickListener(this);
		findViewById(R.id.config_text_code).setOnClickListener(this);
		findViewById(R.id.config_color_settings).setOnClickListener(this);
		findViewById(R.id.config_vibrator_settings).setOnClickListener(this);
		mTvFontCode = (TextView) findViewById(R.id.config_tv_font_code);
		mTvFontSize = (TextView) findViewById(R.id.config_tv_font_size);
		mTvVibrator = (TextView) findViewById(R.id.config_tv_vibrator_settings);
		load();
	}
	private void load(){
		mTvFontSize.setText(String.valueOf(Config.fontSize));
		mTvFontCode.setText(Config.fontCode);
		if(Config.vibrator==true){
			mTvVibrator.setText(R.string.open_vibrator);
		}else{
			mTvVibrator.setText(R.string.close_vibrator);
		}
	}
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.config_tv_back_btn:
			finish();
			return;
		case R.id.config_text_code:
			changFontCode();
			return;
		case R.id.config_text_size:
			changeFontSize();
			return;
		case R.id.config_color_settings:
			startActivity(new Intent(this, ConfigColorActivity.class));
			return;
		case R.id.config_btn_default:
			Config.loadDefaut();
			load();
			return;
		case R.id.config_vibrator_settings:
			Config.vibrator = !Config.vibrator;
			load();
			return;
		}
		
	}
	private void changFontCode(){
		final String[] Items = new String[]{"GBK","UTF-8"};
		int select = 0;
		if(Config.fontCode.equals(Items[1])){
			select = 1;
		}
		new AlertDialog.Builder(this).setTitle("选择")
									 .setSingleChoiceItems(Items, select, new AlertDialog.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											Config.fontCode = Items[which];
											load();
											dialog.dismiss();
										}
									})
									 .show();
	}
	private void changeFontSize(){
		final EditText editText = new EditText(this);
		editText.setInputType(EditorInfo.TYPE_NUMBER_FLAG_DECIMAL);
		new AlertDialog.Builder(this).setView(editText)
									 .setMessage("设置字体大小")
	    							 .setNegativeButton("取消", null)
	    							 .setPositiveButton("确定", new AlertDialog.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											String str = editText.getText().toString();
											if(TextUtils.isEmpty(str)==false){
												try{
													float size = Float.valueOf(str);
													size = size<12 ? 12 : size;
													size = size>50 ? 50 : size;
													Config.fontSize = size;
													load();
												}catch(Exception e){}
											}
											dialog.dismiss();
										}
									})
	    							 .show();
	}
}
