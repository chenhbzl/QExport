package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Process;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.viewpagerindicator.TabPageIndicator;

import douzi.android.qexport.R;

public class MainActivity extends SherlockFragmentActivity 
       implements IActivity, OnClickListener {
	
	static String 			TAG = "MainActivity";
	
	TabPageIndicator	mIndicator;
	ViewPager			mPager;
	
	final static int REFRESH_ID = 101;
	final static int ABOUT_ID = 102;
	BaseFragment 		mCurrentFragment;
	
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
		mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				BaseFragment fragment = mFragments.get(arg0);
				if(fragment == mCurrentFragment){
					return;
				}
				mCurrentFragment.onLeave();
				fragment.onInto();
				mCurrentFragment = fragment;
				updatePageState(mCurrentFragment);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
		
		ActionBar bar = getSupportActionBar();
		bar.setTitle("快播合体助手");
		bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		bar.setCustomView(R.layout.action_bar_title);
		initFragments();
	}
	
	private void initFragments(){
		MainPagerAdapter adapter = (MainPagerAdapter) mPager.getAdapter();
		mFragments.add(new LocalVideoFragment().setIActivity(this));
		mFragments.add(new ShareVideoFragment().setIActivity(this));
		mFragments.add(new LocalVideoFragment().setIActivity(this));
		mFragments.add(new LocalVideoFragment().setIActivity(this));
		mFragments.add(new LocalVideoFragment().setIActivity(this));
		adapter.setFragments(mFragments);
		mIndicator.setViewPager(mPager);
		mCurrentFragment = mFragments.get(0);
	}
	
	void updatePageState(BaseFragment current){
		invalidateOptionsMenu();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean showRefresh = mCurrentFragment.showRefreshButton();
		menu.add(0, REFRESH_ID, 0, "刷新").setIcon(R.drawable.ic_refresh).setVisible(showRefresh).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(0, ABOUT_ID, 0, "关于");
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean showRefresh = mCurrentFragment.showRefreshButton();
		menu.getItem(0).setVisible(showRefresh);
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        Toast.makeText(this, "douzifly@gmail.com", Toast.LENGTH_SHORT).show();
	    }else if(item.getItemId() == REFRESH_ID){
	    	if(mCurrentFragment != null){
	    		mCurrentFragment.onRefreshPressed();
	    	}
	    }else if(item.getItemId() == ABOUT_ID){
	    	Toast.makeText(MainActivity.this, "Dev:Leo Design:Jack", Toast.LENGTH_SHORT).show();
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		default:
			break;
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
	public void showProgressOnActionBar() {
		setProgressBarIndeterminateVisibility(true);
	}

	@Override
	public void hideProgressOnActionBar() {
		setProgressBarIndeterminateVisibility(false);
	}

}
