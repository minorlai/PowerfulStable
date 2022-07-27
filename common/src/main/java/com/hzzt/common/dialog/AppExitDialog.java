package com.hzzt.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.hzzt.common.R;
import com.hzzt.common.databinding.DialogExitBinding;


/**
 * @author: Allen
 * @date: 2022/7/22
 * @description: 退出对话框
 */
public class AppExitDialog extends Dialog {
    private DialogExitBinding binding;
    private static AppExitDialog dialog;
    private ViewItemClickListener listener;
    private Context context;

    public static AppExitDialog getInstance(Context context) {
//        if (dialog == null) {
//            synchronized (AppExitDialog.class) {
//                if (dialog == null) {
                    dialog = new AppExitDialog(context);
//                }
//            }
//        }
        return dialog;
    }

    public AppExitDialog(Context context) {
        super(context, R.style.MyDialog2);
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_exit, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (screenWidth * 0.85);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        getWindow().setWindowAnimations(R.style.anim_style);// 设置动画


        binding.tvYes.setOnClickListener(v -> {
            if (listener != null)
                listener.clickView();
            dismiss();
        });
        binding.tvNo.setOnClickListener(v -> dismiss());
    }

    public void setListener(ViewItemClickListener listener) {
        this.listener = listener;
    }

    public interface ViewItemClickListener {
        void clickView();
    }
}
