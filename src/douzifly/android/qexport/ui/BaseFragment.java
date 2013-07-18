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
	
	/**
	 * 如果子类处理了，返回false，那么父类不会处理
	 * @return
	 */
	public boolean onClosePressed() {
		return false;
	}
	
	public boolean showTipOffButton(){
		return false;
	}
}
