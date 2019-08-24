package cn.viewphoto.eedit.editor;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Config {
	private static final String CONFIG_NAME 					= "config";
	private static final String	KEY_FONT_SIZE 					= "FontSize";
	private static final String KEY_FONT_CODE					= "FontCode";
	private static final String KEY_CURSOR_COLOR				= "color0";
	private static final String KEY_CLASS_FIELD_HEAD_BG_COLOR	= "color1";
	private static final String KEY_METHOD_PARAM_HEAD_BG_COLOR	= "color2";
	private static final String KEY_LOCAL_HEAD_BG_COLOR			= "color3";
	private static final String KEY_SELECT_GRID_CONTENT_COLOR	= "color4";
	private static final String KEY_TEXT_COLOR_EXPLAIN			= "color5";
	private static final String KEY_TEXT_COLOR_TYPE				= "color6";
	private static final String KEY_TEXT_COLOR_CLASS_NAME		= "color7";
	private static final String KEY_TEXT_COLOR_FIELD_NAME		= "color8";
	private static final String KEY_TEXT_COLOR_METHOD_NAME		= "color9";
	private static final String KEY_TEXT_COLOR_PARAM_NAME		= "color10";
	private static final String KEY_TEXT_COLOR_LOCAL_NAME		= "color11";
	private static final String KEY_VIBRATOR					= "vibrator";
	
	public static float	fontSize = 30;
	public static String fontCode = "GBK";
	public static int classFieldHeadBgColor 		= 0xFFE1E1E1;//类与成员表头背景色
	public static int methodParamHeadBgColor	 	= 0xFFE6EDE4;//方法与参数表头背景色
	public static int localHeadBgColor				= 0xFFD9E3F0;//变量表头背景色
	public static int selectGridContentColor		= 0xFFE65A5A;//打钩的颜色
	public static int textColorExplain				= 0xFF008000;//注释文本颜色
	public static int textColorType					= 0xFF0000FF;//数据类型文本颜色
	public static int textColorClassName			= 0xFF000000;//类名的文本颜色
	public static int textColorFieldName			= 0xFF000000;//类成员名文本颜色
	public static int textColorMethodName			= 0xFF000000;//方法名的文本颜色
	public static int textColorParamName			= 0xFF000000;//参数名的文本颜色
	public static int textColorLocalName			= 0xFF000000;//变量名的文本颜色
	public static int cursorColor					= 0xFFFF0000;//光标颜色
	public static boolean vibrator					= true;//是否开启震动
	
	public static void loadConfig(Context context){
		SharedPreferences share = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		fontSize = share.getFloat(KEY_FONT_SIZE, 30);
		fontCode = share.getString(KEY_FONT_CODE, "GBK");
		classFieldHeadBgColor = share.getInt(KEY_CLASS_FIELD_HEAD_BG_COLOR, 0xFFE1E1E1);
		methodParamHeadBgColor = share.getInt(KEY_METHOD_PARAM_HEAD_BG_COLOR, 0xFFE6EDE4);
		localHeadBgColor = share.getInt(KEY_LOCAL_HEAD_BG_COLOR, 0xFFD9E3F0);
		selectGridContentColor = share.getInt(KEY_SELECT_GRID_CONTENT_COLOR, 0xFFE65A5A);
		textColorExplain = share.getInt(KEY_TEXT_COLOR_EXPLAIN, 0xFF008000);
		textColorType = share.getInt(KEY_TEXT_COLOR_TYPE, 0xFF0000FF);
		textColorClassName	= share.getInt(KEY_TEXT_COLOR_CLASS_NAME,0xFF000000);
		textColorFieldName	= share.getInt(KEY_TEXT_COLOR_FIELD_NAME,0xFF000000);
		textColorMethodName	= share.getInt(KEY_TEXT_COLOR_METHOD_NAME,0xFF000000);
		textColorParamName	= share.getInt(KEY_TEXT_COLOR_PARAM_NAME,0xFF000000);
		textColorLocalName	= share.getInt(KEY_TEXT_COLOR_LOCAL_NAME,0xFF000000);
		vibrator = share.getBoolean(KEY_VIBRATOR, true);
		cursorColor = share.getInt(KEY_CURSOR_COLOR, 0xFFFF0000);
	}
	public static void saveConfig(Context context){
		SharedPreferences share = context.getSharedPreferences(CONFIG_NAME, Context.MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putFloat(KEY_FONT_SIZE, fontSize);
		editor.putString(KEY_FONT_CODE, fontCode);
		editor.putInt(KEY_CLASS_FIELD_HEAD_BG_COLOR,classFieldHeadBgColor);
		editor.putInt(KEY_METHOD_PARAM_HEAD_BG_COLOR,methodParamHeadBgColor);
		editor.putInt(KEY_LOCAL_HEAD_BG_COLOR,localHeadBgColor);
		editor.putInt(KEY_SELECT_GRID_CONTENT_COLOR,selectGridContentColor);
		editor.putInt(KEY_TEXT_COLOR_EXPLAIN,textColorExplain);
		editor.putInt(KEY_TEXT_COLOR_TYPE,textColorType);
		editor.putInt(KEY_TEXT_COLOR_CLASS_NAME,textColorClassName);
		editor.putInt(KEY_TEXT_COLOR_FIELD_NAME,textColorFieldName);
		editor.putInt(KEY_TEXT_COLOR_METHOD_NAME,textColorMethodName);
		editor.putInt(KEY_TEXT_COLOR_PARAM_NAME,textColorParamName);
		editor.putInt(KEY_TEXT_COLOR_LOCAL_NAME,textColorLocalName);
		editor.putInt(KEY_CURSOR_COLOR, cursorColor);
		editor.putBoolean(KEY_VIBRATOR, vibrator);
		editor.commit();
	}
	public static void loadDefaut(){
		fontSize = 30;
		fontCode = "GBK";
		classFieldHeadBgColor 		= 0xFFE1E1E1;//类与成员表头背景色
		methodParamHeadBgColor	 	= 0xFFE6EDE4;//方法与参数表头背景色
		localHeadBgColor			= 0xFFD9E3F0;//变量表头背景色
		selectGridContentColor		= 0xFFE65A5A;//打钩的颜色
		textColorExplain			= 0xFF008000;//注释文本颜色
		textColorType				= 0xFF0000FF;//数据类型文本颜色
		textColorClassName			= 0xFF000000;//类名的文本颜色
		textColorFieldName			= 0xFF000000;//类成员名文本颜色
		textColorMethodName			= 0xFF000000;//方法名的文本颜色
		textColorParamName			= 0xFF000000;//参数名的文本颜色
		textColorLocalName			= 0xFF000000;//变量名的文本颜色
		cursorColor					= 0xFFFF0000;
		vibrator = true;
	}
}
