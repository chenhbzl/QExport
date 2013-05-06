/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzi.android.qexport.model.web;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import douzi.android.qexport.model.ISharedVideoProvider;
import douzi.android.qexport.model.SharedVideoInfo;

/**
 * 
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoWebProvider implements ISharedVideoProvider{
	
	public final static String URL_RAND = "http://dzsvr.sinaapp.com/rand";
	public final static String TAG = "VideoWebProvider";

	AsyncHttpClient mClient = new AsyncHttpClient();
	
	@Override
	public void getVideos(final OnSharedVideoLoadedListener l) {
		mClient.get(URL_RAND, new JsonHttpResponseHandler(){
			
			@Override
			public void onSuccess(JSONArray response) {
				super.onSuccess(response);
				Log.d(TAG, "onSuccess content:" + response);
				List<SharedVideoInfo> videos = new ArrayList<SharedVideoInfo>();
				for(int i = 0; i < response.length(); i++){
					SharedVideoInfo v = new SharedVideoInfo();
					try {
						JSONObject obj = response.getJSONObject(i);
						v.title = obj.getString("title");
						v.id = obj.getInt("id");
						v.hash = obj.getString("hash");
						videos.add(v);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				l.onVideoLoaded(true, videos);
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				Log.d(TAG, "onFailure content:" + content);
				l.onVideoLoaded(false, null);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.d(TAG, "onFinish");
			}
		});
	}
	
}
