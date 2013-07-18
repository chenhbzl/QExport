/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.ui.toolbox;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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
    
    Button mBtnToggle;
    TextView mTxtInfo;
    boolean mTransportEnable = false;
    QHttpServer mServer;
    
    @Override
    public String getTitle() {
        return "传输到电脑";
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServer = new QHttpServer();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.transport, null);
        setupView(root);
        return root;
    }
    
    private void setupView(View root){
        mBtnToggle = (Button) root.findViewById(R.id.btnTransportToggle);
        mTxtInfo = (TextView) root.findViewById(R.id.txtInfo);
        
        mBtnToggle.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        toggleTransport();
                    }
                }).start();
               
            } 
        });
        updateUI();
    }
    
    private void toggleTransport(){
       mTransportEnable = !mTransportEnable;
       if(mTransportEnable){
           try {
            mServer.start();
        } catch (final Exception e) {
            e.printStackTrace();
            getActivity().runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "开启失败:"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
           
            mTransportEnable = false;
        }
       }else{
           mServer.stop();
       }
       
       getActivity().runOnUiThread(new Runnable() {
        
        @Override
        public void run() {
            updateUI();
        }
       });
       
    }
    
    private void updateUI(){
       mBtnToggle.setText(mTransportEnable ? R.string.close_transport : R.string.open_transport);
       mTxtInfo.setText(mTransportEnable ? mServer.getListenAddr() : "");
    }
}
