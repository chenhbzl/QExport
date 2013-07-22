/*
xc * @project :QExport
 * @author  :huqiming 
 * @date    :2013-6-19
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import douzifly.android.qexport.utils.FileUtil;

import android.util.Log;

/**
 *
 */
public class BaiduExportUtil {
	private static final String TAG = "BaiduExportUtil";

	private static final String REGEX_VIDEO_NAME = "_?[a-f,0-9]{8}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{12}";
	private static final String REGEX_CACHE_FILE = "((\\.bdv_[0-9]{4})|(_?[a-f,0-9]{8}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{12}\\.bdv))$";
	private static final String REGEX_CACHE_FILE_INDEX = "(_[0-9]{4})$";
	private static final String REGEX_OTHER_CACHE_FILE = "file:///.*?_?[a-f,0-9]{8}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{12}\\.bdv";

	/**
	 * 通过缓存文件夹获取电影名称
	 * @param folderName
	 * @return
	 */
	public static String getRealNameFromFolder(String folderName) {
		if (folderName == null) {
			return null;
		}
		return folderName.replaceAll(REGEX_VIDEO_NAME, "");
	}

	/**
	 * 是否是百度缓存文件夹
	 * @param folderName
	 * @return
	 */
	public static boolean isBaiduCacheFolder(String folderName) {
		Pattern p = Pattern.compile(REGEX_VIDEO_NAME);
		Matcher m = p.matcher(folderName);
		return m.find();
	}

	/**
	 * 获取目录空间大小
	 * @param subPaths
	 * @return
	 */
	public static long getFolderTotalSize(String[] subPaths) {
		if (subPaths == null || subPaths.length < 1) {
			return 0;
		}
		long totalSize = 0;
		for (String path : subPaths) {
			totalSize += new File(path).length();
		}

		return totalSize;
	}

	/**
	 * 通过文件名获取序号，取不到返回-1
	 * @param fileName
	 * @return
	 */
	private static int getIndexByName(String fileName) {
		int index = -1;
		Pattern p = Pattern.compile(REGEX_CACHE_FILE_INDEX);
		Matcher m = p.matcher(fileName);
		if (m.find()) {
			String indexStr = m.group();
			try {
				index = Integer.valueOf(indexStr.substring(1));
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		return index;
	}

	/**
	 * 获取百度资源的缓存文件路径
	 * @param folder
	 * @return
	 */
	private static String[] getBdCacheSubFilePaths(File folder) {
		File[] files = folder.listFiles(new BdCacheFileNameFilter());

		if (files == null || files.length < 1) {
			Log.d(TAG, "getCacheSubFilePaths files is empty: ");
			return null;
		}

		Arrays.sort(files, new CacheFileComparator());

		String[] paths = new String[files.length];
		int i = 0;
		for (File f : files) {
			paths[i] = f.getPath();
//			Log.d(TAG, "getCacheSubFilePaths path: " + f.getPath());
			i++;
		}
		return paths;
	}

	/**
	 * 解析资源文件，从中获取到缓存文件的路径
	 * @param file
	 * @return
	 */
	private static String[] getOtherCacheSubFilePaths(File file) {
		String cfg = FileUtil.readFile(file);
		Log.d(TAG, "cfg: " + cfg);
		Pattern p = Pattern.compile(REGEX_OTHER_CACHE_FILE);
		Matcher m = p.matcher(cfg);
		int count = 0;
		int size = 1024;
		String[] buffer = new String[size];
		while (m.find()) {
			if (count < size) {
				buffer[count] = m.group().substring("file://".length());
				Log.d(TAG, "Matcher: " + buffer[count]);
			} else {
				size = 2 * size;
				String[] newBuffer = Arrays.copyOf(buffer, size);
				buffer = newBuffer;
			}
			count++;
		}
		String[] result = Arrays.copyOf(buffer, count);
		return result;
	}

	/**
	 * 获取缓存文件的路径
	 * @param folder
	 * @return
	 */
	public static String[] getCacheSubFilePaths(File folder) {
		String fileName = getRealNameFromFolder(folder.getName());
		String filePath = folder.getPath() + "/" + fileName + ".bdv";
		File file = new File(filePath);
		if (file.exists()) {
			// 其他正版资源的缓存文件路径
			return getOtherCacheSubFilePaths(file);
		} else {
			// 百度资源的缓存文件路径
			return getBdCacheSubFilePaths(folder);
		}
	}

	public static class CacheFileComparator implements Comparator<File> {

		@Override
		public int compare(File lhs, File rhs) {
			int lIndex = getIndexByName(lhs.getName());
			int rIndex = getIndexByName(rhs.getName());

			if (lIndex > -1 && rIndex > -1) {
				return lIndex - rIndex;
			} else {
				return (int) (lhs.lastModified() - rhs.lastModified());
			}
		}
	}

	public static class BdCacheFileNameFilter implements FilenameFilter {

		@Override
		public boolean accept(File dir, String filename) {
			Pattern p = Pattern.compile(REGEX_CACHE_FILE);
			Matcher m = p.matcher(filename);
			return m.find();
		}

	}

}
