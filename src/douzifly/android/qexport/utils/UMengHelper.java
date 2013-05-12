package douzifly.android.qexport.utils;

import java.util.HashMap;

import android.content.Context;
import android.util.Log;

import com.umeng.analytics.MobclickAgent;

public class UMengHelper {
	
	final static String TAG = "UMengHelper";

	public static final String EVENT_TIP_OFF = "tipOff";
	public static final String EVENT_PLAY_SHARE = "playShare";
	public static final String EVENT_REFRESH_SHARE = "refreshShare";
	public static final String EVENT_COLLECTION = "collection";
	public static final String EVENT_MERGE = "merge";
	
	public static void logTipOff(Context ctx, int videoId){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("videoId", "" + videoId);
		MobclickAgent.onEvent(ctx, UMengHelper.EVENT_TIP_OFF, map);
	}
	
	public static void logPlayShare(Context ctx, int videoId){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("videoId", "" + videoId);
		MobclickAgent.onEvent(ctx, UMengHelper.EVENT_PLAY_SHARE, map);
	}
	
	public static void logRefreshShare(Context ctx, boolean isTooMany){
		Log.d(TAG, "logRereshShare");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("isTooMany", "" + isTooMany);
		MobclickAgent.onEvent(ctx, UMengHelper.EVENT_REFRESH_SHARE, map);
	}
	
	public static void logCollection(Context ctx, int videoId){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("videoId", "" + videoId);
		MobclickAgent.onEvent(ctx, UMengHelper.EVENT_COLLECTION, map);
	}
	
	public static void logMerge(Context ctx){
		MobclickAgent.onEvent(ctx, UMengHelper.EVENT_MERGE);
	}
	
}
