/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzi.android.qexport.model;

import java.util.List;

/**
 * @author Xiaoyuan Lau
 *
 */
public interface ISharedVideoProvider {
	
	void getVideos(OnSharedVideoLoadedListener l);
	
	public interface OnSharedVideoLoadedListener{
		public void onVideoLoaded(boolean sucess, List<SharedVideoInfo> videos);
	}
	
	public void updateVideo(SharedVideoInfo v);
}
