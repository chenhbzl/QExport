/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzi.android.qexport;

import douzi.android.qexport.model.ISharedVideoProvider;
import douzi.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzi.android.qexport.model.web.SharedVideoWebProvider;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoController {
	
	public void getRandVideos(OnSharedVideoLoadedListener l){
		ISharedVideoProvider provider = new SharedVideoWebProvider();
		provider.getVideos(l);
	}
	
}
