/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.controller;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
	final static long DEFAULT_LIMTIED_PERID = 10 * 1000;
	
	int 	mRequestRandCount = 0;
	long 	mLastRequestTime = 0;	
	int 	mMaxRequestRandCount = DEFAULT_REQUEST_RAND_MAX;
	long 	mLimitedPerid = DEFAULT_LIMTIED_PERID;
	OnWaitListener mWaitListener;
	
	public void getRandVideos(OnSharedVideoLoadedListener l){
		SharedVideoApi.getVideos(l);
	}
	
	public void setWaitListener(OnWaitListener l) {
	    mWaitListener = l;
	}
	
	private boolean mWaiting = false;
	private void startWaitCount() {
	    mWaiting = true;
	    final Timer t = new Timer();
	    t.scheduleAtFixedRate(new TimerTask() {
            
            @Override
            public void run() {
                if(System.currentTimeMillis() - mLastRequestTime < DEFAULT_LIMTIED_PERID){
                    if(mWaitListener != null) {
                        mWaitListener.onWait();
                    }
                }else {
                    // reset
                    mRequestRandCount = 0;
                    mWaiting = false;
                    if(mWaitListener != null) {
                        mWaitListener.onEndWait();
                    }
                    t.cancel();
                }
            }
        }, 0, 1000);
	}
	
	/**
	 * 有限制的获取随机影片
	 * @param l
	 * @return
	 */
	public boolean getRandVideosByLimted(final OnSharedVideoLoadedListener l){
		Log.d(TAG, "getRandVideosByLimted mRequestRandCount:" + mRequestRandCount 
		        + " max:" +DEFAULT_REQUEST_RAND_MAX + " isWaiting:" + mWaiting);
		if(mWaiting) return false;
		if(mRequestRandCount >= DEFAULT_REQUEST_RAND_MAX){
		    startWaitCount();
		    return false;
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
	
	public static interface OnWaitListener {
	    void onWait();
	    void onEndWait();
	}
}
