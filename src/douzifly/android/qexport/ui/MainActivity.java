package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.umeng.analytics.MobclickAgent;
import com.viewpagerindicator.TabPageIndicator;

import douzi.android.qexport.R;
import douzifly.android.qexport.settings.ShareSetting;
import douzifly.android.qexport.ui.AnnouncementFragment.OnAnnouncementChooseListner;
import douzifly.android.qexport.ui.toolbox.FaveFragment;
import douzifly.android.qexport.ui.toolbox.ToolBoxContentFragment;
import douzifly.android.qexport.ui.toolbox.TransportFragment;
import douzifly.android.qexport.ui.toolbox.ToolBoxContentFragment.OnModuleClickListener;
import douzifly.android.qexport.ui.toolbox.ToolBoxFragment;
import douzifly.android.qexport.utils.YoumiHelper;

public class MainActivity extends SherlockFragmentActivity 
       implements IActivity, OnClickListener, OnModuleClickListener {
	
	static String 			TAG = "MainActivity";
	
	TabPageIndicator   mIndicator;
	ViewPager          mPager;
	ImageButton        mBtnRefresh;
	Handler            mHandler = new Handler();
	View               mBtnTipOffContainer;
	View               mRefreshContainer;
	View               mBtnToolContainer;
	ImageButton        mBtnTool;
	TextView		   mTxtActionBarTitle;
	View               mContentContianer;
	
	FaveFragment       mFaveFragment;
	TransportFragment  mTranFragment;
	View               mBtnBack;
	
	
	final static int REFRESH_ID = 101;
	final static int ABOUT_ID = 102;
	BaseFragment 		mCurrentFragment;
	BaseFragment        mCurrentToolFragment;
	
	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setupView();
		YoumiHelper.init(this);
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
				updatePageState();
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
		bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_action_bar));
		View customActionView = getLayoutInflater().inflate(R.layout.actionbar_title, null);
		bar.setCustomView(customActionView);
		mBtnRefresh = (ImageButton) customActionView.findViewById(R.id.btn_refresh);
		mRefreshContainer = customActionView.findViewById(R.id.refresh_container);
		mBtnTipOffContainer = customActionView.findViewById(R.id.tip_off_container);
		mBtnToolContainer = findViewById(R.id.btnToolContainer);
		mContentContianer = findViewById(R.id.contentContainer);
		mBtnTool = (ImageButton) findViewById(R.id.btnTool);
		mTxtActionBarTitle = (TextView) customActionView.findViewById(R.id.title);
		mBtnBack = customActionView.findViewById(R.id.btnBack);
		
		mBtnToolContainer.setOnClickListener(this);
        mBtnTipOffContainer.setOnClickListener(this);
        mRefreshContainer.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);
		
		initFragments();
		updatePageState();
	}
	
	MainPagerAdapter mPagerAdapter;
	private void initFragments(){
	    mPagerAdapter = (MainPagerAdapter) mPager.getAdapter();
		mFragments.add(new LocalVideoFragment().setIActivity(this));
		mFragments.add(new ShareVideoFragment().setIActivity(this));
		ToolBoxFragment toolFrag = new ToolBoxFragment();
		toolFrag.setIActivity(this);
		toolFrag.setListener(this);
		mFragments.add(toolFrag);
		
		mPagerAdapter.setFragments(mFragments);
		mIndicator.setViewPager(mPager);
		mCurrentFragment = mFragments.get(0);
	}
	
	void updatePageState(){
		if(mCurrentFragment == null){
			return;
		}
		mBtnTipOffContainer.setVisibility(mCurrentFragment.showTipOffButton() ? View.VISIBLE : View.GONE);
		mRefreshContainer.setVisibility(mCurrentFragment.showRefreshButton() ? View.VISIBLE : View.GONE);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, ABOUT_ID, 0, "关于");
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        Toast.makeText(this, "douzifly@gmail.com", Toast.LENGTH_SHORT).show();
	    }else if(item.getItemId() == REFRESH_ID){
	    	
	    }else if(item.getItemId() == ABOUT_ID){
	        //Toast.makeText(MainActivity.this, "Dev:Leo Design:Jack", Toast.LENGTH_SHORT).show();
	        AnnouncementFragment.showAnnouncement(getSupportFragmentManager(), new OnAnnouncementChooseListner() {
                
                @Override
                public void onAnnouncementClosed(boolean agree) {
                    ShareSetting.setAgreeShare(MainActivity.this, agree);
                }
            });
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.refresh_container:
		    if(mCurrentFragment != null){
                mCurrentFragment.onRefreshPressed();
            }
		    break;
		case R.id.tip_off_container:
		    if(mCurrentFragment != null){
                mCurrentFragment.onTipOffPressed();
            }
		    break;
		case R.id.btnBack:
		    hideToolPanel();
		    break;
		default:
			break;
		}
	}
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){

	        if(mCurrentFragment == mCurrentToolFragment) {
	            hideToolPanel();
	            return true;
	        }
	        
            new AlertDialog.Builder(this)
                    .setTitle("退出")
                    .setMessage("确定吗?")
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    finish();
                                }
                            }).setNegativeButton("取消", null ).show();
            return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    android.os.Process.killProcess(Process.myPid());
	}
	
	Animation mRefreshRotateAnim;

	@Override
	public void showProgressOnActionBar() {
		Log.d(TAG, "showProgressOnActionBar");
		if(mRefreshRotateAnim == null){
			mRefreshRotateAnim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
		}
		if(mBtnRefresh.getAnimation() != null){
			return;
		}
		mBtnRefresh.startAnimation(mRefreshRotateAnim);
	}

	@Override
	public void hideProgressOnActionBar() {
		Log.d(TAG, "hideProgressOnActionBar");
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mBtnRefresh.clearAnimation();
				mBtnRefresh.setAnimation(null);
			}
		}, 800);
		
	}
	
	
	Animation mHideContentAnim;
	Animation mShowContentAnim;
	
	void setToolButtonImage(boolean toolPanelVisible) {
	    mBtnTool.setImageResource(toolPanelVisible ? R.drawable.btn_close : R.drawable.btn_tool);
	}
	
	
	public boolean showToolPanel(BaseFragment fragment) {
	    
	    if(mHideContentAnim != null && !mHideContentAnim.hasEnded() || (mShowContentAnim != null && !mShowContentAnim.hasEnded())){
	        return false;
	    }
	    
	   if(mHideContentAnim == null) {
	       mHideContentAnim = AnimationUtils.loadAnimation(this, R.anim.slide_out_to_left);
	       mHideContentAnim.setAnimationListener(new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                mPager.setVisibility(View.GONE);
                mIndicator.setVisibility(View.GONE);
            }
        });
	   }
	   mContentContianer.startAnimation(mHideContentAnim);
	   loadToolFragment(fragment);
	   mCurrentFragment.onLeave();
	   mCurrentFragment = fragment;
	   mCurrentFragment.onInto();
	   updatePageState();
	   setActionBarTitle(fragment.getTitle());
	   mBtnBack.setVisibility(View.VISIBLE);
	   return true;
	}
	
	void loadToolFragment(BaseFragment fragment){  
	    if(mCurrentToolFragment == fragment) {
	        return;
	    }
	    FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction tran = fm.beginTransaction();
	    if(mCurrentToolFragment != null) {
	        Log.d(TAG, "hide:" + mCurrentToolFragment);
	        tran.hide(mCurrentToolFragment);
	    }
	    if(!fragment.isAdded()){
	        Log.d(TAG, "not added:" + fragment);
	        tran.add(R.id.toolBoxContainer, fragment);
	    }else {
	        tran.show(fragment);
	    }
	    tran.commit();
	    mCurrentToolFragment = fragment;
	}
	
	public boolean hideToolPanel() {
	        
	    if(mShowContentAnim != null && !mShowContentAnim.hasEnded() || (mHideContentAnim != null && !mHideContentAnim.hasEnded())) {
	        return false;
	    }
	    
	    
       if(mShowContentAnim == null) {
           mShowContentAnim = AnimationUtils.loadAnimation(this, R.anim.slide_in_from_left);
       }
       mContentContianer.startAnimation(mShowContentAnim);
       mPager.setVisibility(View.VISIBLE);
       mIndicator.setVisibility(View.VISIBLE);
       mCurrentToolFragment.onLeave();
       mCurrentFragment = mPagerAdapter.getItem(mPager.getCurrentItem());
       updatePageState();
       mBtnBack.setVisibility(View.GONE);
       setActionBarTitle("快播合体助手");
       return true;
	}

	@Override
	public void setActionBarTitle(String text) {
		if(mTxtActionBarTitle != null) {
			mTxtActionBarTitle.setText(text);
		}
	}

    @Override
    public void onModuleClicked(int module) {
        if(module == ToolBoxContentFragment.MODULE_FAVE) {
            if(mFaveFragment == null) {
                mFaveFragment = new FaveFragment();
                mFaveFragment.setIActivity(this);
            }
            showToolPanel(mFaveFragment);
        }else if (module == ToolBoxContentFragment.MODULE_TRAN) {
            if(mTranFragment == null) {
                mTranFragment = new TransportFragment();
                mTranFragment.setIActivity(this);
            }
            showToolPanel(mTranFragment);
        }
    }

}
