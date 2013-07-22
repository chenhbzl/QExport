/*
 * @project :QExport
 * @auther  :huqiming 
 * @date    :2013-7-22
 */
package douzifly.android.qexport.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileUtil {

	public static String readFile(String path) {
		if (path == null) {
			return null;
		}

		File file = new File(path);
		return readFile(file);
	}

	public static String readFile(File file) {
		if (file == null || !file.exists()) {
			return null;
		}

		BufferedReader reader = null;
		String result = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			result = sb.toString();
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
