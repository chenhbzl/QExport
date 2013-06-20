/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.controller;

import java.util.List;

import douzifly.android.qexport.model.CacheDir;
import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;

/**
 * @author douzifly
 *
 */
public class QvodExport extends AbsExport {

    private CacheDir mCacheDir = new CacheDir();
    
    @Override
    public List<VideoInfo> scan() {
        String folder = AppSetting.getQVODCacheFolder();
        mCacheDir.setRootDir(folder);
        mCacheDir.scan();
        return mCacheDir.getVideos();
    }

    @Override
    public List<VideoInfo> getVideos() {
        return mCacheDir.getVideos();
    }

    
    
}
