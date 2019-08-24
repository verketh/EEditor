package cn.viewphoto.eedit.editor;

import android.content.Context;
import android.os.Vibrator;
import cn.viewphoto.eedit.view.Cursor;
import cn.viewphoto.eedit.view.EEditText;

public class HandleInput {
	public static final String TAG = "HandleInput";
	private StringBuilder	mStringBuilder;
	public HandleInput(Context context) {
		
		mStringBuilder = new StringBuilder();
	}
	public void handle(EEditText view,Table table, int rowIndex,int gridIndex, Cursor cursor, String input) {
		//Log.i(TAG, "rowIndex:"+rowIndex+" gridInde:"+gridIndex);
		Row row= table.getRow(rowIndex);
		if(checkRow(table,row,input)==false) return;
		//Log.i(TAG, "rowContent:"+row.getRowContent());
		Grid grid = row.getGrid(gridIndex);
		if(checkGrid(grid,input)==false) return;
		if(input.length()==1){
			char ch = input.charAt(0);
			switch(ch){
			case '\n':
				inputEnter(table, rowIndex, gridIndex, cursor);
				return;
			case 0://删除符
				inputDelete(table, rowIndex, gridIndex, cursor);
				return;
			}
		}
		int inputIndex = grid.getInputIndex(cursor, table.getPaint(),table.getMarginX());
		//Log.i(TAG, "index:"+inputIndex);
		mStringBuilder.delete(0, mStringBuilder.length());//删除缓存的字符
		mStringBuilder.append(grid.getContent());
		mStringBuilder.insert(inputIndex, input);
		String content = mStringBuilder.toString();
		grid.setContent(content);
		row.setLineBreak();//设置换行符位置
		table.measure();
		setCursorPos(table, row, grid, cursor, inputIndex + input.length(),cursor.getY());
		table.changeInputRowPosition();//将显示位置滚动到光标位置
	}
	private void inputDelete(Table table, int rowIndex,int gridIndex, Cursor cursor){
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		int inputIndex = grid.getInputIndex(cursor, table.getPaint(), table.getMarginX());
		int rowType = row.getRowType();
		if(rowType!=Parser.TYPE_CODE){
			//删除表格内的内容
			if(inputIndex==0) return;
			inputIndex--;
			String content = grid.getContent();
			mStringBuilder.delete(0, mStringBuilder.length());
			mStringBuilder.append(content.substring(0, inputIndex));
			mStringBuilder.append(content.substring(inputIndex+1, content.length()));
			grid.setContent(mStringBuilder.toString());
			row.setLineBreak();
			table.measure();
			setCursorPos(table, row, grid, cursor, inputIndex,cursor.getY());
		}else{
			if(inputIndex==0 && (rowIndex-1<0 || table.getRow(rowIndex-1).getRowType()!=Parser.TYPE_CODE)) return;
			if(inputIndex==0){
				//将当前行的内容追加到上一行，并删除当前行
				rowIndex--;
				String content;
				mStringBuilder.delete(0, mStringBuilder.length());
				row = table.getRow(rowIndex);
				content = row.getRowContent();//上一行
				int end;
				if(content.endsWith("\n")){
					end = content.length() - 1;
					mStringBuilder.append(content, 0, content.length() - 1);
				}else{
					end = content.length();
					mStringBuilder.append(content);
				}
				content = grid.getContent();//当前行
				mStringBuilder.append(content);
				row.setRowContent(mStringBuilder.toString());
				table.delete(rowIndex+1);
				table.measure();
				row.setLineBreak();
				setCursorPos(table, row, row.getGrid(0), cursor, end,row.getY());
			}else{
				inputIndex--;
				String content = grid.getContent();
				mStringBuilder.delete(0, mStringBuilder.length());
				mStringBuilder.append(content.substring(0, inputIndex));
				mStringBuilder.append(content.substring(inputIndex+1, content.length()));
				grid.setContent(mStringBuilder.toString());
				row.setLineBreak();
				table.measure();
				setCursorPos(table, row, grid, cursor, inputIndex,cursor.getY());
			}
		}
		table.changeInputRowPosition();
	}
	/**
	 * 处理换行符输入
	 * @param table
	 * @param rowIndex
	 * @param gridIndex
	 * @param cursor
	 * @param input
	 */
	private void inputEnter(Table table, int rowIndex,int gridIndex, Cursor cursor){
		//Log.i(TAG, "-------------------------");
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		int rowType = row.getRowType();
		if(rowType!=Parser.TYPE_CODE){
			if(rowType==Parser.TYPE_CLASS_HEAD || rowType==Parser.TYPE_METHOD_HEAD){
				rowIndex++;
				row = table.getRow(rowIndex);
				if(row==null) return;
				setCursorPos(table, grid, cursor, 0, row.getY(),row);
				table.changeInputRowPosition();
				return;
			}
			if(rowType==Parser.TYPE_METHOD || rowType==Parser.TYPE_PARAM_HEAD || rowType==Parser.TYPE_PARAM){
				addParam(table, rowIndex, gridIndex, cursor);
				return;
			}
			if(rowType==Parser.TYPE_CLASS || rowType==Parser.TYPE_FIELD_HEAD || rowType==Parser.TYPE_FIELD){
				addField(table, rowIndex, gridIndex, cursor);
				return;
			}
			if(rowType==Parser.TYPE_LOCAL || rowType==Parser.TYPE_LOCAL_HEAD){
				addLocal(table, rowIndex, gridIndex, cursor);
				return;
			}
		}else{
			addCode(table, rowIndex, gridIndex, cursor);
		}
	}
	private void setCursorPos(Table table,Row row, Grid grid,Cursor cursor,int end,float y){
		float textWidth =  table.getPaint().measureText(grid.getContent(),0,end);
		float x = grid.getX() + textWidth + table.getMarginX() - cursor.getWidth() / 2;
		x = row.getRowType()!=Parser.TYPE_CODE ? x + table.getMarginX() : x;
		//Log.i(TAG, "gridX:"+grid.getX()+"cursorx:"+x+"textwidth:"+textWidth);
		cursor.setPosition(x, y);
	}
	private void setCursorPos(Table table,Grid grid,Cursor cursor,float x,float y,Row row){
		float pos = grid.getCursorX(x, table.getPaint(),false) + table.getMarginX() - cursor.getWidth() / 2;
		pos = row.getRowType()!=Parser.TYPE_CODE ? pos + table.getMarginX() : pos;
		cursor.setPosition(pos, y);
	}
	/**
	 * 添加代码行
	 * @param table
	 * @param rowIndex
	 * @param gridIndex
	 * @param cursor
	 */
	private void addCode(Table table, int rowIndex,int gridIndex, Cursor cursor){
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		String content = grid.getContent();
		//mStringBuilder.delete(0, mStringBuilder.length());
		int index = grid.getInputIndex(cursor, table.getPaint(), table.getMarginX());
		Row addRow;
		if(index==0){
			addRow = new Row();
			Parser.parser(addRow, "\n");
			table.insert(rowIndex, addRow);//添加到前一行
			table.measure();
			setCursorPos(table, grid, cursor, 0, row.getY(),row);
		}else{
			grid.setContent(content.substring(0,index));
			addRow = new Row();
			Parser.parser(addRow, content.substring(index, content.length()));
			table.insert(rowIndex + 1, addRow);//添加到后一行
			addRow.setLineBreak();
			table.measure();
			setCursorPos(table, addRow.getGrid(0), cursor, 0, addRow.getY(),addRow);
			//Log.i(TAG, "index:"+rowIndex);
		}
		//Log.i(TAG, "eindex:"+rowIndex);
		table.changeInputRowPosition();
	}
	/**
	 * 添加局部变量行
	 * @param table
	 * @param rowIndex
	 * @param gridIndex
	 * @param cursor
	 */
	private void addLocal(Table table, int rowIndex,int gridIndex, Cursor cursor){
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		int type = row.getRowType();
		Row addRow;
		int addRowNum = 0;
		switch(type){
		case Parser.TYPE_LOCAL_HEAD:
		case Parser.TYPE_LOCAL:
			rowIndex++;
			addRow = new Row();
			Parser.parser(addRow, Parser.STR_LOCAL + " 变量\n");
			table.insert(rowIndex, addRow);
			addRowNum++;
			table.measure();//重新计算各个行的宽高
			setCursorPos(table, grid, cursor, 0,row.getY() + grid.getHeight() * addRowNum,addRow);
		}
		
		table.changeInputRowPosition();//将输入位置显示出来
	}
	private void addField(Table table, int rowIndex,int gridIndex, Cursor cursor){
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		Row nextRow = table.getRow(rowIndex + 1);
		int type = row.getRowType();
		Row addRow;
		int addRowNum = 0;
		switch(type){
		case Parser.TYPE_CLASS:
			rowIndex++;
			if(nextRow==null || nextRow.getRowType()!=Parser.TYPE_FIELD_HEAD){
				addRow = new Row();
				Parser.parserHead(addRow, Parser.TYPE_FIELD_HEAD);
				table.insert(rowIndex, addRow);
			}
			addRowNum++;
		case Parser.TYPE_FIELD_HEAD:
		case Parser.TYPE_FIELD:
			rowIndex++;
			addRow = new Row();
			Parser.parser(addRow, Parser.STR_FIELD + " 成员\n");
			table.insert(rowIndex, addRow);
			addRowNum++;
			table.measure();//重新计算各个行的宽高
			setCursorPos(table, grid, cursor, 0,row.getY() + grid.getHeight() * addRowNum,addRow);
		}
		
		table.changeInputRowPosition();//将输入位置显示出来
	}
	/**
	 * 添加参数行
	 * @param table
	 * @param rowIndex
	 * @param gridIndex
	 * @param cursor
	 */
	private void addParam(Table table, int rowIndex,int gridIndex, Cursor cursor){
		Row row = table.getRow(rowIndex);
		Grid grid = row.getGrid(gridIndex);
		Row nextRow = table.getRow(rowIndex + 1);
		int type = row.getRowType();
		Row addRow;
		int addRowNum = 0;
		switch(type){
		case Parser.TYPE_METHOD:
			rowIndex++;
			if(nextRow==null || nextRow.getRowType()!=Parser.TYPE_PARAM_HEAD){//添加表头
				addRow = new Row();
				Parser.parserHead(addRow, Parser.TYPE_PARAM_HEAD);
				table.insert(rowIndex, addRow);
			}
			addRowNum++;
		case Parser.TYPE_PARAM_HEAD:
		case Parser.TYPE_PARAM:
			rowIndex++;
			addRow = new Row();
			Parser.parser(addRow, Parser.STR_PARAM + " 参数\n");
			table.insert(rowIndex, addRow);
			addRowNum++;
			table.measure();//重新计算各个行的宽高
			setCursorPos(table, grid, cursor, 0,row.getY() + grid.getHeight() * addRowNum,addRow);
		}
		
		table.changeInputRowPosition();//将输入位置显示出来
	}
	private boolean checkRow(Table table,Row row,String input){
		if(Parser.isHead(row)==true && input.charAt(input.length() - 1)!='\n'){//表头不允许输入
			table.vibrator();//震动
			return false;
		}
		return true;
	}
	private boolean checkGrid(Grid grid,String input){
		if(grid.isSelectMod()==true && input.charAt(input.length() - 1)!='\n'){//选择模块不允许输入
			return false;
		}
		return true;
	}
}
