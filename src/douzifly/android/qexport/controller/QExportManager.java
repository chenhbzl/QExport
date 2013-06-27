/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzifly.android.qexport.controller;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import android.util.Log;
import douzifly.android.qexport.controller.IQExport.OnMergeProgressChangedListener;
import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;

/**
 * @author douzifly
 *
 */
public class QExportManager{
	
	static final String TAG = "QExport";
	static void log(String msg){
		Log.d(TAG,msg);
	}
	
	
	// key:VIDEO_SOURCE 
	private Hashtable<Integer, IQExport> mQExports = new Hashtable<Integer, IQExport>();
	private ExportListener mListener;
	private Thread mScanThread;
	
	public void addExport(int videoSource, IQExport qexp){
	    mQExports.put(videoSource, qexp);
	}
		
	public synchronized void scan(){
		if(mScanThread != null){
			log("scaning return");
			return;
		}
		mScanThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
			    
			    Iterator<Entry<Integer, IQExport>> exps = mQExports.entrySet().iterator();
			    List<VideoInfo> allVideos = new ArrayList<VideoInfo>();
			    while(exps.hasNext()){
			        IQExport q = exps.next().getValue();
			        Log.d(TAG, "scan use :" + q);
			        q.scan();
			        List<VideoInfo> videos = q.getVideos();
			        Log.d(TAG, "scan ok videos count:" + (videos == null ? 0 : videos.size()));
			        if(videos != null && videos.size() > 0){
			            allVideos.addAll(videos);
			        }
			    }
			    
			    if(mListener != null){
                    mListener.onScanOk(allVideos);
                }
			    
				mScanThread = null;
			}
		});
		mScanThread.start();
	}
	
	public void setExportListener(ExportListener l){
		mListener = l;
	}
	
	
	public void merge(final VideoInfo v){
	    
	    final String outputPath = AppSetting.getExportFolder() + "/" + v.name;
	    Log.d(TAG, "merge video:" + v + " outpath:" + outputPath);
	    final IQExport qexp = mQExports.get(v.source);
	    if(qexp == null){
	        Log.d(TAG, "cant find merge instance");
	        if(mListener != null){
	            mListener.onMergeOk(v, false);
	        }
	        return;
	    }
	    
	    Log.d(TAG, "begin merge use:" + qexp);
	    
		new Thread(new Runnable() {
			
			@Override
			public void run() {
		        boolean success = qexp.merge(v, outputPath, new OnMergeProgressChangedListener() {

                    
                    @Override
                    public void onMergeProgressChanged(VideoInfo v, int progress,
                            int mergeSpeed, int mergeSize) {
                        if(mListener != null){
                            mListener.onMergeProgress(v, progress, mergeSpeed, mergeSize);
                        }
                    }
                });
		        if(mListener != null){
		            mListener.onMergeOk(v, success);
		        }
			}
		}).start();
	}
		
	public static interface ExportListener{
	    public void onScanOk(List<VideoInfo> videos);
	    public void onMergeOk(VideoInfo v, boolean success);
	    public void onMergeProgress(VideoInfo v, int progress, int mergeSpeed, int mergeSize);
	}
	
}
