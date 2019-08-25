package cn.viewphoto.eedit.view;

import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;
import cn.viewphoto.eedit.R;
import cn.viewphoto.eedit.editor.Parser;
import cn.viewphoto.eedit.editor.Row;
import cn.viewphoto.eedit.editor.Table;

public class SelectMenu implements View.OnClickListener{
	private EEditText	mEEditText;
	private View rootView;
	private PopupWindow	mPopupWindow;
	private View	mDeleteRow;
	private View 	mDeleteField;
	private View 	mDeleteMethod;
	private View 	mDeleteParam;
	private View 	mDeleteLocal;
	public SelectMenu(EEditText editText){
		mEEditText = editText;
		Context context = editText.getContext();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rootView = inflater.inflate(R.layout.layout_select_menu, null);
		mDeleteRow = rootView.findViewById(R.id.select_menu_item_delete_row);
		mDeleteRow.setOnClickListener(this);
		mDeleteField = rootView.findViewById(R.id.select_menu_item_delete_field);
		mDeleteField.setOnClickListener(this);
		mDeleteMethod = rootView.findViewById(R.id.select_menu_item_delete_method);
		mDeleteMethod.setOnClickListener(this);
		mDeleteParam = rootView.findViewById(R.id.select_menu_item_delete_param);
		mDeleteParam.setOnClickListener(this);
		mDeleteLocal = rootView.findViewById(R.id.select_menu_item_delete_local);
		mDeleteLocal.setOnClickListener(this);
	}
	public void showMenu(int x,int y){
		if(initMenu()==false) return;
		mPopupWindow = new PopupWindow(rootView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		//popupWindow.setBackgroundDrawable(new clo);
		mPopupWindow.setOutsideTouchable(true);
		//popupWindow.showAsDropDown(v,x,y);
		mPopupWindow.showAtLocation(mEEditText, Gravity.CENTER, x, y);
	}
	private boolean initMenu(){
		int index = mEEditText.getTable().getInputRowIndex();
		if(index<0) return false;
		Row row = mEEditText.getTable().getRow(index);
		int type = row.getRowType();
		if(type==Parser.TYPE_VERSION || type==Parser.TYPE_CLASS || type==Parser.TYPE_CLASS_HEAD){
			return false;
		}
		if(type==Parser.TYPE_CODE){
			mDeleteRow.setVisibility(View.VISIBLE);
		}else{
			mDeleteRow.setVisibility(View.GONE);
		}
		if(type==Parser.TYPE_FIELD || type==Parser.TYPE_FIELD_HEAD){
			mDeleteField.setVisibility(View.VISIBLE);
		}else{
			mDeleteField.setVisibility(View.GONE);
		}
		if(type==Parser.TYPE_METHOD || type==Parser.TYPE_METHOD_HEAD){
			mDeleteMethod.setVisibility(View.VISIBLE);
		}else{
			mDeleteMethod.setVisibility(View.GONE);
		}
		if(type==Parser.TYPE_PARAM || type==Parser.TYPE_PARAM_HEAD){
			mDeleteParam.setVisibility(View.VISIBLE);
		}else{
			mDeleteParam.setVisibility(View.GONE);
		}
		if(type==Parser.TYPE_LOCAL || type==Parser.TYPE_LOCAL_HEAD){
			mDeleteLocal.setVisibility(View.VISIBLE);
		}else{
			mDeleteLocal.setVisibility(View.GONE);
		}
		return true;
	}
	private void deleteRow(int index){
		Table table = mEEditText.getTable();
		Point point = table.getCursorPoint();
		if(index-1>=0){
			int type = table.getRow(index - 1).getRowType();
			if(type!=Parser.TYPE_CODE){//上一行不是代码
				if(table.getRow(index + 1)==null){//当前行为行末
					return;
				}else if(table.getRow(index + 1).getRowType()!=Parser.TYPE_CODE){//下一行不是代码
					return;
				}
			}
		}
		table.delete(index);
		table.measure();
		table.onClick(0, point.y);
		mEEditText.postInvalidate();
	}
	private void deleteField(int index){
		Table table = mEEditText.getTable();
		Point point = table.getCursorPoint();
		int type = table.getRow(index).getRowType();
		if(type==Parser.TYPE_FIELD_HEAD){//删除所有成员
			Row row;
			while(type==Parser.TYPE_FIELD_HEAD || type==Parser.TYPE_FIELD){
				table.delete(index);
				row = table.getRow(index);
				if(row==null) break;
				type = row.getRowType();
			}
		}else{
			//删除单个成员，如只有一个成员，需将成员表头一起删除
			if(index-1>=0 && table.getRow(index-1).getRowType()==Parser.TYPE_FIELD_HEAD
				&& ((table.getRow(index+1)!=null && table.getRow(index+1).getRowType()!=Parser.TYPE_FIELD)
				|| table.getRow(index+1)==null)){
				table.delete(index);
				table.delete(index-1);
			}else{
				table.delete(index);
			}
		}
		table.measure();
		table.onClick(0, point.y);
		mEEditText.postInvalidate();
	}
	private void deleteMethod(int index){
		Table table = mEEditText.getTable();
		Point point = table.getCursorPoint();
		int type = table.getRow(index).getRowType();
		if(type==Parser.TYPE_METHOD){
			if(index-1>=0 && table.getRow(index-1).getRowType()==Parser.TYPE_METHOD_HEAD){
				index--;
			}
		}
		boolean canStop = false;
		Row row;
		while(!(type!=Parser.TYPE_CODE && canStop==true)){
			table.delete(index);
			row = table.getRow(index);
			if(row==null) break;
			type = row.getRowType();
			if(type==Parser.TYPE_CODE){
				canStop = true;
			}
		}
		table.measure();
		table.onClick(0, point.y);
		mEEditText.postInvalidate();
	}
	private void deleteParam(int index){
		Table table = mEEditText.getTable();
		Point point = table.getCursorPoint();
		int type = table.getRow(index).getRowType();
		if(type==Parser.TYPE_PARAM_HEAD){//删除所有参数
			Row row;
			while(type==Parser.TYPE_PARAM_HEAD || type==Parser.TYPE_PARAM){
				table.delete(index);
				row = table.getRow(index);
				if(row==null) break;
				type = row.getRowType();
			}
		}else{
			//删除单个参数，如只有一个参数，需将参数表头一起删除
			if(index-1>=0 && table.getRow(index-1).getRowType()==Parser.TYPE_PARAM_HEAD
				&& ((table.getRow(index+1)!=null && table.getRow(index+1).getRowType()!=Parser.TYPE_PARAM)
				|| table.getRow(index+1)==null)){
				table.delete(index);
				table.delete(index-1);
			}else{
				table.delete(index);
			}
		}
		table.measure();
		table.onClick(0, point.y);
		mEEditText.postInvalidate();
	}
	private void deleteLocal(int index){
		Table table = mEEditText.getTable();
		Point point = table.getCursorPoint();
		int type = table.getRow(index).getRowType();
		if(type==Parser.TYPE_LOCAL_HEAD){//删除所有参数
			Row row;
			while(type==Parser.TYPE_LOCAL_HEAD || type==Parser.TYPE_LOCAL){
				table.delete(index);
				row = table.getRow(index);
				if(row==null) break;
				type = row.getRowType();
			}
		}else{
			//删除单个参数，如只有一个参数，需将参数表头一起删除
			if(index-1>=0 && table.getRow(index-1).getRowType()==Parser.TYPE_LOCAL_HEAD
				&& ((table.getRow(index+1)!=null && table.getRow(index+1).getRowType()!=Parser.TYPE_LOCAL)
				|| table.getRow(index+1)==null)){
				table.delete(index);
				table.delete(index-1);
			}else{
				table.delete(index);
			}
		}
		table.measure();
		table.onClick(0, point.y);
		mEEditText.postInvalidate();
	}
	@Override
	public void onClick(View v) {
		int index = mEEditText.getTable().getInputRowIndex();
		if(index<0) return;
		switch(v.getId()){
		case R.id.select_menu_item_delete_row:
			deleteRow(index);
			break;
		case R.id.select_menu_item_delete_field:
			deleteField(index);
			break;
		case R.id.select_menu_item_delete_method:
			deleteMethod(index);
			break;
		case R.id.select_menu_item_delete_param:
			deleteParam(index);
			break;
		case R.id.select_menu_item_delete_local:
			deleteLocal(index);
			break;
		}
		mPopupWindow.dismiss();
	}
}
