/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.controller;

import java.util.List;

import android.util.Log;

import douzifly.android.qexport.model.ISharedVideoProvider;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzifly.android.qexport.model.web.SharedVideoWebProvider;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoController {
	
	final static String TAG = "SharedVideoController"; 
	
	/** 默认的请求限制次数 */
	final static int DEFAULT_REQUEST_RAND_MAX = 10;
	
	/** 默认的刷新限制时间 */
	final static long DEFAULT_LIMTIED_PERID = 30 * 1000;
	
	int 	mRequestRandCount = 0;
	long 	mLastRequestTime = 0;	
	int 	mMaxRequestRandCount = DEFAULT_REQUEST_RAND_MAX;
	long 	mLimitedPerid = DEFAULT_LIMTIED_PERID;
	
	public void getRandVideos(OnSharedVideoLoadedListener l){
		ISharedVideoProvider provider = new SharedVideoWebProvider();
		provider.getVideos(l);
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
		ISharedVideoProvider provider = new SharedVideoWebProvider();
		SharedVideoInfo v = new SharedVideoInfo();
		v.title = title;
		v.hash = hash;
		provider.updateVideo(v);
	}
	
}
