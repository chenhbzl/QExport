/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-10
 */
package douzifly.android.qexport.ui;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import douzi.android.qexport.R;
import douzifly.android.qexport.controller.SharedVideoController;
import douzifly.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzifly.android.qexport.model.SharedVideoInfo;


/**
 * @author Xiaoyuan Lau
 *
 */
public class ShareVideoFragment extends BaseFragment implements OnItemClickListener{
	
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
		mBtnChange = (Button) v.findViewById(R.id.btn_change);
		mListView.setAdapter(new SharedVideoAdapter(getActivity()));
		
		mBtnChange.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				scanSharedVideo();
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
		showProgressOnActionBar();
		isScanShareding = true;
		mSharedVideoController.getRandVideos(new OnSharedVideoLoadedListener() {
			
			@Override
			public void onVideoLoaded(boolean sucess, List<SharedVideoInfo> videos) {
				isScanShareding = false;
				hideProgressOnActionBar();
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
}
