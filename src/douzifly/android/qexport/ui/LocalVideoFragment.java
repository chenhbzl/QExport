/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-9
 */
package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import douzifly.android.qexport.controller.QExport;
import douzifly.android.qexport.controller.SharedVideoController;
import douzifly.android.qexport.controller.QExport.ExportListener;
import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.utils.UMengHelper;

/**
 * @author Xiaoyuan Lau
 *
 */
public class LocalVideoFragment extends BaseFragment implements OnItemClickListener, ExportListener{

	final static String TAG = "LocalVideoFragment";
	
	QExport            mQExport;
	ListView           mListView;
	String             cacheFolder = LocalVideoHelper.P2P_CACHE_FOLDER;;
	String             exportFolder = LocalVideoHelper.EXPORT_FOLDER;
	LocalVideoAdapter  mLocalAdapter;
	List<Integer>      mMergeing = new ArrayList<Integer>();
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
		scanLocal();
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
		scanLocal();
		return v;
	}
	
	View setupView(LayoutInflater inflater){
		
		View root = inflater.inflate(R.layout.local_video, null);
				
		mListView = (ListView) root.findViewById(R.id.listResult);
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
	
	private void scanLocal(){
		if(mQExport == null){
			mQExport = new QExport();
			mQExport.setExportListener(this);
		}
		showProgressOnActionBar();
		String folder = Environment.getExternalStorageDirectory() + "/" + cacheFolder;
		mQExport.scan(folder);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		handleLocalClick(arg2);
	}
	
	private void meger(final VideoInfo v, final String target) {
		mMergeing.add(v.postion);
		SharedVideoController share = new SharedVideoController();
		share.uploadVideo(v.name, v.hash);
		mQExport.merge(v, target);
		UMengHelper.logMerge(getActivity());
	}
	
	@Override
	public void onScanOk() {
		Log.d(TAG,"onScanOk");
		Activity activity = getActivity();
		if(activity == null){
		    return;
		}
		activity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				hideProgressOnActionBar();
				List<VideoInfo> orgin = mQExport.getVideos();
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
			}
		});
	}

	@Override
	public void onMergeOk(final VideoInfo v,final boolean sucess) {
	    mMergeing.remove((Integer)v.postion);
		getActivity().runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
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
				Log.d("debug","updateProgress:" + progress + " " + v.name);
				mLocalAdapter.updateProgress(v.postion, progress, speed, writed);
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
	
	/**
	 * @param pos
	 */
	private void handleLocalClick(int pos) {
		final VideoInfo v = mLocalAdapter.getItem(pos);
		if(v == null){
			Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mMergeing.size() > 0){
			Toast.makeText(getActivity(), "忙着呢~~", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final String target = Environment.getExternalStorageDirectory() + "/" + exportFolder + "/" + v.name;
	    String msg = target;
		if(LocalVideoHelper.isVideoMerged(v.name, v.size, exportFolder)){
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
                        meger(v, target);
                    }
                })
				.show();
			
			return;
		}
	    new AlertDialog.Builder(getActivity()).setTitle("保存到:")
	    							.setMessage(msg)
	    							.setPositiveButton("合体", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    meger(v, target);
					}
					
	    		}).setNegativeButton("算了", null)
	    		.show();
	}
}
