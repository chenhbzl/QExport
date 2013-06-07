/**
 * douzifly @2013-6-8
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.settings;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author douzifly
 *
 */
public class ShareSetting {
    
    public static final String PREF_NAME = "share_setting";
    public static final String KEY_AGREE_SHARE = "agree_share";
    
    public static boolean isAgreeShare(Context ctx){

        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_AGREE_SHARE, false);
    }
    
    public static void setAgreeShare(Context ctx, boolean agree){
        SharedPreferences sp = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_AGREE_SHARE, agree).commit();
    }
}
