package com.hzzt.common.widget.timer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;

import com.hzzt.common.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author: Allen
 * @date: 2022/7/23
 * @description: 倒计时
 */
public class CountDownView extends View {
    /**
     * 填充背景画笔
     */
    private Paint bgPaint;
    /**
     * 文字画笔
     */
    private TextPaint textPaint;
    /**
     * 分隔号画笔
     */
    private TextPaint semicolonPaint;

    private int frameCount = 3;
    private float bgCenterY;
    /**
     * 输入框的宽高
     */
    private int tvWidthSize = dip2px(80);

    /**
     * 圆角大小
     */
    private int radius = dip2px(0);
    /**
     * 圆角填充颜色
     */
    private int mBgColor = Color.BLACK;
    /**
     * 文字颜色
     */
    private int mTextColor = Color.WHITE;

    /**
     * 文字大小
     */
    private int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 50, getResources().getDisplayMetrics());

    /**
     * 分号颜色
     */
    private int mSemicolonColor = Color.BLACK;

    /**
     * 分号大小
     */
    private int semicolonSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 60, getResources().getDisplayMetrics());


    /**
     * 边框间隔颜色
     */
    private int intervalSize = dip2px(20);

    /**
     * 倒计时时间
     * 单位秒
     */
    private int countDownTime = 60;
    /**
     * 是否默认启动倒计时
     */
    private boolean isAutoPlay = false;
    private OnCallBackListener onCallBackListener;

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownView);
        intervalSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_ctv_interval_width, intervalSize);
        radius = typedArray.getDimensionPixelSize(R.styleable.CountDownView_ctv_radius, radius);
        textSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_ctv_text_size, textSize);
        tvWidthSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_ctv_text_width, tvWidthSize);
        mTextColor = typedArray.getColor(R.styleable.CountDownView_ctv_text_color, mTextColor);
        semicolonSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_ctv_text_size, semicolonSize);
        mSemicolonColor = typedArray.getColor(R.styleable.CountDownView_ctv_semicolon_color, mSemicolonColor);
        mBgColor = typedArray.getColor(R.styleable.CountDownView_ctv_bg, mBgColor);
        countDownTime = typedArray.getInt(R.styleable.CountDownView_ctv_time, countDownTime);
        isAutoPlay = typedArray.getBoolean(R.styleable.CountDownView_ctv_auto_play, isAutoPlay);
        typedArray.recycle();
        setBackgroundColor(Color.TRANSPARENT);
        // 增加文本监听器.
        bgPaint = new Paint();
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(mBgColor);

        // 增加文本监听器.
        textPaint = new TextPaint();
        textPaint.setColor(mTextColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        // 增加文本监听器.
        semicolonPaint = new TextPaint();
        semicolonPaint.setColor(mSemicolonColor);
        semicolonPaint.setAntiAlias(true);
        semicolonPaint.setFakeBoldText(true);
        semicolonPaint.setStrokeWidth(3f);
        semicolonPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        semicolonPaint.setTextSize(semicolonSize);
        if (isAutoPlay) start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float bgWidth;
        bgWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        float bgHeight;
        if (heightMode == MeasureSpec.EXACTLY) {
            bgHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        } else {
            bgHeight = tvWidthSize + dip2px(2);
        }
        bgCenterY = bgHeight / 2;
        setMeasuredDimension((int) bgWidth, (int) bgHeight);
    }


    public void setCountDownTime(int countDownTime) {
        setCountDownTime(countDownTime, false);
    }

    public void setCountDownTime(int countDownTime, boolean isStart) {
        this.countDownTime = countDownTime;
        if (isStart) {
            stop();
            start();
        }
    }


    /**
     * 启动倒计时
     */
    public void start() {
        if (timer == null && countDownTime > 0) {
            timer = new Timer();
            timer.schedule(timerTask= new TimerTask() {
                @Override
                public void run() {
                    countDownTime--;
                    invalidate();
                    if (onCallBackListener != null)
                        onCallBackListener.onTick(countDownTime);
                    if (countDownTime <= 0) {
                        timerTask.cancel();
                        timer.cancel();
                        timer = null;
                    }
                }
            }, 1000, 1000);
        }
    }

    /**
     * 停止倒计时倒计时
     */
    public void stop() {
        if (timer != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStatEndRadiusRect(frameCount, canvas);
        canvas.save();
        drawText(frameCount, canvas);
        canvas.save();
        drawSemicolon(frameCount, canvas);
        canvas.save();
    }

    /**
     * 绘制前后圆角输入框不支持焦点输入框
     */
    private void drawStatEndRadiusRect(int count, Canvas canvas) {
        if (radius == 0) {
            radius = dip2px(5);
        }
        int left = (getWidth() - count * (tvWidthSize) - intervalSize * (count - 1)) / 2;
        for (int i = 0; i < count; i++) {
            RectF rectF = new RectF(left + (tvWidthSize + intervalSize) * i,
                    bgCenterY - tvWidthSize / 2, left + (tvWidthSize + intervalSize) * i + tvWidthSize, bgCenterY + tvWidthSize / 2);
            canvas.drawRoundRect(rectF, radius, radius, bgPaint);
        }
    }


    /**
     * 绘制文字
     */
    private void drawText(int count, Canvas canvas) {
        String[] times = formatTime(countDownTime).split(":");
        int left = (getWidth() - count * (tvWidthSize) - intervalSize * (count - 1)) / 2;
        for (int i = 0; i < count; i++) {
            String text = times[i];
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            float textWidth = Layout.getDesiredWidth(text, textPaint);
            int textHeight = rect.height();
            canvas.drawText(text, left + (tvWidthSize + intervalSize) * i + tvWidthSize / 2 - textWidth / 2, bgCenterY + textHeight / 2, textPaint);
        }
    }


    /**
     * 填充边框颜色
     */
    public void setBgPaintColor(int color) {
        this.bgPaint.setColor(color);
    }

    //文字 冒号
    public void setTextPointColor(int color){
        this.textPaint.setColor(color);
        this.semicolonPaint.setColor(color);
    }

    /**
     * 绘制分号
     */
    private void drawSemicolon(int count, Canvas canvas) {
        int left = (getWidth() - count * (tvWidthSize) - intervalSize * (count - 1)) / 2;
        String text = ":";
        Rect rect = new Rect();
        semicolonPaint.getTextBounds(text, 0, text.length(), rect);
        float textWidth = Layout.getDesiredWidth(text, semicolonPaint);
        int textHeight = rect.height();
        for (int i = 0; i < count - 1; i++) {
            canvas.drawText(text, left + (tvWidthSize + intervalSize) * (i + 1) - intervalSize / 2 - textWidth / 2, bgCenterY + textHeight / 2, semicolonPaint);
        }
    }

    /**
     * 把密度转换为像素
     */
    private int dip2px(float px) {
        final float scale = getScreenDensity();
        return (int) (px * scale + 0.5);
    }

    /**
     * 得到设备的密度
     */
    private float getScreenDensity() {
        return getResources().getDisplayMetrics().density;
    }

    private Timer timer;
    private TimerTask timerTask;
//    private TimerTask timerTask = new TimerTask() {
//        @Override
//        public void run() {
//            countDownTime--;
//            invalidate();
//            if (onCallBackListener != null)
//                onCallBackListener.onTick(countDownTime);
//            if (countDownTime <= 0) {
//                timerTask.cancel();
//                timer.cancel();
//                timer = null;
//            }
//        }
//    };


    /**
     * 秒转成 00：00：00
     *
     * @param seconds
     * @return
     */
    private String formatTime(long seconds) {
        int temp;
        StringBuffer sb = new StringBuffer();
        if (seconds >= 3600) {
            temp = (int) (seconds / 3600);
            sb.append((seconds / 3600) < 10 ? "0" + temp + ":" : temp + ":");
        } else {
            sb.append("00:");
        }
        temp = (int) (seconds % 3600 / 60);
        changeSeconds(seconds, temp, sb);
        return sb.toString();
    }

    private void changeSeconds(long seconds, int temp, StringBuffer sb) {
        sb.append((temp < 10) ? "0" + temp + ":" : "" + temp + ":");
        temp = (int) (seconds % 3600 % 60);
        sb.append((temp < 10) ? "0" + temp : "" + temp);
    }

    public void setOnCallBackListener(OnCallBackListener onCallBackListener) {
        this.onCallBackListener = onCallBackListener;
    }

    public interface OnCallBackListener{
        void onTick(int downTimer);
    }
}
