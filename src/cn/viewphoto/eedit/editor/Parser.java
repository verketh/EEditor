package cn.viewphoto.eedit.editor;

import java.util.List;

import android.util.Log;

public class Parser {
	public static final String TAG = "Parser";
	public static final int TYPE_VERSION = 0;
	public static final int TYPE_CLASS = 1;
	public static final int TYPE_FIELD = 2;
	public static final int TYPE_METHOD = 3;
	public static final int TYPE_PARAM = 4;
	public static final int TYPE_LOCAL = 5;
	public static final int TYPE_CODE = 6;
	public static final int TYPE_CLASS_HEAD = 7;
	public static final int TYPE_FIELD_HEAD = 8;
	public static final int TYPE_METHOD_HEAD = 9;
	public static final int TYPE_PARAM_HEAD = 10;
	public static final int TYPE_LOCAL_HEAD = 11;
	public static final int CONTENT_TYPE_TEXT = 0;// 文本类型
	public static final int CONTENT_TYPE_SELECT_PUBLIC = 1;// 公开
	public static final int CONTENT_TYPE_SELECT_REFERENCE = 2;// 参考
	public static final int CONTENT_TYPE_SELECT_EMPRY = 3;// 可空
	public static final int CONTENT_TYPE_SELECT_STATIC = 4;// 静态
	public static final int CONTENT_TYPE_SELECT_ARRAY = 5;// 数组

	public static final String STR_VERSION = ".版本";
	public static final String STR_CLASS = ".程序集";
	public static final String STR_FIELD = ".程序集变量";
	public static final String STR_METHOD = ".子程序";
	public static final String STR_PARAM = ".参数";
	public static final String STR_LOCAL = ".局部变量";
	public static final String STR_PUBLIC = "公开";
	public static final String STR_REFERENCE = "参考";
	public static final String STR_EMPRY = "可空";
	public static final String STR_STATIC = "静态";
	public static final String STR_ARRAY = "数组";
	
	public static final String STR_IF_ELSE_START = ".如果";
	public static final String STR_IF_ELSE_MID = ".否则";
	public static final String STR_IF_ELSE_END = ".如果结束";
	public static final String STR_IF_START = ".如果真";
	public static final String STR_IF_END = ".如果真结束";
	public static final String STR_JUDGE_START = ".判断开始";
	public static final String STR_JUDGE_MID_1 = ".判断";
	public static final String STR_JUDGE_MID_2 = ".默认";
	public static final String STR_JUDGE_END = ".判断结束";
	public static final String STR_JUDGE_LOOP_START = ".判断循环首";
	public static final String STR_JUDGE_LOOP_END = ".判断循环尾";
	public static final String STR_LOOP_JUDGE_START = ".循环判断首";
	public static final String STR_LOOP_JUDGE_END = ".循环判断尾";
	public static final String STR_NUMBER_LOOP_START = ".计次循环首";
	public static final String STR_NUMBER_LOOP_END = ".计次循环尾";
	public static final String STR_VARIATE_LOOP_START = ".变量循环首";
	public static final String STR_VARIATE_LOOP_END = ".变量循环尾";

	public static final int CODE_TYPE_IF_ELSE_START = 0x10000;
	public static final int CODE_TYPE_IF_ELSE_MID = 0x20000;
	public static final int CODE_TYPE_IF_ELSE_END = 0x30000;
	public static final int CODE_TYPE_IF_START = 0x40000;
	public static final int CODE_TYPE_IF_END = 0x50000;
	public static final int CODE_TYPE_JUDGE_START = 0x60000;
	public static final int CODE_TYPE_JUDGE_MID_1 = 0x70000;
	public static final int CODE_TYPE_JUDGE_MID_2 = 0x80000;
	public static final int CODE_TYPE_JUDGE_END = 0x90000;
	public static final int CODE_TYPE_JUDGE_LOOP_START = 0xA0000;
	public static final int CODE_TYPE_JUDGE_LOOP_END = 0xB0000;
	public static final int CODE_TYPE_LOOP_JUDGE_START = 0xC0000;
	public static final int CODE_TYPE_LOOP_JUDGE_END = 0xD0000;
	public static final int CODE_TYPE_NUMBER_LOOP_START = 0xE0000;
	public static final int CODE_TYPE_NUMBER_LOOP_END = 0xE0000;
	public static final int CODE_TYPE_VARIATE_LOOP_START = 0xF0000;
	public static final int CODE_TYPE_VARIATE_LOOP_END = 0x100000;
	public static final int CODE_TYPE_CODE = 0x200000;

	private static StringBuilder mStringBuilder = new StringBuilder();

	private static int getCodeType(String line) {
		String str = line.replaceAll(" ", "");// 清除所有空格
		if (STR_IF_ELSE_START.startsWith(str)) {
			return CODE_TYPE_IF_ELSE_START;
		}
		if (STR_IF_ELSE_MID.startsWith(str)) {
			return CODE_TYPE_IF_ELSE_MID;
		}
		if (STR_IF_ELSE_END.startsWith(str)) {
			return CODE_TYPE_IF_ELSE_END;
		}
		if (STR_IF_START.startsWith(str)) {
			return CODE_TYPE_IF_START;
		}
		if (STR_IF_END.startsWith(str)) {
			return CODE_TYPE_IF_END;
		}
		if (STR_JUDGE_START.startsWith(str)) {
			return CODE_TYPE_JUDGE_START;
		}
		if (STR_JUDGE_MID_1.startsWith(str)) {
			return CODE_TYPE_JUDGE_MID_1;
		}
		if (STR_JUDGE_MID_2.startsWith(str)) {
			return CODE_TYPE_JUDGE_MID_2;
		}
		if (STR_JUDGE_END.startsWith(str)) {
			return CODE_TYPE_JUDGE_END;
		}
		if (STR_JUDGE_LOOP_START.startsWith(str)) {
			return CODE_TYPE_JUDGE_LOOP_START;
		}
		if (STR_JUDGE_LOOP_END.startsWith(str)) {
			return CODE_TYPE_JUDGE_LOOP_END;
		}
		if (STR_LOOP_JUDGE_START.startsWith(str)) {
			return CODE_TYPE_LOOP_JUDGE_START;
		}
		if (STR_LOOP_JUDGE_END.startsWith(str)) {
			return CODE_TYPE_LOOP_JUDGE_END;
		}
		if (STR_NUMBER_LOOP_START.startsWith(str)) {
			return CODE_TYPE_NUMBER_LOOP_START;
		}
		if (STR_NUMBER_LOOP_END.startsWith(str)) {
			return CODE_TYPE_NUMBER_LOOP_END;
		}
		if (STR_VARIATE_LOOP_START.startsWith(str)) {
			return CODE_TYPE_VARIATE_LOOP_START;
		}
		if (STR_VARIATE_LOOP_END.startsWith(str)) {
			return CODE_TYPE_VARIATE_LOOP_END;
		}
		return CODE_TYPE_CODE;
	}

	public static String getSelectStr(int type) {
		switch (type) {
		case CONTENT_TYPE_SELECT_ARRAY:
			return STR_ARRAY;
		case CONTENT_TYPE_SELECT_EMPRY:
			return STR_EMPRY;
		case CONTENT_TYPE_SELECT_PUBLIC:
			return STR_PUBLIC;
		case CONTENT_TYPE_SELECT_REFERENCE:
			return STR_REFERENCE;
		case CONTENT_TYPE_SELECT_STATIC:
			return STR_STATIC;
		}
		return "";
	}

	public static boolean isHead(Row row) {
		int type = row.getRowType();
		switch (type) {
		case TYPE_CLASS_HEAD:
			return true;
		case TYPE_FIELD_HEAD:
			return true;
		case TYPE_METHOD_HEAD:
			return true;
		case TYPE_PARAM_HEAD:
			return true;
		case TYPE_LOCAL_HEAD:
			return true;
		}
		return false;
	}

	public static void parserHead(Row row, int type) {
		List<Grid> gridList = row.getGridList();
		switch (type) {
		case TYPE_CLASS_HEAD:
			row.setRowType(type);
			parserClassHead(gridList);
			break;
		case TYPE_FIELD_HEAD:
			row.setRowType(type);
			parserFieldHead(gridList);
			break;
		case TYPE_METHOD_HEAD:
			row.setRowType(type);
			parserMethodHead(gridList);
			break;
		case TYPE_PARAM_HEAD:
			row.setRowType(type);
			parserParamHead(gridList);
			break;
		case TYPE_LOCAL_HEAD:
			row.setRowType(type);
			parserLocalHead(gridList);
		}
	}

	private static void parserLocalHead(List<Grid> gridList) {
		createGrid(gridList, 5);
		for (Grid grid : gridList) {
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		Grid grid;
		grid = gridList.get(0);
		grid.setContent("变量名");
		grid = gridList.get(1);
		grid.setContent("类型");
		grid = gridList.get(2);
		grid.setContent("静态");
		grid = gridList.get(3);
		grid.setContent("数组");
		grid = gridList.get(4);
		grid.setContent("备注");
	}

	private static void parserParamHead(List<Grid> gridList) {
		createGrid(gridList, 6);
		for (Grid grid : gridList) {
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		Grid grid;
		grid = gridList.get(0);
		grid.setContent("参数名");
		grid = gridList.get(1);
		grid.setContent("类型");
		grid = gridList.get(2);
		grid.setContent("参考");
		grid = gridList.get(3);
		grid.setContent("可空");
		grid = gridList.get(4);
		grid.setContent("数组");
		grid = gridList.get(5);
		grid.setContent("备注");
	}

	private static void parserMethodHead(List<Grid> gridList) {
		createGrid(gridList, 4);
		for (Grid grid : gridList) {
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		Grid grid;
		grid = gridList.get(0);
		grid.setContent("方法名");
		grid = gridList.get(1);
		grid.setContent("返回值类型");
		grid = gridList.get(2);
		grid.setContent("公开");
		grid = gridList.get(3);
		grid.setContent("备注");
	}

	private static void parserFieldHead(List<Grid> gridList) {
		createGrid(gridList, 4);
		for (Grid grid : gridList) {
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		Grid grid;
		grid = gridList.get(0);
		grid.setContent("成员名");
		grid = gridList.get(1);
		grid.setContent("类型");
		grid = gridList.get(2);
		grid.setContent("数组");
		grid = gridList.get(3);
		grid.setContent("备注");
	}

	private static void parserClassHead(List<Grid> gridList) {
		createGrid(gridList, 4);
		for (Grid grid : gridList) {
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		Grid grid;
		grid = gridList.get(0);
		grid.setContent("类名");
		grid = gridList.get(1);
		grid.setContent("基类");
		grid = gridList.get(2);
		grid.setContent("公开");
		grid = gridList.get(3);
		grid.setContent("备注");
	}

	public static void parser(Row row, String line) {
		List<Grid> gridList = row.getGridList();
		if (line != null) {
			if (line.startsWith(STR_LOCAL)) {
				row.setRowType(TYPE_LOCAL);
				parserLocal(gridList, line);
				return;
			}
			if (line.startsWith(STR_PARAM)) {
				row.setRowType(TYPE_PARAM);
				parserParam(gridList, line);
				return;
			}
			if (line.startsWith(STR_METHOD)) {
				row.setRowType(TYPE_METHOD);
				parserMethod(gridList, line);
				return;
			}
			if (line.startsWith(STR_FIELD)) {
				row.setRowType(TYPE_FIELD);
				parserField(gridList, line);
				return;
			}
			if (line.startsWith(STR_CLASS)) {
				row.setRowType(TYPE_CLASS);
				parserClass(gridList, line);
				return;
			}
			if (line.startsWith(STR_VERSION)) {
				row.setRowType(TYPE_VERSION);
				parserVersion(gridList, line);
				return;
			}
		}
		row.setRowType(TYPE_CODE | getCodeType(line));
		parserCode(gridList, line);
	}
	private static void parserVersion(List<Grid> gridList, String line){
		createGrid(gridList, 1);
		Grid grid = gridList.get(0);
		if (line == null) {
			line = "\n";
		}
		grid.setContent(line);
		grid.setContentType(CONTENT_TYPE_TEXT);
		if(line.endsWith("\n")){
			Log.i(TAG, "换行结尾");
		}else{
			Log.i(TAG, "非换行结尾");
		}
		
	}
	private static void parserCode(List<Grid> gridList, String line) {
		createGrid(gridList, 1);
		Grid grid = gridList.get(0);
		if (line == null) {
			line = "\n";
		}
		grid.setContent(line);
		grid.setContentType(CONTENT_TYPE_TEXT);
	}

	/**
	 * .局部变量 变量名, 数据型, 静态, "数组", 备注
	 * 
	 * @param gridList
	 * @param line
	 */
	private static void parserLocal(List<Grid> gridList, String line) {
		createGrid(gridList, 5);
		char ch;
		boolean start = true, marks = false;
		int startPos = 0, endPos = 0, index = 0;
		Grid grid;
		for (int i = 0; i < gridList.size(); i++) {// 初始化
			grid = gridList.get(i);
			grid.setContent("");
			if (i == 2) {
				grid.setContentType(CONTENT_TYPE_SELECT_STATIC);
			} else {
				grid.setContentType(CONTENT_TYPE_TEXT);
			}
		}
		for (int i = STR_LOCAL.length(); i < line.length() && index < gridList.size(); i++) {
			ch = line.charAt(i);
			if (ch != ' ') {
				if (start) {
					startPos = i;
					endPos = i;
					start = false;
				} else {
					endPos++;
				}
			}
			if (ch == '\"') {// 将双引号内的内容作为一个整体
				marks = marks ? false : true;
			}
			if (ch == ',' && marks == false) {
				grid = gridList.get(index);
				String str = line.substring(startPos, endPos);
				if (index == 3) {
					str = str.replaceAll("\"", "");
				}
				grid.setContent(str);
				index++;
				start = true;
			}
		}
		// 最后一项取完剩下的内容
		if (index >= gridList.size()) {
			index = gridList.size() - 1;
		}
		grid = gridList.get(index);
		String str = line.substring(startPos, line.length());
		if (index == 3) {
			str = str.replaceAll("\"", "");
		}
		grid.setContent(str);

	}

	/**
	 * .参数 参数名, 数据型, 参考 可空 数组, 备注
	 * 
	 * @param gridList
	 * @param line
	 */
	private static void parserParam(List<Grid> gridList, String line) {
		createGrid(gridList, 6);
		char ch;
		boolean start = true;
		int startPos = 0, endPos = 0, index = 0;
		Grid grid;
		String str;
		// 初始化
		for (int i = 0; i < gridList.size(); i++) {
			grid = gridList.get(i);
			grid.setContent("");
			if (i > 1 && i < 5) {
				if (i == 2)
					grid.setContentType(CONTENT_TYPE_SELECT_REFERENCE);
				if (i == 3)
					grid.setContentType(CONTENT_TYPE_SELECT_EMPRY);
				if (i == 4)
					grid.setContentType(CONTENT_TYPE_SELECT_ARRAY);
			} else {
				grid.setContentType(CONTENT_TYPE_TEXT);
			}
		}
		for (int i = STR_PARAM.length(); i < line.length() && index < gridList.size(); i++) {
			ch = line.charAt(i);
			if (ch != ' ' || (ch == ' ' && index > 1 && index < 5)) {
				if (start) {
					startPos = i;
					endPos = i;
					start = false;
				} else {
					endPos++;
				}
			}
			if (ch == ',') {
				str = line.substring(startPos, endPos);
				if (index > 1 && index < 5) {
					grid = gridList.get(2);
					if (str.indexOf(STR_REFERENCE) != -1) {
						grid.setContent(STR_REFERENCE);
					}
					grid = gridList.get(3);
					if (str.indexOf(STR_EMPRY) != -1) {
						grid.setContent(STR_EMPRY);
					}
					grid = gridList.get(4);
					if (str.indexOf(STR_ARRAY) != -1) {
						grid.setContent(STR_ARRAY);
					}
					index = 5;
				} else {
					grid = gridList.get(index);
					grid.setContentType(CONTENT_TYPE_TEXT);
					grid.setContent(str);
					index++;
				}
				start = true;
			}
		}
		// 最后一项取完剩下的内容
		if (index >= gridList.size()) {
			index = gridList.size() - 1;
		}
		grid = gridList.get(index);
		str = line.substring(startPos, line.length());
		if (index > 1 && index < 5) {
			grid = gridList.get(2);
			if (str.indexOf(STR_REFERENCE) != -1) {
				grid.setContent(STR_REFERENCE);
			}
			grid = gridList.get(3);
			if (str.indexOf(STR_EMPRY) != -1) {
				grid.setContent(STR_EMPRY);
			}
			grid = gridList.get(4);
			if (str.indexOf(STR_ARRAY) != -1) {
				grid.setContent(STR_ARRAY);
			}
			index = 5;
		} else {
			grid.setContentType(CONTENT_TYPE_TEXT);
			grid.setContent(str);
		}
		// 添加\n
		if ("".equals(gridList.get(5).getContent()) == true) {
			for (int i = 4; i > 1; i--) {
				grid = gridList.get(i);
				if ("".equals(grid.getContent()) == false) {
					grid.setContent(grid.getContent() + "\n");
					break;
				}
			}
		}
	}

	/**
	 * .子程序 方法名, 返回值类型, 公开, 备注
	 * 
	 * @param gridList
	 * @param line
	 */
	private static void parserMethod(List<Grid> gridList, String line) {
		createGrid(gridList, 4);
		char ch;
		boolean start = true;
		int startPos = 0, endPos = 0, index = 0;
		Grid grid;
		for (int i = 0; i < gridList.size(); i++) {// 初始化
			grid = gridList.get(i);
			grid.setContent("");
			if (i == 2) {
				grid.setContentType(CONTENT_TYPE_SELECT_PUBLIC);
			} else {
				grid.setContentType(CONTENT_TYPE_TEXT);
			}
		}
		for (int i = STR_METHOD.length(); i < line.length() && index < gridList.size(); i++) {
			ch = line.charAt(i);
			if (ch != ' ') {
				if (start) {
					startPos = i;
					endPos = i;
					start = false;
				} else {
					endPos++;
				}
			}
			if (ch == ',') {
				grid = gridList.get(index);
				grid.setContent(line.substring(startPos, endPos));
				index++;
				start = true;
			}
		}
		// 最后一项取完剩下的内容
		if (index >= gridList.size()) {
			index = gridList.size() - 1;
		}
		grid = gridList.get(index);
		grid.setContent(line.substring(startPos, line.length()));
	}

	/**
	 * .程序集变量 变量名, 整数型, , "数组", 备注
	 * 
	 * @param gridList
	 * @param line
	 */
	private static void parserField(List<Grid> gridList, String line) {
		createGrid(gridList, 5);
		char ch;
		boolean start = true, marks = false;
		int startPos = 0, endPos = 0, index = 0;
		Grid grid;
		for (int i = 0; i < gridList.size(); i++) {// 初始化
			grid = gridList.get(i);
			grid.setContent("");
			grid.setContentType(CONTENT_TYPE_TEXT);
		}
		for (int i = STR_FIELD.length(); i < line.length() && index < gridList.size(); i++) {
			ch = line.charAt(i);
			if (ch != ' ') {
				if (start) {
					startPos = i;
					endPos = i;
					start = false;
				} else {
					endPos++;
				}
			}
			if (ch == '\"') {// 将双引号内的内容作为一个整体
				marks = marks ? false : true;
			}
			if (ch == ',' && marks == false) {
				grid = gridList.get(index);
				String str = line.substring(startPos, endPos);
				if (index == 3) {
					str = str.replaceAll("\"", "");
				}
				grid.setContent(str);
				index++;
				start = true;
			}
		}
		// 最后一项取完剩下的内容
		if (index >= gridList.size()) {
			index = gridList.size() - 1;
		}
		grid = gridList.get(index);
		String str = line.substring(startPos, line.length());
		if (index == 3) {
			str = str.replaceAll("\"", "");
		}
		grid.setContent(str);
		gridList.remove(2);// 移除第二项
	}

	/**
	 * .程序集 类名, 基类, 公开, 备注
	 * 
	 * @param gridList
	 * @param line
	 */
	private static void parserClass(List<Grid> gridList, String line) {
		createGrid(gridList, 4);
		char ch;
		boolean start = true;
		int startPos = 0, endPos = 0, index = 0;
		Grid grid;
		// 初始化
		for (int i = 0; i < gridList.size(); i++) {// 初始化
			grid = gridList.get(i);
			grid.setContent("");
			if (i == 2) {
				grid.setContentType(CONTENT_TYPE_SELECT_PUBLIC);
			} else {
				grid.setContentType(CONTENT_TYPE_TEXT);
			}

		}
		for (int i = STR_CLASS.length(); i < line.length() && index < gridList.size(); i++) {
			ch = line.charAt(i);
			if (ch != ' ') {
				if (start) {
					startPos = i;
					endPos = i;
					start = false;
				} else {
					endPos++;
				}
			}
			if (ch == ',') {
				grid = gridList.get(index);
				grid.setContent(line.substring(startPos, endPos));
				index++;
				start = true;
			}
		}
		// 最后一项取完剩下的内容
		if (index >= gridList.size()) {
			index = gridList.size() - 1;
		}
		grid = gridList.get(index);
		grid.setContent(line.substring(startPos, line.length()));
	}

	private static void createGrid(List<Grid> gridList, int lenght) {
		for (int i = 0; i < lenght; i++) {// 创建足够的grid
			if (i >= gridList.size()) {
				gridList.add(new Grid());
			}
		}
		// 移除多余的grid
		for (int i = gridList.size() - 1; i >= lenght; i--) {
			gridList.remove(i);
		}
	}

	public static String getRowContent(Row row) {
		List<Grid> gridList = row.getGridList();
		switch (row.getRowType()) {
		case TYPE_VERSION:
			return gridList.get(0).getContent();
		case TYPE_CLASS:
			return getClassContent(gridList);
		case TYPE_FIELD:
			return getFieldContent(gridList);
		case TYPE_METHOD:
			return getMethodContent(gridList);
		case TYPE_PARAM:
			return getParamContent(gridList);
		case TYPE_LOCAL:
			return getLocalContent(gridList);
		case TYPE_CODE:
			return gridList.size() > 0 ? gridList.get(0).getContent() : "";
		}

		return "";
	}

	/**
	 * .程序集 类名, 基类, 公开, 备注
	 * 
	 * @return
	 */
	private static String getClassContent(List<Grid> gridList) {
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(STR_CLASS);
		mStringBuilder.append(" ");
		Grid grid;
		String str;
		for (int i = 0; i < gridList.size(); i++) {
			grid = gridList.get(i);
			str = grid.getContent();
			mStringBuilder.append(str);
			if (str.endsWith("\n")) {
				break;
			}
			mStringBuilder.append(", ");
			/*
			 * if(i+1<gridList.size()){ mStringBuilder.append(", "); }
			 */
		}
		return mStringBuilder.toString();
	}

	/**
	 * .程序集变量 变量名, 整数型, , "数组", 备注
	 * 
	 * @return
	 */
	private static String getFieldContent(List<Grid> gridList) {
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(STR_FIELD);
		mStringBuilder.append(" ");
		Grid grid;
		String str;
		for (int i = 0; i < gridList.size(); i++) {
			if (i == 2 && i + 1 < gridList.size()) {// 添加一个空项
				mStringBuilder.append(", ");
			}
			grid = gridList.get(i);
			str = grid.getContent();
			if (i == 2 && "".equals(str) == false) {// 数组加双引号
				mStringBuilder.append("\"");
			}
			mStringBuilder.append(str);
			if (i == 2 && "".equals(str) == false) {// 数组加双引号
				if (str.endsWith("\n")) {
					mStringBuilder.insert(mStringBuilder.length() - 1, "\"");
				} else {
					mStringBuilder.append("\"");
				}
			}
			if (str.endsWith("\n")) {
				break;
			}
			mStringBuilder.append(", ");
			/*
			 * if(i+1<gridList.size()){ mStringBuilder.append(", "); }
			 */
		}
		return mStringBuilder.toString();
	}

	/**
	 * .子程序 方法名, 返回值类型, 公开, 备注
	 * 
	 * @return
	 */
	private static String getMethodContent(List<Grid> gridList) {
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(STR_METHOD);
		mStringBuilder.append(" ");
		Grid grid;
		String str;
		for (int i = 0; i < gridList.size(); i++) {
			grid = gridList.get(i);
			str = grid.getContent();
			mStringBuilder.append(str);
			if (str.endsWith("\n")) {
				break;
			}
			mStringBuilder.append(", ");
			/*
			 * if(i+1<gridList.size()){ mStringBuilder.append(", "); }
			 */
		}
		return mStringBuilder.toString();
	}

	/**
	 * .参数 参数名, 数据型, 参考 可空 数组, 备注
	 * 
	 * @return
	 */
	private static String getParamContent(List<Grid> gridList) {
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(STR_PARAM);
		mStringBuilder.append(" ");
		Grid grid;
		String str;
		for (int i = 0; i < gridList.size(); i++) {
			grid = gridList.get(i);
			str = grid.getContent();
			if (i == 3) {
				if ("".equals(str) == false && "".equals(gridList.get(i - 1).getContent()) == false) {
					mStringBuilder.append(" ");
				}
			}
			if (i == 4) {
				if ("".equals(str) == false && ("".equals(gridList.get(i - 1).getContent()) == false
						|| "".equals(gridList.get(i - 2).getContent()) == false)) {
					mStringBuilder.append(" ");
				}
			}
			mStringBuilder.append(str);
			if (str.endsWith("\n")) {
				break;
			}
			if (i <= 1 || i >= 4) {
				mStringBuilder.append(", ");
			}
			/*
			 * if(i+1<gridList.size()){ if(i>1 && i<4){ mStringBuilder.append(
			 * " "); }else{ mStringBuilder.append(", "); } }
			 */
		}
		return mStringBuilder.toString();
	}

	/**
	 * .局部变量 变量名, 数据型, 静态, "数组", 备注
	 * 
	 * @return
	 */
	private static String getLocalContent(List<Grid> gridList) {
		mStringBuilder.delete(0, mStringBuilder.length());
		mStringBuilder.append(STR_LOCAL);
		mStringBuilder.append(" ");
		Grid grid;
		String str;
		for (int i = 0; i < gridList.size(); i++) {
			grid = gridList.get(i);
			str = grid.getContent();
			if (i == 3 && "".equals(str) == false) {// 数组加双引号
				mStringBuilder.append("\"");
			}
			mStringBuilder.append(str);
			if (i == 3 && "".equals(str) == false) {// 数组加双引号
				if (str.endsWith("\n")) {
					mStringBuilder.insert(mStringBuilder.length() - 1, "\"");
				} else {
					mStringBuilder.append("\"");
				}
			}
			if (str.endsWith("\n")) {
				break;
			}
			mStringBuilder.append(", ");
			/*
			 * if(i+1<gridList.size()){ mStringBuilder.append(", "); }
			 */
		}
		return mStringBuilder.toString();
	}
}
