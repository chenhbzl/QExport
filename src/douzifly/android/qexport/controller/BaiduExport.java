/*
 * @project :QExport
 * @author  :huqiming 
 * @date    :2013-6-19
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.umeng.common.Log;

import douzifly.android.qexport.model.VideoInfo;
import douzifly.android.qexport.settings.AppSetting;

/**
 * 百度资源合并
 */
public class BaiduExport extends AbsExport {
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
		info.source = VideoInfo.SOURCE_BAIDU;
		info.name = name;
		info.paths = BaiduExportUtil.getCacheSubFilePaths(file);
		info.size = BaiduExportUtil.getFolderTotalSize(info.paths);
//		Log.d(TAG, "getVideoInfo paths length: " + info.paths.length);
		Log.d(TAG, "getVideoInfo real name: " + name + " info.size: " + info.size);
		return info;
	}

	@Override
	public List<VideoInfo> getVideos() {
		return mVideoInfos;
	}

}
