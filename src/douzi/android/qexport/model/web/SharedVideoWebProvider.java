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
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import douzi.android.qexport.model.ISharedVideoProvider;
import douzi.android.qexport.model.SharedVideoInfo;

/**
 * 
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoWebProvider implements ISharedVideoProvider{
	
	public final static String URL_RAND = "http://dzsvr.sinaapp.com/rand";
	public final static String URL_UPLOAD = "http://dzsvr.sinaapp.com/add";
	public final static String TAG = "VideoWebProvider";
	
	public final static String URL_ALL = "http://dzsvr.sinaapp.com/?start=0&count=10000";

	AsyncHttpClient mClient = new AsyncHttpClient();
	
	@Override
	public void getVideos(final OnSharedVideoLoadedListener l) {
		mClient.get(URL_ALL, new JsonHttpResponseHandler(){
			
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
			public void onFailure(Throwable e, JSONObject errorResponse) {
				// TODO Auto-generated method stub
				super.onFailure(e, errorResponse);
				Log.d(TAG, "onFailure json");
				l.onVideoLoaded(false, null);
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				super.onFailure(error, content);
				Log.d(TAG, "onFailure content:" + content);
				l.onVideoLoaded(false, null);
			}
			
			@Override
			public void onSuccess(int statusCode, JSONObject response) {
				super.onSuccess(statusCode, response);
				Log.d(TAG, "onSucess jsonObject");
				l.onVideoLoaded(true, null);
			}
			
			@Override
			public void onFinish() {
				super.onFinish();
				Log.d(TAG, "onFinish");
			}
		});
	}

	@Override
	public void updateVideo(SharedVideoInfo v) {
		Log.d(TAG, "updateVideo t:" + v.title + " h:" + v.hash);
		RequestParams params = new RequestParams();
		params.put("t", v.title);
		params.put("h", v.hash);
		mClient.get(URL_UPLOAD, params, new AsyncHttpResponseHandler(){
			@Override
			public void onSuccess(String content) {
				// TODO Auto-generated method stub
				super.onSuccess(content);
				Log.d(TAG, "onSuccess:"+ content);
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				// TODO Auto-generated method stub
				super.onFailure(error, content);
				Log.d(TAG, "onFailure:"+ content);
			}
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				Log.d(TAG, "onFinish");
			}
		});
	}
	
}
