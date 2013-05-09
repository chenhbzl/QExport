/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import douzi.android.qexport.R;
import douzi.android.qexport.R.id;
import douzi.android.qexport.R.layout;
import douzifly.android.qexport.model.SharedVideoInfo;

/**
 * @author Xiaoyuan Lau
 *
 */
public class SharedVideoAdapter extends BaseAdapter {
	public SharedVideoAdapter(Context ctx){
		mContext = ctx; 
	}
	
	private Context mContext;
	private List<SharedVideoInfo> mVideos;
	public void setVideos(List<SharedVideoInfo> videos){
		mVideos = videos;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mVideos == null ? 0 : mVideos.size();
	}

	@Override
	public SharedVideoInfo getItem(int arg0) {
		if(mVideos == null){
			return null;
		}
		return mVideos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		View v = null;
		ViewHolder tag;
		if(arg1 == null){
			LayoutInflater lf = (LayoutInflater) mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = lf.inflate(R.layout.list_shared_item, null);
			ViewHolder holder = new ViewHolder();
			holder.textTitle = (TextView)v.findViewById(R.id.txt_title);
			v.setTag(holder);
			tag = holder;
		}else{
			tag = (ViewHolder)arg1.getTag();
			v = arg1;
		}
		updateTag(tag, arg0);
		return v;
	}
	
	public static class ViewHolder{
		public TextView textTitle;
	}

	private void updateTag(ViewHolder tag, int pos){
		SharedVideoInfo v = mVideos.get(pos);
		tag.textTitle.setText(v.title);
	}
}
