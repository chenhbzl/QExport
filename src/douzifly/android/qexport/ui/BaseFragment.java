/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-9
 */
package douzifly.android.qexport.ui;

import android.support.v4.app.Fragment;

/**
 * @author Xiaoyuan Lau
 *
 */
public class BaseFragment extends Fragment {
	
	protected IActivity activity;
	
	public BaseFragment setIActivity(IActivity a){
		activity = a;
		return this;
	}
	
	public String getTitle(){
		return "";
	}
	
	public boolean showRefreshButton(){
		return true;
	}
	
	public void showProgressOnActionBar(){
		if(activity == null) return;
		activity.showProgressOnActionBar();
	}
	
	public void hideProgressOnActionBar(){
		if(activity == null) return;
		activity.hideProgressOnActionBar();
	}
	
	public void onInto(){}
	
	public void onLeave(){}
	
	public void onRefreshPressed(){}
	
	public void onTipOffPressed(){}
	
	public boolean showTipOffButton(){
		return false;
	}
}
