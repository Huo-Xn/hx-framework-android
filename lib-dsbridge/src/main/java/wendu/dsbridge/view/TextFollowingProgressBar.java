package wendu.dsbridge.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;


/**
 * @author: admin
 * @date: 2022/9/17
 */
public class TextFollowingProgressBar extends ProgressBar {

    private static final String TAG = "TextFollowingProgressBar";
    private Paint mTextPaint;
    private String text;

    public TextFollowingProgressBar(Context context) {
        super(context);
    }

    public TextFollowingProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    private void initPaint() {
        //初始化，画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(24);
        mTextPaint.setAntiAlias(true);
    }

    public TextFollowingProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public TextFollowingProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public synchronized void setProgress(int progress) {
        setText(progress);
        super.setProgress(progress);
    }

    public void setTextColor(int color){
        mTextPaint.setColor(color);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = new Rect();
        mTextPaint.getTextBounds(this.text, 0, this.text.length(), rect);
//        获取一份宽度
        double percentileWidth = (double) getWidth() / 100;
//        获取文字高度
        int y = (getHeight() / 2) - rect.centerY();

        if (getProgress()>=90) {
            canvas.drawText(this.text, (float) (90 * percentileWidth), y, mTextPaint);
        }else {
            canvas.drawText(this.text, (float) (getProgress() * percentileWidth), y, mTextPaint);
        }

    }

    //设置文字内容
    private void setText(int progress) {
        if (progress ==100) {
            mTextPaint.setTextSize(20);
            this.text = "完成";
        }else {
            this.text = String.valueOf(progress) + "%";
        }

    }



//        if (getProgress() >= 0 && getProgress() <= 5) {
//        } else {
//            String pro =
//                    new BigDecimal(percentileWidth * getProgress()).setScale(0, BigDecimal.ROUND_HALF_UP).toString();
//            if (getProgress() > 5 && getProgress() <= 10) {
//                int x = Integer.valueOf(pro) - rect.centerX() * 2;
//                canvas.drawText(this.text, x, y, mTextPaint);
//            } else {
//                int x = Integer.valueOf(pro) - rect.centerX() * text.length();
//                canvas.drawText(this.text, x, y, mTextPaint);
//            }
//        }

}
