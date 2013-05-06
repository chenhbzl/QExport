package douzi.android.qexport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import douzi.android.qexport.QExport.ExportListener;
import douzifly.android.uilib.GridProgressBar;

public class MainActivity extends SherlockActivity 
       implements OnClickListener,ExportListener,OnItemClickListener, OnNavigationListener {
	
	QExport            mQExport;
	Button             mScanButton;
	ListView           mResultListView;
	String             cacheFolder = "p2pcache";
	String             exportFolder = "p2pMerged";
	static             String TAG = "MainActivity";
	ResultAdapter      mAdapter;
	List<Integer>      mMergeing = new ArrayList<Integer>();
	GridProgressBar    mProgress;
	
	String[]		   mNavigations = new String[]{"我的合并", "合并分享"};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setupView();
	}
	
	private void setupView(){
		mScanButton = (Button) findViewById(R.id.btnScan);
		mResultListView = (ListView) findViewById(R.id.listResult);
		mProgress = (GridProgressBar) findViewById(R.id.progressBar);

		
		mAdapter = new ResultAdapter(this);
		mResultListView.setAdapter(mAdapter);
		
		mScanButton.setOnClickListener(this);
		mResultListView.setOnItemClickListener(this);
		setProgressBarIndeterminateVisibility(false);
		
		ActionBar bar = getSupportActionBar();
		bar.setHomeButtonEnabled(true);
		bar.setTitle("");
		bar.setBackgroundDrawable(new ColorDrawable(0xff0099cc));
		
		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		
		bar.setListNavigationCallbacks(new ArrayAdapter<String>(this, R.layout.navigation_text, mNavigations), this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    if(item.getItemId() == android.R.id.home){
	        Toast.makeText(this, "douzifly@gmail.com", Toast.LENGTH_SHORT).show();
	    }else if(item.getItemId() == 100){
	        scan();
	    }
	    return super.onOptionsItemSelected(item);
	}

	
	private void scan(){
		if(mQExport == null){
			mQExport = new QExport();
			mQExport.setExportListener(this);
		}
		setProgressBarIndeterminateVisibility(true);
		String folder = Environment.getExternalStorageDirectory() + "/" + cacheFolder;
		mScanButton.setEnabled(false);
		mQExport.scan(folder);
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    scan();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnScan:
			scan();
			break;

		default:
			break;
		}
	}

	@Override
	public void onScanOk() {
		Log.d(TAG,"onScanOk");
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				setProgressBarIndeterminateVisibility(false);
				mScanButton.setEnabled(true);
				List<VideoInfo> orgin = mQExport.getVideos();
				List<VideoInfo> copy = null;
				if(orgin != null && orgin.size() > 0){
				  copy = new ArrayList<VideoInfo>(orgin);
				}
				if(copy == null){
				    Toast.makeText(MainActivity.this, "没有发现视频", Toast.LENGTH_SHORT).show();
				}
				mAdapter.setVideos(copy);
			}
		});
	}

	@Override
	public void onMergeOk(final VideoInfo v,final boolean sucess) {
	    mMergeing.remove((Integer)v.postion);
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				if(!sucess){
					Toast.makeText(getApplicationContext(), "Failed:" + v.name, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final VideoInfo v = mAdapter.getItem(arg2);
		if(v == null){
			Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
			return;
		}
		
		for(Integer i : mMergeing){
		    if(i == arg2){
		        Toast.makeText(this, "merging...", Toast.LENGTH_SHORT).show();
		        return;
		    }
		}
		
		final String target = Environment.getExternalStorageDirectory() + "/" + exportFolder + "/" + v.name;
	    String msg = target;
		File f = new File(target);
		if(f.exists() && f.length() == v.size){
			 new AlertDialog.Builder(this).setTitle("已经合并啦")
				.setMessage("播放?")
				.setPositiveButton("播放", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent i = new Intent(Intent.ACTION_VIEW);
					String type = "video/*";
			        Uri uri = Uri.parse(target);
			        i.setDataAndType(uri, type);
			        try{
			        	startActivity(i);
			        }catch(Exception e){
			        	Toast.makeText(getApplicationContext(), "无法播放，请安装块播播放器",  Toast.LENGTH_SHORT).show();
			        }
				}
				}).setNegativeButton("重新合并", new DialogInterface.OnClickListener() {
                    
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMergeing.add(v.postion);
                        mQExport.merge(v, target);
                    }
                })
				.show();
			
			return;
		}
	    new AlertDialog.Builder(this).setTitle("保存到:")
	    							.setMessage(msg)
	    							.setPositiveButton("合并", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
					    mMergeing.add(v.postion);
						mQExport.merge(v, target);
					}
	    		}).setNegativeButton("算了", null)
	    		.show();
	    
	}

	@Override
	public void onMergeProgress(final VideoInfo v,final int progress, final int speed, final int writed ) {
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				Log.d("debug","updateProgress:" + progress + " " + v.name);
				mAdapter.updateProgress(v.postion, progress, speed, writed);	
				mProgress.setProgress(progress);
			}
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(this)
                    .setTitle("退出")
                    .setMessage("确定吗?")
                    .setPositiveButton("好",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    finish();
                                }
                            }).setNegativeButton("先不", null ).show();
            return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    android.os.Process.killProcess(Process.myPid());
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		return false;
	}

}
