/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzi.android.qexport;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import android.util.Log;

/**
 * @author douzifly
 *
 */
public class CacheDir {
	
	static final String TAG = "CacheDir";
	static void log(String msg){
		Log.d(TAG,msg);
	}
	
	private String mRootDir;
	private List<VideoInfo> mVideos = new ArrayList<VideoInfo>();
	
	
	public String getRootDir(){
		return mRootDir;
	}
	
	public void setRootDir(String dir){
		mRootDir = dir;
	}

	public void scan(){
		File r = new File(mRootDir);
		if(!r.isDirectory()){
			log("path:" + mRootDir + " is not directory");
			return;
		}
		File[] subs = r.listFiles();
		if(subs == null || subs.length == 0){
			log("path:" + mRootDir + " has no childs");
			return;
		}
		mVideos.clear();
		int i = 0;
		for(File f : subs){
			if(!f.isDirectory()){
				log(f.getName() + " is not directory ignore");
				continue;
			}
			VideoInfo video = getVideo(f.getPath());
			if(video == null){
			    continue;
			}
			video.postion = i++;
			if(video != null){
				mVideos.add(video);
			}
		}
	}
	
	public List<VideoInfo> getVideos(){
		return mVideos;
	}
	
	/**
	 * 通过Hash获取P2PCache目录下所有文件路径
	 * @param folder
	 * @return
	 */
	private VideoInfo getVideo(String folder){
		folder = folder.trim();
		File f = new File(folder);
		if(!f.exists() || !f.isDirectory()){
			return null;
		}
		VideoInfo v = new VideoInfo();
		String[] files =  f.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String filename) {
				if(filename.contains(".!mv")){
					return true;
				}
				return false;
			}
		});
		if(files == null || files.length == 0){
			return null;
		}
		v.name = files[0].substring(0,files[0].lastIndexOf("_"));
		StringComparator comparator = new StringComparator();
		Arrays.sort(files, comparator);
		long size = 0;
		for(int i = 0 ; i < files.length ; i++){
			files[i] = folder + "/" + files[i];
			File fi = new File(files[i]);
			log(fi.getPath()+" exists:" + fi.exists() + " len:"+fi.length());
			size += fi.length();
		}
		v.paths = files;
		v.size = size;
		v.hash = "qvod://" + v.size + "|" + f.getName() + "|" + v.name+"|";
		return v;
	}
	
	private static class StringComparator implements Comparator<String>{

		@Override
		public int compare(String lhs, String rhs) {
			int lIndex = getFileIndex(lhs);
			int rIndex = getFileIndex(rhs);
			return lIndex - rIndex;
		}
		
		public int getFileIndex(String name){
			int lastUnderLine = name.lastIndexOf("_");
			int lastDot = name.lastIndexOf(".!mv");
			if(lastDot > lastUnderLine && lastUnderLine > 0){
				return Integer.valueOf(name.substring(lastUnderLine + 1, lastDot));
			}
			return 0;
		}
		
	}
}
