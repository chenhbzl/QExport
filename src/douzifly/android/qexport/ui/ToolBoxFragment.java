/**
 * douzifly @Jul 17, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.ui;

import douzi.android.qexport.R;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author douzifly
 *
 */
public class ToolBoxFragment extends BaseFragment implements OnClickListener{
    
    private View mBtnFave;
    private View mBtnTransfer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tool_box, null);
        setupView(root);
        return root;
    }
    
    private void setupView(View root) {
        mBtnFave = root.findViewById(R.id.btnFaveContainer);
        mBtnTransfer = root.findViewById(R.id.btnTransferContainer);
        
        mBtnFave.setOnClickListener(this);
        mBtnTransfer.setOnClickListener(this);
    }
    
    @Override
    public boolean showRefreshButton() {
        return false;
    }

    @Override
    public void onClick(View v) {
        Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
    }
    
}
