/**
 * douzifly @Jun 18, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.controller;

import java.io.File;

import android.util.Log;
import douzifly.android.qexport.settings.AppSetting;
import douzifly.android.qexport.utils.NetworkUtils;
import fi.iki.elonen.SimpleWebServer;

/**
 * 
 * 提供文件传输支持
 * @author douzifly
 *
 */
public class QHttpServer {
    
    static final String TAG = "QHttpServer";

    SimpleWebServer mServer;
    
    String mListenerAddr = "";
    
    public synchronized void start() throws Exception{
        String wwwRoot = AppSetting.getExportFolder();
        File f = new File(wwwRoot);
        if(!f.exists()) {
        	f.mkdirs();
        }
        boolean quiet = false;
        int port = 8080;
        Log.d(TAG, "start " + wwwRoot + " q:" + quiet);
        if(mServer != null){
            Log.e(TAG, "already started");
            return ;
        }
        String host = NetworkUtils.getLocalIP();
        if(host == null){
            throw new RuntimeException("cant get host");
        }
        Log.d(TAG, "host:" + host);
        File root = new File(wwwRoot);
        if(!root.isDirectory() || !root.exists()){
            throw new IllegalArgumentException("wwwRoot is not directory or not exists");
        }
        mListenerAddr = host + ":" + port;
        mServer = new SimpleWebServer(host, port, root, quiet);
        mServer.start();
    }
    
    public String getListenAddr(){
        return mListenerAddr;
    }
    
    public synchronized void stop(){
        Log.d(TAG,  "stop");
        if(mServer == null){
            return;
        }
        mServer.stop();
        mServer = null;
    }
}
