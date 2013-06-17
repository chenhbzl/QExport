/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.controller;

import java.util.List;

import douzifly.android.qexport.model.VideoInfo;

/**
 * @author douzifly
 *
 */
public interface IQExport {
    
    List<VideoInfo> scan();
    List<VideoInfo> getVideos();
    boolean merge(final VideoInfo v,final String outputPath, OnMergeProgressChangedListener l);
    
    static interface OnMergeProgressChangedListener{
        public void onMergeProgressChanged(VideoInfo v, int progress, int mergeSpeed, int mergeSize);
    }
}
