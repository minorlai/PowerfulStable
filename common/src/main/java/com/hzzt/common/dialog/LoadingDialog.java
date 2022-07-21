package com.hzzt.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.hzzt.common.R;
import com.hzzt.common.databinding.DialogLoadingBinding;


/**
 * @author: Allen
 * @date: 2022/6/9
 * @description: 加载对话框
 */
public class LoadingDialog extends Dialog {
    private DialogLoadingBinding binding;
    private Context context;
    private static LoadingDialog dialog = null;
    private String loadingTxt;

    public static LoadingDialog getDialog() {
        return dialog;
    }

    public static LoadingDialog getInstance(Context context, String loadingTxt) {
        if (dialog == null) {
            synchronized (LoadingDialog.class) {
                if (dialog == null) {
                    dialog = new LoadingDialog(context, loadingTxt);
                } else {
                    dialog.dismiss();
                    dialog = new LoadingDialog(context, null);
                }
            }
        } else {
            dialog.dismiss();
            dialog = new LoadingDialog(context, null);
        }
        return dialog;
    }


    public LoadingDialog(Context context, String loadingTxt) {
        super(context, R.style.MyDialog);
        if (TextUtils.isEmpty(loadingTxt)) {
            this.loadingTxt = context.getResources().getString(R.string.app_loading);
        } else {
            this.loadingTxt = loadingTxt;
        }
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_loading, null, false);
        setContentView(binding.getRoot());

        //dialog text
//        binding.tvStatusText.setVisibility(TextUtils.isEmpty(loadingTxt) ? View.GONE : View.VISIBLE);
//        binding.tvStatusText.setText(loadingTxt);
        // 设置外部无效
        setCanceledOnTouchOutside(false);
        //清除Dialog底背景模糊和黑暗度
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);

        //dialog的背景暗度
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.alpha = 0.9f;
        getWindow().setAttributes(params);
    }

    //设置加载颜色
    public void setProgressColor(int color) {
        binding.spinKit.setColor(color);
    }

    /**
     * 显示
     */
    public void showDialog() {
        try {
            if (context != null && dialog != null) {
                if (!dialog.isShowing()) {
                    dialog.show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //销毁
    public void dismissDialog() {
        if (context != null && dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
