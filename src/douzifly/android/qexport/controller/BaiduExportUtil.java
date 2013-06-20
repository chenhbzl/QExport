/*
 * @project :QExport
 * @author  :huqiming 
 * @date    :2013-6-19
 */
package douzifly.android.qexport.controller;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 *
 */
public class BaiduExportUtil {
	private static final String TAG = "BaiduExportUtil";

	private static final String REGEX_VIDEO_NAME = "_?[a-f,0-9]{8}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{12}";
	private static final String REGEX_CACHE_FILE = "((_[0-9]{4})|(_?[a-f,0-9]{8}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{4}\\-[a-f,0-9]{12}\\.bdv))$";
	private static final String REGEX_CACHE_FILE_INDEX = "(_[0-9]{4})$";

	public static String getRealNameFromFolder(String folderName) {
		if (folderName == null) {
			return null;
		}
		return folderName.replaceAll(REGEX_VIDEO_NAME, "");
	}

	public static boolean isBaiduCacheFolder(String folderName) {
		Pattern p = Pattern.compile(REGEX_VIDEO_NAME);
		Matcher m = p.matcher(folderName);
		return m.find();
	}

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

	public static String[] getCacheSubFilePaths(File folder) {
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
			Log.d(TAG, "getCacheSubFilePaths path: " + f.getPath());
			i++;
		}
		return paths;
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
