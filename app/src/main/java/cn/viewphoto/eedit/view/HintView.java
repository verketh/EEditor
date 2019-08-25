package cn.viewphoto.eedit.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.viewphoto.eedit.R;
import cn.viewphoto.eedit.adapter.HintAdapter;
import cn.viewphoto.eedit.adapter.HintItem;
import cn.viewphoto.eedit.editor.Grid;
import cn.viewphoto.eedit.editor.Parser;
import cn.viewphoto.eedit.editor.Row;
import cn.viewphoto.eedit.editor.Table;
import cn.viewphoto.eedit.util.PinYinUtil;

public class HintView implements ListView.OnItemClickListener{
	private final String symbols = ".－×÷＼％＋≠＝＜＞≤≥≈且或“” ().";
	private PopupWindow	mPopupWindow;
	private View 		mRootView;
	private EEditText	mEEditText;
	private HintAdapter mHintAdapter;
	private List<Row>	mVarList;
	private int 		mStartPos;//被替换的文本的起始位置
	private int 		mEndPos;//被替换文本的结束位置
	private Grid		mGrid;//被替换的源
	
	public HintView(EEditText eEditText) {
		mEEditText = eEditText;
		Context context = eEditText.getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mRootView = inflater.inflate(R.layout.layout_hint_root, null);
		ListView listView = (ListView) mRootView.findViewById(R.id.hint_list_view);
		mHintAdapter = new HintAdapter(context,listView);
		listView.setAdapter(mHintAdapter);
		listView.setOnItemClickListener(this);
		mVarList = new ArrayList<Row>();
	}
	public void dismiss(){
		if(mPopupWindow!=null && mPopupWindow.isShowing()){
			mPopupWindow.dismiss();
		}
	}
	/**
	 * 检查当前输入，并决定是否显示提示view
	 * @param width
	 * @param height
	 */
	public void checkAndShow(int width,int height){
		if(check()==false) return;
		if(mPopupWindow==null){
			mPopupWindow = new PopupWindow(mRootView, width, (int) (height * 0.3));
			mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
			//mPopupWindow.setOutsideTouchable(true);
		}
		if(mPopupWindow.isShowing()==true){
			return;
		}
		mPopupWindow.showAtLocation(mEEditText, Gravity.CENTER, 0, 0);
	}
	private boolean check(){
		Table table = mEEditText.getTable();
		int inputRowIndex = table.getInputRowIndex();
		if(inputRowIndex<0) return false;
		Row row = table.getRow(inputRowIndex);
		if(row.getRowType()!=Parser.TYPE_CODE){//处理代码行的提示
			return false;
		}
		String match = subInputString(table, row);
		Log.i("AAA", match);
		mVarList.clear();
		table.getFieldParamLocalRow(mVarList);
		return matchVar(mVarList, match);
	}
	/**
	 * 匹配变量，将匹配的变量添加到列表
	 * @param varList
	 * @param match
	 * @return
	 */
	private boolean matchVar(List<Row> varList,String match){
		Grid grid;
		String varName;
		List<HintItem> hintList = mHintAdapter.getList();
		HintItem item;
		int index = 0;
		for(Row varRow:varList){
			grid = varRow.getGrid(0);
			if(grid!=null){
				varName = grid.getContent();//获取变量名
				//取出末尾的换行符
				varName = varName.endsWith("\n") ? varName.substring(0, varName.length()-1) : varName;
				if(PinYinUtil.matchFirstPinYin(match, varName)==true){
					if(index>=hintList.size()){
						item = new HintItem();
						hintList.add(item);
					}else{
						item = hintList.get(index);
					}
					item.setTitle(varName);
					if(varRow.getRowType()==Parser.TYPE_FIELD){
						item.setLibCnName("成员");
					}else if(varRow.getRowType()==Parser.TYPE_PARAM){
						item.setLibCnName("参数");
					}else if(varRow.getRowType()==Parser.TYPE_LOCAL){
						item.setLibCnName("局部变量");
					}else{
						item.setLibCnName("");
					}
					item.setLibName("");
					item.setContent(varName);
					index++;
				}
			}
		}
		if(index==0) return false;
		for(int i=hintList.size()-1;i>=index;i--){
			hintList.remove(i);
		}
		mHintAdapter.notifyDataSetChanged();//刷新数据
		return true;
	}
	/**
	 * 截取输入短句
	 * @param table
	 * @param inputRow
	 * @return
	 */
	private String subInputString(Table table,Row inputRow){
		String result = "";
		int gridIndex = inputRow.getInputGridIndex(table.getCursor(), table.getMarginX());
		if(gridIndex<0) return result;
		Grid grid = inputRow.getGrid(gridIndex);
		//获取内容
		String content = grid.getContent();
		//if("".equals(content)==true) return result;
		//获取输入位置
		int index = grid.getInputIndex(table.getCursor(), table.getPaint(), table.getMarginX());
		int start=index;
		for(;start>=0;start--){
			if(symbols.indexOf(content.charAt(start))!=-1){
				start++;
				break;
			}
		}
		start = start < 0 ? 0 : start;
		result = content.substring(start, index);
		//存储当前操作行信息
		mStartPos = start;
		mEndPos = index;
		mGrid = grid;
		return result;
	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		Log.e("", "列表项被点击");
		Table table = mEEditText.getTable();
		String content = mGrid.getContent();
		String replace = mHintAdapter.getList().get(position).getContent();
		content = content.substring(0, mStartPos) + replace + content.substring(mEndPos);
		mGrid.setContent(content);
		Cursor cursor = table.getCursor();
		//设置光标位置
		float x = mGrid.getCursorX(mStartPos + replace.length(), table.getPaint())+table.getMarginX() - cursor.getWidth() / 2;
		cursor.setPosition(x, cursor.getY());
		//重新计算行宽
		table.measure();
		dismiss();
		mEEditText.postInvalidate();
	}
}
