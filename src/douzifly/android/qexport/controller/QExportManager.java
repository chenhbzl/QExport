/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzifly.android.qexport.controller;

import java.util.List;

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
	
	
	private IQExport mQExport;
	private ExportListener mListener;
	private Thread mScanThread;
	
	public QExportManager(IQExport export){
	    mQExport = export;
	}
	
		
	public synchronized void scan(){
		if(mScanThread != null){
			log("scaning return");
			return;
		}
		mScanThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				mQExport.scan();
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
		if(mQExport == null){
			return null;
		}
		return mQExport.getVideos();
	}
	
	public void merge(final VideoInfo v){
	    
	    final String outputPath = AppSetting.getExportFolder() + "/" + v.name;
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			    if(mQExport != null){
			        boolean success = mQExport.merge(v, outputPath, new OnMergeProgressChangedListener() {
                        
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
			}
		}).start();
	}
		
	public static interface ExportListener{
	    public void onScanOk();
	    public void onMergeOk(VideoInfo v, boolean success);
	    public void onMergeProgress(VideoInfo v, int progress, int mergeSpeed, int mergeSize);
	}
	
}
