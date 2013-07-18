/**
 * Author:Xiaoyuan
 * Date: Jul 18, 2013
 * 深圳快播科技
 */
package douzifly.android.qexport.ui.toolbox;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.TextView;
import douzi.android.qexport.R;
import douzifly.android.qexport.ui.BaseFragment;

/**
 * 工具箱选择界面
 * 
 * @author Xiaoyuan
 * 
 */
public class ToolBoxContentFragment extends BaseFragment implements
		OnClickListener {
	
	public final static int  MODULE_FAVE = 0;
	public final static int  MODULE_TRAN = 1;

	private View       mBtnFave;
	private View       mBtnTransfer;
	private TextView   mTxtVersion;
	
	private OnModuleClickListener mListener;
	
	public void setListener(OnModuleClickListener l) {
		mListener = l;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.tool_box_content, null);
		setupView(root);
		return root;
	}

	private void setupView(View root) {
		mBtnFave = root.findViewById(R.id.btnFave);
		mBtnTransfer = root.findViewById(R.id.btnTransfer);

		mBtnFave.setOnClickListener(this);
		mBtnTransfer.setOnClickListener(this);
		mTxtVersion = (TextView) root.findViewById(R.id.txtVersion);
		
		setVersion();
	}
	
	private void setVersion() {
	    try{
	        PackageManager pm = getActivity().getPackageManager();
	        PackageInfo info = pm.getPackageInfo(getActivity().getPackageName(), 0);
	        mTxtVersion.setText(info.versionName);
	    }catch(Exception e) {
	        mTxtVersion.setText("");
	    }
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnFave:
			if(mListener != null) {
				mListener.onModuleClicked(MODULE_FAVE);
			}
			break;
		case R.id.btnTransfer:
			if(mListener != null) {
				mListener.onModuleClicked(MODULE_TRAN);
			}
			break;
		default:
			break;
		}
	}
	
	public static interface OnModuleClickListener{
		void onModuleClicked(int module);
	}
	
	@Override
	public boolean showRefreshButton() {
		return false;
	}
	
	@Override
	public String getTitle() {
		return "我的工具箱";
	}
}
