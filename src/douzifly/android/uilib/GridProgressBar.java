/**
 * douzifly @2013-3-23
 * github.com/douzifly
 * douzifly@gmail.com
 */
package douzifly.android.uilib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 
 * �������������windows��Ƭ�������
 * @author douzifly
 *
 */
public class GridProgressBar extends View {
    
    private final static String TAG = "GridProgressBar";
    private final static int    DEFAULT_SIZE = 30;
    
    private int     mNormalColor = 0xff0099cc;
    private int     mCoverColor  = 0xff669900;
    private Paint   mPaint       = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int     mColumCount  = 10;
    private int     mRowCount    = 0;
    private int     mGridSize    = 20; // wrap content ��Ч�������Զ�����
    private int     mMax         = 100;
    private int     mProgress    = 0;

    static class Grid{
        int l,t,r,b;
        boolean animating;
    }
  
    public GridProgressBar(Context context) {
        super(context);
    }
    
  
    public GridProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setGridSize(int size){
        mGridSize = size;
        requestLayout();
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int mw = MeasureSpec.getSize(widthMeasureSpec);
        int mh = MeasureSpec.getSize(heightMeasureSpec);
        Log.d(TAG, "mw:" + mw + " mh:" + mh + " wmode:" + Helper.getModeDesc(widthMode) + " hmode:" + Helper.getModeDesc(heightMode));
        if(widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST){
            // �߿�wrap_content
            Log.d(TAG, "AT_MOST"+" mCol:" + mColumCount + " gs:" + mGridSize);
            int w = mw;
            mColumCount = w / DEFAULT_SIZE;
            mGridSize = DEFAULT_SIZE;
            mRowCount = (mMax + mColumCount - 1) / mColumCount;
            setMeasuredDimension(w, mRowCount * mGridSize);
            Log.d(TAG, "mGridSize:" + mGridSize + " mRowCount:" + mRowCount + " mColumCount:" + mColumCount);
            return;
        }else if(widthMode == MeasureSpec.EXACTLY){
            // ���ȷ�����Զ�ȷ���߶ȸ��GridSize
            mColumCount = mw / mGridSize;
            mRowCount = (mMax + mColumCount - 1) / mColumCount;
            setMeasuredDimension(mw, mRowCount * mGridSize);
            Log.d(TAG, "mGridSize:" + mGridSize + " mRowCount:" + mRowCount + " mColumCount:" + mColumCount);
            return;
        }
        
        int width = getWidth();
        int hegith = getHeight();
        Log.d(TAG,"width:" + width + " height:" + hegith);
        mGridSize = width / mColumCount;
        mRowCount = (mMax + mColumCount - 1) / mColumCount;
        Log.d(TAG, "mGridSize:" + mGridSize + " mRowCount:" + mRowCount + " mColumCount:" + mColumCount);
    }
    
    
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Style.FILL);
        for(int i = 0; i < mRowCount ; i++){
            for(int j = 0; j < mColumCount; j++){
                int drawProgress = i * mColumCount + j;
                int l = j * mGridSize + 1;
                int t = i * mGridSize + 1;
                int r = l + mGridSize - 2;
                int b = t + mGridSize - 2;
                if(drawProgress < mProgress){
                    // draw progress 
                    mPaint.setColor(mCoverColor);
                }else{
                    // draw normal
                    mPaint.setColor(mNormalColor);
                }
                if(drawProgress < mMax){
                    canvas.drawRect(l, t, r, b, mPaint);
                }
            }
        }
    }
    
    public void setProgress(int progress){
        if(progress == mProgress){
            return;
        }
        mProgress = progress;
        invalidate();
    }
    

}
