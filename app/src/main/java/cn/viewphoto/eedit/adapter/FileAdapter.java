package cn.viewphoto.eedit.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.viewphoto.eedit.R;

public class FileAdapter extends BaseAdapter {
	public static final String TAG = "FileAdapter";
	private Context 	mContext;
	private File		mDirectory;
	private List<File>  mFileList;
	public FileAdapter(Context context) {
		mContext = context;
		mDirectory = Environment.getExternalStorageDirectory();
		mFileList = new ArrayList<File>();
		loadItem();
	}
	public boolean setDirectory(File f){
		if(f.canRead()==false){
			return false;
		}
		mDirectory = f;
		loadItem();
		notifyDataSetChanged();
		return true;
	}
	public String getPath(){
		return mDirectory.getAbsolutePath();
	}
	private void loadItem(){
		File f;
		File[] files = mDirectory.listFiles();
		mFileList.clear();
		f = mDirectory.getParentFile();
		//添加返回上一层的列表项
		if(f==null){
			mFileList.add(mDirectory);
		}else{
			mFileList.add(f);
		}
		//添加新建文件列表项
		mFileList.add(new File(mDirectory.getAbsoluteFile() + "/新建"));
		if(files==null){
			return;
		}
		sort(files);//排序
		String name;
		for(int i=0;i<files.length;i++){
			f = files[i];
			if(f.isDirectory()==true){
				mFileList.add(f);
				files[i] = null;
			}
		}
		for(int i=0;i<files.length;i++){
			f = files[i];
			if(f!=null){
				name = f.getName().toLowerCase(Locale.getDefault());
				if(name.endsWith(".txt")){
					mFileList.add(f);
				}
			}
		}
	}
	private void sort(File[] files){
		File f;
		for(int i=0;i<files.length;i++){
			for(int j=0;j<files.length-i-1;j++){
				if(files[j].compareTo(files[j+1])>0){
					f = files[j];
					files[j] = files[j+1];
					files[j+1] = f;
				}
			}
		}
	}
	@Override
	public int getCount() {
		return mFileList.size();
	}

	@Override
	public Object getItem(int position) {
		return mFileList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.layout_item_file_list, null);
		}
		TextView icoTv = (TextView) convertView.findViewById(R.id.item_textView_icon);
		TextView nameTv = (TextView) convertView.findViewById(R.id.item_textView_name);
		File file = mFileList.get(position);
		String name = position==0 ? ".." : file.getName();
		if(position==1){
			icoTv.setBackgroundResource(R.drawable.ic_menu_btn_add);
		}else{
			if(file.isDirectory()){
				icoTv.setBackgroundResource(R.drawable.ic_menu_archive);
			}else{
				icoTv.setBackgroundResource(R.drawable.ic_menu_paste_holo_light);
			}
		}
		nameTv.setText(name);
		return convertView;
	}

}
