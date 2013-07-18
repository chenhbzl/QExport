/**
 * Project:  QDroid
 * Author:   Xiaoyuan Lau
 * Company:  QVOD Ltd.
 * Date:	2013-5-6
 */
package douzifly.android.qexport.ui.toolbox;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import douzi.android.qexport.R;
import douzifly.android.qexport.model.SharedVideoInfo;

/**
 * @author Xiaoyuan Lau
 *
 */
public class FaveVideoAdapter extends BaseAdapter {
	public FaveVideoAdapter(Context ctx){
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
			int height = arg2.getResources().getDimensionPixelSize(R.dimen.list_item_height);
			v.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, height));
			ViewHolder holder = new ViewHolder();
			holder.textTitle = (TextView)v.findViewById(R.id.txt_title);
			holder.btnTipOff = (Button) v.findViewById(R.id.list_item_btn_tip_off);
			v.setTag(holder);
			holder.btnTipOff.setTag(holder);
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
		public Button   btnTipOff;
		public int      pos;
	}

	private void updateTag(ViewHolder tag, int pos){
		SharedVideoInfo v = mVideos.get(pos);
		tag.textTitle.setText(v.title);
		tag.btnTipOff.setVisibility(View.GONE);
		tag.pos = pos;
	}
	
}
