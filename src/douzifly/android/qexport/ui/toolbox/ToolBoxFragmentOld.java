///**
// * douzifly @Jul 17, 2013
// * github.com/douzifly
// * douzifly@gmail.com
// */
//package douzifly.android.qexport.ui.toolbox;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import douzi.android.qexport.R;
//import douzifly.android.qexport.ui.BaseFragment;
//import douzifly.android.qexport.ui.IActivity;
//import douzifly.android.qexport.ui.toolbox.ToolBoxContentFragment.OnModuleClickListener;
//
///**
// * @author douzifly
// *
// */
//public class ToolBoxFragmentOld extends BaseFragment implements OnModuleClickListener{
//    
//    private BaseFragment 			mCurrentFragment;
//    private FaveFragment 			mFaveFragment;
//    private TransportFragment 		mTranFragment;
//    private ToolBoxContentFragment  mToolBoxContentFragment;
//    private int                     mToolContainerId;
//    
//    public void setToolContainerId(int resId) {
//        mToolContainerId = resId;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//            Bundle savedInstanceState) {
//    	View root = inflater.inflate(R.layout.tool_box, null);
//    	setupView(root);
//    	showToolBoxContentFragment();
//        return root;
//    }
//    
//    private void setupView(View root) {
//    }
//    
//    @Override
//    public BaseFragment setIActivity(IActivity a) {
//    	return super.setIActivity(a);
//    }
//    
//    @Override
//    public String getTitle() {
//    	return "我的工具箱";
//    }
//    
//    @Override
//    public boolean showRefreshButton() {
//    	// 如果没有子界面，那么不显示，如果有子界面，看字节面是否需要显示
//        return mCurrentFragment == null ? false : mCurrentFragment.showRefreshButton();
//    }
//
//    private FragmentTransaction getFragmentTran() {
//    	FragmentManager fm = getFragmentManager();
//    	return fm.beginTransaction();
//    }
//    
//    private void hideCurrentFragment() {
//    	if(mCurrentFragment == null) {
//    		return;
//    	}
//    	
//    	FragmentTransaction tran = getFragmentTran();
//    	tran.hide(mCurrentFragment);
//    	tran.commit();
//    	mCurrentFragment.onLeave();
//    }
//    
//    private void setCurrentFragment(BaseFragment frag) {
//    	mCurrentFragment = frag;
//    	frag.onInto();
//    	activity.setActionBarTitle(frag.getTitle());
//    } 
//    
//    private void showFaveFramgent() {
//        activity.showToolPanel();
//    	hideCurrentFragment();
//    	FragmentTransaction tran = getFragmentTran();
//    	if(mFaveFragment == null) {
//    		mFaveFragment = new FaveFragment();
//    		mFaveFragment.setIActivity(activity);
//    		setAnimation(tran);
//    		tran.add(mToolContainerId, mFaveFragment);
//    	}else {
//    		setAnimation(tran);
//    		tran.show(mFaveFragment);
//    	}
//    	tran.commit();
//    	setCurrentFragment(mFaveFragment);
//    }
//    
//    private void showTransferFragment() {
//        activity.showToolPanel();
//    	hideCurrentFragment();
//    	FragmentTransaction tran = getFragmentTran();
//    	if(mTranFragment == null) {
//    		mTranFragment = new TransportFragment();
//    		mTranFragment.setIActivity(activity);
//    		setAnimation(tran);
//    		tran.add(mToolContainerId, mTranFragment);
//    	}else {
//    		setAnimation(tran);
//    		tran.show(mTranFragment);
//    	}
//    	tran.commit();
//    	setCurrentFragment(mTranFragment);
//    }
//    
//    private void showToolBoxContentFragment() {
//    	hideCurrentFragment();
//    	FragmentTransaction tran = getFragmentTran();
//    	if(mToolBoxContentFragment == null) {
//    		mToolBoxContentFragment = new ToolBoxContentFragment();
//    		mToolBoxContentFragment.setIActivity(activity);
//    		mToolBoxContentFragment.setListener(this);
//    		setAnimation(tran);
//    		tran.add(R.id.toolBoxFragmentContainer, mToolBoxContentFragment);
//    	}else {
//    		setAnimation(tran);
//    		tran.show(mToolBoxContentFragment);
//    	}
//    	tran.commit();
//    	setCurrentFragment(mToolBoxContentFragment);
//    }
//    
//    private void setAnimation(FragmentTransaction tran) {
//    	tran.setCustomAnimations(R.anim.slide_in_from_bottom_no_fill, R.anim.slide_out_to_bottom_no_fill,
//    			R.anim.slide_in_from_bottom_no_fill, R.anim.slide_out_to_bottom_no_fill);
//    }
//
//    @Override
//    public boolean onClosePressed() {
//    	if(mCurrentFragment == mToolBoxContentFragment) {
//    		return false;
//    	}
//    	
//    	showToolBoxContentFragment();
//    	return true;
//    }
//    
//
//	@Override
//	public void onModuleClicked(int module) {
//		switch (module) {
//		case ToolBoxContentFragment.MODULE_FAVE:
//			showFaveFramgent();
//			break;
//		case ToolBoxContentFragment.MODULE_TRAN:
//			showTransferFragment();
//			break;
//		default:
//			break;
//		}
//	}
//	
//	    
//}
