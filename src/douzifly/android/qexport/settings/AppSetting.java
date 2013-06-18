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
    final static String EXPORT_FOLDER_NAME = "p2pMerged";
    
    static String QVOD_CACHE_FOLDER;
    static String EXPORT_FOLDER;
    
    // TODO detect which is real qvod cache folder
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
