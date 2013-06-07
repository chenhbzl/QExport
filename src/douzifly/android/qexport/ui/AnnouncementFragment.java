/**
 * douzifly @2013-6-8
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.qexport.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.CheckBox;
import douzi.android.qexport.R;

/**
 * @author douzifly
 *
 */
public class AnnouncementFragment extends DialogFragment{

    CheckBox mChbAgreeShare;
    
    OnAnnouncementChooseListner mListener;
    
    public void setListner(OnAnnouncementChooseListner l){
        mListener = l;
    }
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
        View v = getActivity().getLayoutInflater().inflate(R.layout.announcement, null);
        mChbAgreeShare = (CheckBox) v.findViewById(R.id.announcementCboxAgree);
        Dialog d = b.setView(v).setTitle(R.string.announcementTitle).setPositiveButton(R.string.confirm, new OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(mListener != null){
                    mListener.onAnnouncementClosed(mChbAgreeShare.isChecked());
                }
            }
        }).setCancelable(false).create();
        setCancelable(false);
        return d;
    }
    
    public static interface OnAnnouncementChooseListner{
        void onAnnouncementClosed(boolean agree);
    }
    
    public static void showAnnouncement(FragmentManager fm, OnAnnouncementChooseListner l){
        AnnouncementFragment announcement = new AnnouncementFragment();
        announcement.setListner(l);
        announcement.show(fm, "announcement");
    }
}
