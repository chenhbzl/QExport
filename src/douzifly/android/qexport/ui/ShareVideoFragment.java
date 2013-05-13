/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-10
 */
package douzifly.android.qexport.ui;

import java.util.HashMap;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import douzi.android.qexport.R;
import douzifly.android.qexport.controller.SharedVideoController;
import douzifly.android.qexport.model.SharedVideoApi.OnSharedVideoLoadedListener;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.ui.SharedVideoAdapter.OnTipOffClickListener;
import douzifly.android.qexport.utils.UMengHelper;
import douzifly.android.utils.TimeUtils;


/**
 * @author Xiaoyuan Lau
 *
 */
public class ShareVideoFragment extends BaseFragment implements 
	OnItemClickListener, 
	OnItemLongClickListener,
	OnTipOffClickListener{
	
	final static String TAG = "ShareVideoFragment";
	
	ListView mListView;
	Button	 mBtnChange;
	SharedVideoController mSharedVideoController = new SharedVideoController();
	boolean  mViewDestoryed = false;
	List<SharedVideoInfo> mVideos;
	
	@Override
	public String getTitle() {
		return "大家的合体";
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View v = setupView(inflater);
		return v;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated");
		if(mViewDestoryed){
			Log.d(TAG, "mViewDestoryed == true reloadData");
			mViewDestoryed = false;
			((SharedVideoAdapter)mListView.getAdapter()).setVideos(mVideos);
		}
	};
	
	View setupView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.share_video, null);
		mListView = (ListView) v.findViewById(R.id.listView);
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mBtnChange = (Button) v.findViewById(R.id.btn_change);
		mListView.setAdapter(new SharedVideoAdapter(getActivity()).setOnTipOffClickListener(this));
		
		mBtnChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(((SharedVideoAdapter)mListView.getAdapter()).isTipOffMode()){
					toggleTipOffMode();
				}else{
					scanSharedVideo();
				}
			}
		});
		return v;
	}
	
	boolean isScanShareding = false;
	private synchronized void scanSharedVideo(){
		Log.d(TAG, "scanSharedVideo");
		if(isScanShareding){
			Log.d(TAG, "scaning return");
			return;
		}
		isScanShareding = true;
		boolean sucess = mSharedVideoController.getRandVideosByLimted(new OnSharedVideoLoadedListener() {
			
			@Override
			public void onVideoLoaded(boolean sucess, List<SharedVideoInfo> videos) {
				isScanShareding = false;
				hideProgressOnActionBar();
				hideCenterProgress();
				if(sucess){
					if(videos == null || videos.size() == 0){
						Toast.makeText(getActivity(), "暂时木有分享", Toast.LENGTH_SHORT).show();
					}else{
						if(mListView == null){
							return;
						}
						SharedVideoAdapter adapter = (SharedVideoAdapter) mListView.getAdapter();
						mVideos = videos;
						if(adapter == null){
							return;
						}
						adapter.setVideos(videos);
					}
				}else{
					Toast.makeText(getActivity(), "貌似网络不给力", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		UMengHelper.logRefreshShare(getActivity(), !sucess);
		
		if(!sucess){
			isScanShareding = false;
			long waitTime = TimeUtils.millsToSeconds(mSharedVideoController.getWaitingTime());
			if(waitTime == 0) waitTime = 1;
			String tip = String.format("刷这么多，要累死我吗? 耐心等待%d秒吧", waitTime);
			Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
		}else{
			showProgressOnActionBar();
			showCenterProgress();
		}
	}
	
	private void handleSharedClick(int pos){
		SharedVideoAdapter adapter = (SharedVideoAdapter) mListView.getAdapter();
		SharedVideoInfo v = adapter.getItem(pos);
		Log.d(TAG, "click v hash:" + v.hash);
		Intent i = new Intent("QvodPlayer.VIDEO_PLAY_ACTION");
		String type = "video/*";
        Uri uri = Uri.parse(v.hash);
        i.setDataAndType(uri, type);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
        	startActivity(i);
        	checkAndCloseTipOffMode();
        	UMengHelper.logPlayShare(getActivity(), v.id);
        }catch(Exception e){
        	Toast.makeText(getActivity(), "先装个快播再播放吧",  Toast.LENGTH_SHORT).show();
        }
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		handleSharedClick(arg2);
	}
	
	boolean firstInto = true;
	@Override
	public void onInto() {
		super.onInto();
		if(firstInto){
			scanSharedVideo();
			firstInto = false;
		}
	}
	
	@Override
	public void onLeave() {
		super.onLeave();
		checkAndCloseTipOffMode();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestory");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mViewDestoryed = true;
		Log.d(TAG, "onDestroyView");
	}
	
	@Override
	public void onRefreshPressed() {
		scanSharedVideo();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
//		SharedVideoAdapter adapter = (SharedVideoAdapter) mListView.getAdapter();
//		SharedVideoInfo v = adapter.getItem(arg2);
//		Toast.makeText(getActivity(), v.toString(), Toast.LENGTH_SHORT).show();
		return true;
	}
	
	@Override
	public void onTipOffPressed() {
		toggleTipOffMode();
	}
	
	private void toggleTipOffMode(){
	    if(mListView == null){
	        return;
	    }
		SharedVideoAdapter adapter = (SharedVideoAdapter) mListView.getAdapter();
		adapter.toggleTipOffMode();
		if(adapter.isTipOffMode()){
			mBtnChange.setText("取消");
		}else{
			mBtnChange.setText("换一批");
		}
	}
	
	boolean isTipOffMode(){
		if(mListView == null) return false;
		SharedVideoAdapter adapter = (SharedVideoAdapter) mListView.getAdapter();
		if(adapter == null) return false;
		return adapter.isTipOffMode();
	}
	
	void checkAndCloseTipOffMode(){
		if(isTipOffMode()){
			toggleTipOffMode();
		}
	}

	@Override
	public void onTipOffClicked(SharedVideoInfo v) {
		Log.d(TAG, "onTipOffClicked:" + v);
		mSharedVideoController.logTipOff(v.id);
		toggleTipOffMode();
		UMengHelper.logTipOff(getActivity(), v.id);
		Toast.makeText(getActivity(), "举报已提交", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public boolean showTipOffButton() {
		return true;
	}
	
	
	ProgressBar mPbCenter;
	void showCenterProgress(){
	    try{
	        if(mPbCenter == null){
	            mPbCenter = (ProgressBar)getActivity().getLayoutInflater().inflate(R.layout.progress_yellow, null);
	        }
	        FrameLayout decor = (FrameLayout)getActivity().getWindow().getDecorView();
	        decor.addView(mPbCenter, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
	    }catch(Exception e){
	        
	    }
	}
	
	void hideCenterProgress(){
	    try{
	        FrameLayout decor = (FrameLayout)getActivity().getWindow().getDecorView();
	        decor.removeView(mPbCenter);
	    }catch(Exception e){
	        
	    }
	}
}
