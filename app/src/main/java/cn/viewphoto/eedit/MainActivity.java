package cn.viewphoto.eedit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Locale;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.viewphoto.eedit.R;
import cn.viewphoto.eedit.adapter.FileAdapter;
import cn.viewphoto.eedit.editor.Config;
import cn.viewphoto.eedit.editor.Editor;
import cn.viewphoto.eedit.editor.Parser;
import cn.viewphoto.eedit.editor.SymbolList;
import cn.viewphoto.eedit.util.UpdateUtil;
import cn.viewphoto.eedit.view.EEditText;
import cn.viewphoto.eedit.view.SlideMenu;

public class MainActivity extends Activity implements View.OnClickListener,ListView.OnItemClickListener{
	public static final String TAG = "MainActivity";
	private SlideMenu mSlideMenu;
	private FileAdapter mFileAdapter;
	private TextView	mMenuFileNameTv;
	private EEditText	mEEditText;
	private TextView	mTitleTv;
	private File		mFile;
	private ListView	mFileListView;
	//private Handler	mHandler;
	//private int 	mTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Config.loadConfig(this);//加载配置
		mSlideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		ImageView menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);
		menuImg.setOnClickListener(this);
		mFileAdapter = new FileAdapter(this);
		mMenuFileNameTv = (TextView) findViewById(R.id.menu_file_textView);
		mMenuFileNameTv.setText(mFileAdapter.getPath());
		mFileListView = (ListView) findViewById(R.id.menu_file_listView);
		mFileListView.setAdapter(mFileAdapter);
		mFileListView.setOnItemClickListener(this);
		mEEditText = (EEditText) findViewById(R.id.main_eedittext);
		mEEditText.setTextSize(Config.fontSize);//设置文本大小
		mTitleTv = (TextView) findViewById(R.id.title_bar_name);
		mTitleTv.setText(R.string.app_name);
		UpdateUtil.checkUpdate(this, false);//检查更新
		ScrollView scrollView = (ScrollView) findViewById(R.id.main_scroll_symbo);
		new SymbolList(this, mEEditText, scrollView);//显示符号列表
		//////////////////////////////
		//HintSQLiteOpenHelper.getInstance(this).getReadableDatabase();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//Log.i(TAG, "菜单创建");
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.getItem(0).setEnabled(mEEditText.getTable().canAddMethod());
		menu.getItem(1).setEnabled(mEEditText.getTable().canAddLocal());
		menu.getItem(2).setEnabled(mEEditText.getTable().getEnabled());
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int id = item.getItemId();
		switch(id){
		case R.id.menu_settings:
			startActivityForResult(new Intent(this, ConfigActivity.class), 1);
			return true;
		case R.id.menu_add_method:
			mEEditText.getTable().addMethod();
			return true;
		case R.id.menu_add_local:
			mEEditText.getTable().addLocal();
			return true;
		case R.id.menu_save:
			saveFile(mEEditText.getTable().getEditor(),true);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.title_bar_menu_btn:
			if(mSlideMenu.isMainScreenShowing()){
				mSlideMenu.openMenu();
			}else{
				mSlideMenu.closeMenu();
			}
			//隐藏软键盘
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(mEEditText.getWindowToken(), 0);
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Adapter adapter = parent.getAdapter();
		final File f = (File) adapter.getItem(position);
		if(position==1){//创建文件
			final EditText editText = new EditText(this);
			new AlertDialog.Builder(this).setView(editText)
										 .setMessage("请输入文件名")
		    							 .setNegativeButton("取消", null)
		    							 .setPositiveButton("确定", new AlertDialog.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
												createFile(f.getParentFile(),editText.getText().toString());
												mFileAdapter.setDirectory(f.getParentFile());
												mSlideMenu.closeMenu();
												dialog.dismiss();
											}
										})
		    							 .show();
		}else{
			if(f.isDirectory()){//打开目录
				if(mFileAdapter.setDirectory(f)){
					
					mMenuFileNameTv.setText(mFileAdapter.getPath());
				}else{
					Toast.makeText(this, R.string.open_directory_fail, Toast.LENGTH_SHORT).show();
				}
			}else{
				//处理文件打开
				openFile(f);
			}
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode==1){
			mEEditText.setTextSize(Config.fontSize);
		}
	}

	@Override
	protected void onDestroy() {
		saveFile(mEEditText.getTable().getEditor(),false);
		Config.saveConfig(this);//保存配置
		super.onDestroy();
	}
	/**
	 * 创建代码文件
	 * @param parent
	 * @param name
	 */
	private void createFile(File parent,String name){
		if(TextUtils.isEmpty(name)){
			Toast.makeText(this, "文件名不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		if(name.toLowerCase(Locale.getDefault()).endsWith(".txt")==false){
			name = name + ".txt";
		}
		String path = parent.getAbsolutePath();
		if(name.startsWith("/")){
			path += name;
		}else{
			path = path + "/" + name;
		}
		File file = new File(path);
		try {
			if(file.createNewFile()==false){
				Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
				return;
			}
			Editor e = new Editor();
			e.append(Parser.STR_VERSION );
			e.append("2\n\n");
			e.append(Parser.STR_CLASS);
			e.append("类名\n");
			mFile = file;
			saveFile(e, false);
			openFile(file);
		} catch (IOException e) {
			Toast.makeText(this, "创建文件失败", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	private void openFile(File f){
		BufferedReader reader = null;
		try{
			Editor editor = new Editor();
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(f),Config.fontCode));
			String str;
			while((str = reader.readLine())!=null){
				editor.append(str);
				editor.append("\n");
			}
			//mEEditText.setEditor(editor);
			mEEditText.setVisibility(View.VISIBLE);
			//
			mEEditText.getTable().setEditor(editor);
			mSlideMenu.closeMenu();
			mEEditText.postInvalidate();
			mTitleTv.setText(f.getName());
			mFile = f;
		}catch(Exception e){
			mFile = null;
			Toast.makeText(this, R.string.open_file_fail, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}finally{
			if(reader!=null){
				try{
					reader.close();
				}catch(Exception e){}
				
			}
		}
	}
	/**
	 * 保存文件
	 * @param editor
	 */
	private void saveFile(Editor editor,boolean showHint){
		if(mFile==null){
			if(showHint) Toast.makeText(this, R.string.save_file_fail, Toast.LENGTH_SHORT).show();
			return;
		}
		BufferedWriter write = null;
		try{
			write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile),Config.fontCode));
			for(int i=0;i<editor.getLineCount();i++){
				write.write(editor.getLine(i));
			}
			if(showHint) Toast.makeText(this, R.string.save_file_succeed, Toast.LENGTH_SHORT).show();
		}catch(Exception e){
			if(showHint) Toast.makeText(this, R.string.save_file_fail, Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}finally{
			if(write!=null){
				try{
					write.close();
				}catch(Exception e){}
			}
		}
	}
	
}
