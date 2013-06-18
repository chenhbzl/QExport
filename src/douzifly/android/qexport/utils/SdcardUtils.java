/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.utils;

import android.os.Environment;

/**
 * @author douzifly
 *
 */
public class SdcardUtils {
    
    public static String getRootPath(){
        return Environment.getExternalStorageDirectory().toString();
    }
    
    // TODO return multiple sdcard root path
    public static String[] getRootPaths(){
        return null;
    }
    
}
