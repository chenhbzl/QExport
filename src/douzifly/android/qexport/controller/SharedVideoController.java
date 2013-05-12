/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.controller;

import java.util.List;

import android.util.Log;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.SharedVideoApi;
import douzifly.android.qexport.model.SharedVideoApi.OnSharedVideoLoadedListener;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoController {
	
	final static String TAG = "SharedVideoController"; 
	
	/** 默认的请求限制次数 */
	final static int DEFAULT_REQUEST_RAND_MAX = 20;
	
	/** 默认的刷新限制时间 */
	final static long DEFAULT_LIMTIED_PERID = 30 * 1000;
	
	int 	mRequestRandCount = 0;
	long 	mLastRequestTime = 0;	
	int 	mMaxRequestRandCount = DEFAULT_REQUEST_RAND_MAX;
	long 	mLimitedPerid = DEFAULT_LIMTIED_PERID;
	
	public void getRandVideos(OnSharedVideoLoadedListener l){
		SharedVideoApi.getVideos(l);
	}
	
	/**
	 * 有限制的获取随机影片
	 * @param l
	 * @return
	 */
	public boolean getRandVideosByLimted(final OnSharedVideoLoadedListener l){
		Log.d(TAG, "getRandVideosByLimted mRequestRandCount:" + mRequestRandCount + " max:" );
		if(mRequestRandCount >= DEFAULT_REQUEST_RAND_MAX){
			Log.d(TAG, "request times out of limited");
			if(System.currentTimeMillis() - mLastRequestTime < DEFAULT_LIMTIED_PERID){
				Log.d(TAG, "limited period not reached, still waiting");
				return false;
			}else{
				// reset
				mRequestRandCount = 0;
			}
		}
		
		getRandVideos(new OnSharedVideoLoadedListener() {
			
			@Override
			public void onVideoLoaded(boolean sucess, List<SharedVideoInfo> videos) {
				if(sucess) {
					mRequestRandCount ++;
					mLastRequestTime = System.currentTimeMillis();
				}
				l.onVideoLoaded(sucess, videos);
			}
		});
		return true;
	}
	
	public long getWaitingTime(){
		if(mLastRequestTime == 0) return 0;
		return mLimitedPerid - (System.currentTimeMillis() - mLastRequestTime);
	}
	
	public void uploadVideo(String title, String hash){
		if(title == null || hash == null){
			return;
		}
		SharedVideoInfo v = new SharedVideoInfo();
		v.title = title;
		v.hash = hash;
		SharedVideoApi.updateVideo(v);
	}
	
	/**
	 * 记录举报
	 */
	public void logTipOff(int id){
		SharedVideoApi.increaseFiledValue(SharedVideoApi.FILED_TIP_OFF, id);
	}
	
	/**
	 * 记录收藏
	 */
	public void logCollection(int id){
		SharedVideoApi.increaseFiledValue(SharedVideoApi.FILED_COLLECTION, id);
	}
	
	/**
	 * 记录播放
	 */
	public void logPlayCount(int id){
		SharedVideoApi.increaseFiledValue(SharedVideoApi.FILED_PLAY_COUNT, id);
	}
	
}
