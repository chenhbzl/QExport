/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-10
 */
package douzifly.android.qexport.ui;

import java.io.File;

import douzifly.android.qexport.settings.AppSetting;

/**
 * @author Xiaoyuan Lau
 *
 */
public class LocalVideoHelper {
	
	public static boolean isVideoMerged(String videoName, long videoSize){
	    try{
	        final String target = AppSetting.getExportFolder() + "/" + videoName;
	        File f = new File(target);
	        return f.exists() && f.length() == videoSize ? true : false;
	    }catch(Exception e){
	        return false;
	    }
	}
	
	
}
