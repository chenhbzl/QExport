/*
 * @project :QExport
 * @author  :huqiming 
 * @date    :2013-6-19
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;

/**
 * 百度资源合并
 */
public class BaiduExport implements IQExport {
	private static final String TAG = "BaiduExport";
	private List<VideoInfo> mVideoInfos;

	public BaiduExport() {
	}

	@Override
	public List<VideoInfo> scan() {
		String path = AppSetting.getBaiduCacheFolder();
		File rootFile = new File(path);
		if (rootFile == null || !rootFile.exists() || !rootFile.isDirectory()) {
			return null;
		}

		File[] files = rootFile.listFiles();
		if (files == null || files.length < 1) {
			return null;
		}

		List<VideoInfo> list = new ArrayList<VideoInfo>();
		int i = 0;
		for (File file : files) {
			VideoInfo v = getVideoInfo(file);
			if (v != null) {
				v.postion = i;
				list.add(v);
				i++;
			}
		}

		mVideoInfos = list;
		return list;
	}

	private VideoInfo getVideoInfo(File file) {
		if (file == null || !file.exists() || !file.isDirectory()) {
			return null;
		}

		String folderName = file.getName();
		if (!BaiduExportUtil.isBaiduCacheFolder(folderName)) {
			return null;
		}
		String name = BaiduExportUtil.getRealNameFromFolder(folderName);
		VideoInfo info = new VideoInfo();
		info.name = name;
		info.paths = BaiduExportUtil.getCacheSubFilePaths(file);
		info.size = BaiduExportUtil.getFolderTotalSize(info.paths);
//		Log.d(TAG, "getVideoInfo paths length: " + info.paths.length);
//		Log.d(TAG, "getVideoInfo real name: " + name + " info.size: " + info.size);
		return info;
	}

	@Override
	public List<VideoInfo> getVideos() {
		return mVideoInfos;
	}

	@Override
	public boolean merge(VideoInfo v, String outputPath, OnMergeProgressChangedListener l) {
		if (v == null || v.paths == null || v.paths.length == 0) {
			return false;
		}
		File f = new File(outputPath);
		FileOutputStream os = null;

		int oldProgress = 0;
		int newProgress = 0;
		try {
			if (f.exists()) {
				f.delete();
			}
			f.getParentFile().mkdir();
			f.createNewFile();
			os = new FileOutputStream(f);
			byte[] buf = new byte[1024];
			int writed = 0;
			FileInputStream is = null;
			if (l != null)
				l.onMergeProgressChanged(v, 0, 0, 0);
			long lastTime = System.currentTimeMillis();
			int lastWrite = 0;
			for (String p : v.paths) {
				is = new FileInputStream(new File(p));
				int r = 0;
				while ((r = is.read(buf)) != -1) {
					os.write(buf, 0, r);
					writed += r;
					newProgress = (int) ((float) writed / (float) v.size * 100);
					if (newProgress != oldProgress) {
						// log("progress:"+newProgress+ " " + v.name);
						long time = System.currentTimeMillis();
						long speed = 0;
						long timediff = time - lastTime;
						if (timediff < 1000) {
							timediff = 1000;
						}
						speed = (writed - lastWrite) / (timediff / 1000);
						lastTime = time;
						lastWrite = writed;
						if (l != null)
							l.onMergeProgressChanged(v, newProgress, (int) speed, writed);
						oldProgress = newProgress;
					}
				}
				is.close();
			}
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
			if (os != null) {
				try {
					os.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			return false;
		}
		return true;
	}

}
