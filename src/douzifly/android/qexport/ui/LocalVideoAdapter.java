/**
 * Project:QDroid
 * Author:XiaoyuanLau
 * Company:QVOD Ltd.
 */
package douzifly.android.qexport.ui;

import java.util.List;

import douzi.android.qexport.R;
import douzi.android.qexport.R.drawable;
import douzi.android.qexport.R.id;
import douzi.android.qexport.R.layout;
import douzifly.android.qexport.model.VideoInfo;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * @author douzifly
 *
 */
public class LocalVideoAdapter extends BaseAdapter{

	public LocalVideoAdapter(Context ctx){
		mContext = ctx; 
	}
	
	private Context mContext;
	private List<VideoInfo> mVideos;
	public void setVideos(List<VideoInfo> videos){
		mVideos = videos;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
//		Log.d("debug","getCount:" + mVideos);
		return mVideos == null ? 0 : mVideos.size();
	}

	@Override
	public VideoInfo getItem(int arg0) {
//		Log.d("debug","getItem mVideos:" + mVideos);
		if(mVideos == null){
			return null;
		}
		return mVideos.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
//		Log.d("debug","getItemId:" + arg0 + " mVideos:" + mVideos);
		return arg0;
	}
	

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
//		Log.d("debug","getView:" + mVideos);
		View v = null;
		ViewHolder tag;
		if(arg1 == null){
			LayoutInflater lf = (LayoutInflater) mContext.
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = lf.inflate(R.layout.list_result_item, null);
			ViewHolder holder = new ViewHolder();
			holder.textName = (TextView)v.findViewById(R.id.txtName);
			holder.textSize = (TextView)v.findViewById(R.id.txtSize);
			holder.textProgress = (TextView) v.findViewById(R.id.txtProgress);
			holder.progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
			holder.mergeOk = v.findViewById(R.id.icon_ok);
			holder.container = v;
			v.setTag(holder);
			tag = holder;
		}else{
			tag = (ViewHolder)arg1.getTag();
			v = arg1;
		}
		updateTag(tag, arg0);
//		Log.d("debug","getView return:"+v);
		return v;
	}
	
	public static class ViewHolder{
		public TextView textName;
		public TextView textSize;
		public TextView textProgress;
		public View container;
		public ProgressBar progressBar;
		public View		mergeOk;
	}

	private void updateTag(ViewHolder tag, int pos){
		VideoInfo v = mVideos.get(pos);
		tag.textName.setText(v.name);
		if(v.progress > 0 && v.progress < 100){
		    tag.textSize.setText(parseFileSize(v.mergeSize, true)+ "/" + parseFileSize(v.size, true));
			tag.textProgress.setVisibility(View.VISIBLE);
			//tag.textProgress.setText(parseFileSize(v.mergeSpeed, true)+"/s");
			tag.textProgress.setText(String.format("%02d%%", v.progress));
			tag.progressBar.setVisibility(View.VISIBLE);
		}else{
		    tag.textSize.setText(parseFileSize(v.size, true));
			tag.textProgress.setVisibility(View.GONE);
			tag.progressBar.setVisibility(View.GONE);
		}
		if(v.progress == 100){
			tag.textProgress.setVisibility(View.GONE);
			tag.progressBar.setVisibility(View.GONE);
			tag.mergeOk.setVisibility(View.VISIBLE);
		}
	}
	
	public void updateProgress(int pos, int progress, int speed, int size){
		if(mVideos != null && mVideos.size() > pos){
		    VideoInfo v = mVideos.get(pos);
			v.progress = progress;
			v.mergeSize = size;
			v.mergeSpeed = speed;
			notifyDataSetChanged();
		}
	}
	
	public static String parseFileSize(long length, boolean hasUnit) {

		int i = 0;
		while (length > 1024) {
			length = length / 1024;
			i++;
		}

		if (hasUnit) {
			String[] syn = { "B", "KB", "M", "G" };
			return length + syn[i];
		} else {
			return length + "";
		}
	}
	
}
