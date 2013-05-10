/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-10
 */
package douzifly.android.qexport.ui;

import java.io.File;

import android.os.Environment;

/**
 * @author Xiaoyuan Lau
 *
 */
public class LocalVideoHelper {
	
	public final static String P2P_CACHE_FOLDER = "p2pcache";
	public final static String EXPORT_FOLDER = "p2pMerged";
	
	public static boolean isVideoMerged(String videoName, long videoSize, String exportFolder){
		final String target = Environment.getExternalStorageDirectory() + "/" + exportFolder + "/" + videoName;
		File f = new File(target);
		return f.exists() && f.length() == videoSize ? true : false;
	}
	
	
}
