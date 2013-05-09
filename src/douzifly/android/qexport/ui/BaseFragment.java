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
	
	public String getTitle(){
		return "";
	}
	
	public boolean showRefreshButton(){
		return false;
	}
}
