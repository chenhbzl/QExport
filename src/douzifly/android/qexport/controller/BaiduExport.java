/*
 * @project :QExport
 * @author  :huqiming 
 * @date    :2013-6-19
 */
package douzifly.android.qexport.controller;

import java.util.List;

import douzifly.android.qexport.model.VideoInfo;

/**
 * 百度资源合并
 */
public class BaiduExport implements IQExport {
	private static final String TAG = "BaiduExport";

	public BaiduExport() {
	}

	@Override
	public List<VideoInfo> scan() {
		return null;
	}

	@Override
	public List<VideoInfo> getVideos() {
		return null;
	}

	@Override
	public boolean merge(VideoInfo v, String outputPath, OnMergeProgressChangedListener l) {
		return false;
	}

}
