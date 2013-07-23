/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-9
 */
package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import douzi.android.qexport.R;
import douzifly.android.qexport.controller.QExportManager;
import douzifly.android.qexport.controller.QExportManager.ExportListener;
import douzifly.android.qexport.controller.BaiduExport;
import douzifly.android.qexport.controller.QvodExport;
import douzifly.android.qexport.controller.SharedVideoController;
import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;
import douzifly.android.qexport.utils.UMengHelper;

/**
 * @author Xiaoyuan Lau
 *
 */
public class LocalVideoFragment extends BaseFragment implements 
		OnItemClickListener, ExportListener, OnRefreshListener<ListView>{

	final static String TAG = "LocalVideoFragment";
	
	QExportManager     mQExport;
	ListView           mListView;
	PullToRefreshListView mPullListView;

	LocalVideoAdapter  mLocalAdapter;
//	List<Integer>      mMergeing = new ArrayList<Integer>();
	int				   mMergeingPos = -1;
//	GridProgressBar    mProgress;
	ProgressBar		   mProgress1;
	ProgressBar		   mProgress2;
	View			   mProgressContainer;
	
	boolean            mHasNotifyNoResource = false;
	
	int blue = 0xff0099cc;
	int black = 0xff434343;
	int yellow = 0xfffa5a16;
	
	@Override
	public boolean showRefreshButton() {
		return true;
	}
	
	@Override
	public void onRefreshPressed() {
		scanLocal(true);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Log.d(TAG, "onCreate");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = setupView(inflater);
		scanLocal(true);
		return v;
	}
	
	View setupView(LayoutInflater inflater){
		
		View root = inflater.inflate(R.layout.local_video, null);
		mPullListView = (PullToRefreshListView) root.findViewById(R.id.listResult);		
		mPullListView.setOnRefreshListener(this);
		mListView = mPullListView.getRefreshableView();
//		mListView = (ListView) root.findViewById(R.id.listResult);	
//		mProgress = (GridProgressBar) root.findViewById(R.id.progressBar);
//		mProgress.setNormalColor(yellow);
//		mProgress.setCoverColor(blue);
//		mProgress.setVisibility(View.GONE);
		
		mProgress1 = (ProgressBar) root.findViewById(R.id.progressBar1);
		mProgress2 = (ProgressBar) root.findViewById(R.id.progressBar2);
		mProgressContainer = root.findViewById(R.id.bottom);
		
		mLocalAdapter = new LocalVideoAdapter(getActivity());
		mListView.setAdapter(mLocalAdapter);
		mListView.setOnItemClickListener(this);
		
		return root;
	}
	
	@Override
	public String getTitle() {
		return "我的合体";
	}
	
	private void scanLocal(boolean pullDown){
		if(mQExport == null){
			// 快播
			mQExport = new QExportManager();
			mQExport.addExport(VideoInfo.SOURCE_QVOD, new QvodExport());
			mQExport.addExport(VideoInfo.SOURCE_BAIDU, new BaiduExport());
			// 百度
			mQExport.setExportListener(this);
		}
		showProgressOnActionBar();
		
		if(pullDown) {
			mPullListView.setRefreshing();
		}
		
		mQExport.scan();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// 这是下拉刷新列表的bug，暂时还未找到原因
		// 列表中元素的下标从1开始而不是从0开始
		handleLocalClick(arg2 -1);
	}
	
	private void setMergeingPos(int pos){
		mMergeingPos = pos;
	}
	
	private void resetMergeingPos(){
		mMergeingPos = -1;
	}
	
	private void meger(final VideoInfo v, final String target, int pos) {
//		mMergeing.add(v.postion);
		setMergeingPos(pos);
		SharedVideoController share = new SharedVideoController();
		share.uploadVideo(v.name, v.hash);
		mQExport.merge(v);
		UMengHelper.logMerge(getActivity());
	}
	
	@Override
	public void onScanOk(final List<VideoInfo> videos) {
		Log.d(TAG,"onScanOk");
		Activity activity = getActivity();
		if(activity == null){
		    return;
		}
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				hideProgressOnActionBar();
				List<VideoInfo> orgin = videos;
				List<VideoInfo> copy = null;
				if(orgin != null && orgin.size() > 0){
				  copy = new ArrayList<VideoInfo>(orgin);
				}
				if(copy == null && !mHasNotifyNoResource){
				    Toast.makeText(getActivity(), "好孩子，还没看过快播吧？", Toast.LENGTH_SHORT).show();
				    mHasNotifyNoResource = true;
				}
				mLocalAdapter.setVideos(copy);
				mListView.setAdapter(mLocalAdapter);
				if(mPullListView.isRefreshing()){
					mPullListView.onRefreshComplete();
				}
			}
		});
	}

	@Override
	public void onMergeOk(final VideoInfo v,final boolean sucess) {
//	    mMergeing.remove((Integer)v.postion);
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG, "onMergeOk");
				resetMergeingPos();
				mProgressContainer.setVisibility(View.GONE);
				if(!sucess){
					new AlertDialog.Builder(getActivity()).setTitle("节操没了").setCancelable(false)
					.setMessage("合体:" + v.name + " 失败")
					.setPositiveButton("好吧", null).show();
				}else{
					new AlertDialog.Builder(getActivity()).setTitle("哇，合体成功").setCancelable(false)
					.setMessage(v.name)
					.setPositiveButton("噢啦", null).show();
				}
				
			}
		});
	}
	
	@Override
	public void onMergeProgress(final VideoInfo v,final int progress, final int speed, final int writed ) {
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d(TAG,"updateProgress:" + progress + " " + v.name);
				if(mMergeingPos == -1 || mMergeingPos >= mLocalAdapter.getCount()){
					return;
				}
				
				mLocalAdapter.updateProgress(mMergeingPos, progress, speed, writed);
				if(mProgressContainer.getVisibility() != View.VISIBLE){
					mProgressContainer.setVisibility(View.VISIBLE);
				}
				
				mProgress1.setProgress(progress);
				mProgress2.setProgress(progress - 50);
				
				if(progress == 100){
					mProgressContainer.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private void showDownloadDialog(){
		new AlertDialog.Builder(getActivity()).setTitle("无法合体")
		.setMessage("未下载完的百度资源无法合体，请先把任务下载完")
		.setPositiveButton("好的，知道了", null).show();
	}
	
	/**
	 * @param pos
	 */
	private void handleLocalClick(final int pos) {
		Log.d(TAG, "handleLocalClick pos: " + pos);
		final VideoInfo v = mLocalAdapter.getItem(pos);
		if(v == null){
			Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mMergeingPos > -1){
			Toast.makeText(getActivity(), "忙着呢~~", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String exportFolder;
		try{
		    exportFolder = AppSetting.getExportFolder() + "/" + v.name;
		}catch(Exception e){
		    Toast.makeText(getActivity(), "没有sd卡", Toast.LENGTH_SHORT).show();
		    return;
		}
		
		// 未下载完的百度资源无法合并，提示用户
		if(v.source == VideoInfo.SOURCE_BAIDU && !v.downloadComplete){
			showDownloadDialog();
			return;
		}
		
		final String target = exportFolder;
		if(LocalVideoHelper.isVideoMerged(v.name, v.size)){
			 new AlertDialog.Builder(getActivity()).setTitle("已经合体啦")
				.setMessage("马上播放?")
				.setPositiveButton("播放", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					String type = "video/*";
			        Uri uri = Uri.parse(target);
			        i.setDataAndType(uri, type);
			        try{
			        	startActivity(i);
			        }catch(Exception e){
			        	Toast.makeText(getActivity(), "先装个快播再播放吧",  Toast.LENGTH_SHORT).show();
			        }
				}
				}).setNegativeButton("重新合体", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        meger(v, target, pos);
                    }
                })
				.show();
			
			return;
		}
	    new AlertDialog.Builder(getActivity()).setTitle("保存到:")
	    							.setMessage(target)
	    							.setPositiveButton("合体", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    meger(v, target, pos);
					}
					
	    		}).setNegativeButton("算了", null)
	    		.show();
	}

	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		scanLocal(false);
	}
}
