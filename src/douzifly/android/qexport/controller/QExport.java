/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import douzifly.android.qexport.model.CacheDir;
import douzifly.android.qexport.model.VideoInfo;

import android.util.Log;

/**
 * @author douzifly
 *
 */
public class QExport {
	
	static final String TAG = "QExport";
	static void log(String msg){
		Log.d(TAG,msg);
	}
	
	private CacheDir mCacheDir = new CacheDir();
	private ExportListener mListener;
	private Thread mScanThread;
	
	public synchronized void scan(String cacheFolder){
		if(mScanThread != null){
			log("scaning return");
			return;
		}
		mCacheDir.setRootDir(cacheFolder);
		mScanThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				mCacheDir.scan();
				if(mListener != null){
					mListener.onScanOk();
				}
				mScanThread = null;
			}
		});
		mScanThread.start();
	}
	
	public void setExportListener(ExportListener l){
		mListener = l;
	}
	
	public List<VideoInfo> getVideos(){
		if(mCacheDir == null){
			return null;
		}
		return mCacheDir.getVideos();
	}
	
	public void merge(final VideoInfo v,final String outputPath){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mergeFile(v, outputPath);
			}
		}).start();
	}
	
	private boolean mergeFile(VideoInfo v, String outputPath){
		if(v == null || v.paths == null || v.paths.length == 0){
		    if(mListener != null){
		        mListener.onMergeOk(v, false);
		    }
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
			mListener.onMergeProgress(v, 0, 0, 0);
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
						log("progress:"+newProgress+ " " + v.name);
						long time = System.currentTimeMillis();
						long speed = 0;
						long timediff = time - lastTime;
						if(timediff < 1000){
							timediff = 1000;
						}
						speed = (writed - lastWrite) / (timediff / 1000);
						lastTime = time;
						lastWrite = writed;
						mListener.onMergeProgress(v, newProgress, (int)speed, writed);
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
			if(mListener != null){
				mListener.onMergeOk(v, false);
			}
			return false;
		}
		if(mListener != null){
			mListener.onMergeOk(v, true);
		}
		return true;
	}	
	
	public static interface ExportListener{
		public void onScanOk();
		public void onMergeOk(VideoInfo v, boolean sucess);
		public void onMergeProgress(VideoInfo v, int progress, int mergeSpeed, int mergeSize);
	}
	
}
