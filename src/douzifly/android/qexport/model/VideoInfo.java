/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzifly.android.qexport.model;


/**
 * @author douzifly
 *
 */
public class VideoInfo {
	public String name;
	public String[] paths;
	public long size;
	public String hash;
	public int postion;
	public int progress = -1;
	public int mergeSpeed;
	public int mergeSize;
}
