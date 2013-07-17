/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-9
 */
package douzifly.android.qexport.ui;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Xiaoyuan Lau
 *
 */
public class MainPagerAdapter extends FragmentPagerAdapter{
	
	final static String TAG = "MainPagerAdapter";
	
	private List<BaseFragment> mFragments = new ArrayList<BaseFragment>(); 
	
	/**
	 * @param fm
	 */
	public MainPagerAdapter(FragmentManager fm) {
		super(fm);
	}
	
	public void setFragments(List<BaseFragment> fragments){
		if(fragments == mFragments){
			return;
		}
		mFragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public BaseFragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragments.get(position).getTitle();
	}
	
}
