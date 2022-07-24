package com.hzzt.common.listener;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import androidx.annotation.NonNull;


/**
 * @author: Allen
 * @date: 2021/6/9
 * @description: 点击textview设置
 */
public class MyClickableSpan extends ClickableSpan {
    private Context context;

    public MyClickableSpan(Context context) {
        this.context = context;
    }

    @Override
    public void onClick(@NonNull View widget) {
//        //这里的判断是为了去掉在点击后字体出现的背景色
//        if (widget instanceof TextView) {
//            ((TextView) widget).setHighlightColor(Color.TRANSPARENT);
//        }
//        //在这里写下你想要的点击效果
//        context.startActivity(newIntent(context, AgreementActivity.class));
    }

    @Override
    public void updateDrawState(TextPaint ds) {
   //  ds.setColor(ContextCompat.getColor(context, R.color.main_color));  // 通过这里设置出来的颜色有差别
    }




}
