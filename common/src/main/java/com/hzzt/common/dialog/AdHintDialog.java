package com.hzzt.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.hzzt.common.R;
import com.hzzt.common.databinding.DialogAdHintBinding;
import com.hzzt.common.databinding.DialogDisconnectServerBinding;


/**
 * @author: Allen
 * @date: 2022/7/22
 * @description: 断开服务器对话框
 */
public class AdHintDialog extends Dialog {
    private DialogAdHintBinding binding;
    private static AdHintDialog dialog;
    private Context context;

    public static AdHintDialog getInstance(Context context) {
        if (dialog == null) {
            synchronized (AdHintDialog.class) {
                if (dialog == null) {
                    dialog = new AdHintDialog(context);
                }
            }
        }
        return dialog;
    }

    public AdHintDialog(Context context) {
        super(context, R.style.MyDialog2);
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_ad_hint, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (screenWidth * 0.85);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        getWindow().setWindowAnimations(R.style.anim_style);// 设置动画
    }

}
