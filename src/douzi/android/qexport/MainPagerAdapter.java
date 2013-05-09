/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-9
 */
package douzi.android.qexport;

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
	public Fragment getItem(int arg0) {
		return mFragments.get(arg0);
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

}
