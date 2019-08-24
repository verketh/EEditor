package cn.viewphoto.eedit.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.viewphoto.eedit.R;

public class HintAdapter extends BaseAdapter {
	private List<HintItem>	mHintItemList;
	private Context mContext;
	private ListView	mListView;
	private LayoutInflater	mInflater;
	public HintAdapter(Context context,ListView list) {
		mContext = context;
		mListView = list;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	public List<HintItem> getList(){
		if(mHintItemList==null){
			mHintItemList = new ArrayList<HintItem>();
		}
		return mHintItemList;
	}
	@Override
	public int getCount() {
		int result = 0;
		if(mHintItemList!=null){
			result = mHintItemList.size();
		}
		return result;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView = mInflater.inflate(R.layout.layout_hint_item, null);
		}
		HintItem item = mHintItemList.get(position);
		((TextView)convertView.findViewById(R.id.hint_item_title)).setText(item.getTitle());
		((TextView)convertView.findViewById(R.id.hint_item_lib)).setText(item.getLibCnName() + " - " + item.getLibName());
		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("", "item 被点击");
				mListView.getOnItemClickListener().onItemClick(mListView, v, position, getItemId(position));
			}
		});
		return convertView;
	}

}
