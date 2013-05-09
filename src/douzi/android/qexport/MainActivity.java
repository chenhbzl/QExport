package douzi.android.qexport;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

import douzi.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzi.android.qexport.model.SharedVideoInfo;

public class MainActivity extends SherlockFragmentActivity 
       implements OnClickListener, OnNavigationListener, TabListener {
	
	static String 			TAG = "MainActivity";
	String[]		   		mNavigations = new String[]{"我的合体", "大家的合体"};
	int				   		mCurNaviPos = 0;
	SharedVideoAdapter		mSharedVideoAdapter;
	SharedVideoController	mSharedVideoController = new SharedVideoController();
	
	TabPageIndicator	mIndicator;
	ViewPager			mPager;
	
	final static int REFRESH_ID = 101;
	final static int ABOUT_ID = 102;
	
	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setupView();
	}
	
	private void setupView(){
		setProgressBarIndeterminateVisibility(false);
		
		mIndicator = (TabPageIndicator) findViewById(R.id.indicator);
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
		
		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(false);
		bar.setTitle(mNavigations[0]);
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE|ActionBar.DISPLAY_SHOW_HOME);
		mSharedVideoAdapter = new SharedVideoAdapter(this);
		// set navigation mode
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//bar.setListNavigationCallbacks(new ArrayAdapter<String>(this, R.layout.navigation_text, mNavigations), this);
//		bar.addTab(bar.newTab().setText(mNavigations[0]).setTabListener(this), true);
//		bar.addTab(bar.newTab().setText(mNavigations[1]).setTabListener(this));
		
		initFragments();
	}
	
	private void initFragments(){
		MainPagerAdapter adapter = (MainPagerAdapter) mPager.getAdapter();
		mFragments.add(new LocalVideoFragment());
		mFragments.add(new LocalVideoFragment());
		adapter.setFragments(mFragments);
		mIndicator.setViewPager(mPager);
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
	    }else if(item.getItemId() == ABOUT_ID){
	    	Toast.makeText(MainActivity.this, "Dev:Leo Design:Jack", Toast.LENGTH_SHORT).show();
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	
	private boolean isScanShareding = false;
	private void scanSharedVideo(){
		if(isScanShareding){
			return;
		}
		isScanShareding = true;
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
					}else{
						mSharedVideoAdapter.setVideos(videos);
					}
				}else{
					Toast.makeText(MainActivity.this, "貌似网络不给力", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
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
