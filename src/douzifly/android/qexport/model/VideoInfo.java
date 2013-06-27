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
    /** describe video comes from qvod */
    public final static int SOURCE_QVOD = 0;
    /** describe video comes from baidu */
    public final static int SOURCE_BAIDU = 1;
    
    public final static int SOURCE_SOHU = 2;
    
    public final static int SOURCE_YOUKU = 3; 
	public String name;
	public String[] paths;
	public long size;
	public String hash;
	public int postion;
	public int progress = -1;
	public int mergeSpeed;
	public int mergeSize;
	
	/**
	 * video source  such as {@link #SOURCE_QVOD} etc...
	 */
	public int source;
}
