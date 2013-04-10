/**
 * douzifly @2013-3-24
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.uilib;

import android.view.View.MeasureSpec;

/**
 * @author douzifly
 *
 */
public class Helper {
    public static String getModeDesc(int mode){
        if(mode == MeasureSpec.AT_MOST){
            return "AT_MOST";
        }
        if(mode == MeasureSpec.EXACTLY){
            return "EXACTLY";
        }
        if(mode == MeasureSpec.UNSPECIFIED){
            return "UNSPECIFIED";
        }
        return "UNKNOWN";
    }
}
