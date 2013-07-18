/**
 * douzifly @Jul 17, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.ui.toolbox;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import douzi.android.qexport.R;
import douzifly.android.qexport.ui.BaseFragment;
import douzifly.android.qexport.ui.toolbox.ToolBoxContentFragment.OnModuleClickListener;

/**
 * @author douzifly
 *
 */
public class ToolBoxFragment extends BaseFragment implements OnModuleClickListener{
    private final static String TAG = "ToolBoxFragment";
    private ToolBoxContentFragment  mToolBoxContentFragment;
    private OnModuleClickListener   mListener;
    
    public void setListener(OnModuleClickListener l) {
        mListener = l;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
    	View root = inflater.inflate(R.layout.tool_box, null);
    	setupView(root);
    	showToolBoxContentFragment();
        return root;
    }
    
    private void setupView(View root) {
    }
    
    @Override
    public String getTitle() {
    	return "我的工具箱";
    }
    
    @Override
    public boolean showRefreshButton() {
    	// 如果没有子界面，那么不显示，如果有子界面，看字节面是否需要显示
        return false;
    }

    private FragmentTransaction getFragmentTran() {
    	FragmentManager fm = getFragmentManager();
    	return fm.beginTransaction();
    }
   
    
    private void showToolBoxContentFragment() {
    	FragmentTransaction tran = getFragmentTran();
    	Log.d(TAG, "showToolBoxContentFragment:" + mToolBoxContentFragment);
    	mToolBoxContentFragment = new ToolBoxContentFragment();
    	mToolBoxContentFragment.setIActivity(activity);
    	mToolBoxContentFragment.setListener(this);
//    		setAnimation(tran);
    	tran.add(R.id.toolBoxFragmentContainer, mToolBoxContentFragment);
    	tran.commit();
    }
    
    private void setAnimation(FragmentTransaction tran) {
    	tran.setCustomAnimations(R.anim.slide_in_from_bottom_no_fill, R.anim.slide_out_to_bottom_no_fill,
    			R.anim.slide_in_from_bottom_no_fill, R.anim.slide_out_to_bottom_no_fill);
    }


	@Override
	public void onModuleClicked(int module) {
		if(mListener != null) {
		    mListener.onModuleClicked(module);
		}
	}
	
	@Override
	public void onDetach() {
	    super.onDetach();
	    Log.d(TAG, "onDetach");
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Log.d(TAG, "onDestory");
	}
	    
}
