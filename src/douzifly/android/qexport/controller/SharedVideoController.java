/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.controller;

import douzifly.android.qexport.model.ISharedVideoProvider;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.ISharedVideoProvider.OnSharedVideoLoadedListener;
import douzifly.android.qexport.model.web.SharedVideoWebProvider;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoController {
	
	public void getRandVideos(OnSharedVideoLoadedListener l){
		ISharedVideoProvider provider = new SharedVideoWebProvider();
		provider.getVideos(l);
	}
	
	public void uploadVideo(String title, String hash){
		ISharedVideoProvider provider = new SharedVideoWebProvider();
		SharedVideoInfo v = new SharedVideoInfo();
		v.title = title;
		v.hash = hash;
		provider.updateVideo(v);
	}
	
}
