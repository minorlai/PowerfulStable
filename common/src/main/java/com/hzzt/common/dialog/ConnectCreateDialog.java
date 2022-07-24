package com.hzzt.common.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.hzzt.common.R;
import com.hzzt.common.databinding.DialogConnectCreateBinding;


/**
 * @author: Allen
 * @date: 2022/7/22
 * @description: 连接其他服务器对话框
 */
public class ConnectCreateDialog extends Dialog {
    private DialogConnectCreateBinding binding;
    private static ConnectCreateDialog dialog;
    private Context context;

    public static ConnectCreateDialog getInstance(Context context) {
        if (dialog == null) {
            synchronized (ConnectCreateDialog.class) {
                if (dialog == null) {
                    dialog = new ConnectCreateDialog(context);
                }
            }
        }
        return dialog;
    }

    public ConnectCreateDialog(Context context) {
        super(context, R.style.MyDialog2);
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_connect_create, null, false);
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        int screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth(); // 屏幕宽
        lp.gravity = Gravity.CENTER;
        lp.width = (int) (screenWidth * 0.85);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        getWindow().setWindowAnimations(R.style.anim_style);// 设置动画
    }

//    @Override
//    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
//            return false;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
