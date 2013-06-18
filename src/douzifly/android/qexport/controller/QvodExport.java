/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import douzifly.android.qexport.model.CacheDir;
import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;

/**
 * @author douzifly
 *
 */
public class QvodExport implements IQExport {

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

    @Override
    public boolean merge(VideoInfo v, String outputPath, OnMergeProgressChangedListener l) {
        if(v == null || v.paths == null || v.paths.length == 0){
            return false;
        }
        File f = new File(outputPath);
        FileOutputStream os = null;

        int oldProgress = 0;
        int newProgress = 0;
        try {
            if(f.exists()){
                f.delete();
            }
            f.getParentFile().mkdir();
            f.createNewFile();
            os = new FileOutputStream(f);
            byte[] buf = new byte[1024];
            int writed = 0;
            FileInputStream is = null;
            if( l != null)
                l.onMergeProgressChanged(v, 0, 0, 0);
            long lastTime = System.currentTimeMillis();
            int lastWrite = 0;
            for(String p : v.paths){
                is = new FileInputStream(new File(p));
                int r = 0;
                while((r = is.read(buf)) != -1){
                    os.write(buf, 0, r);
                    writed += r;
                    newProgress = (int)((float)writed / (float)v.size * 100);
                    if(newProgress != oldProgress){
//                        log("progress:"+newProgress+ " " + v.name);
                        long time = System.currentTimeMillis();
                        long speed = 0;
                        long timediff = time - lastTime;
                        if(timediff < 1000){
                            timediff = 1000;
                        }
                        speed = (writed - lastWrite) / (timediff / 1000);
                        lastTime = time;
                        lastWrite = writed;
                        if (l != null) l.onMergeProgressChanged(v, newProgress, (int)speed, writed);
                        oldProgress = newProgress;
                    }
                }
                is.close();
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
            if(os != null){
                try{
                    os.close();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
            return false;
        }
        return true;
    }
    
}
