package douzi.android.qexport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

import douzi.android.qexport.QExport.ExportListener;
import douzi.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzi.android.qexport.model.SharedVideoInfo;
import douzifly.android.uilib.GridProgressBar;

public class MainActivity extends SherlockActivity 
       implements OnClickListener,ExportListener,OnItemClickListener, OnNavigationListener, TabListener {
	
	QExport            mQExport;
	Button             mScanButton;
	ListView           mListView;
	String             cacheFolder = "p2pcache";
	String             exportFolder = "p2pMerged";
	static             String TAG = "MainActivity";
	LocalVideoAdapter  mLocalAdapter;
	List<Integer>      mMergeing = new ArrayList<Integer>();
	GridProgressBar    mProgress;
	
	String[]		   mNavigations = new String[]{"我的合体", "大家的合体"};
	
	int				   mCurNaviPos = 0;
	
	SharedVideoAdapter	mSharedVideoAdapter;
	
	SharedVideoController mSharedVideoController = new SharedVideoController();
	
	final static int REFRESH_ID = 101;
	final static int ABOUT_ID = 102;
	
	int blue = 0xff0099cc;
	int black = 0xff434343;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setupView();
		scanLocal();
	}
	
	private void setupView(){
		mScanButton = (Button) findViewById(R.id.btnScan);
		mListView = (ListView) findViewById(R.id.listResult);
		mProgress = (GridProgressBar) findViewById(R.id.progressBar);
		mProgress.setNormalColor(black);
		mProgress.setVisibility(View.GONE);
		
		mLocalAdapter = new LocalVideoAdapter(this);
		mListView.setAdapter(mLocalAdapter);
		
		mScanButton.setOnClickListener(this);
		mListView.setOnItemClickListener(this);
		setProgressBarIndeterminateVisibility(false);
		
		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(false);
		bar.setTitle(mNavigations[0]);
	
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
//		bar.setLogo(R.drawable.ic_launcher);
//		bar.setDisplayShowHomeEnabled(false);
//		bar.setDisplayUseLogoEnabled(true);
		mSharedVideoAdapter = new SharedVideoAdapter(this);
		
		
		// set navigation mode
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		//bar.setListNavigationCallbacks(new ArrayAdapter<String>(this, R.layout.navigation_text, mNavigations), this);
		bar.addTab(bar.newTab().setText(mNavigations[0]).setTabListener(this), true);
		bar.addTab(bar.newTab().setText(mNavigations[1]).setTabListener(this));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, REFRESH_ID, 0, "刷新").setIcon(R.drawable.ic_refresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, ABOUT_ID, 0, "关于");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        Toast.makeText(this, "douzifly@gmail.com", Toast.LENGTH_SHORT).show();
	    }else if(item.getItemId() == REFRESH_ID){
	    	if(mCurNaviPos == 0)
	    		scanLocal();
	    	else
	    		scanSharedVideo();
	    }else if(item.getItemId() == ABOUT_ID){
	    	Toast.makeText(MainActivity.this, "Dev:Leo Design:Jack", Toast.LENGTH_SHORT).show();
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	private void scanLocal(){
		if(mQExport == null){
			mQExport = new QExport();
			mQExport.setExportListener(this);
		}
		setProgressBarIndeterminateVisibility(true);
		String folder = Environment.getExternalStorageDirectory() + "/" + cacheFolder;
		mScanButton.setEnabled(false);
		mQExport.scan(folder);
	}
	
	private boolean isScanShareding = false;
	private void scanSharedVideo(){
		if(isScanShareding){
			return;
		}
		isScanShareding = true;
		mListView.setAdapter(null);
		mProgress.setVisibility(View.GONE);
		setProgressBarIndeterminateVisibility(true);
		mSharedVideoController.getRandVideos(new OnSharedVideoLoadedListener() {
			
			@Override
			public void onVideoLoaded(boolean sucess, List<SharedVideoInfo> videos) {
				isScanShareding = false;
				if(mCurNaviPos != 1){
					return;
				}
				setProgressBarIndeterminateVisibility(false);
				if(sucess){
					if(videos == null || videos.size() == 0){
						Toast.makeText(MainActivity.this, "暂时木有分享", Toast.LENGTH_SHORT).show();
						mListView.setAdapter(null);
					}else{
						mSharedVideoAdapter.setVideos(videos);
						mListView.setAdapter(mSharedVideoAdapter);
					}
				}else{
					mListView.setAdapter(null);
					Toast.makeText(MainActivity.this, "貌似网络不给力", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnScan:
			scanLocal();
			break;

		default:
			break;
		}
	}

	@Override
	public void onScanOk() {
		Log.d(TAG,"onScanOk");
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(false);
				mScanButton.setEnabled(true);
				List<VideoInfo> orgin = mQExport.getVideos();
				List<VideoInfo> copy = null;
				if(orgin != null && orgin.size() > 0){
				  copy = new ArrayList<VideoInfo>(orgin);
				}
				if(copy == null){
				    Toast.makeText(MainActivity.this, "好孩子，还没看过快播吧？", Toast.LENGTH_SHORT).show();
				}
				mLocalAdapter.setVideos(copy);
				mListView.setAdapter(mLocalAdapter);
			}
		});
	}

	@Override
	public void onMergeOk(final VideoInfo v,final boolean sucess) {
	    mMergeing.remove((Integer)v.postion);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(!sucess){
					new AlertDialog.Builder(MainActivity.this).setTitle("节操没了").setCancelable(false)
					.setMessage("合体:" + v.name + " 失败")
					.setPositiveButton("好吧", null).show();
				}else{
					new AlertDialog.Builder(MainActivity.this).setTitle("哇靠，合体成功").setCancelable(false)
					.setMessage(v.name)
					.setPositiveButton("噢啦", null).show();
				}
				
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		if(mCurNaviPos == 0){
			handleLocalClick(pos);
		}else if(mCurNaviPos == 1){
			handleSharedClick(pos);
		}
	}
	
	private void handleSharedClick(int pos){
		SharedVideoInfo v = mSharedVideoAdapter.getItem(pos);
		Log.d(TAG, "click v hash:" + v.hash);
		Intent i = new Intent("QvodPlayer.VIDEO_PLAY_ACTION");
		String type = "video/*";
        Uri uri = Uri.parse(v.hash);
        i.setDataAndType(uri, type);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try{
        	startActivity(i);
        }catch(Exception e){
        	Toast.makeText(getApplicationContext(), "先装个快播再播放吧",  Toast.LENGTH_SHORT).show();
        }
		
	}

	/**
	 * @param pos
	 */
	private void handleLocalClick(int pos) {
		final VideoInfo v = mLocalAdapter.getItem(pos);
		if(v == null){
			Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mMergeing.size() > 0){
			Toast.makeText(this, "忙着呢~~", Toast.LENGTH_SHORT).show();
			return;
		}
		
		final String target = Environment.getExternalStorageDirectory() + "/" + exportFolder + "/" + v.name;
	    String msg = target;
		File f = new File(target);
		if(f.exists() && f.length() == v.size){
			 new AlertDialog.Builder(this).setTitle("已经合体啦")
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
			        	Toast.makeText(getApplicationContext(), "先装个快播再播放吧",  Toast.LENGTH_SHORT).show();
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
	    new AlertDialog.Builder(this).setTitle("保存到:")
	    							.setMessage(msg)
	    							.setPositiveButton("合体", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    meger(v, target);
					}
					
	    		}).setNegativeButton("算了", null)
	    		.show();
	}
	
	private void meger(final VideoInfo v, final String target) {
		mMergeing.add(v.postion);
		mQExport.merge(v, target);
		mSharedVideoController.uploadVideo(v.name, v.hash);
	}

	@Override
	public void onMergeProgress(final VideoInfo v,final int progress, final int speed, final int writed ) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("debug","updateProgress:" + progress + " " + v.name);
				mLocalAdapter.updateProgress(v.postion, progress, speed, writed);
				if(mProgress.getVisibility() != View.VISIBLE && mCurNaviPos == 0){
					mProgress.setVisibility(View.VISIBLE);
				}
				mProgress.setProgress(progress);
				if(progress == 100){
					mProgress.setVisibility(View.GONE);
				}
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle("退出")
                    .setMessage("确定吗?")
                    .setPositiveButton("好",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    finish();
                                }
                            }).setNegativeButton("先不", null ).show();
            return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    android.os.Process.killProcess(Process.myPid());
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		if(itemPosition == mCurNaviPos){
			return true;
		}
		mCurNaviPos = itemPosition;
		if (itemPosition == 0){
			// 加载本地视频
			scanLocal();
		}else if(itemPosition == 1){
			// 加载共享视频
			scanSharedVideo();
		}
		return true;
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		int itemPosition = tab.getPosition();
		if(itemPosition == mCurNaviPos){
			return;
		}
		getSupportActionBar().setTitle(mNavigations[itemPosition]);
		mCurNaviPos = itemPosition;
		if (itemPosition == 0){
			// 加载本地视频
			scanLocal();
		}else if(itemPosition == 1){
			// 加载共享视频
			scanSharedVideo();
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

}
