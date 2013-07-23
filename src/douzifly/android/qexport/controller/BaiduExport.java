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
		for (File file : files) {
			VideoInfo v = getVideoInfo(file);
			if (v != null) {
				list.add(v);
			}
		}

		mVideoInfos = list;
		return list;
	}

	private VideoInfo getVideoInfo(File file) {
		if (file == null || !file.exists() || !file.isDirectory()) {
			return null;
		}

		return BaiduExportUtil.getVideoInfo(file);
	}

	@Override
	public List<VideoInfo> getVideos() {
		return mVideoInfos;
	}

}
