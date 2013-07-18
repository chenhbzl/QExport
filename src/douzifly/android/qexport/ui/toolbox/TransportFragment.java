/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.ui.toolbox;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import douzi.android.qexport.R;
import douzifly.android.qexport.controller.QHttpServer;
import douzifly.android.qexport.ui.BaseFragment;

/**
 * @author douzifly
 *
 */
public class TransportFragment extends BaseFragment{
    
    final static String TAG = "TransportFragment";
    
    TextView mTxtInfo;
    QHttpServer mServer;
    TextView mTxtTip;
    TextView mTxtTip1;
    
    @Override
    public String getTitle() {
        return "关闭传输";
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.transport, null);
        setupView(root);
        return root;
    }
    
    @Override
    public boolean showRefreshButton() {
        return false;
    }
    
    private void setupView(View root){
        mTxtInfo = (TextView) root.findViewById(R.id.txtInfo);
        mTxtTip = (TextView) root.findViewById(R.id.txtTip);
        mTxtTip1 = (TextView) root.findViewById(R.id.txtTip1);
        updateUI(WHAT_CLOSE);
        start();
    }
    
    @Override
    public void onInto() {
        super.onInto();
        if(getActivity() != null) {
            start();
        }
    }
    
    @Override
    public void onLeave() {
        super.onLeave();
        close();
    }
    
    
    private void close() {
        if(mServer != null) {
            Log.d(TAG, "stop");
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    mServer.stop();
                    mHandler.sendEmptyMessage(WHAT_CLOSE);
                }
            }).start();
          
        }
    }
    
    final static int WHAT_START = 0;
    final static int WHAT_CLOSE = 1;
    final static int WHAT_FAILED = 2;
    
    Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            updateUI(msg.what);
        };
    };
    
    private void start(){
        
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                try {
                    mServer = new QHttpServer();
                    Log.d(TAG, "start");
                    mServer.start();
                    mHandler.sendEmptyMessage(WHAT_START);
                } catch (final Exception e) {
                    mHandler.sendEmptyMessage(WHAT_FAILED);
                }
            }
        }).start();
        
        
    }
    
    private void updateUI(int what){
       mTxtTip.setVisibility((what == WHAT_START || what == WHAT_FAILED)? View.VISIBLE : View.GONE);
       if(what == WHAT_FAILED){
           mTxtTip.setText("开启失败了，确保手机已经开启wifi");
       }
       mTxtTip1.setVisibility(what == WHAT_START ? View.VISIBLE : View.GONE);
       mTxtInfo.setVisibility(what == WHAT_START ? View.VISIBLE : View.GONE);
       mTxtInfo.setText(what == WHAT_START ? "http://" + mServer.getListenAddr() : "");
       
    }
}
