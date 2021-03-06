/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-10
 */
package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import net.youmi.android.banner.AdSize;
import net.youmi.android.banner.AdView;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import douzi.android.qexport.R;
import douzifly.android.qexport.controller.SharedVideoController;
import douzifly.android.qexport.controller.SharedVideoController.OnWaitListener;
import douzifly.android.qexport.model.SharedVideoApi.OnSharedVideoLoadedListener;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.db.CacheManager;
import douzifly.android.qexport.model.db.VideoCache;
import douzifly.android.qexport.settings.ShareSetting;
import douzifly.android.qexport.ui.AnnouncementFragment.OnAnnouncementChooseListner;
import douzifly.android.qexport.ui.SharedVideoAdapter.OnTipOffClickListener;
import douzifly.android.qexport.utils.TimeUtils;
import douzifly.android.qexport.utils.UMengHelper;


/**
 * @author Xiaoyuan Lau
 *
 */
public class ShareVideoFragment extends BaseFragment implements 
    OnItemClickListener, 
	OnItemLongClickListener,
	OnTipOffClickListener,
	OnRefreshListener<ListView>, 
	OnClickListener,
	OnWaitListener{
	
	final static String TAG = "ShareVideoFragment";
	
	ListView mListView;
	PullToRefreshListView mPullListView;
	SharedVideoAdapter mAdapter;
//	Button	 mBtnChange;
	SharedVideoController mSharedVideoController = new SharedVideoController();
	boolean  mViewDestoryed = false;
	List<SharedVideoInfo> mVideos;
//	View mBottomContainer;
	ViewGroup adLayout;
	ImageButton mBtnCloseAd;
	AdView mAdView;
	// whether is showing a dialog that notice too many refresh
	boolean mIsShowMoreTip = false;
	String mTipRefreshing = "正在寻找下一批";
    String mTipPull = "下拉换一批";
    String mTipRelease = "放开换一批";
    
    String mTipWaiting = "点击广告，支持开发者";
	
	
	@Override
	public String getTitle() {
		return "大家的合体";
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View v = setupView(inflater);
		mSharedVideoController.setWaitListener(this);
		return v;
	}
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
		Log.d(TAG, "onViewCreated");
		if(mViewDestoryed){
			Log.d(TAG, "mViewDestoryed == true reloadData");
			mViewDestoryed = false;
			mAdapter.setVideos(mVideos);
		}
	};
	
	View setupView(LayoutInflater inflater){
		View v = inflater.inflate(R.layout.share_video, null);
		mPullListView = (PullToRefreshListView) v.findViewById(R.id.listView);
		mBtnCloseAd = (ImageButton) v.findViewById(R.id.btnCloseAd);
		mPullListView.setOnRefreshListener(this);
		mListView = mPullListView.getRefreshableView();
		mListView.setOnItemClickListener(this);
		mListView.setOnItemLongClickListener(this);
		mAdapter = new SharedVideoAdapter(getActivity()).setOnTipOffClickListener(this);
		mListView.setAdapter(mAdapter);
		
		// Pull Texts
		LoadingLayout loading = mPullListView.getHeaderLayout();
		
		loading.setRefreshingLabel(mTipRefreshing);
		loading.setPullLabel(mTipPull);
		loading.setReleaseLabel(mTipRelease);
		
		//实例化广告条
		mAdView = new AdView(getActivity(), AdSize.SIZE_320x50);
	    //获取要嵌入广告条的布局
	    adLayout =(ViewGroup)v.findViewById(R.id.adLayout);
	    //将广告条加入到布局中
	    adLayout.addView(mAdView);
		setAdLayoutVisibility(false);
		
		mBtnCloseAd.setOnClickListener(this);
		
		adLayout.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(TAG, "touch");
                return false;
            }
        });
		
		return v;
	}
	
	void setAdLayoutVisibility(boolean visible){
	    if(adLayout == null) return;
	    adLayout.setVisibility(visible ? View.VISIBLE : View.GONE);
//	    mBtnCloseAd.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	boolean isScanShareding = false;
	@SuppressLint("DefaultLocale")
    private synchronized void scanSharedVideo(boolean pullDown){
	    
	    if(mIsShowMoreTip) {
	        return;
	    }
	    
	    final Context ctx = getActivity();
	    if(ctx == null){
	        return;
	    }
		Log.d(TAG, "scanSharedVideo");
		boolean agree = ShareSetting.isAgreeShare(ctx);
		if(!agree){
		    AnnouncementFragment.showAnnouncement(getFragmentManager(), new OnAnnouncementChooseListner() {
                
                @Override
                public void onAnnouncementClosed(boolean agree) {
                    ShareSetting.setAgreeShare(ctx, agree);
                    if(!agree){
                        setAdLayoutVisibility(false);
                        return;
                    }else{
                        scanSharedVideo(false);
                    }
                }
            });
		    return;
		}
		
		if(pullDown) { 
			mPullListView.setRefreshing();
		}
		
		setAdLayoutVisibility(true);
		
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
						SharedVideoAdapter adapter = mAdapter;
						mVideos = videos;
						if(adapter == null){
							return;
						}
						adapter.setVideos(videos);
					}
				}else{
					Toast.makeText(getActivity(), "貌似网络不给力", Toast.LENGTH_SHORT).show();
				}
				
				mPullListView.onRefreshComplete();
			}
		});
		
		UMengHelper.logRefreshShare(getActivity(), !sucess);
		
		if(!sucess){
			isScanShareding = false;
//			long waitTime = TimeUtils.millsToSeconds(mSharedVideoController.getWaitingTime());
//			if(waitTime == 0) waitTime = 1;
//			String tip = String.format("刷这么多，要累死我吗? 耐心等待%d秒吧", waitTime);
//			Toast.makeText(getActivity(), tip, Toast.LENGTH_SHORT).show();
		}else{
			showProgressOnActionBar();
			showCenterProgress();
		}
	}
	
	
	
	
	private void handleSharedClick(int pos){
		SharedVideoAdapter adapter = mAdapter;
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
			scanSharedVideo(true);
			firstInto = false;
		}
//		showBottomContainer();
	}
	
	
	
	
	@Override
	public void onLeave() {
		super.onLeave();
		checkAndCloseTipOffMode();
//		hideBottomContainer();
	}
	
//	void hideBottomContainer(){
//	    if(mBottomContainer == null) return;
//	    mBottomContainer.setVisibility(View.INVISIBLE);
//	    mBottomContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_to_bottom));
//	}
//	
//	void showBottomContainer(){
//	    mBottomContainer.setVisibility(View.VISIBLE);
//	    mBottomContainer.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_from_bottom));
//	}
	
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
		scanSharedVideo(true);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
        try{
            CacheManager<SharedVideoInfo> cache = new CacheManager<SharedVideoInfo>(new VideoCache());
            List<SharedVideoInfo> videos = new ArrayList<SharedVideoInfo>();
            videos.add(mVideos.get(arg2));
            cache.save(getActivity(), videos);
            Toast.makeText(getActivity(), "收藏成功", Toast.LENGTH_SHORT).show();
            UMengHelper.logCollection(getActivity(), mVideos.get(arg2).id);
        }catch(Exception e){
            Log.d(TAG, "store exp:" + e.getMessage());
        }

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
		SharedVideoAdapter adapter = mAdapter;
		adapter.toggleTipOffMode();
	}
	
	boolean isTipOffMode(){
		if(mListView == null) return false;
		SharedVideoAdapter adapter = mAdapter;
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
//	    try{
//	        if(mPbCenter == null){
//	            mPbCenter = (ProgressBar)getActivity().getLayoutInflater().inflate(R.layout.progress_yellow, null);
//	        }
//	        FrameLayout decor = (FrameLayout)getActivity().getWindow().getDecorView();
//	        decor.addView(mPbCenter, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//	    }catch(Exception e){
//	        
//	    }
	}
	
	void hideCenterProgress(){
//	    try{
//	        FrameLayout decor = (FrameLayout)getActivity().getWindow().getDecorView();
//	        decor.removeView(mPbCenter);
//	    }catch(Exception e){
//	        
//	    }
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
//		String label = getString(R.string.refreshing);
//		refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
		scanSharedVideo(false);
	}

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onclick v:" + v);
        if(v.getId() == R.id.btnCloseAd) {
            Toast.makeText(getActivity(), "ad close click", Toast.LENGTH_SHORT).show();
            
        }
    }

    @Override
    public void onWait() {
        mIsShowMoreTip = true;
        getActivity().runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                long waitTime = TimeUtils.millsToSeconds(mSharedVideoController.getWaitingTime());
                Log.d(TAG, "onWait :" + waitTime);
                if(waitTime == 0) waitTime = 1;
                LoadingLayout loading = mPullListView.getHeaderLayout();
                loading.setRefrehingProgressVisible(false);
                loading.setRefreshingLabel(mTipWaiting + "(等待" + waitTime + " 秒)");
            }
        });
    }

    @Override
    public void onEndWait() {
        Log.d(TAG, "onEndWait");
        mIsShowMoreTip = false;
        getActivity().runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                try{
                    LoadingLayout loading = mPullListView.getHeaderLayout();
                    loading.setRefreshingLabel(mTipRefreshing);
                    loading.setRefrehingProgressVisible(true);
                    mPullListView.onRefreshComplete();
                }catch(Exception e) {
                    
                }
            }
        });
    }
}
