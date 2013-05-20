/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.model;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoInfo {
	public int id;
	public String title;
	public String hash;
	public int tipOffCount;
	public int collectionCount;
	public int playCount;
	
	@Override
	public String toString() {
//		return "id:" + id + " tip_off:" + tipOffCount + " collection:" + collectionCount
//				+ " playCount:" + playCount;
	    return title;
    }
}
