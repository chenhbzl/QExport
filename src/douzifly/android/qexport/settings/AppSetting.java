/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.settings;

import douzifly.android.qexport.utils.SdcardUtils;

/**
 * @author douzifly
 *
 */
public class AppSetting {
    
    final static String P2PCACHE_FOLDER_NAME = "p2pcache";
    final static String BAIDU_CACHE_FOLDER_PATH = "baidu/video/file";
    final static String EXPORT_FOLDER_NAME = "p2pMerged";
    
    static String BAIDU_CACHE_FOLDER;
    static String QVOD_CACHE_FOLDER;
    static String EXPORT_FOLDER;
    
    // TODO detect which is real baidu cache folder
    public static String getBaiduCacheFolder(){
        if(BAIDU_CACHE_FOLDER == null){
        	BAIDU_CACHE_FOLDER =  SdcardUtils.getRootPath() + "/" + BAIDU_CACHE_FOLDER_PATH; 
        }
        return BAIDU_CACHE_FOLDER;
    }
    
    public static String getQVODCacheFolder(){
        if(QVOD_CACHE_FOLDER == null){
           QVOD_CACHE_FOLDER =  SdcardUtils.getRootPath() + "/" + P2PCACHE_FOLDER_NAME; 
        }
        return QVOD_CACHE_FOLDER;
    }
    
    // TODO detect real sdcard root path when multiple sdcards
    public static String getExportFolder(){
        if(EXPORT_FOLDER == null){
            EXPORT_FOLDER =  SdcardUtils.getRootPath() + "/" + EXPORT_FOLDER_NAME; 
         }
         return EXPORT_FOLDER;
    }
}
