package cn.viewphoto.eedit.util;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import cn.viewphoto.eedit.R;

public class UpdateUtil {
	private static final String TAG = "UpdateUtil";
	private static final String URL = "http://www.viewphoto.cn/app/";
	private static Context mContext;
	private static boolean mHintShow = false;
	private static AppInfo mAppInfo;
	private static long mDowndId = -1;
	private static DownloadManager mDownloadManager;

	public static void checkUpdate(Context context, boolean hintShow) {
		mContext = context;
		mHintShow = hintShow;
		check();
	}

	private static void check() {
		try {
			PackageManager packageManager = mContext.getPackageManager();
			ApplicationInfo applicationInfo = packageManager.getApplicationInfo(mContext.getPackageName(), 0);
			PackageInfo packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
			AsyncHttpClient client = new AsyncHttpClient();
			//封装当前版本信息
			mAppInfo = new AppInfo();
			mAppInfo.setAppName(packageManager.getApplicationLabel(applicationInfo).toString());
			mAppInfo.setVersionName(packageInfo.versionName);
			mAppInfo.setVersionCode(packageInfo.versionCode);
			mAppInfo.setTag("");
			//将应用信息对象转成json
			ObjectMapper mapper = new ObjectMapper();
			String jsonStr = mapper.writeValueAsString(mAppInfo);
			//将json字符串转成httpEntity
			StringEntity entity = new StringEntity(jsonStr, "UTF-8");
			//post异步请求
			client.post(mContext, URL + "update.php", entity, "UTF-8",
					new BaseJsonHttpResponseHandler<UpdateUtil.AppInfo>() {

						@Override
						public void onFailure(int statusCode, Header[] headers, Throwable arg2, String rawJsonResponse,
								AppInfo appInfo) {
							if (mHintShow) {
								Toast.makeText(mContext, mContext.getResources().getString(R.string.update_hint_fail), Toast.LENGTH_SHORT)
										.show();
							}
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse,
								AppInfo appInfo) {
							if (mAppInfo.versionCode < appInfo.versionCode) {
								mAppInfo = appInfo;
								//弹出对话框让用户选择是否更新
								new AlertDialog.Builder(mContext).setTitle(R.string.update_update)
											   .setNegativeButton(R.string.update_later, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													dialog.dismiss();
												}
											})
											   .setNeutralButton(R.string.update_now, new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													//创建文件下载请求
													DownloadManager.Request request = new DownloadManager.Request(
															Uri.parse(URL + mAppInfo.fileName));
													//设置保存目录
													request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS,
															mAppInfo.fileName);
													mDownloadManager = (DownloadManager) mContext
															.getSystemService(Context.DOWNLOAD_SERVICE);
													//开始下载
													mDowndId = mDownloadManager.enqueue(request);
													dialog.dismiss();
												}
											})
											   .setMessage("V" + appInfo.getVersionName() + "\n" + appInfo.getTag())
											   .show();
							}else if (mHintShow) {//无新版本
								Toast.makeText(mContext, mContext.getResources().getString(R.string.update_hint_no_update), Toast.LENGTH_SHORT)
										.show();
							}

						}

						@Override
						protected AppInfo parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
							Log.i(TAG, rawJsonData);
							return new ObjectMapper().readValue(new JsonFactory().createJsonParser(rawJsonData),
									AppInfo.class);
						}
					});
		} catch (Exception e) {
			e.printStackTrace();
			if (mHintShow) {
				Toast.makeText(mContext, mContext.getResources().getString(R.string.update_hint_fail), Toast.LENGTH_SHORT)
						.show();
			}

		}
	}
	/**
	 * 应用版本信息实例
	 * @author Administrator
	 *
	 */
	@JsonIgnoreProperties(ignoreUnknown = true)
	private static class AppInfo {

		private String appName;
		private String versionName;
		private int versionCode;
		private String tag;
		private String fileName;

		@JsonProperty("FileName")
		public String getFileName() {
			return fileName;
		}

		@JsonProperty("FileName")
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}

		@JsonProperty("AppName")
		public String getAppName() {
			return appName;
		}

		@JsonProperty("AppName")
		public void setAppName(String appName) {
			this.appName = appName;
		}

		@JsonProperty("VersionName")
		public String getVersionName() {
			return versionName;
		}

		@JsonProperty("VersionName")
		public void setVersionName(String versionName) {
			this.versionName = versionName;
		}

		@JsonProperty("VersionCode")
		public int getVersionCode() {
			return versionCode;
		}

		@JsonProperty("VersionCode")
		public void setVersionCode(int versionCode2) {
			this.versionCode = versionCode2;
		}

		@JsonProperty("Tag")
		public String getTag() {
			return tag;
		}

		@JsonProperty("Tag")
		public void setTag(String tag) {
			this.tag = tag;
		}
	}
	/**
	 * 监听下载完成的Receiver
	 * @author Administrator
	 *
	 */
	public static class DownLoadCompleteReceiver extends BroadcastReceiver {
		private Context mContext;
		@Override
		public void onReceive(Context context, Intent intent) {
			this.mContext = context;
			if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
				long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				//是否是本应用下载的文件
				if(id!=UpdateUtil.mDowndId){
					return;
				}
				//查询文件的目录
				DownloadManager.Query query = new DownloadManager.Query();
				query.setFilterById(id);
				if (UpdateUtil.mDownloadManager == null) {
					return;
				}
				Cursor c = UpdateUtil.mDownloadManager.query(query);
				if (c.moveToFirst()) {
					int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
					// 下载失败也会返回这个广播，所以要判断下是否真的下载成功
					if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
						// 获取下载好的 apk 路径
						String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
						// 提示用户安装
						Log.i(TAG, uriString);
						promptInstall(Uri.parse("file://" + uriString));
					}
				}
			}
		}

		private void promptInstall(Uri data) {
			Intent promptInstall = new Intent(Intent.ACTION_VIEW).setDataAndType(data,
					"application/vnd.android.package-archive");
			// FLAG_ACTIVITY_NEW_TASK 可以保证安装成功时可以正常打开 app
			promptInstall.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			this.mContext.startActivity(promptInstall);
		}
	}
}
