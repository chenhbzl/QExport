/**
 * douzifly @2013-6-8
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.utils;

import douzifly.android.qexport.settings.YoumiSetting;
import android.content.Context;
import net.youmi.android.AdManager;

/**
 * @author douzifly
 *
 */
public class YoumiHelper {

    public static void init(Context ctx){
        AdManager.getInstance(ctx).init(YoumiSetting.APP_ID, YoumiSetting.APP_KEY, YoumiSetting.DEBUG_MODE);
    }
}
