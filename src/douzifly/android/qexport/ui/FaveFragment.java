package douzifly.android.qexport.ui;

import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.Toast;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.db.CacheManager;
import douzifly.android.qexport.model.db.VideoCache;
import douzifly.android.qexport.utils.UMengHelper;

/**
 * Created by douzifly on 13-5-20.
 */
public class FaveFragment extends BaseFragment{

    final static String TAG = "FaveFragment";
    ListView listView;
    List<SharedVideoInfo> mVideos;
    FaveVideoAdapter mAdapter;

    @Override
    public String getTitle() {
        return "我的收藏";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                SharedVideoInfo v = mVideos.get(pos);
                Intent i = new Intent("QvodPlayer.VIDEO_PLAY_ACTION");
                String type = "video/*";
                Uri uri = Uri.parse(v.hash);
                i.setDataAndType(uri, type);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                try{
                    startActivity(i);
                    UMengHelper.logPlayShare(getActivity(), v.id);
                }catch(Exception e){
                    Toast.makeText(getActivity(), "先装个快播再播放吧", Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        listView.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                try{
                    SharedVideoInfo v = mAdapter.getItem(arg2);
                    CacheManager<SharedVideoInfo> cm = new CacheManager<SharedVideoInfo>(new VideoCache());
                    cm.remove(getActivity(), v.id);
                    Toast.makeText(getActivity(), douzi.android.qexport.R.string.delete_sucess, Toast.LENGTH_SHORT).show();
                    loadVideo();
                }catch(Exception e){
                    Log.d(TAG, "onItemLongClick e:" + e.getMessage());
                }
                return true;
            }
        });
        
        loadVideo();
        return listView;
    }

    @Override
    public void onRefreshPressed() {
        loadVideo();
    }

    @Override
    public void onInto() {
        loadVideo();
    }

    void loadVideo(){
        showProgressOnActionBar();
        try{
            CacheManager<SharedVideoInfo> cache = new CacheManager<SharedVideoInfo>(new VideoCache());
            mVideos = cache.load(getActivity());
            if(mAdapter == null){
                mAdapter = new FaveVideoAdapter(getActivity());
            }
            mAdapter.setVideos(mVideos);
            listView.setAdapter(mAdapter);
        } catch (Exception e){
            Log.d(TAG, "loadVideo exp:" + e.getMessage());
        }
        hideProgressOnActionBar();
    }
}

