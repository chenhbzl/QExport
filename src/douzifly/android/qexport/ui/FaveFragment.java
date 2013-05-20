package douzifly.android.qexport.ui;

import android.R;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import douzifly.android.qexport.model.SharedVideoInfo;
import douzifly.android.qexport.model.db.CacheManager;
import douzifly.android.qexport.model.db.VideoCache;
import douzifly.android.qexport.utils.UMengHelper;

import java.util.List;

/**
 * Created by douzifly on 13-5-20.
 */
public class FaveFragment extends BaseFragment{

    final static String TAG = "FaveFragment";
    ListView listView;
    List<SharedVideoInfo> mVideos;

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
        try{
            CacheManager<SharedVideoInfo> cache = new CacheManager<SharedVideoInfo>(new VideoCache());
            mVideos = cache.load(getActivity());
            ArrayAdapter<SharedVideoInfo> adapter = new ArrayAdapter<SharedVideoInfo>(getActivity(), R.layout.simple_list_item_1, mVideos);
            listView.setAdapter(adapter);
        } catch (Exception e){
            Log.d(TAG, "loadVideo exp:" + e.getMessage());
        }
    }
}

