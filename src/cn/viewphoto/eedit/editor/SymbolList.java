package cn.viewphoto.eedit.editor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ScrollView;
import cn.viewphoto.eedit.R;
import cn.viewphoto.eedit.view.EEditText;

public class SymbolList implements EEditText.OnSoftInput,GridView.OnItemClickListener{
	private String[] 	mSymbos = {"→",".","＝","＋","－","×","÷","(",")","＞","≥","＜","≤","≠","“","”","'"};
	private EEditText				mEEditText;
	private Context					mContext;
	private ScrollView	mScrollView;
	public SymbolList(Context context,EEditText eEditText,ScrollView rootView) {
		mContext = context;
		mEEditText = eEditText;
		mScrollView = rootView;
		init();
	}
	private void init(){
		mEEditText.setOnSoftInput(this);
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		GridView gridView = (GridView) inflater.inflate(R.layout.layout_symbol_root, null);
		mScrollView.addView(gridView);
		gridView.setNumColumns(mSymbos.length);//设置列数
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.layout_symbol_item, mSymbos);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(this);
		
	}
	@Override
	public void onOpen() {
		mScrollView.setVisibility(View.VISIBLE);
	}
	@Override
	public void onClose() {
		mScrollView.setVisibility(View.INVISIBLE);
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String str = mSymbos[position];
		if(position==0){
			str = "    ";
		}
		mEEditText.getTable().onInput(mEEditText, str);
		mEEditText.invalidate();
	}
}
